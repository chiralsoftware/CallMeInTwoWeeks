package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.entities.WebUser;
import chiralsoftware.cmi2w.security.JpaUserDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.logging.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Allow the user to change settings
 * 
 * @author hh
 */
@Controller
public class ControlPanelController {
    private static final Logger LOG = Logger.getLogger(ControlPanelController.class.getName());
    
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/secure/controlpanel")
    public String controlPanelGet(@AuthenticationPrincipal JpaUserDetails userDetails,
            Model model) {
        model.addAttribute("webUser", userDetails.getWebUser());
        return "secure/controlpanel";
    }

    @Transactional
    @PostMapping("/secure/controlpanel")
    public String controlPanelUpdate(@AuthenticationPrincipal JpaUserDetails userDetails, 
            @ModelAttribute WebUser webUser,
            RedirectAttributes redirectAttributes) {
        LOG.info("I need to persist this modified web user: " + webUser);
        final WebUser oldWebUser = userDetails.getWebUser();
        oldWebUser.setAsteriskContext(webUser.getAsteriskContext());
        oldWebUser.setAsteriskDialPrefix(webUser.getAsteriskDialPrefix());
        oldWebUser.setAsteriskExtension(webUser.getAsteriskExtension());
        entityManager.merge(oldWebUser);
        entityManager.flush();
        redirectAttributes.addFlashAttribute("message", "User settings updated");
        return "redirect:/secure/index.htm";
    }    
}
