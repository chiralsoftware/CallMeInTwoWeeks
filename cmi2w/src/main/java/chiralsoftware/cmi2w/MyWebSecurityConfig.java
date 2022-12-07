package chiralsoftware.cmi2w;

import chiralsoftware.cmi2w.security.MyAuthenticationSuccessHandler;
import java.util.logging.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests().requestMatchers("/style/**", "/", "/index.htm", "/login", "/signup").permitAll();
//        http.authorizeHttpRequests().
//                .csrf().disable()
//                requestMatchers("/secure/**").hasRole("USER").and().formLogin().loginPage("/login").successForwardUrl("/secure/index.htm");
        
//        http.authorizeHttpRequests().
//                requestMatchers("/admin/**").hasRole("ADMIN").and().formLogin().
//                    defaultSuccessUrl("/admin/list-users.htm");
        
//        return http.build();


        return http.
                authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/style/**", "/", "/index.htm", "/login", "/signup").permitAll();
                    auth.requestMatchers("/secure/**").hasAnyRole("USER");
//                    auth.anyRequest().authenticated(); 
                    auth.requestMatchers("/admin/**").hasRole("ADMIN");
                }).
                formLogin(customizer -> { 
//                    customizer.loginPage("/login"); // default page generated
                    customizer.successHandler(new MyAuthenticationSuccessHandler());
                    LOG.info("I set the new success handler!");
                }).
                build();
    }
    
    @Bean 
    public UserDetailsService userDetailsService() {
        final UserDetails user = User.withUsername("user").password("hello").authorities("user").build();
        LOG.info("the fake user details service has been called!!!");
        return new InMemoryUserDetailsManager(user);
    }
    
}    
