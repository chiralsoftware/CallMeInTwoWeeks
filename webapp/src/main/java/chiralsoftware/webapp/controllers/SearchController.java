package chiralsoftware.webapp.controllers;

import java.util.logging.Logger;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This should do text searches of notes an contacts
 * See: https://www.baeldung.com/hibernate-search
 * 
 */
@Controller
public class SearchController {

    private static final Logger LOG = Logger.getLogger(SearchController.class.getName());
    
    @GetMapping("/secure/search")
    public void search(String q) {
        LOG.info("I should be doing a search for: " + q);
        FullTextEntityManager f;
    }
}
