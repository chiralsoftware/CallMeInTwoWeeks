package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.entities.Contact;
import chiralsoftware.cmi2w.entities.ContactRecord;
import chiralsoftware.cmi2w.entities.TemporaryImage;
import chiralsoftware.cmi2w.security.JpaUserDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import static java.lang.System.currentTimeMillis;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import jakarta.validation.Valid;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.apache.commons.lang3.StringUtils.trim;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping(value = "/secure/contact-new")
    public String addContactGet(@ModelAttribute Contact contact) {
        LOG.info("Adding a new contact");
        return "secure/contact-new";
    }
    
    @Transactional
    @PostMapping(value = "/secure/contact-new")
    public String addContactPost(@AuthenticationPrincipal JpaUserDetails userDetails, @ModelAttribute @Valid Contact contact, RedirectAttributes redirectAttributes,
            BindingResult result) {
        if (entityManager == null)
            LOG.info("Oh no!  EntityManager was null!");
        if (result.hasErrors()) {
            LOG.info("there are errors in the submitted contact, so go back and do it again.");
            return "secure/contact-new";
        }

        contact.setUserId(userDetails.getUserId());
        LOG.info("Read to add new contact! " + contact);

        entityManager.persist(contact);
        redirectAttributes.addFlashAttribute("message", "Contact: " + contact.getName() + " added");
        // ./contact-details-[[${contact.id}]].htm
        return "redirect:/secure/contact-details-" + contact.getId() + ".htm";
    }

    @GetMapping(value = "/secure/contact-details-{contactId}.htm")
    @Transactional(readOnly = true)
    public String details(Authentication authentication, @PathVariable Long contactId, Model model) {
        final Contact contact =
                entityManager.find(Contact.class, contactId);
        model.addAttribute("contact", contact);
        final List<ContactRecord> contactRecords =
                entityManager.createQuery("from ContactRecord cr where cr.contactId = " + contactId + " "
                + "order by contactTime desc", ContactRecord.class).getResultList();

        model.addAttribute("contactRecords", contactRecords);
        model.addAttribute("dateUtility", new DateUtility());
        
        model.addAttribute("dialoutEnabled", ((JpaUserDetails)authentication.getPrincipal()).isDialoutEnabled());
        return "secure/contact-details";
    }

    
    @Transactional(readOnly = true)
    @GetMapping(value = "/secure/contact-edit-{contactId}.htm")
    public String edit(@PathVariable("contactId") Long contactId, Model model) {
        final Contact contact =
                entityManager.find(Contact.class, contactId);
        model.addAttribute("contact", contact);
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

        oldContact.setEmail(trim(contact.getEmail()));
        oldContact.setName(normalizeSpace(contact.getName()));
        oldContact.setNotes(trim(contact.getNotes()));
        oldContact.setOrganization(normalizeSpace(contact.getOrganization()));
        oldContact.setPhone(normalizeSpace(contact.getPhone()));
        oldContact.setExtension(normalizeSpace(contact.getExtension()));
        oldContact.setMobile(normalizeSpace(contact.getMobile()));

        oldContact.setAddressLine1(normalizeSpace(contact.getAddressLine1()));
        oldContact.setAddressLine2(normalizeSpace(contact.getAddressLine2()));
        oldContact.setAddressCity(normalizeSpace(contact.getAddressCity()));
        oldContact.setAddressProvince(normalizeSpace(contact.getAddressProvince()));
        oldContact.setAddressPostalCode(normalizeSpace(contact.getAddressPostalCode()));
        oldContact.setAddressCountry(normalizeSpace(contact.getAddressCountry()));

        if ("1".equalsIgnoreCase(deleteImage))
            oldContact.setIcon(null);

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

    /** Add a contact record */
    @Transactional
    @PostMapping(value = "/secure/contact-details-{contactId}.htm")
    public String contactRecordPost(@AuthenticationPrincipal JpaUserDetails userDetails, @PathVariable Long contactId, @RequestParam("nextDate") String nextDateString,
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
        contactRecord.setContactTime(currentTimeMillis());
        try {
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            df.setTimeZone(userDetails.getTimeZone());
            final Date d = df.parse(nextDateString);
            LOG.info("The parsed date is: " + d);
            contactRecord.setNextContactTime(d.getTime());
        } catch (ParseException pe) {
            LOG.info("Oh no! " + pe);
            contactRecord.setNextContactTime(new Date().getTime());
        }
        contactRecord.setContactId(contactId);
        contactRecord.setNotes(trim(notes));
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
    @GetMapping(value = "/secure/contact-image-{id}")
    public ResponseEntity<byte[]> showContactImage(@PathVariable Long id) {
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-type", IMAGE_PNG_VALUE);
        final Contact contact = entityManager.find(Contact.class, id);
        if (contact == null) {
            LOG.warning("The contact for id: " + id + " returned NULL!");
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(entityManager.find(Contact.class, id).getIcon(),
                responseHeaders, HttpStatus.OK);
    }
}
