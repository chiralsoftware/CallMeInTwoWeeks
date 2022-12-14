package chiralsoftware.cmi2w;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * To have a deployable WAR file, there must be a SpringBootServletInitializer
 * subclass. See:
 * https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.traditional-deployment
 */
@SpringBootApplication
public class Cmi2wApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Cmi2wApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Cmi2wApplication.class);
    }

}
