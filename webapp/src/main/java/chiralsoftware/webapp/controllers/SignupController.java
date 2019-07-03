package chiralsoftware.webapp.controllers;

import entities.WebUser;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * A Controller to manage user signups
 *
 * @author hh
 */
@Controller
@RequestMapping("/signup.htm")
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
    @RequestMapping(method = RequestMethod.GET)
    public String onGet(@ModelAttribute SignupData signupData) {
        return "signup";
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public String onPost(@ModelAttribute @Valid SignupData signupData,BindingResult result) {
        if (entityManager == null)
            LOG.info("Oh no! there was no entitymanager!");

        LOG.info("The webuser is: " + signupData);
        LOG.info("The result is: " + result);
        
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
        if(activationRequired) { LOG.info("activation is required."); webUser.setActive(false); }
        entityManager.persist(webUser);

        // we should use "flash" variables to show the result to the user
        return "redirect:login.htm"; // success

    }
}
