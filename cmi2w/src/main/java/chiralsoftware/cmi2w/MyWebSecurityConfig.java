package chiralsoftware.cmi2w;

import chiralsoftware.cmi2w.security.MyAuthenticationSuccessHandler;
import chiralsoftware.cmi2w.security.MyUserDetailsService;
import jakarta.annotation.PostConstruct;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configure security on this
 */
@Configuration
@EnableWebSecurity
public class MyWebSecurityConfig {

    private static final Logger LOG = Logger.getLogger(MyWebSecurityConfig.class.getName());

    public MyWebSecurityConfig(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    private final MyUserDetailsService myUserDetailsService;

    @Value("${rememberMeKey:changeme}")
    private String rememberMeKey;

    @PostConstruct
    public void postConstruct() {
        if (rememberMeKey.equals("changeme")) {
            LOG.warning("The rememberMeKey value is set to the default value. This is unsafe in a production system.");
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

// for info on redirecting depending on the user role:
// https://www.baeldung.com/spring-redirect-after-login
// see this about new security config, plus a really great tour
// of how to create a user details service:
// https://www.youtube.com/watch?v=awcCiqBO36E
        http.
                csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")).
                authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/style/**", "/", 
                            "/index.htm", "/logout",
                            "/login", "/signup", "/h2-console/**").permitAll();
                    auth.requestMatchers("/secure/**").
                            hasRole("USER");
                    auth.requestMatchers("/admin/**").hasRole("ADMIN");
                }).
                formLogin(customizer -> {
                    customizer.successHandler(new MyAuthenticationSuccessHandler());
                    LOG.info("I set the new success handler!");
                }).
                userDetailsService(myUserDetailsService).
                headers(headers -> headers.frameOptions().sameOrigin()); // this is also needed for the h2 console
        if(! rememberMeKey.equals("changeme"))
                http.rememberMe(rememberMeConfig -> {
                    rememberMeConfig.key(rememberMeKey);
                });

        return http.build();
    }

    /**
     * We are using a no-op password encoder to be compatible with existing
     * tables. This is not advisable in larger deployments.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
