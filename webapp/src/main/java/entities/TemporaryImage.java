package entities;

import java.io.IOException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.servlet.http.Part;

/**
 * Holds a temporary image upload,
 * to allow the user to preview it
 * 
 * @author hh
 */
@Entity
public class TemporaryImage {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** No-args constructor, needed for JPA */
    public TemporaryImage() { }
    
    /** Quickly create a TemporaryImage from an uploaded Part */
    public TemporaryImage(Part p) throws IOException {
        type = p.getContentType();
        name = p.getName();
        fileSize = p.getSize();
        content = new byte[(int) fileSize];
        if(p.getInputStream().read(content) != fileSize) 
            throw new IOException("Failed to read " + fileSize + " bytes "
                    + "for file: " + name);
    }
    
    /** Link to the user ID this is associated with */
    @Column
    private Long userId;
    
    @Column(length=1000000)
    @Lob
    private byte[] content;
    
    @Column 
    private String type;
    
    @Column
    private String name;
    
    @Column
    private long uploadTime = System.currentTimeMillis();
    
    @Column
    private long fileSize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        if(content != null) setFileSize(content.length);
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    
    
}
