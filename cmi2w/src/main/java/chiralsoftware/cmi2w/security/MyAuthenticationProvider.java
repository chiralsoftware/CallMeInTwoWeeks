package chiralsoftware.cmi2w.security;

import chiralsoftware.cmi2w.daos.MyAuthToken;
import chiralsoftware.cmi2w.entities.WebUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Authenticate users using JPA.
 * This should be changed to use a UserDetailsService instead of an AuthenticationProvider.
 */
@Component
public class MyAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = Logger.getLogger(MyAuthenticationProvider.class.getName());
    @PersistenceContext
    private EntityManager entityManager;
    
    @Value("${adminPassword}")
    private String adminPassword = null;

    @Override
    public Authentication authenticate(Authentication a) throws AuthenticationException {
        LOG.info("..-------- i'm in authenticate");
        if (entityManager == null) {
            LOG.severe("The EntityManager is NULL!");
            return null;
        }

        if (a.getName().equalsIgnoreCase("admin")) { // add an admin role
            if (adminPassword == null) {
                LOG.warning("The adminPassword property in the properties file is not set!");
                throw new InternalAuthenticationServiceException("Admin password is set to null");
            }
            if (adminPassword.equals("admin")) {
                LOG.warning("The adminPassword is set to the default value.  This is not secure!");
            }
            if(adminPassword.equalsIgnoreCase("disabled")) {
                LOG.info("Admin account password is set to disabled, so admin login is blocked.");
                throw new DisabledException("Admin account is disabled");
            }
            if (!adminPassword.equals(a.getCredentials().toString())) {
                LOG.warning("The desired admin password is: " + adminPassword + " "
                        + "and you provided: " + a.getCredentials().toString());
                throw new BadCredentialsException("Admin access denied");
            }
            LOG.warning("Logging in ADMIN user");
            final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(a.getName(),
                    a.getCredentials().toString(), 
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            return auth;
        }

        // FIXME username should not be case sensitive
        final Query q = entityManager.createQuery("from WebUser u where u.name = :name",
                WebUser.class).setParameter("name", a.getName().trim());
        final List<WebUser> users = q.getResultList();
        if (users.size() != 1)
            throw new UsernameNotFoundException("User not found");
        final WebUser webUser = users.get(0);
        if (!webUser.isActive()) {
            LOG.info("Denied; user: " + webUser.getName() + " is not active.");
            throw new DisabledException("User is not active");
        }
        if(!webUser.getPassword().equals(a.getCredentials().toString())) {
            LOG.info("the user: " + webUser + " password didn't match: " + a.getCredentials().toString());
            throw new BadCredentialsException("Password was incorrect");
        }
        LOG.info("Returning a new user object, the user is logged in!");
        final MyAuthToken auth =
                new MyAuthToken(a.getName(), a.getCredentials().toString(),
                singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        LOG.info("here's the auth: "+ auth);

        auth.setWebUser(webUser);
        auth.setUserId(webUser.getId());
        return auth;
    }

    @Override
    public boolean supports(Class<?> type) {
        LOG.info("I'm looking at class: " + type.getName());
        return true;
    }

}
