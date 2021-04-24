package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.entities.WebUser;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author hh
 */
@Controller
public class WebAdminController {

    private static final Logger LOG = Logger.getLogger(WebAdminController.class.getName());
    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(value = "/admin/list-users.htm",
            method = RequestMethod.GET)
    public String onSubmit(Model model) {
        LOG.info("Need to list all the users");
        if (entityManager == null)
            LOG.info("Oh no! EntityManager was null!");

        final Query query = entityManager.createQuery("from WebUser wu order by wu.name");
        final List<WebUser> webUsers = query.getResultList();
        model.addAttribute("webUsers", webUsers);
        return "/admin/list-users";
    }

    /**
     * Show some user data
     */
    @RequestMapping(value = "/admin/user-edit-{webuserId}.htm",
            method = RequestMethod.GET)
    public String showUser(@PathVariable Long webuserId,
            Model model) {
        LOG.info("Showing user: " + webuserId);

        final WebUser webUser = entityManager.find(WebUser.class, webuserId);
        model.addAttribute("webUser", webUser);
        return "/admin/user-edit";
    }

    @RequestMapping(value = "/admin/user-edit-{webuserId}.htm",
            method = RequestMethod.POST)
    @Transactional
    public String updateUser(@ModelAttribute @Valid WebUser webUser, BindingResult bindingResult,
    RedirectAttributes redirectAttributes) {
        LOG.info("Hello!");
        LOG.info("The web user is: " + webUser);
        if(bindingResult.hasErrors()) {
            LOG.info("OH no! the binding result had some ERRORS!!!!");
            redirectAttributes.addAttribute("webuserId", webUser.getId());
            return "/admin/user-edit-{webuserId}.htm";
        }
        LOG.info("I'm about to update the web user");
        entityManager.merge(webUser);
        entityManager.flush();
        LOG.info("I'm done updating it!");
//        final WebUser webUser = entityManager.find(WebUser.class, webuserId);
//        redirectAttributes.addAttribute("webUser", webUser);
        redirectAttributes.addFlashAttribute("message", "User: " + webUser.getName() + " updated");
        return "redirect:/admin/list-users.htm";
    }
}
