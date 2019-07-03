package chiralsoftware.webapp.controllers;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Hold user signup data
 * 
 * @author hh
 */
public class SignupData {
    
    @NotBlank
    @Length(min=2,max=32)
    private String username;
    
    @NotNull
    @Email
    private String email;
    
    @NotBlank
    @Length(min=2,max=32)
    private String password1;
    @NotBlank
    @Length(min=2,max=32)
    private String password2;
    
    public boolean passwordsMatch() {
        if(password1 == null || password2 == null) return false;
        return password1.equalsIgnoreCase(password2);
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password1
     */
    public String getPassword1() {
        return password1;
    }

    /**
     * @param password1 the password1 to set
     */
    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    /**
     * @return the password2
     */
    public String getPassword2() {
        return password2;
    }

    /**
     * @param password2 the password2 to set
     */
    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    @Override
    public String toString() {
        return "SignupData{" + "username=" + username + ", email=" + email + ", "
                + "password1=" + password1 + ", password2=" + password2 + '}';
    }
    
    
}
