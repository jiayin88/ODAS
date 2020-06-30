package filter;

import java.awt.image.BufferedImage;

//to do threshold according to user's input range from 0 - 255

public class Threshold {
	
	public static BufferedImage threshold(BufferedImage img, int i){
		   
		   
		   //get height and width
	 		int width = img.getWidth();
	 		int height = img.getHeight();
	 		int THRESHOLD = i;
	 		
	 		//getting the mean pixel
	 		for(int y=0; y<height; y++){
	 			for(int x=0; x<width; x++){
	 				int p = img.getRGB(x, y);
	 				
	 				int a = (p>>24)&0xff;
					int r = (p>>16)&0xff;
					int g = (p>>8)&0xff;
					int b = p&0xff;
					
					int avg = (r+g+b)/3;
					
					if (avg >= THRESHOLD){avg = 255;}
					else {avg = 0;}
					
					//replace RGB with average
					p = (a<<24) | (avg<<16) | (avg<<8) | avg;
					img.setRGB(x, y, p);
	 			}
	 		}
	 		return img;
	   }

}
