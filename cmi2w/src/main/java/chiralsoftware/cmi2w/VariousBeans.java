package chiralsoftware.cmi2w;

import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create some useful beans
 */
@Configuration
public class VariousBeans {

    private static final TextProcessor processor;
    static {
//        final Configuration configuration = new Configuration();
//         processor = BBProcessorFactory.getInstance().create(configuration);
         processor = BBProcessorFactory.getInstance().create();
    }
    
    @Bean(name = "bbCode")
    public TextProcessor getTextProcessor() {
        return processor;
    }

}
