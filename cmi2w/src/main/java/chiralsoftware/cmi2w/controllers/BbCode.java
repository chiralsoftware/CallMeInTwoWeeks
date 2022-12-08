package chiralsoftware.cmi2w.controllers;

import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;
import org.kefirsf.bb.conf.Configuration;

/**
 * Holder for a BBCode processor. This should be moved in to a bean probably.
 * This is failing with native-image because:
 * Caused by: org.kefirsf.bb.TextProcessorFactoryException: Can't find or open default configuration resource.
* So do something to create a configuration instance so it doesn't need to search for it
 * 
 */
public final class BbCode {
    
    private BbCode() { }

    private static final TextProcessor processor;
    static {
//        final Configuration configuration = new Configuration();
//         processor = BBProcessorFactory.getInstance().create(configuration);
         processor = BBProcessorFactory.getInstance().create();
    }
    
    public static TextProcessor getProcessor() { return  processor; }
}
