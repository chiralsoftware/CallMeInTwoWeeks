package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.daos.MyAuthToken;
import chiralsoftware.cmi2w.entities.WebUser;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
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

    @RequestMapping(value="/secure/controlpanel.htm", method = RequestMethod.GET)
    public ModelAndView controlPanelGet() {
        final Authentication auth = 
                SecurityContextHolder.getContext().getAuthentication();
        if(! (auth instanceof MyAuthToken)) {
            LOG.severe("Auth token was of wrong class!  "
                    + "It was: " + auth.getClass().getName() + ", "
                    + "but I expected: " + MyAuthToken.class.getName());
        }
        final MyAuthToken myAuthToken = (MyAuthToken) auth;
        final ModelAndView modelAndView = new ModelAndView("/secure/controlpanel");
        modelAndView.addObject("webUser", myAuthToken.getWebUser());
        // for testing
//        LOG.info("This is my web user in GET: "+ myAuthToken.getWebUser());
        return modelAndView;
    }

    @Transactional
    @RequestMapping(value="/secure/controlpanel.htm", method = RequestMethod.POST)
    public String controlPanelUpdate(@ModelAttribute WebUser webUser,
            RedirectAttributes redirectAttributes) {
        LOG.info("I need to persist this modified web user: " + webUser);
        final WebUser oldWebUser = 
                ((MyAuthToken) SecurityContextHolder.getContext().getAuthentication()).getWebUser();
        oldWebUser.setAsteriskContext(webUser.getAsteriskContext());
        oldWebUser.setAsteriskDialPrefix(webUser.getAsteriskDialPrefix());
        oldWebUser.setAsteriskExtension(webUser.getAsteriskExtension());
        entityManager.merge(oldWebUser);
        entityManager.flush();
        redirectAttributes.addFlashAttribute("message", "User settings updated");
        return "redirect:/secure/index.htm";
    }    
}
