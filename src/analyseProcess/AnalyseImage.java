package analyseProcess;

import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.plugin.ContrastEnhancer;
import ij.process.ByteProcessor;
import ij.process.EllipseFitter;
import ij.process.FloodFiller;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;

import opticDiscAnalyser.Cropping;
import opticDiscAnalyser.MainWindow;


import filter.*;

/*this is for the analyze process whereby images will be pre-processed and processed to find the optic disc region.
 * This is within the rectangle.
 * If user says that it is incorrect, they will have to detect it manually.
 * Have yet to done on the detection part..
 */

public class AnalyseImage {
	
	static BufferedImage threscup;
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
	public static int cropY;
	public static int cropX;
	public static BufferedImage returnDrawImage;
	public static String ISNT_rule;
	public static String result;
	
	
	// Binary fill by Gabriel Landini, G.Landini at bham.ac.uk
    // 21/May/2008
    static void fill(ImageProcessor ip, int foreground, int background) {
        int width = ip.getWidth();
        int height = ip.getHeight();
        FloodFiller ff = new FloodFiller(ip);
        ip.setColor(127);
        for (int y=0; y<height; y++) {
            if (ip.getPixel(0,y)==background) ff.fill(0, y);
            if (ip.getPixel(width-1,y)==background) ff.fill(width-1, y);
        }
        for (int x=0; x<width; x++){
            if (ip.getPixel(x,0)==background) ff.fill(x, 0);
            if (ip.getPixel(x,height-1)==background) ff.fill(x, height-1);
        }
        byte[] pixels = (byte[])ip.getPixels();
        int n = width*height;
        for (int i=0; i<n; i++) {
        if (pixels[i]==127)
            pixels[i] = (byte)background;
        else
            pixels[i] = (byte)foreground;
        }
    }

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
	
