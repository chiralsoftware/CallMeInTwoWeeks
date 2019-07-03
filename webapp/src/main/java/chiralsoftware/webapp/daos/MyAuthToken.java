/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chiralsoftware.webapp.daos;

import entities.WebUser;
import java.util.Collection;
import java.util.TimeZone;
import java.util.logging.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author hh
 */
public final class MyAuthToken extends UsernamePasswordAuthenticationToken {
    private static final Logger LOG = Logger.getLogger(MyAuthToken.class.getName());

    public MyAuthToken(Object principal, Object credentials,
            Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
    
    private Long userId;
    private WebUser webUser;
    
    public void setUserId(Long l) {
        this.userId = l;
    }
    
    public Long getUserId() {
        return userId;
    }

    /**
     * @return the webUser
     */
    public WebUser getWebUser() {
        return webUser;
    }

    /**
     * @param webUser the webUser to set
     */
    public void setWebUser(WebUser webUser) {
        this.webUser = webUser;
    }
    
    /** Get the user's current time zone, or a suitable default */
    public TimeZone getTimeZone() {
        if(webUser.getTimeZone() == null) return TimeZone.getDefault();
        return TimeZone.getTimeZone(webUser.getTimeZone());
    }
    
    
    @Override
    public String toString() {
        return "MyAuthToken: webUser=" + webUser + ", "
                + "userId=" + userId + ", " + super.toString();
    }
    
}
