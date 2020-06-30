package opticDiscAnalyser;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import filter.*;
import analyseProcess.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;


// left saveImage

public class ManualDetection {
	
	public JSlider slider = new JSlider(JSlider.HORIZONTAL,0,100,50);
	public JPanel panel = new JPanel();
	public JLabel labelImage2 = new ScaledImageLabel();
	public JLabel label = new ScaledImageLabel();
	public JFrame frmManualDetection;
	public BufferedImage img;
	public BufferedImage image;
	JLabel lblBrightnessContrast;
	public static BufferedImage staticImage;
	public static double [] listA = new double[7];
	public static double [] listB = new double[7];
	
	//to save result in the correct list
	public static double [] listDisc = new double[7];
	public static double [] listCup = new double[7];
	public static BufferedImage finalImage;
	
	int c = 0;
	/**
	 * Launch the application.
	 */
	public static void main(final BufferedImage img) {
		ManualDetection window = new ManualDetection(img);
		window.frmManualDetection.setVisible(true);
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public ManualDetection(BufferedImage img) {
		initialize(img);
	}
	
	public class ScaledImageLabel extends JLabel {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected void paintComponent(Graphics g) {
	        ImageIcon icon = (ImageIcon) getIcon();
	        if (icon != null) {
	            ImageDrawer.drawScaledImage(icon.getImage(), this, g);
	        }
	    }
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	public void initialize(final BufferedImage img){
		staticImage = img;
		frmManualDetection = new JFrame();
		frmManualDetection.setTitle("Manual Detection");
		frmManualDetection.setBounds(100, 100, 900, 700);
		frmManualDetection.setLocation(20,20);
		frmManualDetection.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setImage(img);
		image = img;
		
		JMenuBar menuBar = new JMenuBar();
		frmManualDetection.setJMenuBar(menuBar);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmDraw = new JMenuItem("Draw Image");
		mntmDraw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				//making sure image is not null
				c++;
				
				//check if only 2 ovals are drawn
				if (c <= 2){
				//get the ellipse height and width and draw the image
				//set it permanent 
				double startX = DrawOval.getStartResultX();
				double startY = DrawOval.getStartResultY();
				double endX = DrawOval.getEndResultX() - startX;
				double endY = DrawOval.getEndResultY() - startY;
				
				//to get the area of the ellipse
				double area = Math.PI*((double)endX/2)*((double)endY/2);
				
				
				
				BufferedImage r2d = getImage();
				image = new BufferedImage(r2d.getWidth(),r2d.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = (Graphics2D) image.createGraphics();
				g2d.drawImage(r2d,null,0,0);
				g2d.setColor(Color.BLACK);
				g2d.setStroke(new BasicStroke(1));
				g2d.draw(new Ellipse2D.Double(startX, startY, endX, endY));
			//	g2d.drawOval(startX, startY, endX, endY);
				g2d.dispose();
				setImage(image);
				labelImage2.setIcon(new ImageIcon(getImage()));
	        	 
					if (c == 1){
						listA[0] = startX; //starting point of X
						listA[1] = startY; //starting point of Y
						listA[2] = endX; //width
						listA[3] = endY; //height
						listA[4] = area; //area of ROI
						listA[5] = (startX + endX/2); //midpoint of ellipse x-coordinate
						listA[6] = (startY + endY/2); //midpoint of ellipse y-coordinate
						
					}
					else{
						listB[0] = startX; //starting point of X
						listB[1] = startY; //starting point of Y
						listB[2] = endX; //width
						listB[3] = endY; //height
						listB[4] = area; //area of ROI
						listB[5] = (startX + endX/2); //midpoint of ellipse x-coordinate
						listB[6] = (startY + endY/2); //midpoint of ellipse y-coordinate
						
					}
					panel.remove(lblBrightnessContrast);
					panel.remove(slider);
					panel.revalidate();
				}
				else{
					JOptionPane.showMessageDialog(null,"Only 2 ellipses allowed!\n(One for optic disc and the other for optic cup)", "Warning",JOptionPane.PLAIN_MESSAGE);
				}
				
			}
		});
		mnEdit.add(mntmDraw);
		
		JMenuItem mntmRevert = new JMenuItem("Revert Original");
		mntmRevert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				c = 0;
				setImage(img);
				labelImage2.setIcon(new ImageIcon(getImage()));
				
				//setting all array readings back to null
				listA = new double [listA.length];
				listB = new double [listB.length];
				panel.add(lblBrightnessContrast);
				panel.add(slider);
				panel.revalidate();
			}
		});
		mnEdit.add(mntmRevert);

		JMenu mnColor = new JMenu("Color");
		menuBar.add(mnColor);
		
		JMenuItem mntmOriginal = new JMenuItem("Back");
		mntmOriginal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				if(c != 0){
					setImage(image);
					labelImage2.setIcon(new ImageIcon(getImage()));
				}
				else{
					setImage(img);
					labelImage2.setIcon(new ImageIcon(getImage()));
				}
				
			}
		});
		mnColor.add(mntmOriginal);
		
		JMenu mnChannel = new JMenu("Channel");
		mnColor.add(mnChannel);
		
		JMenuItem mntmRedChannel = new JMenuItem("Red channel");
		mntmRedChannel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				BufferedImage r = RedChannel.red(getImage());
				setImage(r);
				labelImage2.setIcon(new ImageIcon(getImage()));
			}
		});
		mnChannel.add(mntmRedChannel);
		
		JMenuItem mntmGreenChannel = new JMenuItem("Green channel");
		mntmGreenChannel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				BufferedImage g = GreenChannel.green(getImage());
				setImage(g);
				labelImage2.setIcon(new ImageIcon(getImage()));
			}
		});
		mnChannel.add(mntmGreenChannel);
		
		JMenuItem mntmBlueChannel = new JMenuItem("Blue channel");
		mntmBlueChannel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				BufferedImage b = BlueChannel.blue(getImage());
				setImage(b);
				labelImage2.setIcon(new ImageIcon(getImage()));
			}
		});
		mnChannel.add(mntmBlueChannel);
		
		JMenu mnGrayscale = new JMenu("Grayscale");
		mnColor.add(mnGrayscale);
		
		JMenuItem mntmAverage = new JMenuItem("Average");
		mntmAverage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				BufferedImage avg = Grayscale.average(getImage());
				setImage(avg);
				labelImage2.setIcon(new ImageIcon(getImage()));
			}
		});
		mnGrayscale.add(mntmAverage);
		
		JMenuItem mntmDesaturation = new JMenuItem("Desaturation");
		mntmDesaturation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				BufferedImage des = Grayscale.desaturation(getImage());
				setImage(des);
				labelImage2.setIcon(new ImageIcon(getImage()));
			}
		});
		mnGrayscale.add(mntmDesaturation);
		
		
		JMenuItem mntmMaximalDecomposition = new JMenuItem("Maximal Decomposition");
		mntmMaximalDecomposition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				BufferedImage max = Grayscale.decompMax(getImage());
				setImage(max);
				labelImage2.setIcon(new ImageIcon(getImage()));
			}
		});
		mnGrayscale.add(mntmMaximalDecomposition);
		
		
		JMenuItem mntmRedFilter = new JMenuItem("Red filter");
		mntmRedFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				BufferedImage red = Grayscale.rgb(getImage(),0);
				setImage(red);
				labelImage2.setIcon(new ImageIcon(getImage()));
			}
		});
		mnGrayscale.add(mntmRedFilter);
		
		JMenuItem mntmGreenFilter = new JMenuItem("Green filter");
		mntmGreenFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				BufferedImage green = Grayscale.rgb(getImage(),1);
				setImage(green);
				labelImage2.setIcon(new ImageIcon(getImage()));
			}
		});
		mnGrayscale.add(mntmGreenFilter);
		
		JMenuItem mntmBlueFilter = new JMenuItem("Blue filter");
		mntmBlueFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				BufferedImage blue = Grayscale.rgb(getImage(),2);
				setImage(blue);
				labelImage2.setIcon(new ImageIcon(getImage()));
			}
		});
		mnGrayscale.add(mntmBlueFilter);
		
		JMenu mnProcess = new JMenu("Process");
		menuBar.add(mnProcess);
		
		JMenuItem mntmHistogramEqualization = new JMenuItem("Histogram equalization");
		mntmHistogramEqualization.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				BufferedImage hist = HistogramEQ.histogramEqualization(getImage());
				setImage(hist);
				labelImage2.setIcon(new ImageIcon(getImage()));
			}
		});
		mnProcess.add(mntmHistogramEqualization);
		
		JMenuItem mntmThreshold = new JMenuItem("Threshold");
		mntmThreshold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//cannot use this method! apparently it will change the original image as well
				//still need to improve on this part
				
				String answer = JOptionPane.showInputDialog(null,"Notice!Need to perform a different process before doing this.\nThreshold at... (0-255)");
				if(answer != null){
				int in = Integer.parseInt(answer);
				BufferedImage thres = Threshold.threshold(getImage(), in);
				setImage(thres);
				labelImage2.setIcon(new ImageIcon(getImage()));
				}
				
			}
		});
		mnProcess.add(mntmThreshold);
		
		JMenuItem mntmGaussianBlur = new JMenuItem("Gaussian Blur");
		mntmGaussianBlur.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String answer = JOptionPane.showInputDialog(null,"Gaussian Blur with sigma...");
				double i = Double.parseDouble(answer);
				Image img3 = getImage();
				ImagePlus imageplus1 = new ImagePlus(null,img3);
				ImageProcessor imp3 = imageplus1.getProcessor();
				imp3.blurGaussian(i);
				BufferedImage gb = imp3.getBufferedImage();
				setImage(gb);
				labelImage2.setIcon(new ImageIcon(getImage()));
			}
		});
		mnProcess.add(mntmGaussianBlur);
		
		frmManualDetection.getContentPane().setLayout(null);
		
		panel.setBounds(0, 0, 164, 379);
		frmManualDetection.getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton btnNewButton = new JButton("Save Image");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//to make sure 2 ellipses are drawn
				if(c >= 2){
					//to save the points and draw on the image.
					//to update all the three information found at the main window
				
					//if area of ROI from listA is greater than from listB, the array data from listA belongs to optic disc and listB is optic cup
					if(listA[4]>= listB[4]){
					
						//copying data from listA into listDisc
						for (int i = 0; i < listA.length; i++) {
			            	listDisc[i] = listA[i];
			        	}
					
					//copying data from listB into listCup
						for (int i = 0; i < listB.length; i++) {
			            	listCup[i] = listB[i];
			        	}
					}else{
					//copying data from listB into listDisc
						for (int i = 0; i < listB.length; i++) {
			            	listDisc[i] = listB[i];
			        	}
					//copying data from listA into listCup
						for (int i = 0; i < listA.length; i++) {
			            	listCup[i] = listA[i];
			        	}
					}
				
					//setting all array readings back to null
					listA = new double [listA.length];
					listB = new double [listB.length];
					
					//to get the correct image
					BufferedImage r = staticImage;
					finalImage = new BufferedImage(r.getWidth(),r.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2d = (Graphics2D) finalImage.createGraphics();
					g2d.drawImage(r,null,0,0);
					g2d.setColor(Color.BLACK);
					g2d.setStroke(new BasicStroke(1));
					g2d.draw(new Ellipse2D.Double(listDisc[0], listDisc[1], listDisc[2], listDisc[3]));
					g2d.draw(new Ellipse2D.Double(listCup[0], listCup[1], listCup[2], listCup[3]));
					g2d.dispose();

			    	ManualDetectionAnalyseImage.manualDetectionAnalyseImage(finalImage);
			    	
			        MainWindow.p1.removeAll();
			        MainWindow.panel.removeAll();
					MainWindow.panel.revalidate();
			       
					//check on the setColor function.. test on the redraw.
					//check on redraw image... see if color got change or not.
					//to get the correct image
					BufferedImage r1 = staticImage;
					BufferedImage correct = new BufferedImage(r1.getWidth(),r1.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D graphics = (Graphics2D) correct.createGraphics();
					graphics.drawImage(r1,null,0,0);
					graphics.setColor(Color.BLACK);
					graphics.setStroke(new BasicStroke(1));
					graphics.draw(new Ellipse2D.Double(listDisc[0], listDisc[1], listDisc[2], listDisc[3]));
					graphics.draw(new Ellipse2D.Double(listCup[0], listCup[1], listCup[2], listCup[3]));
					graphics.dispose();
					
					/// JLabel label = new JLabel(new ImageIcon(correct));
				    // JOptionPane.showMessageDialog(null, label, "clipped image", JOptionPane.PLAIN_MESSAGE);
					
					
					MainWindow.crop = correct;
					MainWindow.areaDisc = ManualDetectionAnalyseImage.areaDisc;
					MainWindow.areaCup = ManualDetectionAnalyseImage.areaCup;
					MainWindow.areaCDR = ManualDetectionAnalyseImage.areaCDR;
					MainWindow.areaRim = ManualDetectionAnalyseImage.areaRim;
					MainWindow.verticalCup = ManualDetectionAnalyseImage.verticalCup;
					MainWindow.verticalDisc = ManualDetectionAnalyseImage.verticalDisc;
					MainWindow.verticalCDR = ManualDetectionAnalyseImage.verticalCDR;
					MainWindow.horizontalCup = ManualDetectionAnalyseImage.horizontalCup;
					MainWindow.horizontalDisc = ManualDetectionAnalyseImage.horizontalDisc;
					MainWindow.horizontalCDR = ManualDetectionAnalyseImage.horizontalCDR;
					MainWindow.meanCDR = ManualDetectionAnalyseImage.meanCDR;
					MainWindow.rimTI = ManualDetectionAnalyseImage.rimTI;
					MainWindow.rimNS = ManualDetectionAnalyseImage.rimNS;
					MainWindow.rimIN = ManualDetectionAnalyseImage.rimIN;
					MainWindow.rimST = ManualDetectionAnalyseImage.rimST;
					MainWindow.cupTI = ManualDetectionAnalyseImage.cupTI;
					MainWindow.discTI = ManualDetectionAnalyseImage.discTI;
					MainWindow.cupNS = ManualDetectionAnalyseImage.cupNS;
					MainWindow.discNS = ManualDetectionAnalyseImage.discNS;
					MainWindow.cupIN = ManualDetectionAnalyseImage.cupIN;
					MainWindow.discIN = ManualDetectionAnalyseImage.discIN;
					MainWindow.cupST = ManualDetectionAnalyseImage.cupST;
					MainWindow.discST = ManualDetectionAnalyseImage.discST;
					MainWindow.cropX = Cropping.xCoordinate();
					MainWindow.cropY = Cropping.yCoordinate();
					MainWindow.ISNT_rule = ManualDetectionAnalyseImage.ISNT_rule;
					MainWindow.resultCDR = ManualDetectionAnalyseImage.result;
			    	
					JLabel labelImage = new ScaledImageLabel();
				    JLabel labelImage2 = new ScaledImageLabel();
					
					labelImage.setPreferredSize(new Dimension(370,370));
					BufferedImage r2d1 = AnalyseImage.returnDrawImage;
					BufferedImage image = new BufferedImage(r2d1.getWidth(),r2d1.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2d1 = (Graphics2D) image.createGraphics();
					g2d1.drawImage(r2d1,null,0,0);
					g2d1.setColor(Color.BLUE);
					g2d1.setStroke(new BasicStroke(9));
					g2d1.drawRect(MainWindow.cropX,MainWindow.cropY,600,600);
					g2d1.dispose();
					labelImage.setIcon(new ImageIcon(image));
					MainWindow.panel.add(labelImage);
					
					labelImage2.setPreferredSize(new Dimension(370,370));
					labelImage2.setIcon(new ImageIcon(MainWindow.crop));
					MainWindow.panel.add(labelImage2);
					
					
					Font fe2 = new Font ("Century Gothic",Font.BOLD,12);
					Font fe = new Font ("Century Gothic",Font.PLAIN,12);
					DecimalFormat df = new DecimalFormat("#.####");
					MainWindow.p1.setLayout(new GridLayout(25,1));
					MainWindow.p1.setBackground(Color.WHITE);
					MainWindow.p1.setOpaque(true);
					
					JLabel jl1 = new JLabel("              All readings are in pixels");
					jl1.setFont(fe2);
					MainWindow.p1.add(jl1);
				
					MainWindow.p1.add(new JLabel(""));
					MainWindow.p1.add(new JLabel(""));
					
					JLabel jl2 = new JLabel("        Area CDR             : " + df.format(MainWindow.areaCDR)+ "                    ");
					jl2.setFont(fe2);
					MainWindow.p1.add(jl2);
					JLabel jl3 = new JLabel("        Area Disc             : " + df.format(MainWindow.areaDisc)+ "                    ");
					jl3.setFont(fe);
					MainWindow.p1.add(jl3);
					JLabel jl4 = new JLabel("        Area Cup             : " + df.format(MainWindow.areaCup)+ "                    ");
					jl4.setFont(fe);
					MainWindow.p1.add(jl4);
					JLabel jl5 = new JLabel("        Area Rim              : " + df.format(MainWindow.areaRim)+ "                    ");
					jl5.setFont(fe);
					MainWindow.p1.add(jl5);
					
					MainWindow.p1.add(new JLabel(""));
			
					JLabel jl17 = new JLabel("        Mean CDR              :" + df.format(MainWindow.meanCDR)+ "                     ");
					jl17.setFont(fe2);
					MainWindow.p1.add(jl17);
					JLabel jl18 = new JLabel("        " + MainWindow.resultCDR);
					jl18.setFont(fe2);
					MainWindow.p1.add(jl18);
					
					MainWindow.p1.add(new JLabel(""));
					
					JLabel jl6 = new JLabel("        Horizontal CDR    : " + df.format(MainWindow.horizontalCDR)+ "                 ");
					jl6.setFont(fe2); 
					MainWindow.p1.add(jl6);
					JLabel jl7 = new JLabel("        Horizontal Cup   : " + df.format(MainWindow.horizontalCup)+ "                 ");
					jl7.setFont(fe);
					MainWindow.p1.add(jl7);
					JLabel jl8 = new JLabel("        Horizontal Disc   : " + df.format(MainWindow.horizontalDisc)+ "                 ");
					jl8.setFont(fe);
					MainWindow.p1.add(jl8);
					
					MainWindow.p1.add(new JLabel(""));
					
					JLabel jl9 = new JLabel("        Vertical CDR        : " + df.format(MainWindow.verticalCDR)+ "                   ");
					jl9.setFont(fe2);
					MainWindow.p1.add(jl9);
					JLabel jl10 = new JLabel("        Vertical Cup       : " + df.format(MainWindow.verticalCup)+ "                   ");
					jl10.setFont(fe);
					MainWindow.p1.add(jl10);
					JLabel jl11 = new JLabel("        Vertical Disc      : " + df.format(MainWindow.verticalDisc)+ "                   ");
					jl11.setFont(fe);
					MainWindow.p1.add(jl11);
					
					MainWindow.p1.add(new JLabel(""));
					
					JLabel jl16 = new JLabel("        " + MainWindow.ISNT_rule);
					jl16.setFont(fe2);
					MainWindow.p1.add(jl16);
					JLabel jl12 = new JLabel("        Inferior Rim          : " + df.format(MainWindow.rimTI)+ "                   ");
					jl12.setFont(fe);
					MainWindow.p1.add(jl12);
					JLabel jl13 = new JLabel("        Superior Rim       : " + df.format(MainWindow.rimNS)+ "                    ");
					jl13.setFont(fe);
					MainWindow.p1.add(jl13);
					JLabel jl14 = new JLabel("        Nasal Rim             : " + df.format(MainWindow.rimIN)+ "                    ");
					jl14.setFont(fe);
					MainWindow.p1.add(jl14);
					JLabel jl15 = new JLabel("        Temporal Rim     : " + df.format(MainWindow.rimST)+ "                    ");
					jl15.setFont(fe);
					MainWindow.p1.add(jl15);
					
					MainWindow.panel.add(MainWindow.p1);

					MainWindow.frmOpticDiscAnalysing.setVisible(true);
					
			    	frmManualDetection.dispose();
					
				}else{
					JOptionPane.showMessageDialog(null,"Please draw 2 ellipses.", "Warning",JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		btnNewButton.setBounds(23, 11, 112, 23);
		panel.add(btnNewButton);
		
		lblBrightnessContrast = new JLabel("Brightness / Contrast");
		lblBrightnessContrast.setBounds(10, 45, 125, 33);
		panel.add(lblBrightnessContrast);
		
		slider.setBounds(10, 68, 125, 49);
		slider.addChangeListener(new ChangeListener() {
			  @Override
	         public void stateChanged(ChangeEvent e) {

	        	 double temp = ((JSlider) e.getSource()).getValue(); 
	        	 double num = temp/100.0*2.0;
	        	 float y = (float)(num);
	        	 BufferedImage io = Brighten.brighten(getImage(),y);
	        	 labelImage2.setIcon(new ImageIcon(io));
	         }
	        
	      });
		panel.add(slider);
		
		JLabel message = new JLabel("<HTML>Hint: We are only interested <br>  in the 2 ellipse drawn</HTML>");
		message.setBounds(10, 120, 144, 49);
		panel.add(message);
		
		label.setPreferredSize(new Dimension(130,130));
		label.setBounds(10, 200, 130, 130);
		label.setIcon(new ImageIcon(img));
		panel.add(label);
		
		labelImage2.setPreferredSize(new Dimension(600,600));
		labelImage2.setBounds(190,11,600,600);
		frmManualDetection.getContentPane().add(labelImage2);
	  	labelImage2.setIcon(new ImageIcon(img));
		
	  	frmManualDetection.getContentPane().add(updateImage(getImage()));
	}
	
	//use the get and set method to manipulate the image
	void setImage (BufferedImage img){
		this.img = img;
	}
	
	BufferedImage getImage(){
		return img;
	}
	
	Component updateImage(BufferedImage image){
		 
		JPanel panel2 = new JPanel(){
            /**
			 * 
			 */
			//to set image as background
			private static final long serialVersionUID = 1L;
			
			@Override
            protected void paintComponent(Graphics g) {
				
				super.paintComponent(g);
                g.drawImage(getImage(), 0, 0, this);
               // repaint();

            }
		
        };
        
        //to make the drawOval function workable
		panel2.setBounds(190,11,600,600);
		panel2.setLayout(new BorderLayout());
		DrawOval db = new DrawOval();
		panel2.add(db, BorderLayout.CENTER);
		
		return panel2;
	}
}
