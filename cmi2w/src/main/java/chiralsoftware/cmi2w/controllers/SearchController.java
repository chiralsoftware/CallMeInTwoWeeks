package chiralsoftware.cmi2w.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This should do text searches of notes an contacts See:
 * https://www.baeldung.com/hibernate-search
 * When we move this to Spring Boot look at this example for setting
 * properties:
 * https://github.com/netgloo/spring-boot-samples/blob/master/spring-boot-hibernate-search/
 *
 */
@Controller
public class SearchController {

    private static final Logger LOG = Logger.getLogger(SearchController.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/secure/search")
    public void search(String q) {
        LOG.info("I should be doing a search for: " + q);
    }

    @GetMapping("/secure/rebuild-index")
    @ResponseBody
    public String rebuildIndex() {
        return "INDEX REBUILT";
    }
}
