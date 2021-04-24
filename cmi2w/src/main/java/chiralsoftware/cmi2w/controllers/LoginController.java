package chiralsoftware.cmi2w.controllers;

import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 */
@Controller
public class LoginController {
    private static final Logger LOG = Logger.getLogger(LoginController.class.getName());

    @GetMapping("/login")
    public String login() {
        LOG.info("Getting the LOGIN page");
        return "login";
    }
    
    @RequestMapping("/login-admin.htm")
    public String loginAdmin() {
        LOG.info("Showing admin login page");
        return "login-admin";
    }
    
    @RequestMapping("/login-error.htm")
    public String loginError(HttpSession session, Model model) {
      if(session == null) {
          LOG.warning("Session was null!");
          return "login";
      }
      
      if(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") == null) {
          LOG.warning("The SPRING_SECURITY_LAST_EXCEPTION attribute was not found!");
          model.addAttribute("loginErrorMessage", "Login error");
      } else {
          model.addAttribute("loginErrorMessage", 
                  ((Throwable) session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION")).getMessage());
      }
      model.addAttribute("loginError", true);
      return "login";
    }    
  
  @RequestMapping("/login-again.htm")
    public String loginAgain(Model model) {
      model.addAttribute("logoutSuccessful", true);
      LOG.info("User has logged off");
      return "login-again";
  }
    
}
