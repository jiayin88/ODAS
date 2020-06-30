package opticDiscAnalyser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;

import javax.swing.*;
  
public class Cropping extends JLabel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BufferedImage image;
    public Dimension size;
    public Rectangle clip;
    public boolean showClip;
	public static BufferedImage clipped;
    static JFrame f = new JFrame();
	static JScrollPane p = new JScrollPane();
	public static int xResult;
	public static String eyeImage;
	static int x;
    static int y;
	
    public Cropping(BufferedImage image)
    {
        this.image = image;
        
        size = new Dimension(image.getWidth(), image.getHeight());
        showClip = true;
    }
  
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = (getWidth() - size.width)/2;
        int y = (getHeight() - size.height)/2;
        g2.drawImage(image, x, y, this);
        if(showClip)
        {
            if(clip == null)
                createClip();
            g2.setPaint(Color.blue);
            g2.setStroke(new BasicStroke(3));
            g2.draw(clip);
        }
    }
  
    public void setClip(int x, int y)
    {
        // keep clip within raster
        int x0 = 0;
        int y0 = 0;
        if(x < x0 || x + clip.width  > x0 + size.width || y < y0 || y + clip.height > y0 + size.height)
            return;
        clip.setLocation(x, y);
        repaint();
    }
  
    public Dimension getPreferredSize()
    {
        return size;
    }
  
    public void createClip()
    {
    	double w = 600;
        double h = 600;
        clip = new Rectangle((int)w,(int)h);
        clip.x = (int)w;
        clip.y = (int)h;
    }
  
	public void clipImage()
    {
        try
        {
        	double w = 600;
            double h = 600;
            int x0 = 0;
            int y0 = 0;
            x = clip.x - x0;
            y = clip.y - y0;
            clipped = image.getSubimage(x, y,(int)w, (int)h);
            
            //to save the x coordinate to identify if it is left or right eye
            xResult = x;
        }
        catch(RasterFormatException rfe)
        {
           // System.out.println("raster format error: " + rfe.getMessage());
           // return;
        }
        
        //to display to user that this is left or right eye image
        if (xResult <= (image.getWidth()/2)){
        	eyeImage = "Left eye image";
        }
        else{
        	eyeImage = "Right eye image";
        }
        
        int confirmImage = JOptionPane.showConfirmDialog(null, eyeImage + "\nIs this correct?","",JOptionPane.YES_NO_OPTION);
		if (confirmImage == JOptionPane.YES_OPTION){
			ManualDetection.main(clipped);
			setImage(clipped);
			
			//check it is either dispose or setVisible(false)
			f.dispose();		
		}
    }
    
    void setImage (BufferedImage img){
		Cropping.clipped = img;
	}
  
    static BufferedImage getImage(){
		return clipped;
	}
    
    JPanel getUIPanel()
    {
      
        JButton clip = new JButton("clip image");
        clip.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                clipImage();
               
            }
        });
        JPanel panel = new JPanel();
        panel.add(clip);
        return panel;
    }

    
    public static void main(BufferedImage image) 
    {
    	 Cropping test = new Cropping(image);
	        ClipMover mover = new ClipMover(test);
	        test.addMouseListener(mover);
	        test.addMouseMotionListener(mover);
	 
	        f.getContentPane().removeAll();
	        f.getContentPane().revalidate();
	        p = new JScrollPane(test);
	        
	        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        f.getContentPane().add(test.getUIPanel(), "South");
	        f.getContentPane().add(p);
	        f.setSize(700,700);
	        f.setLocation(0,0);
	        f.setVisible(true);
    }
    
  //return the x coordinate
  	static int xCoordinate(){
  		return x;
  	}

  	//return the y coordinate
  	static int yCoordinate(){
  		return y;
  	}

}
  