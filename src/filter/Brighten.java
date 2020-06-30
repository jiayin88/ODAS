package filter;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;


public class Brighten{
	
   public static BufferedImage brighten(BufferedImage original, float val){
	   
	  
	   RescaleOp brighterOp = new RescaleOp(val, 0, null);
	   brighterOp.filter(original,null);
	   
       /* passing source image and brightening depending on val of 1.0f means original brightness */
	   // 1.0f = original brightness, 1.5f = 50% increase while 0.5f= 50% decrease
       BufferedImage dest=changeBrightness(original,val);
       
       return dest;
   }

   public static BufferedImage changeBrightness(BufferedImage src,float val){
       RescaleOp brighterOp = new RescaleOp(val, 0, null);
       return brighterOp.filter(src,null); //filtering
   }
}