	public static BufferedImage analyse(BufferedImage img){
	
	//if you write a new image, it will be saved in ODAS file in ODAS project
	
	//step 1: to convert it into red filter
	BufferedImage temp = Grayscale.rgb(img,0);
	
	//to get the size of the image based on x-axis only to differentiate between right and left eye
	double size = temp.getWidth();
	
	//step 2: to perform histogram equalization method (just the normal image contrast)
	BufferedImage histo = HistogramEQ.histogramEqualization(temp);

	//step 3: to perform Gaussian blur on it with standard deviation or sigma of 1.1
	BufferedImage g = GaussianBlur.gaussianBlur(histo,1.1);
	
	//step 4: to perform thresholding at 250 to locate optic disc
	BufferedImage thres = Threshold.threshold(g,250);
	
	//step 5: locate disc region
	SquareDetectionOfOpticDisc.detectCoordinate(thres);
	
	//step 6: draw a square around that area
	cropX = SquareDetectionOfOpticDisc.finalX-320;
	cropY = SquareDetectionOfOpticDisc.finalY-320;
	
	returnDrawImage = img;
	/*
	Graphics2D g2d = returnDrawImage.createGraphics();
	g2d.setColor(Color.BLUE);
	g2d.setStroke(new BasicStroke(6));
	g2d.drawRect((SquareDetectionOfOpticDisc.finalX-300),(SquareDetectionOfOpticDisc.finalY-300),600,600);
	g2d.dispose();
	
	*/
	
	//check if it is ok to continue or not. If it fails, return original image. else continue
	try{
		BufferedImage crop = img.getSubimage(cropX,cropY,600,600);
	
	//locating the optic disc
	//step 7: crop image from red filter
	BufferedImage crop23 = temp.getSubimage(cropX,cropY,600,600);

	//step 8: perform HEM
	BufferedImage histo2 = HistogramEQ.histogramEqualization(crop23);

	//step 9: Gaussian blur at 1.1
	BufferedImage gb = GaussianBlur.gaussianBlur(histo2,1.1);
	
	//step 10: quantize the image into new color code
	for(int y=0; y<crop23.getHeight(); y++){
		for(int x=0; x<crop23.getWidth(); x++){
			int p = gb.getRGB(x, y);
			
			int a = (p>>24)&0xff;
			int red = (p>>16)&0xff;
			int green = (p>>8)&0xff;
			int blue = p&0xff;
			
			int avg = (red+green+blue)/3;
			
			if (avg <= 127){ avg = 0;}
			else if (avg>=128 && avg<=159){avg = 64;}
			else if (avg>=160 && avg<=186){avg = 128;}
			else if (avg>=187 && avg<=215){avg = 192;}
			else {avg = 255;}
			
			//replace RGB with average
			p = (a<<24) | (avg<<16) | (avg<<8) | avg;
			gb.setRGB(x, y, p);
		}
	}
	
	//starts using ImageJ library
	//step 11: convert image to gray
	Image img2 = gb;
	ImagePlus ip = new ImagePlus(null,img2);
	ImageConverter ic = new ImageConverter(ip);
	ic.convertToGray8();
	
	Image img1 = crop;
	ImagePlus imageplus = new ImagePlus(null,img1);
	ImageProcessor imp2 = imageplus.getProcessor();
	
	//step 12-16: autothreshold,dilate,erode,fill and outline
	ImageProcessor imp = ip.getProcessor();
	ByteProcessor bp = (ByteProcessor)imp.convertToByte(true);
	bp.autoThreshold();
	bp.dilate(4,255);
	bp.erode();
	fill(imp,255,0);
	bp.outline();
	
	BufferedImage im = bp.getBufferedImage();
	
	//locating the possible coordinates of the optic disc
	//step 17: obtain pixel values based on coordinate
	
	 int width = im.getWidth();
	 int height = im.getHeight();
	 int red;
	 int value[][] = new int[width][height];
	
	//obtaining the pixel values based on coordinate
    for(int x=0; x<width; x++) {
        for(int y=0; y<height; y++) {
        	 red = new Color(im.getRGB(x, y)).getRed();
        	value[x][y] = red;
        	}   
        }
	

	  //assuming all optic disc has coordinate (300,300) that falls within the optic disc boundary
	  		
		//going to loop from (300,300) towards the left to identify the first x-coordinate along that x-axis to set as benchmark
		
		int leftX=0;
		int rightX=0;
		
		//looping to get the leftX
		for(int x=300; x>=0; x--){
			
			if (value[x][300] == 0){
					leftX = x;
					break;
			}	
			
			if (x == 1 && leftX == 0){
				leftX = x;
				break;
			}
		}
		
		//looping to get the rightX
				for(int x=300; x<=600; x++){
					
					if (value[x][300] == 0){
							rightX = x;
							break;
					}	
					
					if (x == 599 && rightX == 0){
						rightX = x;
						break;
					}
				}
				
		//to get the inofficial diameter of the ROI and to save coordinates based on this
				
		int range = rightX-leftX ;
		
		int[] xUp = new int[range+1];
		int[] yUp = new int[range+1];
		
		int i=0; 
		
		//when the first y-coordinate hits the black pixel, then save it in your range
		//doing for the upper part 
		for(int x=rightX; x>=leftX; x--){
			for(int y=300; y>=0; y--){
				
				if (value[x][y] == 0){
					xUp[i] = x;
					yUp[i] = y;
					i++;
					break;
				}	
			
				if (y == 1 && value[x][y] != 0){
					xUp[i] = x;
					yUp[i] = y;
					i++;
					break;
				}	
			}
			//System.out.println(xUp[i] + " " + yUp[i]);
		}
		
		
		int[] xDown = new int[range];
		int[] yDown = new int[range];
		
		int j=0; 
		
		//when the first y-coordinate hits the black pixel, then save it in your range
		//doing for the down part  
		for(int x=leftX; x<rightX; x++){
			for(int y=300; y<=600; y++){
				
				if (value[x][y] == 0){
					xDown[j] = x;
					yDown[j] = y;
					j++;
					break;
				}	
			
				if (y == 599 && value[x][y] != 0){
					xDown[j] = x;
					yDown[j] = y;
					j++;
					break;
				}	
			}
		}
		
		
		//to do an array consisting of all the points
		int r = xDown.length + xUp.length;
		float[] xpoints = new float[r];
		float[] ypoints = new float[r];
		
		for(int ii=0;ii<xUp.length;ii++){
			xpoints[ii] = xUp[ii];
			ypoints[ii] = yUp[ii];
			
		}
		
		int leftover = 0;
		for(int ii=xUp.length;ii<xpoints.length;ii++){
			xpoints[ii] = xDown[leftover];
			ypoints[ii] = yDown[leftover];
			leftover++;
		}
			
		//FloatPolygon fp = new FloatPolygon(xpoints,ypoints);
		PolygonRoi py = new PolygonRoi(xpoints,ypoints,2);
		imp.setRoi(py);
		
		EllipseFitter ef = new EllipseFitter();
		ef.fit(imp, null);
		
		//to get int points
		ef.xCoordinates = new int[xpoints.length];
		ef.yCoordinates = new int[ypoints.length];
		for (int a=0; a<xpoints.length;a++){
			ef.xCoordinates[a] = (int)xpoints[a];
			ef.xCoordinates[a] = (int)ypoints[a];
		}
		ef.makeRoi(imp);
		ef.drawEllipse(imp2);
		
		BufferedImage opticDisc = imp2.getBufferedImage();
	
	//to locate the optic cup
	//step 20: Get new polygon and leave only the optic disc region
	PolygonRoi ellipse = new PolygonRoi(ef.xCoordinates,ef.yCoordinates,ef.xCoordinates.length,2);
	imp.setRoi(ellipse);
	
	imp2.fillOutside(ellipse);
    BufferedImage outside = imp2.getBufferedImage();
	
    //step 21: Get maximum decomposition of image
    BufferedImage maxDecomposition = Grayscale.decompMax(outside);
    Image img3 = maxDecomposition;
	ImagePlus imageplus1 = new ImagePlus(null,img3);
	ImageProcessor imp3 = imageplus1.getProcessor();
	
	//step 22: Perform HEM
	ContrastEnhancer ce = new ContrastEnhancer();
	ce.equalize(imageplus1);
	
	//to check if the size is bigger or smaller
	//get the area of the disc
    areaDisc = Math.PI * (ef.major/2) * (ef.minor/2);
	
    //get the pixel value of the threshold image at 245
	BufferedImage threspixel = Threshold.threshold(imp3.getBufferedImage(),245);
	
	//get the value of all pixels
	int width1 = threspixel.getWidth();
	int height1 = threspixel.getHeight();
	int red1;
	int value1[][] = new int[width1][height1];
	
	//obtaining the pixel values based on coordinate
    for(int x=0; x<width1; x++) {
        for(int y=0; y<height1; y++) {
        	 red1 = new Color(threspixel.getRGB(x, y)).getRed();
        	value1[x][y] = red1;
        	}   
        }
	
    double whiteCount = 0;
    //loop to find all the white pixels
    for(int x=0; x<width1; x++) {
        for(int y=0; y<height1; y++) {
        	
        		if(value1[x][y] == 255){
        			whiteCount++;
        		}
        	}   
        }

    //check if the white pixels is greater or less than 20% of the area
 
    double checkMax = 0.4 * areaDisc;
    double checkMid2 = 0.36 * areaDisc;
    double checkMid = 0.31 * areaDisc;
   
    double checkMin = 0.29 * areaDisc;
    double check2 = 0.26 * areaDisc;
    double check = 0.2 * areaDisc;
    double checkLast = 0.16 * areaDisc;
    
    
	//step 23: Apply Gaussian blur at 100.0
	imp3.blurGaussian(100.0);
	
	//step 24: Perform HEM on blurred image
	ce.equalize(imp3);
	BufferedImage contrastcup = imp3.getBufferedImage();
	
	 //if whiteCount is greater than check, go to the if statement, else, enter the else statement
	if(whiteCount < checkLast){
		threscup = Threshold.threshold(contrastcup,230);
	}
	else if(whiteCount > checkLast && whiteCount < check){
		threscup = Threshold.threshold(contrastcup,220);
	}
	else if (whiteCount>check && whiteCount<check2){
		threscup = Threshold.threshold(contrastcup,228);
	}
	else if(whiteCount>check2 && whiteCount<checkMin){
		threscup = Threshold.threshold(contrastcup,220);
	}
	else if(whiteCount>checkMin && whiteCount<checkMid){
		threscup = Threshold.threshold(contrastcup,210);
	}
	else if(whiteCount>checkMid && whiteCount<checkMid2){
		threscup = Threshold.threshold(contrastcup,205);
	}
	else if(whiteCount>checkMid2 && whiteCount<checkMax){
		threscup = Threshold.threshold(contrastcup,180);
	}
	else{
		threscup = Threshold.threshold(contrastcup,180);
	}
	
	
	//step 26: Obtain coordinates of the optic cup
	int widthCup = threscup.getWidth();
	 int heightCup = threscup.getHeight();
	 int white;
	 int value11[][] = new int[width1][height1];
	
	//obtaining the pixel values based on coordinate
   for(int x=0; x<widthCup; x++) {
       for(int y=0; y<heightCup; y++) {
       	 white = new Color(threscup.getRGB(x, y)).getRed();
       	value11[x][y] = white;
       	}   
   }
   
   
	
 //assuming all optic disc has coordinate (ef.xCenter, ef.yCenter) that falls within the optic disc boundary
 		
	//going to loop from (ef.xCenter,ef.yCenter) towards the left to identify the first x-coordinate along that x-axis to set as benchmark
	
	int leftX1=0;
	int rightX1=0;
	int xCenter = (int)ef.xCenter;
	int yCenter = (int)ef.yCenter;
	
	//looping to get the leftX
	for(int x=xCenter; x>=0; x--){
		
		if (value11[x][300] == 0){
				leftX1 = x;
				break;
		}	
		
		if (x == 1 && leftX1 == 0){
			leftX1 = x;
			break;
		}
	}
	
	
	//looping to get the rightX
			for(int x=xCenter; x<=600; x++){
				
				if (value11[x][300] == 0){
						rightX1 = x;
						break;
				}	
				
				if (x == 599 && rightX1 == 0){
					rightX1 = x;
					break;
				}
			}
			
	
	//to get the inofficial diameter of the ROI and to save coordinates based on this
			
	int range1 = rightX1-leftX1 ;
	
	int[] xUp1 = new int[range1+1];
	int[] yUp1 = new int[range1+1];
	
	int i1=0; 
	
	//when the first y-coordinate hits the black pixel, then save it in your range
	//doing for the upper part 
	for(int x=rightX1; x>=leftX1; x--){
		for(int y=yCenter; y>=0; y--){
			
			if (value11[x][y] == 0){
				xUp1[i1] = x;
				yUp1[i1] = y;
				i1++;
				break;
			}	
		
			if (y == 1 && value11[x][y] != 0){
				xUp1[i1] = x;
				yUp1[i1] = y;
				i1++;
				break;
			}	
		}
		//System.out.println(xUp[i] + " " + yUp[i]);
	}
	
	
	int[] xDown1 = new int[range1];
	int[] yDown1 = new int[range1];
	
	int j1=0; 
	
	//when the first y-coordinate hits the black pixel, then save it in your range
	//doing for the down part  
	for(int x=leftX1; x<rightX1; x++){
		for(int y=yCenter; y<=600; y++){
			
			if (value11[x][y] == 0){
				xDown1[j1] = x;
				yDown1[j1] = y;
				j1++;
				break;
			}	
		
			if (y == 599 && value11[x][y] != 0){
				xDown1[j1] = x;
				yDown1[j1] = y;
				j1++;
				break;
			}	
		}
	}
	
	
	//to do an array consisting of all the points
	int r1 = xDown1.length + xUp1.length;
	float[] xpoints1 = new float[r1];
	float[] ypoints1 = new float[r1];
	
	for(int ii=0;ii<xUp1.length;ii++){
		xpoints1[ii] = xUp1[ii];
		ypoints1[ii] = yUp1[ii];
		
	}
	
	int leftover1 = 0;
	for(int ii=xUp1.length;ii<xpoints1.length;ii++){
		xpoints1[ii] = xDown1[leftover1];
		ypoints1[ii] = yDown1[leftover1];
		leftover1++;
	}
	
	//step 27: draw polygon 
	PolygonRoi py1 = new PolygonRoi(xpoints1,ypoints1,2);
	imp.setRoi(py1);
	
	//step 28: Perform Ellipse Fitting algorithm
	EllipseFitter ef1 = new EllipseFitter();
	ef1.fit(imp, null);
	//ef.drawEllipse(imp2);
	
	//to get int points
	ef1.xCoordinates = new int[xpoints1.length];
	ef1.yCoordinates = new int[ypoints1.length];
	for (int a=0; a<xpoints1.length;a++){
		ef1.xCoordinates[a] = (int)xpoints1[a];
		ef1.xCoordinates[a] = (int)ypoints1[a];
	}
	ef1.makeRoi(imp);
	
	//step 29: Draw the Ellipse on the image
	Image img21 = opticDisc;
	ImagePlus imageplus11 = new ImagePlus(null,img21);
	ImageProcessor imp21 = imageplus11.getProcessor();
	
	ef1.drawEllipse(imp21);
	
	//step 30: save image for return
	BufferedImage opticCupAndDisc = imp21.getBufferedImage();
	
	//step 31: Perform analysis
	    
		//retrieve the area of the disc from above
	    //get the area of the cup
	    areaCup = Math.PI * (ef1.major/2) * (ef1.minor/2);
	    
	    areaCDR = areaCup/areaDisc;
	    areaRim = areaDisc - areaCup;
	    
	   //to convert image to black and white for easier analysis
		BufferedImage bNw = blackAndWhite(opticCupAndDisc);
		
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
	    
	    int xMiddle = (int)ef.xCenter;
	    int yMiddle = (int)ef.yCenter;
	    
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
		if(SquareDetectionOfOpticDisc.finalX <= (size/2)){
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
		
		//Return image for viewing
		return imp21.getBufferedImage();
		//catch raster format exception when image could not be analysed
	}catch (Exception e){
		JOptionPane.showMessageDialog(null,"Sorry, unable to analyse image. We will go straight to manual detection.");
		MainWindow.p1.removeAll();
    	MainWindow.panel.removeAll();
		MainWindow.panel.revalidate();
		Cropping.main(img);
		MainWindow.frmOpticDiscAnalysing.setVisible(false);
		return null;
	}
	}
	
}
