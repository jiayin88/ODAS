package filter;

import java.awt.image.*;


/*this is to get the green channel of the image*/

public class GreenChannel {
	public static BufferedImage green(BufferedImage img){
		
		BufferedImage green = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
		 /* Loop through all the pixels */   
        for (int x=0; x < img.getWidth(); x++){          
            for (int y = 0; y < img.getHeight(); y++){  
	                /* Apply the green mask 
	                 * shifting right 4 bits
	                 * 0xff00ff00 = 00GG00, that is why only get the green color
	                 */                      
            	 green.setRGB(x, y, img.getRGB(x, y) & 0xff00ff00);
            }
        }
	return green;
	}
	
}