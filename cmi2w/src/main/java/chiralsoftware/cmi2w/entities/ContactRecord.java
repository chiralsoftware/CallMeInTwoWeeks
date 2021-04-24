package chiralsoftware.cmi2w.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Record of an interaction with a contact
 * 
 */
@Entity
public class ContactRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Link to the Contact this is associated with */
    @Column
    private Long contactId;
    
    /** Link to the user who made this record, not necessarily
     * the same as the user who created the contact
     */
    @Column
    private Long userId;
    
    /** Time when the contact happened */
    @Column
    private long contactTime;
    @Column
    private long nextContactTime;
    
    @Column
    private String notes;
    
    @Column
    private boolean dialed;
    
    @Column
    private boolean voicemail;

    @Column
    private boolean leftmessage = false;
    
    public Map<String,Object> addToMap(Map<String,Object> m) {
        if(m == null) m = new HashMap<>();
        m.put("contactRecordId", id);
        m.put("lastContactTime", contactTime);
        m.put("nextContactTime", nextContactTime);
        if(isBlank(notes)) {
            if(dialed && voicemail && leftmessage) {
                m.put("notes", "called, went to voicemail, left message");
            } else if(dialed && voicemail) {
                m.put("notes", "called, went to voicemail, didn't leave message");
            } else {
                m.put("notes", "called");
            }
        } else {
            m.put("notes", notes);
        }
        m.put("dialed", dialed);
        m.put("voicemail", voicemail);
        return m;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public long getContactTime() {
        return contactTime;
    }

    public void setContactTime(long contactTime) {
        this.contactTime = contactTime;
    }

    public long getNextContactTime() {
        return nextContactTime;
    }

    public void setNextContactTime(long nextContactTime) {
        this.nextContactTime = nextContactTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isDialed() {
        return dialed;
    }

    public void setDialed(boolean dialed) {
        this.dialed = dialed;
    }

    public boolean isVoicemail() {
        return voicemail;
    }

    /** Use this to indicate if the call went to voicemail */
    public void setVoicemail(boolean voicemail) {
        this.voicemail = voicemail;
    }

    public boolean isLeftmessage() {
        return leftmessage;
    }

    public void setLeftmessage(boolean leftmessage) {
        this.leftmessage = leftmessage;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.id);
        hash = 13 * hash + Objects.hashCode(this.contactId);
        hash = 13 * hash + Objects.hashCode(this.userId);
        hash = 13 * hash + (int) (this.contactTime ^ (this.contactTime >>> 32));
        hash = 13 * hash + (int) (this.nextContactTime ^ (this.nextContactTime >>> 32));
        hash = 13 * hash + Objects.hashCode(this.notes);
        hash = 13 * hash + (this.dialed ? 1 : 0);
        hash = 13 * hash + (this.voicemail ? 1 : 0);
        hash = 13 * hash + (this.leftmessage ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ContactRecord other = (ContactRecord) obj;
        if (this.contactTime != other.contactTime)
            return false;
        if (this.nextContactTime != other.nextContactTime)
            return false;
        if (this.dialed != other.dialed)
            return false;
        if (this.voicemail != other.voicemail)
            return false;
        if (this.leftmessage != other.leftmessage)
            return false;
        if (!Objects.equals(this.notes, other.notes))
            return false;
        if (!Objects.equals(this.id, other.id))
            return false;
        if (!Objects.equals(this.contactId, other.contactId))
            return false;
        if (!Objects.equals(this.userId, other.userId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ContactRecord{" + "id=" + id + ", contactId=" + 
                contactId + ", userId=" + userId + ", contactTime=" + 
                contactTime + ", nextContactTime=" + nextContactTime + ", notes=" + 
                notes + ", dialed=" + dialed + ", voicemail=" + voicemail + 
                ", leftmessage=" + leftmessage + '}';
    }
    
}
