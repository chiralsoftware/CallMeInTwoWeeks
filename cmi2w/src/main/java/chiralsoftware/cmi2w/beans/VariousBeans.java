package chiralsoftware.cmi2w.beans;

import java.util.logging.Logger;
import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create some useful beans
 */
@Configuration
public class VariousBeans {

    private static final Logger LOG = Logger.getLogger(VariousBeans.class.getName());
    
    private static final TextProcessor processor;
    static {
//        final Configuration configuration = new Configuration();
//         processor = BBProcessorFactory.getInstance().create(configuration);
         processor = BBProcessorFactory.getInstance().create();
    }
    
    @Bean(name = "bbCode")
    public TextProcessor textProcessor() {
        LOG.info("Returning the bbCode processor bean!");
        return processor;
    }

}
