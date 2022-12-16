package chiralsoftware.cmi2w.controllers;

import chiralsoftware.cmi2w.entities.TemporaryImage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handle image file uploads.
 * 
 */
@Controller
public class FileUploadController {

    private static final Logger LOG = Logger.getLogger(FileUploadController.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping(value = "/secure/upload")
    @ResponseBody
    @Transactional
    public String imageUpload(HttpServletRequest request, HttpServletResponse response) {
        try {
            final Collection<Part> parts = request.getParts();
            LOG.info("I have this many parts: " + parts.size());
            LOG.info("The parts are: " + parts);
            for(Part p : parts) {
                LOG.fine("Part: " + p.getContentType() + " / " + p.getName() + " / " + p.getSize());
                LOG.info("fixme: we should check the content type before trying to create a temporary image");
                final TemporaryImage temporaryImage = new TemporaryImage();
                temporaryImage.setName(p.getName());
                temporaryImage.setType(IMAGE_PNG_VALUE);
                temporaryImage.setContent(ImageUtilities.makeIcon(p.getInputStream()));
                entityManager.persist(temporaryImage);
                return temporaryImage.getId().toString();
            }
        } catch (IOException | ServletException e) {
            LOG.log(INFO, "Caught an exception: ", e);
            return "upload failed " + abbreviate(e.getMessage(), 40);
        }
        return "upload failed - no image was found";
    }

    @GetMapping(value="/secure/temporary-image-{id}")
    @Transactional(readOnly = true)
    // this annotation is needed or else I get:
    // PSQLException: Large Objects may not be used in auto-commit mode.
    public ResponseEntity<byte[]> showTemporaryImage(@PathVariable Long id) {
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-type", IMAGE_PNG_VALUE);
        return new ResponseEntity<>(entityManager.find(TemporaryImage.class, id).getContent(),
                responseHeaders, OK);
    }
        
}
