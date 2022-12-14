package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.entities.Contact;
import chiralsoftware.cmi2w.entities.ContactRecord;
import chiralsoftware.cmi2w.security.JpaUserDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.logging.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping(value="/secure/contactrecord-edit-{contactRecordId}.htm")
    @Transactional(readOnly = true)
    public String contactRecordGet(@AuthenticationPrincipal JpaUserDetails userDetails, 
            @PathVariable Long contactRecordId, Model model) {
        final ContactRecord contactRecord = entityManager.find(ContactRecord.class, contactRecordId);
        model.addAttribute("contactRecord", contactRecord);
        
        final Contact contact = entityManager.find(Contact.class, contactRecord.getContactId());
        model.addAttribute("contact", contact);
        
        // this shouldn't be here!
        model.addAttribute("dateUtility", new DateUtility());

        model.addAttribute("dialoutEnabled", userDetails.isDialoutEnabled());
        
        return "secure/contactrecord-edit";
    }
    
    @PostMapping(value="/secure/contactrecord-edit-{contactRecordId}.htm")
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
            entityManager.remove(contactRecord);
            return "redirect:/secure/contact-details-{contactId}.htm";
        }

        final Date nextContactDate = DateUtility.parseInputString(nextDate);
        if(nextContactDate == null) {
            LOG.info("next contact date was null");
            contactRecord.setNextContactTime(0);
        } else {
            contactRecord.setNextContactTime(nextContactDate.getTime());
        }
        contactRecord.setNotes(notes == null ? null : notes.trim());
        redirectAttributes.addFlashAttribute("message", "Contact updated");
        return "redirect:/secure/contact-details-{contactId}.htm";
    }

}
