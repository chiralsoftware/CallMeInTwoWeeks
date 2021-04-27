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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping(value = "/admin/list-users.htm")
    public String onSubmit(Model model) {
        LOG.info("Need to list all the users");
        if (entityManager == null)
            LOG.info("Oh no! EntityManager was null!");

        final Query query = entityManager.createQuery("from WebUser wu order by wu.name");
        final List<WebUser> webUsers = query.getResultList();
        LOG.info("Here are the users: "+ webUsers);
        model.addAttribute("webUsers", webUsers);
        LOG.info("about to return the list");
        return "admin/list-users";
    }

    /**
     * Show some user data
     */
    @GetMapping(value = "/admin/user-edit-{webuserId}.htm")
    public String showUser(@PathVariable Long webuserId,Model model) {

        final WebUser webUser = entityManager.find(WebUser.class, webuserId);
        model.addAttribute("webUser", webUser);
        return "admin/user-edit";
    }

    @PostMapping(value = "/admin/user-edit-{webuserId}.htm")
    @Transactional
    public String updateUser(@ModelAttribute @Valid WebUser webUser, BindingResult bindingResult,
    RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            LOG.info("OH no! the binding result had some ERRORS!!!!");
            redirectAttributes.addAttribute("webuserId", webUser.getId());
            return "/admin/user-edit-{webuserId}.htm";
        }
        LOG.info("I'm about to update the web user: " + webUser);
        entityManager.merge(webUser);
        entityManager.flush();
        LOG.info("I'm done updating it!");
//        final WebUser webUser = entityManager.find(WebUser.class, webuserId);
//        redirectAttributes.addAttribute("webUser", webUser);
        redirectAttributes.addFlashAttribute("message", "User: " + webUser.getName() + " updated");
        return "redirect:/admin/list-users.htm";
    }
}
