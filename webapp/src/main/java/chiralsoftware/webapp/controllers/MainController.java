package chiralsoftware.webapp.controllers;

import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * A collection of useful request mappings
 *
 * @author hh
 */
@Controller
public class MainController {
    
    private static final Logger LOG = Logger.getLogger(MainController.class.getName());
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private ITemplateResolver templateResolver;
    

    /** Generate the main index page */
    @RequestMapping("/index.html") 
    public String getIndex(Model model) {
        if(entityManager == null) {
            LOG.warning("There is no entity manager");
            model.addAttribute("message", "System is not configured");
            model.addAttribute("systemActive", false);
        } else {
            model.addAttribute("systemActive", true);
        }
        final ServletContextTemplateResolver sctr = (ServletContextTemplateResolver) templateResolver;
//        LOG.info("Is caching enabled? " + sctr.isCacheable());
        if(sctr.isCacheable()) {
//            LOG.info("caching is enabled, so I will turn it off");
            sctr.setCacheable(false);
        }
        return "index";
    }
    
    @RequestMapping(value="/test.htm", method=RequestMethod.GET) 
    public String getTestPage() {
        LOG.info("Ok, i'm in test get page");
        return "test";
    }
}
