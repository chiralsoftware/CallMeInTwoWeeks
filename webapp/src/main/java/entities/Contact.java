package entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

@Entity
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    @NotNull
    private String name;
    
    @Column
    private String organization;
    
    @Column
    private String phone;
    
    @Column
    private String email;
    
    @Column(length=100000)
    private String notes;
    
    @Column
    private String addressLine1;
    @Column
    private String addressLine2;
    @Column
    private String addressCity;
    @Column
    private String addressProvince;
    @Column
    private String addressPostalCode;
    @Column
    private String addressCountry = "US";
    
    @Column 
    // FIXME the userId should be required
//    @NotNull
    private Long userId;
    
    @Column(length=1000000)
    @Lob
    private byte[] icon;
    
    @Column
    private String category;

    public Map<String,Object> addValuesToMap(Map<String,Object> m) {
        if(m == null) m = new HashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("organization", organization);
        m.put("phone", phone);
        m.put("email", email);
        m.put("notes", notes);
        m.put("addressLine1", addressLine1);
        m.put("addressLine2", addressLine2);
        m.put("addressCity", addressCity);
        m.put("addressProvince", addressProvince);
        m.put("addressPostalCode", addressPostalCode);
        m.put("addressCountry", addressCountry);
        m.put("icon", icon);
        m.put("category", category);
        return m;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the organization
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return the addressLine1
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * @param addressLine1 the addressLine1 to set
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * @return the addressLine2
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * @param addressLine2 the addressLine2 to set
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * @return the addressCity
     */
    public String getAddressCity() {
        return addressCity;
    }

    /**
     * @param addressCity the addressCity to set
     */
    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    /**
     * @return the addressProvince
     */
    public String getAddressProvince() {
        return addressProvince;
    }

    /**
     * @param addressProvince the addressProvince to set
     */
    public void setAddressProvince(String addressProvince) {
        this.addressProvince = addressProvince;
    }

    /**
     * @return the addressPostalCode
     */
    public String getAddressPostalCode() {
        return addressPostalCode;
    }

    /**
     * @param addressPostalCode the addressPostalCode to set
     */
    public void setAddressPostalCode(String addressPostalCode) {
        this.addressPostalCode = addressPostalCode;
    }

    /**
     * @return the addressCountry
     */
    public String getAddressCountry() {
        return addressCountry;
    }

    /**
     * @param addressCountry the addressCountry to set
     */
    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the icon
     */
    public byte[] getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.name);
        hash = 17 * hash + Objects.hashCode(this.organization);
        hash = 17 * hash + Objects.hashCode(this.phone);
        hash = 17 * hash + Objects.hashCode(this.email);
        hash = 17 * hash + Objects.hashCode(this.notes);
        hash = 17 * hash + Objects.hashCode(this.addressLine1);
        hash = 17 * hash + Objects.hashCode(this.addressLine2);
        hash = 17 * hash + Objects.hashCode(this.addressCity);
        hash = 17 * hash + Objects.hashCode(this.addressProvince);
        hash = 17 * hash + Objects.hashCode(this.addressPostalCode);
        hash = 17 * hash + Objects.hashCode(this.addressCountry);
        hash = 17 * hash + Objects.hashCode(this.userId);
        hash = 17 * hash + Arrays.hashCode(this.icon);
        hash = 17 * hash + Objects.hashCode(this.category);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Contact other = (Contact) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.organization, other.organization)) {
            return false;
        }
        if (!Objects.equals(this.phone, other.phone)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.notes, other.notes)) {
            return false;
        }
        if (!Objects.equals(this.addressLine1, other.addressLine1)) {
            return false;
        }
        if (!Objects.equals(this.addressLine2, other.addressLine2)) {
            return false;
        }
        if (!Objects.equals(this.addressCity, other.addressCity)) {
            return false;
        }
        if (!Objects.equals(this.addressProvince, other.addressProvince)) {
            return false;
        }
        if (!Objects.equals(this.addressPostalCode, other.addressPostalCode)) {
            return false;
        }
        if (!Objects.equals(this.addressCountry, other.addressCountry)) {
            return false;
        }
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        if (!Arrays.equals(this.icon, other.icon)) {
            return false;
        }
        if (!Objects.equals(this.category, other.category)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Contact{" + "id=" + id + ", name=" + name + ", organization=" + organization + ", phone=" + phone + ", email=" + email + ", notes=" + notes + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", addressCity=" + addressCity + ", addressProvince=" + addressProvince + ", addressPostalCode=" + addressPostalCode + ", addressCountry=" + addressCountry + ", userId=" + userId + ", icon=" + icon + ", category=" + category + '}';
    }
    

}
