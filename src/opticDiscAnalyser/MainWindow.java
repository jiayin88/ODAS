package opticDiscAnalyser;

import java.awt.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import userProfile.*;
import analyseProcess.*;



//this is the main window for ODAS. It consists of everything except the user profile.
//user profile is in another class called as UserProfile
//will be adding along the way while working on the code



public class MainWindow implements ActionListener, Runnable {
	File file;
	File newFile;
	static BufferedImage crop;
	BufferedImage img;
	public static String as;
	
	public JLabel labelImage = new ScaledImageLabel();
	public JLabel labelImage2 = new ScaledImageLabel();
	public static JPanel panel = new JPanel();
	public static JPanel p1 = new JPanel();
	int count =0;
	Connection conn = null; 
	ResultSet rs = null;
	PreparedStatement ps = null;
	
	JButton btnSaveResult = new JButton("Save Result");
	JMenuItem menuSave = new JMenuItem("Save");
	
	JButton btnGenerateReport = new JButton("Generate Report");
	JMenuItem menuGenerateReport = new JMenuItem("Generate Report");
	
	JComboBox<String> selectEyeImage = new JComboBox<>();
	public static String eye = null;
	int ct = 0;
	
	//contains all relevant information of the optic disc and optic cup
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
	public static String ISNT_rule;
	public static String resultCDR;
	 static Thread r;
	
	public static JFrame frmOpticDiscAnalysing;

