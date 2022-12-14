package chiralsoftware.cmi2w.security;

import chiralsoftware.cmi2w.entities.WebUser;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.logging.Logger;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Authenticate users using JPA.
 * This is based on this presentation:  https://www.youtube.com/watch?v=awcCiqBO36E
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger LOG = Logger.getLogger(MyUserDetailsService.class.getName());

    @PersistenceContext
    private EntityManager entityManager;
    
    @Value("${adminPassword}")
    private String adminPassword = null;
    
    private UserDetails adminUserDetails;
    
    @PostConstruct
    public void postConstruct() {
        adminUserDetails = new AdminUserDetails(adminPassword);
        
    }
    
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final String safeUsername = username.toLowerCase().trim();
        LOG.info("I need to load the user details for username: " + abbreviate(safeUsername, 10));
        if(safeUsername.equals("admin")) return adminUserDetails;
        return entityManager.createQuery("from WebUser u where u.name = :name", WebUser.class).
                // for some reason getResultStream() was crashing h2. using getResultList().stream() instead
                // made it work. this must be an h2 driver bug
                setParameter("name", safeUsername).getResultList().stream().findAny().
                map(JpaUserDetails::new).
                orElseThrow(() -> new UsernameNotFoundException("username: " + abbreviate(safeUsername, 10) + " not found"));
    }
}
