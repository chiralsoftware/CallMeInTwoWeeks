package chiralsoftware.cmi2w.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * System user entity
 * 
 * @author hh
 */
@Entity
public class WebUser implements Serializable {
    	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column
        @NotNull
        @Size(min=3,max=32)
	private String name;

	@Column
        @NotNull
        @Email
	private String email;

        @Column
        @NotNull
        private String password;
        
        @Column
        private String asteriskExtension;

        @Column
        private String asteriskContext;
        @Column
        private String asteriskDialPrefix;
        
        /** Return true if this user has dialout configured
     * @return  this user has dialout configured */
        public boolean isDialoutActive() {
            return asteriskContext != null && (! asteriskContext.isEmpty()) &&
                    asteriskExtension != null && (! asteriskExtension.isEmpty());
        }
        
        @Column
        private String timeZone = "America/Los_Angeles";

        /** In production, this should be false and there should be an admin to activate it */
        @Column
        private boolean active = true;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }


    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the asteriskExtension
     */
    public String getAsteriskExtension() {
        return asteriskExtension;
    }

    /**
     * @param asteriskExtension the asteriskExtension to set
     */
    public void setAsteriskExtension(String asteriskExtension) {
        this.asteriskExtension = asteriskExtension;
    }

    /**
     * @return the asteriskContext
     */
    public String getAsteriskContext() {
        return asteriskContext;
    }

    /**
     * @param asteriskContext the asteriskContext to set
     */
    public void setAsteriskContext(String asteriskContext) {
        this.asteriskContext = asteriskContext;
    }

    /**
     * @return the asteriskDialPrefix
     */
    public String getAsteriskDialPrefix() {
        return asteriskDialPrefix;
    }

    /**
     * @param asteriskDialPrefix the asteriskDialPrefix to set
     */
    public void setAsteriskDialPrefix(String asteriskDialPrefix) {
        this.asteriskDialPrefix = asteriskDialPrefix;
    }

    /**
     * @return the timeZone
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * @param timeZone the timeZone to set
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.email);
        hash = 67 * hash + Objects.hashCode(this.password);
        hash = 67 * hash + Objects.hashCode(this.asteriskExtension);
        hash = 67 * hash + Objects.hashCode(this.asteriskContext);
        hash = 67 * hash + Objects.hashCode(this.asteriskDialPrefix);
        hash = 67 * hash + Objects.hashCode(this.timeZone);
        hash = 67 * hash + (this.active ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final WebUser other = (WebUser) obj;
        if (!Objects.equals(this.name, other.name))
            return false;
        if (!Objects.equals(this.email, other.email))
            return false;
        if (!Objects.equals(this.password, other.password))
            return false;
        if (!Objects.equals(this.asteriskExtension, other.asteriskExtension))
            return false;
        if (!Objects.equals(this.asteriskContext, other.asteriskContext))
            return false;
        if (!Objects.equals(this.asteriskDialPrefix, other.asteriskDialPrefix))
            return false;
        if (!Objects.equals(this.timeZone, other.timeZone))
            return false;
        if (this.active != other.active)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WebUser{" + "id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", asteriskExtension=" + asteriskExtension + ", asteriskContext=" + asteriskContext + ", asteriskDialPrefix=" + asteriskDialPrefix + ", timeZone=" + timeZone + ", active=" + active + '}';
    }

}
