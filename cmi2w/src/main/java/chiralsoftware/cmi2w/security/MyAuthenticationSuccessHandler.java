package chiralsoftware.cmi2w.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Make it so that admin users go to the admin section, other users
 * go to the normal place for them
 * 
 * @author hh
 */
public final class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger LOG = Logger.getLogger(MyAuthenticationSuccessHandler.class.getName());
    
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest hsr, HttpServletResponse httpServletResponse, Authentication a) 
            throws IOException, ServletException {
        final Set<String> roles = AuthorityUtils.authorityListToSet(a.getAuthorities());
        LOG.info("Here are the roles: " + roles);
        if(roles.stream().anyMatch(s -> s.toLowerCase().endsWith("admin"))) { 
            LOG.info("Authentication success, user is an admin user.");
//            httpServletResponse.sendRedirect("admin/list-users.htm"); 
            redirectStrategy.sendRedirect(hsr, httpServletResponse, "admin/list-users.htm"); 
            return; 
        }
        if(roles.stream().anyMatch(s -> s.toLowerCase().endsWith("user"))) { 
            LOG.info("Authentication success. user is a normal user.");
//            httpServletResponse.sendRedirect("secure/index.htm"); 
            redirectStrategy.sendRedirect(hsr, httpServletResponse, "secure/index.htm"); 
            return; 
        }
        LOG.warning("Roles set was unexpected: " + roles);
    }
    
}
