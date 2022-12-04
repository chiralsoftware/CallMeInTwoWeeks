package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.entities.WebUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import java.util.logging.Logger;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * A Controller to manage user signups
 *
 * @author hh
 */
@Controller
@RequestMapping("/signup")
public class SignupController {

    private static final Logger LOG = Logger.getLogger(SignupController.class.getName());
    @PersistenceContext
    private EntityManager entityManager;

    /** Set to true to require the administrator to activate the account before using */
//    @Value("${activationRequired:false}")
    private boolean activationRequired = true;

    /**
     * This method doesn't do anything other than ensure that the user object is
     * created
     */
    @GetMapping
    public String onGet(@ModelAttribute SignupData signupData) {
        return "signup";
    }

    @Transactional
    @PostMapping
    public String onPost(@ModelAttribute @Valid SignupData signupData,BindingResult result, 
            RedirectAttributes redirectAttributes) {
        if (entityManager == null)
            LOG.info("Oh no! there was no entitymanager!");

        if(! signupData.passwordsMatch()) {
            result.reject("passwords.dontmatch", "The passwords do not match.");
        }
        
        final Query q = entityManager.
                createQuery("select u from WebUser u where u.name = :name").
                setParameter("name", signupData.getUsername());
        final List<WebUser> l = q.getResultList();
        if(! l.isEmpty()) {
            result.reject("userexists", "The username is taken.");
        }

        if(result.hasErrors()) {
            LOG.info("I'm going back to the signup page!");
            return "signup";
        }
        
        final WebUser webUser = new WebUser();
        webUser.setEmail(signupData.getEmail());
        webUser.setName(signupData.getUsername());
        webUser.setPassword(signupData.getPassword1());
        if(activationRequired) { 
            LOG.info("activation is required."); 
            webUser.setActive(false); 
        }
        entityManager.persist(webUser);

        redirectAttributes.addFlashAttribute("message", "user created");
        return "redirect:login"; // success

    }
}
