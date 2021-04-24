package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.entities.Contact;
import chiralsoftware.cmi2w.entities.ContactRecord;
import java.util.Date;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author hh
 */
@Controller
public class ContactRecordController {
    private static final Logger LOG = Logger.getLogger(ContactRecordController.class.getName());
    
    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(value="/secure/contactrecord-edit-{contactRecordId}.htm",
            method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public String contactRecordGet(@PathVariable Long contactRecordId, Model model) {
        final ContactRecord contactRecord = entityManager.find(ContactRecord.class, contactRecordId);
        model.addAttribute("contactRecord", contactRecord);
        
        final Contact contact = entityManager.find(Contact.class, contactRecord.getContactId());
        model.addAttribute("contact", contact);
        
        model.addAttribute("bbCode", BbCode.getProcessor());
        model.addAttribute("dateUtility", new DateUtility());
        return "/secure/contactrecord-edit";
    }
    
    @RequestMapping(value="/secure/contactrecord-edit-{contactRecordId}.htm",
            method= RequestMethod.POST)
    @Transactional
    public String contactRecordPost(@PathVariable Long contactRecordId, 
    @RequestParam(required = false) String delete, 
    @RequestParam(required = false) String nextDate,
    @RequestParam(required = false) String notes, 
    RedirectAttributes redirectAttributes) {
        final ContactRecord contactRecord = entityManager.find(ContactRecord.class, contactRecordId);
        LOG.info("Updating contact record: "+ contactRecord);
        final Contact contact = entityManager.find(Contact.class, contactRecord.getContactId());
        
        redirectAttributes.addAttribute("contactId", contact.getId());
        
        if("true".equalsIgnoreCase(delete)) {
            LOG.info("Deleted was checked!");
            entityManager.remove(contactRecord);
            // we go back to the contact
            return "redirect:/secure/contact-details-{contactId}.htm";
        }

        LOG.info("The new date is: " + nextDate);
        LOG.info("The new notes: " + notes);
        final Date nextContactDate = DateUtility.parseInputString(nextDate);
        if(nextContactDate == null) {
            LOG.info("next contact date was null");
            contactRecord.setNextContactTime(0);
        } else {
            contactRecord.setNextContactTime(nextContactDate.getTime());
        }
        contactRecord.setNotes(notes == null ? null : notes.trim());
//        redirectAttributes.addAttribute("contact", contact);
//        redirectAttributes.addAttribute("contactRecord", contactRecord);
//
//        redirectAttributes.addAttribute("bbCode", BbCode.getProcessor());
//        redirectAttributes.addAttribute("dateUtility", new DateUtility());
        
        redirectAttributes.addFlashAttribute("message", "Contact updated");
        return "redirect:/secure/contact-details-{contactId}.htm";
    }

}
