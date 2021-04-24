package chiralsoftware.cmi2w.controllers;

import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * A collection of useful request mappings
 */
@Controller
public class MainController {

    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Generate the main index page
     */
    @RequestMapping("/")
    public String getIndex(Model model) {
        if (entityManager == null) {
            LOG.warning("There is no entity manager");
            model.addAttribute("message", "System is not configured");
            model.addAttribute("systemActive", false);
        } else {
            model.addAttribute("systemActive", true);
        }
        return "index";
    }

}