	/**
	 * Launch the application.
	 */
	public static void main(String[]args) throws Exception{
		 r = new Thread(new MainWindow());
		 r.start();
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
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frmOpticDiscAnalysing = new JFrame("Optic Disc Analysing System");
		frmOpticDiscAnalysing.setResizable(false);
		frmOpticDiscAnalysing.setForeground(new Color(0, 102, 204));
		frmOpticDiscAnalysing.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		frmOpticDiscAnalysing.setBackground(new Color(0, 0, 102));
		frmOpticDiscAnalysing.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		
		Font f = new Font("Century Gothic",Font.PLAIN,12);
		UIManager.put("OptionPane.messageFont", f);
	
		JMenuBar menuBar = new JMenuBar();
		menuBar.setForeground(new Color(0, 102, 204));
		frmOpticDiscAnalysing.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setForeground(new Color(0, 0, 0));
		mnFile.setFont(f);
		mnFile.setBackground(new Color(0, 0, 204));
		menuBar.add(mnFile);
		
		final JMenuItem menuOpen = new JMenuItem("Open");
		menuOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				/* get file directory from user for only jpg format */

					try {
						newFile = Directory. getDirectoryUsingJFileChooser(file);
						
						img = ImageIO.read(newFile);
						
						//need to make sure that the image is in the size of greater than 1290x1290
						int width = img.getWidth();
						int height = img.getHeight();

						if (width< 1290 || height<1290){
							JOptionPane.showMessageDialog(null, "Image must be of size greater or equals to 1290 x 1290!");
						
						}
						
						else{

							//to perform the automation part from class AnalyseImage
							BufferedImage cropResult = AnalyseImage.analyse(img);
							if( cropResult != null){
								JLabel label = new JLabel(new ImageIcon(cropResult));
								int confirmImage = JOptionPane.showConfirmDialog(null, label, "Are the optic disc and cup drawn correctly?", JOptionPane.YES_NO_OPTION);
					        
					    		p1.removeAll();
					    		panel.removeAll();
								panel.revalidate();
							
								if (confirmImage == JOptionPane.YES_OPTION){
								
									p1.removeAll();
						    		panel.removeAll();
									panel.revalidate();
								
									crop = cropResult;
									areaDisc = AnalyseImage.areaDisc;
									areaCup = AnalyseImage.areaCup;
									areaCDR = AnalyseImage.areaCDR;
									areaRim = AnalyseImage.areaRim;
									verticalCup = AnalyseImage.verticalCup;
									verticalDisc = AnalyseImage.verticalDisc;
									verticalCDR = AnalyseImage.verticalCDR;
									horizontalCup = AnalyseImage.horizontalCup;
									horizontalDisc = AnalyseImage.horizontalDisc;
									horizontalCDR = AnalyseImage.horizontalCDR;
									meanCDR = AnalyseImage.meanCDR;
									rimTI = AnalyseImage.rimTI;
									rimNS = AnalyseImage.rimNS;
									rimIN = AnalyseImage.rimIN;
									rimST = AnalyseImage.rimST;
									cupTI = AnalyseImage.cupTI;
									discTI = AnalyseImage.discTI;
									cupNS = AnalyseImage.cupNS;
									discNS = AnalyseImage.discNS;
									cupIN = AnalyseImage.cupIN;
									discIN = AnalyseImage.discIN;
									cupST = AnalyseImage.cupST;
									discST = AnalyseImage.discST;
									cropX = AnalyseImage.cropX;
									cropY = AnalyseImage.cropY;
									ISNT_rule = AnalyseImage.ISNT_rule;
									resultCDR = AnalyseImage.result;
								
								
									labelImage.setPreferredSize(new Dimension(370,370));
									panel.add(labelImage);
								
									labelImage2.setPreferredSize(new Dimension(370,370));
						  			panel.add(labelImage2);
						  		
						  			labelImage2.setIcon(new ImageIcon(crop));
							
						  			BufferedImage r2d = AnalyseImage.returnDrawImage;
									BufferedImage image = new BufferedImage(r2d.getWidth(),r2d.getHeight(), BufferedImage.TYPE_INT_ARGB);
									Graphics2D g2d = (Graphics2D) image.createGraphics();
									g2d.drawImage(r2d,null,0,0);
									g2d.setColor(Color.BLUE);
									g2d.setStroke(new BasicStroke(9));
									g2d.drawRect(cropX,cropY,600,600);
									g2d.dispose();
									labelImage.setIcon(new ImageIcon(image));
								
									Font fe2 = new Font ("Century Gothic",Font.BOLD,12);
									Font fe = new Font ("Century Gothic",Font.PLAIN,12);
									DecimalFormat df = new DecimalFormat("#.####");
									p1.setLayout(new GridLayout(25,1));
									p1.setBackground(Color.WHITE);
									p1.setOpaque(true);
								
									JLabel jl1 = new JLabel("              All readings are in pixels");
									jl1.setFont(fe2);
									p1.add(jl1);
							
									p1.add(new JLabel(""));
									p1.add(new JLabel(""));
								
									JLabel jl2 = new JLabel("        Area CDR             : " + df.format(areaCDR)+ "                    ");
									jl2.setFont(fe2);
									p1.add(jl2);
									JLabel jl3 = new JLabel("        Area Disc             : " + df.format(areaDisc)+ "                    ");
									jl3.setFont(fe);
									p1.add(jl3);
									JLabel jl4 = new JLabel("        Area Cup             : " + df.format(areaCup)+ "                    ");
									jl4.setFont(fe);
									p1.add(jl4);
									JLabel jl5 = new JLabel("        Area Rim              : " + df.format(areaRim)+ "                    ");
									jl5.setFont(fe);
									p1.add(jl5);
								
									p1.add(new JLabel(""));
						
									JLabel jl17 = new JLabel("        Mean CDR              :" + df.format(meanCDR)+ "                     ");
									jl17.setFont(fe2);
									p1.add(jl17);
									JLabel jl18 = new JLabel("        " + resultCDR);
									jl18.setFont(fe2);
									p1.add(jl18);
								
									p1.add(new JLabel(""));
								
									JLabel jl6 = new JLabel("        Horizontal CDR    : " + df.format(horizontalCDR)+ "                 ");
									jl6.setFont(fe2); 
									p1.add(jl6);
									JLabel jl7 = new JLabel("        Horizontal Cup   : " + df.format(horizontalCup)+ "                 ");
									jl7.setFont(fe);
									p1.add(jl7);
									JLabel jl8 = new JLabel("        Horizontal Disc   : " + df.format(horizontalDisc)+ "                 ");
									jl8.setFont(fe);
									p1.add(jl8);
								
									p1.add(new JLabel(""));
								
									JLabel jl9 = new JLabel("        Vertical CDR        : " + df.format(verticalCDR)+ "                   ");
									jl9.setFont(fe2);
									p1.add(jl9);
									JLabel jl10 = new JLabel("        Vertical Cup       : " + df.format(verticalCup)+ "                   ");
									jl10.setFont(fe);
									p1.add(jl10);
									JLabel jl11 = new JLabel("        Vertical Disc      : " + df.format(verticalDisc)+ "                   ");
									jl11.setFont(fe);
									p1.add(jl11);
								
									p1.add(new JLabel(""));
								
									JLabel jl16 = new JLabel("        " + ISNT_rule);
									jl16.setFont(fe2);
									p1.add(jl16);
									JLabel jl12 = new JLabel("        Inferior Rim          : " + df.format(rimTI)+ "                   ");
									jl12.setFont(fe);
									p1.add(jl12);
									JLabel jl13 = new JLabel("        Superior Rim       : " + df.format(rimNS)+ "                    ");
									jl13.setFont(fe);
									p1.add(jl13);
									JLabel jl14 = new JLabel("        Nasal Rim             : " + df.format(rimIN)+ "                    ");
									jl14.setFont(fe);
									p1.add(jl14);
									JLabel jl15 = new JLabel("        Temporal Rim     : " + df.format(rimST)+ "                    ");
									jl15.setFont(fe);
									p1.add(jl15);
								
									panel.add(p1);
								
								
								}
							
								if (confirmImage == JOptionPane.NO_OPTION){
								
									p1.removeAll();
						    		panel.removeAll();
									panel.revalidate();
									Cropping.main(img);
								
									frmOpticDiscAnalysing.setVisible(false);

								}
							
							}
							
						}
												
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}

			}
		});
		menuOpen.setFont(f);
		mnFile.add(menuOpen);
		
		
		menuSave.setIcon(new ImageIcon(MainWindow.class.getResource("/javax/swing/plaf/metal/icons/ocean/floppy.gif")));
		menuSave.setFont(f);
		mnFile.add(menuSave);
		menuSave.addActionListener(this);
		
		menuGenerateReport.setIcon(new ImageIcon(MainWindow.class.getResource("/javax/swing/plaf/metal/icons/ocean/file.gif")));
		menuGenerateReport.setFont(f);
		mnFile.add(menuGenerateReport);
		menuGenerateReport.addActionListener(this);
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setFont(f);
		mnHelp.setBackground(new Color(0, 0, 204));
		mnHelp.setForeground(new Color(0, 0, 0));
		menuBar.add(mnHelp);
		
		JMenuItem menuAboutUs = new JMenuItem("About Us");
		menuAboutUs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,"This is an Optic Disc Analysing System that performs basic analysis \n of the optic disc and saves it inside patient's profile.", "About Us",JOptionPane.PLAIN_MESSAGE);
			}
		});
		menuAboutUs.setFont(f);
		mnHelp.add(menuAboutUs);
		frmOpticDiscAnalysing.getContentPane().setLayout(null);
		
		JTextPane txtpnProfile = new JTextPane();
		txtpnProfile.setAutoscrolls(false);
		txtpnProfile.setBounds(0, 0, 1050, 145);
		frmOpticDiscAnalysing.getContentPane().add(txtpnProfile);
		txtpnProfile.setAlignmentY(Component.TOP_ALIGNMENT);
		txtpnProfile.setAlignmentX(Component.LEFT_ALIGNMENT);
		txtpnProfile.setEditable(false);
		txtpnProfile.setForeground(new Color(255, 255, 255));
		txtpnProfile.setBackground(new Color(0, 0, 102));
		
		/*retrieving text from user profile database*/
		
		txtpnProfile.setText("Name: " + UserProfile.getName() + "\r\nRef No: "+ UserProfile.getRefNo() + 
				             "\r\nDate Of Birth: " + UserProfile.getDateOfBirth() + "\r\nGender: " + UserProfile.getGender() +
				             "\r\nRace: " + UserProfile.getEthnic());
		txtpnProfile.setFont(new Font("Century Gothic", Font.BOLD, 15));
		
		
		btnSaveResult.addActionListener(this);
		
		btnSaveResult.setBounds(110, 169, 172, 23);
		frmOpticDiscAnalysing.getContentPane().add(btnSaveResult);
		
		
		btnGenerateReport.setBounds(440, 169, 172, 23);
		frmOpticDiscAnalysing.getContentPane().add(btnGenerateReport);
		btnGenerateReport.addActionListener((ActionListener) this);
		
		selectEyeImage.setBackground(UIManager.getColor("Button.light"));
		selectEyeImage.setModel(new DefaultComboBoxModel<>(new String[] {"Select Eye Image", "RightEye", "LeftEye"}));
		selectEyeImage.setBounds(750, 169, 172, 23);
		frmOpticDiscAnalysing.getContentPane().add(selectEyeImage);
		eye = selectEyeImage.getSelectedItem().toString();
		
		panel.setBounds(5, 202, 1050, 408);
		panel.setBorder(new EmptyBorder(0, 0, 5, 5));
		panel.setLayout(new FlowLayout());
		frmOpticDiscAnalysing.getContentPane().add(panel);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		try{
			
			Properties connectionProps = new Properties();
			connectionProps.put("user", Database.userName);
			connectionProps.put("password", Database.password);
			conn = DriverManager.getConnection("jdbc:mysql://" + Database.serverName + ":" + Database.portNumber + "/" + Database.dbName, connectionProps);

			PreparedStatement ps = null;
			eye = selectEyeImage.getSelectedItem().toString();
			
			if (evt.getSource() == btnSaveResult || evt.getSource() == menuSave) {
				
				//to check that it is not null before saving
				//technically the areaCDR must not be null, if it is 0, then wrong result.
				if(AnalyseImage.areaCDR == 0){
					JOptionPane.showMessageDialog(null, "Error! No results to be saved.");
				}
				else{
				//to check if user has selected right or left eye image first before saving into database
				if(!"Select Eye Image".equals(eye)){
					ct++;
					ps = conn.prepareStatement("insert into " + eye + "(REFNO,LASTVISIT,ORIGINALIMAGE,IMAGE,AREACDR,AREACUP,AREADISC,AREARIM,MEANCDR,RESULT,HORIZONTALCDR,HORIZONTALCUP,HORIZONTALDISC,VERTICALCDR,VERTICALCUP,VERTICALDISC,ISNT_RULE,INFERIORRIM,SUPERIORRIM,NASALRIM,TEMPORALRIM) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					
					Date date = new Date();
					SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
					String DateToStr = format.format(date);
					
					File originalImage = new File(Directory.s);
					ImageIO.write(img,"jpg",originalImage);
					String stringOriginalImage = originalImage.getName();
					
					as = "NewImage" + Directory.s;
					File imageFile = new File(as);
					ImageIO.write(crop, "png", imageFile);
					
					 //JLabel label = new JLabel(new ImageIcon(crop));
				    // JOptionPane.showMessageDialog(null, label, "clipped image", JOptionPane.PLAIN_MESSAGE);
					
					ps.setString(1, UserProfile.getRefNo());
					ps.setString(2, DateToStr);
					ps.setString(3,stringOriginalImage);
					ps.setString(4,as);
					ps.setDouble(5, areaCDR);
					ps.setDouble(6, areaCup);
					ps.setDouble(7, areaDisc);
					ps.setDouble(8, areaRim);
					ps.setDouble(9, meanCDR);
					ps.setString(10, resultCDR);
					ps.setDouble(11, horizontalCDR);
					ps.setDouble(12, horizontalCup);
					ps.setDouble(13, horizontalDisc);
					ps.setDouble(14, verticalCDR);
					ps.setDouble(15, verticalCup);
					ps.setDouble(16, verticalDisc);
					ps.setString(17, ISNT_rule);
					ps.setDouble(18, rimTI);
					ps.setDouble(19, rimNS);
					ps.setDouble(20, rimIN);
					ps.setDouble(21, rimST);
					ps.executeUpdate();
					
					JOptionPane.showMessageDialog(null, "Result Saved!");
					conn.close();
				}
				else{
					ct = 0;
	    			JOptionPane.showMessageDialog(null, "Error! Please select eye image if it is left or right eye ");
	    		}
			}
	     
	    	} 
	    
	    	if (evt.getSource() == btnGenerateReport || evt.getSource() == menuGenerateReport){
	    		//to check if user has selected right or left eye image first before saving into database
				if(!"Select Eye Image".equals(eye)){
					if(ct != 0){
		    		//to generate report..
		    		result.GenerateReport.main();
		    		JOptionPane.showMessageDialog(null, "Generated Report");
					}
					else{
						JOptionPane.showMessageDialog(null, "Please save your result first");
					}
				}
				else{
					
	    			JOptionPane.showMessageDialog(null, "Error! Please select eye image if it is left or right eye ");
	    		}
	    		
	    	}
	    
		}catch(Exception e){
			
		}
	    
	  }

	@Override
	public void run() {
		MainWindow.frmOpticDiscAnalysing.setVisible(true);
		MainWindow.frmOpticDiscAnalysing.setSize(1050, 700);
		MainWindow.frmOpticDiscAnalysing.setLocation(10,10);
		MainWindow.frmOpticDiscAnalysing.addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent e){
				int exit = JOptionPane.showConfirmDialog(null, " Do you wish to exit?","",JOptionPane.YES_NO_OPTION);
				if (exit == JOptionPane.YES_OPTION){
					MainWindow.frmOpticDiscAnalysing.dispose();
				}
				
			}
		});
	}
	
}
