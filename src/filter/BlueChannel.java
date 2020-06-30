package filter;

import java.awt.image.*;



/*this is to get the blue channel of the image*/

public class BlueChannel {
	public static BufferedImage blue(BufferedImage img){
		
		BufferedImage blue = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
		 /* Loop through all the pixels */   
        for (int x=0; x < img.getWidth(); x++){          
            for (int y = 0; y < img.getHeight(); y++){  
	                /* Apply the blue mask 
	                 * shifting right 4 bits
	                 * 0xff0000ff = 0000BB, that is why it only gets the blue color
	                 */                      
            	 blue.setRGB(x, y, img.getRGB(x, y) & 0xff0000ff);
            }
        }
	return blue;
	}
}
