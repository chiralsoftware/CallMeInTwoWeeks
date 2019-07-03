package chiralsoftware.webapp.controllers;

import entities.TemporaryImage;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Testing file uploads
 *
 * @author hh
 */
@Controller
public class FileUploadController {

    private static final Logger LOG = Logger.getLogger(FileUploadController.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(value = "/upload.htm", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String testUpload(HttpServletRequest request,
            HttpServletResponse response) {
        if(request.getSession().getServletContext().getMajorVersion() < 3) {
            LOG.info("Warning! The servlet API version is less than 3 and so this won't work!");
            return null;
        }

        try {
            final Collection<Part> parts = request.getParts();
            LOG.info("I have this many parts: " + parts.size());
            LOG.info("The parts are: " + parts);
            for(Part p : parts) {
                LOG.fine("Part: " + p.getContentType() + " / " + p.getName() + " / " + p.getSize());
                final TemporaryImage temporaryImage = new TemporaryImage();
                temporaryImage.setName(p.getName());
                temporaryImage.setType("image/png");
                temporaryImage.setContent(ImageUtilities.makeIcon(p.getInputStream()));
                entityManager.persist(temporaryImage);
                return temporaryImage.getId().toString();
            }
        } catch (IOException | ServletException e) {
            LOG.log(Level.INFO, "Caught an exception: ", e);
        }
        return "hellothere";
    }

    /** This is a test that shows JPEGs only */
    @RequestMapping(value="/temporary-jpeg-image-{id}.htm", 
            produces = MediaType.IMAGE_JPEG_VALUE, method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    // this annotation is needed or else I get:
    // PSQLException: Large Objects may not be used in auto-commit mode.
    public byte[] showTemporaryJpegImage(@PathVariable Long id) {
        return entityManager.find(TemporaryImage.class, id).getContent();
    }
    
    /** This is a test that shows any image type */
    @RequestMapping(value="/temporary-image-{id}.htm", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> showTemporaryImage(@PathVariable Long id) {
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-type", "image/png");
        return new ResponseEntity<>(entityManager.find(TemporaryImage.class, id).getContent(),
                responseHeaders, HttpStatus.OK);
    }
    
    @RequestMapping(value="/upload-save.htm", method= RequestMethod.POST) 
    @ResponseBody
    @Transactional
    public String saveImage(@RequestParam Long tempImageId) {
        LOG.info("Yeehaw!  I should be saving this image: " + tempImageId);
        return "ok";
    }
    
}
