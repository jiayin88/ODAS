package filter;

import java.awt.*;
import java.awt.image.*;

/*color filter of gray scale which are red, green and blue
 * gray scale using desaturation method
 *Retrieved from http://developer.bostjan-cigan.com/java-color-image-to-grayscale-conversion-algorithm/ 
 */

public class Grayscale {
	// The "pick the color" method
	public static BufferedImage rgb(BufferedImage img, int color) {
	 
	    int alpha, red, green, blue;
	    int newPixel;
	 
	    int[] pixel = new int[3];
	 
	    BufferedImage rgb = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
	    
	    int width = img.getWidth();
	    int height = img.getHeight();
	    for(int x=0; x<width; x++) {
	        for(int y=0; y<height; y++) {
	 
	            // Get pixels by R, G, B
	            alpha = new Color(img.getRGB(x, y)).getAlpha();
	            red = new Color(img.getRGB(x, y)).getRed();
	            green = new Color(img.getRGB(x, y)).getGreen();
	            blue = new Color(img.getRGB(x, y)).getBlue();
	 
	            pixel[0] = red;
	            pixel[1] = green;
	            pixel[2] = blue;
	 
	            int newval = pixel[color];
	 
	            // Return back to original format
	            newPixel = colorToRGB(alpha, newval, newval, newval);
	 
	            // Write pixels into image
	            rgb.setRGB(x, y, newPixel);
	 
	        }
	 
	    }
	 
	    return rgb;        
	 
	}
	
	 // Convert R, G, B, Alpha to standard 8 bit
   public static int colorToRGB(int alpha, int red, int green, int blue) {
 
        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red; newPixel = newPixel << 8;
        newPixel += green; newPixel = newPixel << 8;
        newPixel += blue;
 
        return newPixel;
 
    } 
   
   // The desaturation method
   //newPixel = (min(R, G, B) + max(R, G, B))/2
   
   public static BufferedImage desaturation(BufferedImage img) {
 
	   int alpha, red, green, blue;
	   int newPixel;
 
	   int[] pixel = new int[3];
	   
	   int width = img.getWidth();
	   int height = img.getHeight();
	   
	   BufferedImage des = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
	   int[] desLUT = new int[511];
	   for(int i=0; i<desLUT.length; i++) desLUT[i] = (int) (i / 2);
 
	   for(int x=0; x<width; x++) {
		   for(int y=0; y<height; y++) {
 
			   // Get pixels by R, G, B
			   alpha = new Color(img.getRGB(x, y)).getAlpha();
			   red = new Color(img.getRGB(x, y)).getRed();
			   green = new Color(img.getRGB(x, y)).getGreen();
			   blue = new Color(img.getRGB(x, y)).getBlue();
 
			   pixel[0] = red;
			   pixel[1] = green;
			   pixel[2] = blue;
 
			   int newval = (int) (findMax(pixel) + findMin(pixel));
			   newval = desLUT[newval];
 
			   	// Return back to original format
			   newPixel = colorToRGB(alpha, newval, newval, newval);
 
			   // Write pixels into image
			   des.setRGB(x, y, newPixel);
 
		   }
	   }
 
	   return des;
 
   }    
 
   public static int findMin(int[] pixel) {
 
	   int min = pixel[0];
 
	   for(int i=0; i<pixel.length; i++) {
		   if(pixel[i] < min)
                	min = pixel[i];
	   }
 
	   return min;
 
   }
 
   public static int findMax(int[] pixel) {
 
	   int max = pixel[0];
 
	   for(int i=0; i<pixel.length; i++) {
		   if(pixel[i] > max)
                max = pixel[i];
	   }
 
	   return max;
 
   }
   
   
   //averaging method
   
   public static BufferedImage average(BufferedImage img){
	   
	   
	   BufferedImage avg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
	   
	   int width = img.getWidth();
		int height = img.getHeight();
		
		
		for(int y=0; y< height; y++){
			for(int x=0; x < width; x++){
				int p = img.getRGB(x, y);
				
				int a = (p>>24)&0xff;
				int r = (p>>16)&0xff;
				int g = (p>>8)&0xff;
				int b = p&0xff;
				
				// calculate average
				int avg1 = (r+g+b)/3;
				
				//replace RGB value with average
				p = (a<<24) | (avg1<<16) | (avg1<<8) | avg1;
				
				avg.setRGB(x, y, p);
			}
		}
		
		return avg;
   }
   
// The maximum decomposition method
   public static BufferedImage decompMax(BufferedImage original) {
 
	   int alpha, red, green, blue;
	   int newPixel;
 
	   int[] pixel = new int[3];
 
	   BufferedImage decomp = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
 
	   for(int i=0; i<original.getWidth(); i++) {
		   for(int j=0; j<original.getHeight(); j++) {
 
			   // Get pixels by R, G, B
			   alpha = new Color(original.getRGB(i, j)).getAlpha();
			   red = new Color(original.getRGB(i, j)).getRed();
			   green = new Color(original.getRGB(i, j)).getGreen();
			   blue = new Color(original.getRGB(i, j)).getBlue();
 
			   pixel[0] = red;
			   pixel[1] = green;
			   pixel[2] = blue;
 
			   int newval = findMax(pixel);
 
			   // Return back to original format
			   newPixel = colorToRGB(alpha, newval, newval, newval);
 
			   // Write pixels into image
			   decomp.setRGB(i, j, newPixel);
 
		   }
 
	   }
 
	   return decomp;
 
	}

}