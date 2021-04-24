package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.daos.MyAuthToken;
import chiralsoftware.cmi2w.entities.Contact;
import chiralsoftware.cmi2w.entities.WebUser;
import java.io.IOException;
import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerResponse;
import org.springframework.beans.factory.annotation.Value;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Support click-to-dial links
 *
 */
@Controller
public class ClickToDial {

    private static final Logger LOG = Logger.getLogger(ClickToDial.class.getName());
    
    @Value("#{systemProperties['cmi2winternal.asteriskHost']}")
    private String asteriskHost;
    @Value("#{systemProperties['cmi2winternal.asteriskContext']}")
    private String asteriskContext;
    @Value("#{systemProperties['cmi2winternal.asteriskPassword']}")
    private String asteriskPassword;
    
    @PostConstruct
    public void createManagerFactory() {
        LOG.info("In createManagerFactory, the asteriskHost=" + asteriskHost + ", "
                + "asteriskContext=" + asteriskContext + ", "
                        + "asteriskPassword=" + (asteriskPassword == null ? "(null)" : asteriskPassword.length() + " chars"));
        LOG.info("Should be like: 192.168.14.222, dialer, seekrit");
        managerFactory = new ManagerConnectionFactory(asteriskHost, 
                    asteriskContext, asteriskPassword);
        LOG.info("Created this manager factory: " + managerFactory);
    }

private ManagerConnectionFactory managerFactory = null;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @param channel for example, PJSIP/ext103
     * @param context for example, staff
     * @param extension for example, 93109274613
     */
    private void dialAmi(String channel, String context, String extension) {
        final ManagerConnection managerConnection = managerFactory.createManagerConnection();

        final OriginateAction originateAction = new OriginateAction();
        originateAction.setContext(context);
        originateAction.setChannel(channel);
        originateAction.setExten(extension);
        originateAction.setPriority(1);
        originateAction.setCallerId("3103567869");
        originateAction.setAsync(true);
        LOG.info("Going to dial channel: " + channel + ", context: " + context + ", "
                + "extension: " + extension);
        try {
            LOG.info("About to log in");
            managerConnection.login();
            LOG.info("About to dial");
            final ManagerResponse response
                    = managerConnection.sendAction(originateAction);
            LOG.info("Ok it should have done something i hpoe!!!");
            LOG.info("here is the message: " + response.getMessage());
        } catch (AuthenticationFailedException | IOException | TimeoutException | IllegalArgumentException e) {
            LOG.log(WARNING, "Oh no! ", e);
        }
        managerConnection.logoff();
    }
    

    @PostMapping(value = "/secure/dial-{contactId}.htm")
    @ResponseBody
    @Transactional(readOnly = true)
    public DialResult dial(@PathVariable Long contactId) {
        if (entityManager == null)
            LOG.warning("The entityManager is null!");

        LOG.info("I'm ready to dial!  Contact to dial is: " + contactId);

        final WebUser webUser
                = ((MyAuthToken) getContext().getAuthentication()).getWebUser();
        if (webUser.getAsteriskContext() == null || webUser.getAsteriskContext().isEmpty()
                || webUser.getAsteriskExtension() == null || webUser.getAsteriskExtension().isEmpty()) {
            final DialResult dialResult = new DialResult();
            dialResult.result = "User not configured for dial-out";
            return dialResult;
        }

        LOG.info("The web user I want is: " + webUser);
        final Contact contact = entityManager.find(Contact.class, contactId);
        String dialExtension = webUser.getAsteriskDialPrefix() + contact.getPhone();
        dialExtension = dialExtension.replaceAll("[^0-9]+", "");
        dialAmi(webUser.getAsteriskExtension(), webUser.getAsteriskContext(), dialExtension);
//        final AsteriskDialout asteriskDialout = new AsteriskDialout(webUser.getAsteriskExtension(),
//                webUser.getAsteriskContext(),
//                dialExtension);
//        asteriskDialout.call();
        final DialResult dialResult = new DialResult();
        dialResult.result = "Dialed: " + contact.getPhone() + " at: " + new java.util.Date();
        dialResult.dialedNumber = contact.getPhone();
        return dialResult;
    }

    public static final class DialResult {

        private DialResult() {
        }

        private final String dialTime = new java.util.Date().toString();
        private String dialedNumber;
        private String result;

        public String getDialTime() {
            return dialTime;
        }

        public String getDialedNumber() {
            return dialedNumber;
        }

        public String getResult() {
            return result;
        }

    }
}
