package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.daos.MyAuthToken;
import chiralsoftware.cmi2w.entities.Contact;
import chiralsoftware.cmi2w.entities.ContactRecord;
import chiralsoftware.cmi2w.entities.TemporaryImage;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller to add, list, and search Contacts
 *
 * @author hh
 */
@Controller
public class ContactController {

    private static final Logger LOG = Logger.getLogger(ContactController.class.getName());
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping(value = "/secure/contact-new.htm")
    public String addContactGet(@ModelAttribute Contact contact) {
        LOG.info("Adding a new contact");
        return "secure/contact-new";
    }
    
    @Transactional
    @PostMapping(value = "/secure/contact-new.htm")
    public String addContactPost(@ModelAttribute @Valid Contact contact, RedirectAttributes redirectAttributes,
            BindingResult result) {
        if (entityManager == null)
            LOG.info("Oh no!  EntityManager was null!");
        if (result.hasErrors()) {
            LOG.info("there are errors in the submitted contact, so go back and do it again.");
            return "secure/contact-new";
        }
        contact.setUserId(((MyAuthToken) SecurityContextHolder.getContext().getAuthentication()).getUserId());
        LOG.info("Read to add new contact! " + contact);

        entityManager.persist(contact);
        redirectAttributes.addFlashAttribute("message", "Contact: " + contact.getName() + " added");
        // ./contact-details-[[${contact.id}]].htm
        return "redirect:/secure/contact-details-" + contact.getId() + ".htm";
    }

    @GetMapping(value = "/secure/contact-details-{contactId}.htm")
    @Transactional(readOnly = true)
    public String details(@PathVariable Long contactId, Model model) {
        final Contact contact =
                entityManager.find(Contact.class, contactId);
        model.addAttribute("contact", contact);
        final List<ContactRecord> contactRecords =
                entityManager.createQuery("from ContactRecord cr where cr.contactId = " + contactId + " "
                + "order by contactTime desc", ContactRecord.class).getResultList();

        model.addAttribute("bbCode", BbCode.getProcessor());
        model.addAttribute("contactRecords", contactRecords);
        model.addAttribute("dateUtility", new DateUtility());
        
        model.addAttribute("dialoutEnabled", 
                ((MyAuthToken) SecurityContextHolder.getContext().getAuthentication()).getWebUser().isDialoutActive());
        return "secure/contact-details";
    }

    @GetMapping(value = "/secure/contact-edit-{contactId}.htm")
    public String edit(@PathVariable("contactId") Long contactId, Model model) {
        final Contact contact =
                entityManager.find(Contact.class, contactId);
        model.addAttribute("contact", contact);
        model.addAttribute("bbCode", BbCode.getProcessor());
        model.addAttribute("dateUtility", new DateUtility());
        return "secure/contact-edit";
    }

    @Transactional
    @PostMapping(value = "/secure/contact-edit-{contactId}.htm", params = "action=delete")
    public String deletePost(@PathVariable("contactId") Long contactId,
            //    @RequestParam(required = false) String action,
            @ModelAttribute Contact contact,
            RedirectAttributes model) {
        LOG.info("We need to DELETE this contact: " + contactId);

        final Contact oldContact =
                entityManager.find(Contact.class, contactId);
        Query query = entityManager.createQuery("delete from ContactRecord cr where cr.contactId = " + oldContact.getId());
        query.executeUpdate();
        query = entityManager.createQuery("delete from Contact c where c.id = " + oldContact.getId());
        query.executeUpdate();
//        model.addAttribute("contactId", oldContact.getId());
        return "redirect:/secure/contacts-list.htm";
    }

