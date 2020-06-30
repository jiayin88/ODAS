package filter;

import java.awt.image.*;


/*this is to get the red channel of the image*/
//note: color code is RRGGBBAA

public class RedChannel {

	public static BufferedImage red(BufferedImage img){
		
			BufferedImage red = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
			 /* Loop through all the pixels */   
	        for (int x=0; x < img.getWidth(); x++){          
	            for (int y = 0; y < img.getHeight(); y++){   
	                /* Apply the red mask 
	                 * shifting right 4 bits
	                 * 0xffff0000 = RR0000 and that is why get the red color
	                 */                      
	                red.setRGB(x, y, img.getRGB(x, y) & 0xffff0000);
	            }
	        }
		return red;
	}
}
