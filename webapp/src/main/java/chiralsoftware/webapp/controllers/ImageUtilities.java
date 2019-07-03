package chiralsoftware.webapp.controllers;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Some helpful utilities for processing uploaded image
 * 
 * @author hh
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
        final BufferedImage result = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = result.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        g.fillRect(0,0,result.getWidth(), result.getHeight());
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        
        // find out if we need to scale things
        // we will scale small images UP if we need to
        final float xScale = (float) bufferedImage.getWidth() / WIDTH;
        final float yScale = (float) bufferedImage.getHeight() / HEIGHT;
        LOG.info("The source image width / height: " + bufferedImage.getWidth() + " / " + bufferedImage.getHeight());
        LOG.info("The xScale / yScale is: " + xScale + " / " + yScale);
        
        final float scale = xScale > yScale ? xScale : yScale;
        LOG.info("And so I will use this scale: " + scale);
        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        final int newWidth = Math.round(bufferedImage.getWidth() / scale);
        final int newHeight = Math.round(bufferedImage.getHeight() / scale);
        final int newXPos = (result.getWidth() - newWidth) / 2;
        final int newYPos = (result.getHeight() - newHeight) / 2;
//        LOG.info("The new width: " + newWidth);
//        LOG.info("The new height: " + newHeight);
//        LOG.info("The new xPos: " + newXPos);
//        LOG.info("The new yPos: " + newYPos);
//        LOG.info("Now I am drawing");
        g.drawImage(bufferedImage, newXPos, newYPos, newWidth, newHeight, null);
        g.dispose();
        
//        ImageIO.write(result, "PNG", new File("/tmp/test-output.png"));
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(result, "PNG", baos);
        baos.close();
        return baos.toByteArray();
    }
    
}
