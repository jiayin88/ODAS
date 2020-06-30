package opticDiscAnalyser;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

//to scale an image into your desired output for this case it is 600 * 600
public class ImageScale {

    public static BufferedImage scale(BufferedImage src, int width, int height) throws IOException {
    	BufferedImage dest = new BufferedImage(width, height, src.getType());
    	Graphics2D g = dest.createGraphics();
    	AffineTransform at = AffineTransform.getScaleInstance(
            (double)width/src.getWidth(),
            (double)height/src.getHeight());
    	g.drawRenderedImage(src,at);        
    	return dest;
	}

}
