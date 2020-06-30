package analyseProcess;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


//to return the coordinates of the results obtained for locating the optic disc
public class SquareDetectionOfOpticDisc {
	
	static int finalX;
	static int finalY;
	
	public static BufferedImage detectCoordinate(BufferedImage original) {

		 int width = original.getWidth();
		 int height = original.getHeight();
		 int red;
		 int value[][] = new int[width][height];
		
		
	 
		//to input color region based on the coordinates
		
	    for(int x=0; x<width; x++) {
	        for(int y=0; y<height; y++) {
	        	 red = new Color(original.getRGB(x, y)).getRed();
	        	value[x][y] = red;
	        	}   
	        }
	    
	    //creating a list to identify the x and y coordinates that has a white pixel
	    
	    ArrayList<Integer> x1 = new ArrayList<Integer>();
	    ArrayList<Integer> y1 = new ArrayList<Integer>();
	    
	        
		 for(int x=0; x<width; x++) {
		        for(int y=0; y<height; y++) {
		        	if(((x+100) <= width) && ((y+100) <= height) && ((x-100)>= 0) && ((y-100)>= 0) ){
		        		if(value[x][y]== 255 && value[x-100][y]==255 && value[x+100][y]==255 && value[x][y+100]==255 && value[x][y-100]==255){
		        			x1.add(x);
		        			y1.add(y);
		        		}
		        	}   
		        }
		    }
		 
		 Object checkX[] = x1.toArray();
		 Object checkY[] = y1.toArray();
		 
		 //to determine the average coordinates of X and Y
		 int sumX = 0;
		 int sumY = 0;
		 finalX = 0;
		 finalY = 0;
		 
		 for(int i=0;i<checkX.length;i++){
			 sumX = sumX + ((Integer)checkX[i]).intValue();
		 }
		 
		 if(checkX.length != 0){
			 finalX = sumX/checkX.length;
		 }
		
		 
		 for(int i=0;i<checkY.length;i++){
			 sumY = sumY + ((Integer)checkY[i]).intValue();
		 }
		 
		 if(checkY.length != 0){
		 	finalY = sumY/checkY.length;
		 }
		 
		
	    return original; 
	    
	}
	
	//return the x coordinate
	static int xCoordinate(){
		return finalX;
	}

	//return the y coordinate
	static int yCoordinate(){
		return finalY;
	}
}