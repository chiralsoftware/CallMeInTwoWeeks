package chiralsoftware.cmi2w;

import java.util.logging.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configure security on this
 */
@Configuration
@EnableWebSecurity
public class MyWebSecurityConfig {

    private static final Logger LOG = Logger.getLogger(MyWebSecurityConfig.class.getName());
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers("/style/**", "/", "/index.htm", "/signup").permitAll();
        http.authorizeHttpRequests().
//                .csrf().disable()
                requestMatchers("/secure/**").hasRole("USER").and().formLogin();
        
        http.authorizeHttpRequests().
//                .csrf().disable()
                requestMatchers("/admin/**").hasRole("ADMIN").and().formLogin();
        
        return http.build();
    }
    
    @Bean 
    public UserDetailsService userDetailsService() {
        final UserDetails user = User.withUsername("user").password("hello").authorities("user").build();
        return new InMemoryUserDetailsManager(user);
    }
    
}    
