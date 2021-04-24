package chiralsoftware.cmi2w.controllers;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Make it so that admin users go to the admin section, other users
 * go to the normal place for them
 * 
 * @author hh
 */
public final class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger LOG = Logger.getLogger(MyAuthenticationSuccessHandler.class.getName());
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest hsr, HttpServletResponse httpServletResponse, Authentication a) 
            throws IOException, ServletException {
        final Set<String> roles = AuthorityUtils.authorityListToSet(a.getAuthorities());
        if(roles.stream().anyMatch(s -> s.toLowerCase().endsWith("admin"))) { httpServletResponse.sendRedirect("admin/list-users.htm"); return; }
        if(roles.stream().anyMatch(s -> s.toLowerCase().endsWith("user"))) { httpServletResponse.sendRedirect("secure/index.htm"); return; }
        LOG.info("Roles set was unexpected: " + roles);
    }
    
}
