package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.entities.Contact;
import chiralsoftware.cmi2w.entities.WebUser;
import chiralsoftware.cmi2w.security.JpaUserDetails;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Support click-to-dial links
 *
 */
@Controller
public class ClickToDial {

    private static final Logger LOG = Logger.getLogger(ClickToDial.class.getName());

    @Value("${asteriskHost:#{null}}")
    private String asteriskHost;
    
    /** This context is referring not to the dialplan context, but to the context within
     the manager.conf file */
    @Value("${asteriskContext:#{null}}")
    private String asteriskContext;
    
    /** This refers to the value of secret within manager.conf */
    @Value("${asteriskPassword:#{null}}")
    private String asteriskPassword;
    
    @Value("${asteriskPort:5038}")
    private int asteriskPort;
    
    @Value("${asteriskCallerId:cmi2w}")
    private String asteriskCallerId;

    @PostConstruct
    public void createManagerFactory() {
        LOG.info("In createManagerFactory, the asteriskHost=" + asteriskHost + ", "
                + "asteriskContext=" + asteriskContext + ", "
                + "asteriskPassword=" + (asteriskPassword == null ? "(null)" : asteriskPassword.length() + " chars"));
        LOG.info("Should be like: 192.168.14.222, dialer, seekrit");
        if (isAnyBlank(asteriskHost, asteriskContext, asteriskPassword))
            managerFactory = null;
        else
            managerFactory = new ManagerConnectionFactory(asteriskHost,asteriskPort,
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
        originateAction.setCallerId(asteriskCallerId);
        originateAction.setAsync(true);
//        LOG.info("Going to dial channel: " + channel + ", context: " + context + ", "
//                + "extension: " + extension);
        try {
            LOG.fine("About to log in");
            managerConnection.login();
            LOG.fine("About to dial");
            final ManagerResponse response
                    = managerConnection.sendAction(originateAction);
            LOG.fine("Ok it should have done something i hpoe!!!");
            LOG.fine("Here is the response: " + response.getResponse());
            LOG.fine("here is the message: " + response.getMessage());
        } catch (AuthenticationFailedException | IOException | TimeoutException | IllegalArgumentException e) {
            LOG.log(WARNING, "Oh no! ", e);
        }
        managerConnection.logoff();
    }

    @PostMapping("/secure/dial-{contactId}.htm")
    @ResponseBody
    @Transactional(readOnly = true)
    public DialResult dial(@AuthenticationPrincipal JpaUserDetails userDetails,
            @PathVariable Long contactId, @RequestParam(required = false, defaultValue = "main") String target) {
        if(managerFactory == null) 
            return new DialResult("999999", "dial out is not configured. See logs.", new java.util.Date().toString());

        final WebUser webUser = userDetails.getWebUser();
        if (webUser.getAsteriskContext() == null || webUser.getAsteriskContext().isEmpty()
                || webUser.getAsteriskExtension() == null || webUser.getAsteriskExtension().isEmpty()) {
            return new DialResult("999999", "User not configured for dial-out", new java.util.Date().toString());
        }

        LOG.info("The web user I want is: " + webUser);
        final Contact contact = entityManager.find(Contact.class, contactId);
        final String phoneNumber = switch(target) {
            case "main" -> contact.getPhone();
            case "mobile" -> contact.getMobile();
            default -> contact.getPhone();
        };
        if(isBlank(phoneNumber)) {
            LOG.info("Couldn't find any phone number to dial.");
            return new DialResult("999999", "there was no phone number to dial!", new java.util.Date().toString());
        }
        
        String dialExtension = webUser.getAsteriskDialPrefix() + phoneNumber;
        dialExtension = dialExtension.replaceAll("[^0-9]+", "");
//        LOG.info("Channel: " + webUser.getAsteriskExtension());
//        LOG.info("Context: " + webUser.getAsteriskContext());
//        LOG.info("Extension: " + dialExtension);
        dialAmi(webUser.getAsteriskExtension(), webUser.getAsteriskContext(), dialExtension);
//        final AsteriskDialout asteriskDialout = new AsteriskDialout(webUser.getAsteriskExtension(),
//                webUser.getAsteriskContext(),
//                dialExtension);
//        asteriskDialout.call();
        return new DialResult(phoneNumber,
                "Dialed: " + phoneNumber + " at: " + new java.util.Date(), 
        new java.util.Date().toString());
    }

    public static record DialResult(String dialedNumber, String result, String dialTime) {
        
    }
}
