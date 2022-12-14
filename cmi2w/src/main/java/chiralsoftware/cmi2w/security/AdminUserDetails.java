package chiralsoftware.cmi2w.security;

import java.util.Collection;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User details just for the admin user.
 * In a larger system with multiple admin users this should be merged
 * in to the JpaUserDetails class
 */
final class AdminUserDetails implements UserDetails {
    
    private final String adminPassword;
    
    AdminUserDetails(String adminPassword) {
        if(adminPassword == null) throw new NullPointerException("don't create this user with a null password");
        if(adminPassword.isBlank()) throw new IllegalArgumentException("don't create a blank admin password");
        this.adminPassword = adminPassword.trim();
    }

    @Override
    public String toString() {
        return "AdminUserDetails{" + "adminPassword=" + adminPassword + ", authorities=" + authorities + '}';
    }

    private final Collection<GrantedAuthority> authorities =
            Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return adminPassword;
    }

    @Override
    public String getUsername() {
        return "admin";
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
    
}
