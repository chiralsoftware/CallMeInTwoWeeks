package chiralsoftware.cmi2w;

import chiralsoftware.cmi2w.security.MyAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Configure security on this
 */
@Configuration
@EnableWebSecurity
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/", "/home", "/index.html", "/login", "/signup.htm", 
                            "/style/**"
                    ).permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/secure/**").hasRole("USER").
                    anyRequest().anonymous()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .successHandler(new MyAuthenticationSuccessHandler())
                    .and()
                .logout()
                    .permitAll();
    }
    
    
}    
