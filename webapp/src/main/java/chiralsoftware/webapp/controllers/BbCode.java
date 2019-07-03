package chiralsoftware.webapp.controllers;

import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;
import org.kefirsf.bb.conf.Configuration;

/**
 * Holder for a BBCode processor. This should be moved in to a bean probably
 *
 * @author hh
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
