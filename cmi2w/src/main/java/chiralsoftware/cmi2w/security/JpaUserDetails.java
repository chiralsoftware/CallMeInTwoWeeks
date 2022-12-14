package chiralsoftware.cmi2w.security;

import chiralsoftware.cmi2w.entities.WebUser;
import java.util.Collection;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A class to implement UserDetails
 */
public final class JpaUserDetails implements UserDetails {

    private static final Logger LOG = Logger.getLogger(JpaUserDetails.class.getName());
    
    
    JpaUserDetails(WebUser webUser) {
        if(webUser == null) throw new NullPointerException("Can't create JpaUserDetails with a null WebUser");
        this.webUser = webUser;
    }
    
    private final WebUser webUser;
    
    public Long getUserId() { return webUser.getId(); }
    
    public boolean isDialoutEnabled() { return webUser.isDialoutActive(); }
    
    /* 
    
if(webUser.getTimeZone() == null) return TimeZone.getDefault();
        return TimeZone.getTimeZone(webUser.getTimeZone());    
    */
    public TimeZone getTimeZone() { 
        final String tzString = webUser.getTimeZone();
        if(tzString == null || StringUtils.isBlank(tzString)) return TimeZone.getDefault();
        return  TimeZone.getTimeZone(tzString);
    }
    
    private final Collection<GrantedAuthority> authorities =
            Set.of(new SimpleGrantedAuthority("ROLE_USER"));

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return webUser.getPassword();
    }

    @Override
    public String getUsername() {
        return webUser.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public WebUser getWebUser() {
        return webUser;
    }
}