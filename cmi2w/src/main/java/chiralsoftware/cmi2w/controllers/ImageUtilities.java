package chiralsoftware.cmi2w.controllers;

import java.awt.AlphaComposite;
import static java.awt.AlphaComposite.CLEAR;
import static java.awt.AlphaComposite.SRC_OVER;
import java.awt.Graphics2D;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static javax.imageio.ImageIO.write;

/**
 * Some helpful utilities for processing uploaded image
 * 
 */
public final class ImageUtilities {
    private static final Logger LOG = Logger.getLogger(ImageUtilities.class.getName());
    
    private ImageUtilities() {
        throw new RuntimeException("Don't instantiate this");
    }
    
    private static final int WIDTH = 256;
    private static final int HEIGHT = 256;
    
    public static byte[] makeIcon(InputStream is) throws IOException {
        final BufferedImage bufferedImage = ImageIO.read(is);
        final BufferedImage result = new BufferedImage(WIDTH, HEIGHT, TYPE_INT_ARGB);
        final Graphics2D g = result.createGraphics();
        g.setComposite(AlphaComposite.getInstance(CLEAR));
        g.fillRect(0,0,result.getWidth(), result.getHeight());
        g.setComposite(AlphaComposite.getInstance(SRC_OVER));
        
        // find out if we need to scale things
        // we will scale small images UP if we need to
        final float xScale = (float) bufferedImage.getWidth() / WIDTH;
        final float yScale = (float) bufferedImage.getHeight() / HEIGHT;
        LOG.info("The source image width / height: " + bufferedImage.getWidth() + " / " + bufferedImage.getHeight());
        
        final float scale = xScale > yScale ? xScale : yScale;
        
        g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);

        final int newWidth = Math.round(bufferedImage.getWidth() / scale);
        final int newHeight = Math.round(bufferedImage.getHeight() / scale);
        final int newXPos = (result.getWidth() - newWidth) / 2;
        final int newYPos = (result.getHeight() - newHeight) / 2;

        g.drawImage(bufferedImage, newXPos, newYPos, newWidth, newHeight, null);
        g.dispose();
        
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        write(result, "PNG", baos);
        baos.close();
        return baos.toByteArray();
    }
    
}
