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

// for info on redirecting depending on the user role:
// https://www.baeldung.com/spring-redirect-after-login

// see this about new security config, plus a really great tour
// of how to create a user details service:
// https://www.youtube.com/watch?v=awcCiqBO36E
// todo: implement all this like it's shown in the video
        return http.
                csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")).
                authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/style/**", "/", "/index.htm", "/login", "/signup", "/h2-console/**").permitAll();
                    auth.requestMatchers("/secure/**").hasAnyRole("USER");
//                    auth.anyRequest().authenticated(); 
                    auth.requestMatchers("/admin/**").hasRole("ADMIN");
                }).
                formLogin(customizer -> { 
//                    customizer.loginPage("/login"); // default page generated
                    customizer.successHandler(new MyAuthenticationSuccessHandler());
                    LOG.info("I set the new success handler!");
                }).
                headers(headers -> headers.frameOptions().sameOrigin()). // this is also needed for the h2 console
                build();
    }
    
}    
