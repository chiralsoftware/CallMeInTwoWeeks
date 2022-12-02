package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.entities.Contact;
import chiralsoftware.cmi2w.entities.ContactRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import static java.lang.System.currentTimeMillis;
import java.util.ArrayList;
import static java.util.Collections.shuffle;
import static java.util.Collections.sort;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Generate lists of contacts
 *
 */
@Controller
public class ContactListController {

    private static final Logger LOG = Logger.getLogger(ContactListController.class.getName());
    @PersistenceContext
    private EntityManager entityManager;

    private static final Comparator<ContactAndRecord> newestContactRecord
            = new Comparator<ContactAndRecord>() {
                @Override
                public int compare(ContactAndRecord o1, ContactAndRecord o2) {
                    int o1ContactTime = (int) (o1.getContactTime() / 1000);
                    int o2ContactTime = (int) (o2.getContactTime() / 1000);

                    return o2ContactTime - o1ContactTime;
                }
            };

    /** Make this into a Java 16 record! */
    private static final class ContactAndRecord {

        private ContactAndRecord(Contact contact, ContactRecord contactRecord) {
            this.contact = contact;
            this.contactRecord = contactRecord;
        }

        private Map<String, Object> getValuesAsMap() {
            final Map<String, Object> result = new HashMap<>();
            contact.addValuesToMap(result);
            if (contactRecord != null) contactRecord.addToMap(result);
            return result;
        }
        private final Contact contact;
        private final ContactRecord contactRecord;

        private long getContactTime() {
            if (contactRecord == null) {
                return 0;
            }
            return contactRecord.getContactTime();
        }

        private long getNextCallTime() {
            if (contactRecord == null) {
                return Long.MAX_VALUE;
            }
            if (contactRecord.getNextContactTime() == 0) {
                return Long.MAX_VALUE;
            }
            return contactRecord.getNextContactTime();
        }
    }

    private List<ContactAndRecord> listAll(String category) {
        final List<ContactAndRecord> result = new ArrayList<>();

        final Query q;
        if(category != null && (! category.isEmpty())) {
            q = entityManager.createQuery("from Contact c where c.category = :cat", Contact.class);
            q.setParameter("cat", category);
        } else {
            q = entityManager.createQuery("from Contact c", Contact.class);
        }
        
        final List<Contact> allContacts = q.getResultList();
        final Query crQuery = entityManager.createQuery("from ContactRecord cr where cr.contactId = :id order by cr.contactTime desc",
                        ContactRecord.class);
        crQuery.setMaxResults(1);
        for (Contact c : allContacts) {
            final List<ContactRecord> records
                    = crQuery.setParameter("id", c.getId()).getResultList();
            if (records.size() > 1) {
                LOG.warning("Oh no! multiple contact records returned???");
            }
            if (records.isEmpty()) {
                result.add(new ContactAndRecord(c, null));
            } else {
                result.add(new ContactAndRecord(c, records.get(0)));
            }
        }
        return result;
    }
    
    /** Return a list of the possible categories */
    List<String> listCategories() {
        final List<String> result = entityManager.
                createQuery("select distinct contact.category "
                        + "from Contact contact order by contact.category", String.class).
                getResultList();
        return result;
    }

    // For the dashboard we have a very similar operation
    // but we show things on different views
    /** The read-only transaction is needed for Postgres. see:
     * https://forums.ohdsi.org/t/specify-transactional-on-jpa-repository-for-operations-involving-lob-fields/370/7
     */
    @GetMapping(value = {"/secure/index.htm", "/secure/contacts-list.htm"})
    @Transactional(readOnly = true) 
    public String dashboard(Model model, @RequestParam(required = false) String sort,
            @RequestParam(required=false) String category) {

        model.addAttribute("categories", listCategories());
        model.addAttribute("selectedCategory", category);
        
        LOG.info("getting the dashboard view");

        // Get a list of all relevant contacts
        final List<ContactAndRecord> everything = listAll(category);
        LOG.info("got all the contacts: " + everything.size());
        
        final int limit = sort == null ? 5 : 1000;
        
        if (sort == null || "recent".equalsIgnoreCase(sort)) {
            // the MOST RECENT calls made
            sort(everything, newestContactRecord);
            final List<Map<String, Object>> mostRecentContacts = new ArrayList<>();
            for (int i = 0; i < limit && i < everything.size(); i++) {
                mostRecentContacts.add(everything.get(i).getValuesAsMap());
            }
            if(sort == null) {
                model.addAttribute("mostRecent", mostRecentContacts);
            } else {
                model.addAttribute("contactTableName", "Recent");
                model.addAttribute("contacts", mostRecentContacts);
            }
        }

        if (sort == null || "old".equalsIgnoreCase(sort)) {
            // the LEAST RECENT calls made
            final List<Map<String, Object>> leastRecentContacts = new ArrayList<>();
            for (int i = everything.size() - 1; i >= 0 && i >= everything.size() - limit; i--) {
                leastRecentContacts.add(everything.get(i).getValuesAsMap());
            }
            if(sort == null) {
                model.addAttribute("leastRecent", leastRecentContacts);
            } else {
                model.addAttribute("contactTableName", "Old");
                model.addAttribute("contacts", leastRecentContacts);
            }
        }

        // MOST URGENT calls - sort by next contact date
        /**
         * We need an instance comparator so that we can have a fixed current
         * time, otherwise the compare operation may be different as it is
         * called repeatedly
         */
        if (sort == null || "urgent".equalsIgnoreCase(sort)) {
            final List<Map<String, Object>> urgentContacts = new ArrayList<>();
            sort(everything, new Comparator<ContactAndRecord>() {
                private final long currentTime
                        = (int) (currentTimeMillis() / 1000);

                @Override
                public int compare(ContactAndRecord o1, ContactAndRecord o2) {
                    int urgency1 = (int) ((o1.getNextCallTime() - currentTime) / 1000);
                    int urgency2 = (int) ((o2.getNextCallTime() - currentTime) / 1000);
                    return urgency1 - urgency2;
                }
            });
            for (int i = 0; i < limit && i < everything.size(); i++) {
                urgentContacts.add(everything.get(i).getValuesAsMap());
            }
            if(sort == null) {
                model.addAttribute("urgent", urgentContacts);
            } else {
                model.addAttribute("contactTableName", "Urgent");
                model.addAttribute("contacts", urgentContacts);
            }
        }

        // a list at random!
        if (sort == null || "random".equalsIgnoreCase(sort)) {
            shuffle(everything);
            final List<Map<String, Object>> randomContacts = new ArrayList<>();
            for (int i = 0; i < limit && i < everything.size(); i++) {
                randomContacts.add(everything.get(i).getValuesAsMap());
            }
            if(sort == null) {
                model.addAttribute("random", randomContacts);
            } else {
                model.addAttribute("contactTableName", "Lucky");
                model.addAttribute("contacts", randomContacts);
            }
        }
        
        LOG.info("ok everything is sorted!");

        model.addAttribute("bbCode", BbCode.getProcessor());
        model.addAttribute("dateUtility", new DateUtility());

        if (sort == null) {
            model.addAttribute("contactTableName", null);
            return "secure/index";
        } // dashboard view

        return "secure/contacts-list";
    }
}