    @Transactional
    @PostMapping(value = "/secure/contact-edit-{contactId}.htm",
            params = "!action")
    public String editPost(@PathVariable("contactId") Long contactId,
            @ModelAttribute Contact contact,
            @RequestParam(required = false) Long tempImageId,
            @RequestParam(required = false) String deleteImage,
            RedirectAttributes model) {
        final Contact oldContact =
                entityManager.find(Contact.class, contactId);

        oldContact.setEmail(contact.getEmail());
        oldContact.setName(contact.getName());
        oldContact.setNotes(contact.getNotes());
        oldContact.setOrganization(contact.getOrganization());
        oldContact.setPhone(contact.getPhone());

        oldContact.setAddressLine1(contact.getAddressLine1());
        oldContact.setAddressLine2(contact.getAddressLine2());
        oldContact.setAddressCity(contact.getAddressCity());
        oldContact.setAddressProvince(contact.getAddressProvince());
        oldContact.setAddressPostalCode(contact.getAddressPostalCode());
        oldContact.setAddressCountry(contact.getAddressCountry());

        LOG.info("Value of deleteImage is: " + deleteImage);
        if ("1".equalsIgnoreCase(deleteImage))
            oldContact.setIcon(null);

        LOG.info("HEY THERE! temp image id is: " + tempImageId);
        if (tempImageId != null && tempImageId > 0) {
            // let's also save this image
            final byte[] iconBytes = entityManager.find(TemporaryImage.class, tempImageId).getContent();
            LOG.info("I set the icon bytes in the contact, size = " + iconBytes.length);
            oldContact.setIcon(iconBytes);
        }
        entityManager.merge(oldContact);
        model.addAttribute("contactId", oldContact.getId());
        return "redirect:/secure/contact-details-{contactId}.htm";
    }

    @Transactional
    @PostMapping(value = "/secure/contact-details-{contactId}.htm")
    public String detailsPost(@PathVariable Long contactId, @RequestParam("nextDate") String nextDateString,
            @RequestParam("notes") String notes,
            @RequestParam(value = "dialed", required = false) String dialed,
            @RequestParam(value = "voicemail", required = false) String voicemail,
            @RequestParam(value = "leftmessage", required = false) String leftmessage,
            RedirectAttributes model) {
// FIXME - verify that the contact is there, is owned by the user
//        final Contact contact =
//                entityManager.find(Contact.class, contactId);
        model.addAttribute("contactId", contactId);
        // the format from datetime-local is:
        // '2013-10-11T23:58'
        // which is in the user's local TZ
        final ContactRecord contactRecord = new ContactRecord();
        contactRecord.setContactTime(System.currentTimeMillis());
        try {
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            df.setTimeZone(((MyAuthToken) SecurityContextHolder.getContext().getAuthentication()).getTimeZone());
            final Date d = df.parse(nextDateString);
            LOG.info("The parsed date is: " + d);
            contactRecord.setNextContactTime(d.getTime());
        } catch (ParseException pe) {
            LOG.info("Oh no! " + pe);
            contactRecord.setNextContactTime(new Date().getTime());
        }
        contactRecord.setContactId(contactId);
        contactRecord.setNotes(notes);
        contactRecord.setDialed("1".equalsIgnoreCase(dialed));
        contactRecord.setVoicemail("1".equalsIgnoreCase(voicemail));
        contactRecord.setLeftmessage("1".equalsIgnoreCase(leftmessage));
        entityManager.persist(contactRecord);
        LOG.info("New contactRecord: " + contactRecord + " has been persisted!");
        return "redirect:/secure/contact-details-{contactId}.htm";
    }

    /**
     * Display an image for a contact
     */
    @Transactional(readOnly = true) 
    @GetMapping(value = "/secure/contact-image-{id}.htm")
    public ResponseEntity<byte[]> showContactImage(@PathVariable Long id) {
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-type", "image/png");
        final Contact contact = entityManager.find(Contact.class, id);
        if (contact == null) {
            LOG.warning("The contact for id: " + id + " returned NULL!");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(entityManager.find(Contact.class, id).getIcon(),
                responseHeaders, HttpStatus.OK);
    }
}
