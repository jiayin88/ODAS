package opticDiscAnalyser;

import java.awt.Color;
import java.awt.image.BufferedImage;

import opticDiscAnalyser.ManualDetection;

public class ManualDetectionAnalyseImage {
	
	public static double areaDisc;
	public static double areaCup;
	public static double areaCDR;
	public static double areaRim;
	public static double verticalCup;
	public static double verticalDisc;
	public static double verticalCDR;
	public static double horizontalCup;
	public static double horizontalDisc;
	public static double horizontalCDR;
	public static double meanCDR;
	public static int rimTI;
	public static int rimNS;
	public static int rimIN;
	public static int rimST;
	public static int cupTI;
	public static int discTI;
	public static int cupNS;
	public static int discNS;
	public static int cupIN; 
	public static int discIN;
	public static int cupST;
	public static int discST;
	public static String ISNT_rule;
	public static String result;
	
	 public static BufferedImage blackAndWhite(BufferedImage img){
	 	   //get height and width
	 		int width = img.getWidth();
	 		int height = img.getHeight();
	 		
	 		for(int y=0; y<height; y++){
	 			for(int x=0; x<width; x++){
	 				int p = img.getRGB(x, y);
	 				
	 				int a = (p>>24)&0xff;
	 				int r1 = (p>>16)&0xff;
	 				int g1 = (p>>8)&0xff;
	 				int b = p&0xff;
	 				
	 				int avg = (r1+g1+b)/3;
	 				
	 				if (avg == 0){avg = 0;}
	 				else {avg = 255;}
	 				
	 				//replace RGB with average
	 				p = (a<<24) | (avg<<16) | (avg<<8) | avg;
	 				img.setRGB(x, y, p);
	 			}
	 		}
	 		return img;
	    }
	  
	//these are all just readings only with no images being passed around
	public static BufferedImage manualDetectionAnalyseImage(BufferedImage img){
		
		areaDisc = ManualDetection.listDisc[4];
		areaCup = ManualDetection.listCup[4];
		areaCDR = areaCup / areaDisc;
		areaRim = areaDisc - areaCup;
		
	
	    //to convert image to black and white for easier analysis
	    BufferedImage bNw = blackAndWhite(img);
				
		// save the values in a 2D array
		int w = bNw.getWidth();
		int h = bNw.getHeight();
		int black;
		int color[][] = new int[w][h];
				
		//obtaining the pixel values based on coordinate
		for(int x=0; x<w; x++) {
			for(int y=0; y<h; y++) {
		     	black = new Color(bNw.getRGB(x, y)).getRed();
	        	color[x][y] = black;
			}   
	    }
				
			    
		//get the center of the optic disc and get the ISNT quadrant
			    
	     int xMiddle = (int)ManualDetection.listDisc[5];
			    int yMiddle = (int)ManualDetection.listDisc[6];
			    
			    //loop to the left to get two values cupST and discST
			    cupST=0;
			    discST=0;
			    int counter = 0;
			    
				for(int x=xMiddle;x>=0;x--){
					
					
					if(color[x][yMiddle] == 0 && counter == 0){
						cupST = xMiddle - x;
						counter++;
						continue;
					}
					
					if(color[x][yMiddle] == 0 && counter == 1 && color[x+1][yMiddle] != 0){
						discST = xMiddle - x;
						break;
					}
					
					if(counter == 1 && color[x][yMiddle] != 0 && x== 0){
						discST = xMiddle - x;
						break;
					}
					
				}
				
				//loop to the right to get two values cupIN and discIN
				 cupIN=0;
				 discIN=0;
				 counter = 0;
				    
				for(int x=xMiddle;x<600;x++){
					
					if(color[x][yMiddle] == 0 && counter == 0){
							cupIN = x - xMiddle;
							counter++;
							continue;
					}
					
					if(color[x][yMiddle] == 0 && counter == 1 && color[x-1][yMiddle] != 0){
							discIN = x - xMiddle;
							break;
					}
					
					if(counter == 1 && color[x][yMiddle] != 0 && x== 599){
						discIN = x - xMiddle;
						break;
					}
						
				}
				
				//loop upwards to get two values cupNS and discNS
				 cupNS=0;
				 discNS=0;
				 counter = 0;
				    
				for(int y=yMiddle;y>=0;y--){
						
					if(color[xMiddle][y] == 0 && counter == 0){
							cupNS = yMiddle - y;
							counter++;
							continue;
					}
					
					if(color[xMiddle][y] == 0 && counter == 1 && color[xMiddle][y+1] != 0){
							discNS = yMiddle - y;
							break;
					}
					
					if(counter == 1 && color[xMiddle][y] != 0 && y== 0){
						discNS = yMiddle - y;
						break;
					}
						
				}
				
				
				//loop downwards to get two values cupTI and discTI
				 cupTI=0;
				 discTI=0;
				 counter = 0;
				    
				for(int y=yMiddle;y<600;y++){
						
					if(color[xMiddle][y] == 0 && counter == 0){
							cupTI = y - yMiddle;
							counter++;
							continue;
					}
					
					if(color[xMiddle][y] == 0 && counter == 1 && color[xMiddle][y-1] != 0){
							discTI = y - yMiddle;
							break;
					}
					
					if(counter == 1 && color[xMiddle][y] != 0 && y== 599){
						discTI = y - yMiddle;
						break;
					}
					
				}
				
				//vertical optic cup (verticalCup)
				verticalCup = cupTI + cupNS;
				
				//vertical optic disc (verticalDisc)
				verticalDisc = discTI + discNS;
				
				//vertical CDR
				verticalCDR = verticalCup/verticalDisc;
				
				//horizontal optic cup (horizontalCup)
				horizontalCup = cupIN + cupST;
				
				//horizontal optic disc (horizontalDisc)
				horizontalDisc = discIN + discST;
				
				//horizontal CDR
				horizontalCDR = horizontalCup/horizontalDisc;
				
				// inferior rim
				rimTI = discTI - cupTI;
				
				//superior rim
				rimNS = discNS - cupNS;
				
				int disc = discST;
				int cup = cupST;
			
				
				// to check if it is left or right image. If image is left, then temporal at left and nasal at right
				// If image is right, temporal at right, nasal at left
				
				//to check for left image
				if(Cropping.eyeImage.equals("Left eye image")){
					//rim of IN (rimIN)
					rimIN = discIN - cupIN;
				
					//rim of ST (rimST)
					rimST = discST - cupST;

				}
				else{ // if it is a right eye image
					
					discST = discIN;
					cupST = cupIN;
					discIN = disc;
					cupIN = cup;
					
					//rim of IN (rimIN)
					rimIN = discIN - cupIN;
			
					//rim of ST (rimST)
					rimST = discST - cupST;
					
				}
				
				//check if it violates or obeys ISNT rule
				if(rimTI>=rimNS && rimNS>=rimIN && rimIN>=rimST){
					ISNT_rule = "Obeys ISNT rule";
				}
				else{
					ISNT_rule = "Violates ISNT rule";
				}
				
				//meanCDR by averaging horizontalCDR + verticalCDR
				meanCDR = (horizontalCDR + verticalCDR)/2;
				
				double meanPercentage = Math.floor(meanCDR * 100);
				
				if(meanPercentage >= 50.0){
					result = "At risk";
				}
				else{
					result = "Normal";
				}
		
				return img;
	}
	
}
