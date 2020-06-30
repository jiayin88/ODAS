package userProfile;

// still haven't add action listener and also the search engine because have yet to create a database
//please create a database!

import opticDiscAnalyser.*;

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Panel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class UserProfile {

	public JFrame frmUserProfile;
	public JTextField txtSearchProfile;
	public JTextField txtName;
	public JTextField txtRefNo;
	public JTextField txtEthnic;
	public JLabel lblCreateNewUser;
	public JButton btnSearch,btnCreateProfile;
	
	public static String name;
	public static String refNo;
	public static String dateOfBirth;
	public static String gender;
	public static String ethnic;
	private JTextField txtDOB;
	private JTextField txtGender;
	
	public static String searchProfile;
	Connection conn = null; 
	ResultSet rs = null;
	PreparedStatement ps = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserProfile window = new UserProfile();
					window.frmUserProfile.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UserProfile() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize(){
		frmUserProfile = new JFrame();
		frmUserProfile.setResizable(false);
		frmUserProfile.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		frmUserProfile.setTitle("User Profile");
		frmUserProfile.setBackground(new Color(0, 0, 128));
		frmUserProfile.setBounds(100, 100, 436, 285);
		frmUserProfile.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmUserProfile.getContentPane().setLayout(null);
		
		Font f = new Font("Century Gothic",Font.PLAIN,12);
		UIManager.put("OptionPane.messageFont", f);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 434, 262);
		frmUserProfile.getContentPane().add(scrollPane);
		
		Panel panel = new Panel();
		panel.setForeground(new Color(0, 0, 0));
		panel.setBackground(new Color(245, 245, 245));
		scrollPane.setViewportView(panel);
		panel.setLayout(null);
		
		//displaying default text as Ref no. and set column to 14
		//user will have to insert either ic no or passport no (their ref no)
		txtSearchProfile = new JTextField("Ref no",14);
		txtSearchProfile.setToolTipText("Enter your Ref no. here");
		txtSearchProfile.setBounds(124, 10, 201, 20);
		panel.add(txtSearchProfile);
		
		JLabel lblSearchProfile = new JLabel("Search Profile :");
		lblSearchProfile.setBounds(27, -5, 200, 50);
		panel.add(lblSearchProfile);
		
		//btnSearch is to search user profile
		JButton btnSearch = new JButton("Search");
		//perform action to it.
		// need to check it against database. take caution! haven't done yet
		
		btnSearch.addActionListener(new ActionListener() {
			@Override 
			public void actionPerformed(ActionEvent e) {
				
				searchFunction f = new searchFunction();
				searchProfile = txtSearchProfile.getText();
				ResultSet rs = null;
				rs = f.search(searchProfile);
				try{
					 if(rs.next()){
						 int reply = JOptionPane.showConfirmDialog(null,"This profile exist.\n" + "Do you wish to proceed?","Confirmation", JOptionPane.YES_NO_OPTION);
							
							if (reply == JOptionPane.YES_OPTION){
								//link it to the MainWindow
								
								setName(rs.getString("name"));
								setRefNo(rs.getString("refNo"));
								setDateOfBirth(rs.getString("dateOfBirth"));
								setGender(rs.getString("gender"));
								setEthnic(rs.getString("ethnic"));
								
								try {
									MainWindow.main(null);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
						}
					else{
							JOptionPane.showMessageDialog(null,"Sorry, this profile does not exist.");
					}
				}catch (Exception ex){
					
				}
			}	
		});
		btnSearch.setBounds(333, 9, 89, 23);
		panel.add(btnSearch);
		

		lblCreateNewUser = new JLabel("Create New User");
		lblCreateNewUser.setFont(new Font("Century Gothic", Font.BOLD, 15));
		lblCreateNewUser.setBounds(10, 41, 200, 35);
		panel.add(lblCreateNewUser);
		
		JLabel lblName = new JLabel("Name                 :");
		lblName.setVerticalAlignment(SwingConstants.TOP);
		lblName.setBounds(27, 76, 200, 50);
		panel.add(lblName);
		
		JLabel lblRefNo = new JLabel("Ref no.               :");
		lblRefNo.setVerticalAlignment(SwingConstants.TOP);
		lblRefNo.setBounds(27, 104, 200, 50);
		panel.add(lblRefNo);
		
		JLabel lblDateOfBirth = new JLabel("Date of Birth     :");
		lblDateOfBirth.setVerticalAlignment(SwingConstants.TOP);
		lblDateOfBirth.setBounds(27, 132, 200, 50);
		panel.add(lblDateOfBirth);
		
		JLabel lblGender = new JLabel("Gender              :");
		lblGender.setVerticalAlignment(SwingConstants.TOP);
		lblGender.setBounds(27, 160, 200, 50);
		panel.add(lblGender);
		
		JLabel lblRace = new JLabel("Race                  :");
		lblRace.setVerticalAlignment(SwingConstants.TOP);
		lblRace.setBounds(27, 188, 200, 50);
		panel.add(lblRace);
		
		txtName = new JTextField("Name",50);
		txtName.setBounds(125, 75, 200, 20);
		panel.add(txtName);
		
		txtRefNo = new JTextField("IC no/ Passport no",14);
		txtRefNo.setBounds(125, 103, 200, 20);
		panel.add(txtRefNo);
		
		txtEthnic = new JTextField("Ethnic",10);
		txtEthnic.setBounds(125, 190, 200, 20);
		panel.add(txtEthnic);
		
		txtDOB = new JTextField("yyyy/mm/dd", 10);
		txtDOB.setBounds(124, 134, 200, 20);
		panel.add(txtDOB);
		
		txtGender = new JTextField("Female/Male", 7);
		txtGender.setBounds(124, 162, 200, 20);
		panel.add(txtGender);
		
		//CreateProfile button to create new profile
		JButton btnCreateProfile = new JButton("Create Profile");
		btnCreateProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//still havent include if profile is wrong.
				//if all information is correct
				name = txtName.getText();
				refNo = txtRefNo.getText();
				ethnic = txtEthnic.getText();
				gender = txtGender.getText();
				dateOfBirth = txtDOB.getText();
				
				//if it does not meet any of the criteria mentioned below
				if(name.length()>40 || refNo.length()>12 || ethnic.length()>10 || gender.length()>6 ||dateOfBirth.length()>10){
					JOptionPane.showMessageDialog(null, "Please enter correct input.Length must be:\n" +
							"Name in 40 letters (Example: Aishah)\nRefNo in 14 numbers/letters (Example: 861224085128)" + 
							"\nEthnic in 10 letters (Example: Malay)\nGender in 6 letters (Example: Female)\n" +
							"DateOfBirth in yyyy/mm/dd format (Exampel: 1986/12/24)");
				}
				else{
					
					setName(name);
					setRefNo(refNo);
					setEthnic(ethnic);
					setGender(gender);
					setDateOfBirth(dateOfBirth);
				
					int reply = JOptionPane.showConfirmDialog(null,"Is this correct?\n\n" + 
						"Name: "+ getName() + "\n" +
						"Ref: " + getRefNo() + "\n" + 
						"Race: " + getEthnic() + "\n" + 
						"Gender: " + getGender() + "\n" +
						"Date Of Birth: " + getDateOfBirth() + "\n\n" + "Do you wish to proceed?","Confirmation", JOptionPane.YES_NO_OPTION);
			
					if (reply == JOptionPane.YES_OPTION){
						//link it to the MainWindow
					
						try {
							Properties connectionProps = new Properties();
							connectionProps.put("user", Database.userName);
							connectionProps.put("password", Database.password);
							conn = DriverManager.getConnection("jdbc:mysql://" + Database.serverName + ":" + Database.portNumber + "/" + Database.dbName, connectionProps);
							Statement stmt = conn.createStatement();
							stmt.executeUpdate("INSERT INTO USERPROFILE VALUES ('" + refNo + "','" + name + "','" + dateOfBirth + "','" + gender + "','" + ethnic + "');");
							
							
							MainWindow.main(null);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
					
			}
		});
		btnCreateProfile.setBounds(135, 222, 177, 23);
		panel.add(btnCreateProfile);
	}
	
	public class searchFunction{

		public ResultSet search(String s){
			try{
				Properties connectionProps = new Properties();
				connectionProps.put("user", Database.userName);
				connectionProps.put("password", Database.password);
				conn = DriverManager.getConnection("jdbc:mysql://" + Database.serverName + ":" + Database.portNumber + "/" + Database.dbName, connectionProps);
				
				ps = conn.prepareStatement("SELECT * FROM USERPROFILE WHERE REFNO = ?");
				ps.setString(1,s);
				rs = ps.executeQuery();
						
			}catch(Exception e){
				
			}
			return rs;	
		}
	}
	
	//use the get and set method to manipulate the user profile
		void setName (String name){
			UserProfile.name = name;
		}
		
		public static String getName(){
			return name;
		}
	
		void setRefNo (String refNo){
			UserProfile.refNo = refNo;
		}
		
		public static String getRefNo(){
			return refNo;
		}
	
		void setDateOfBirth (String dateOfBirth){
			UserProfile.dateOfBirth = dateOfBirth;
		}
		
		public static String getDateOfBirth(){
			return dateOfBirth;
		}
		
		void setGender (String gender){
			UserProfile.gender = gender;
		}
		
		public static String getGender(){
			return gender;
		}
		
		void setEthnic (String ethnic){
			UserProfile.ethnic = ethnic;
		}
		
		public static String getEthnic(){
			return ethnic;
		}
}