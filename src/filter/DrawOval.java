package filter;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;

public class DrawOval extends JComponent
{
	
	// ArrayLists that contain each shape drawn along with
	// that shapes stroke and fill
	
        /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		static ArrayList<Shape> shapes = new ArrayList<Shape>();
        ArrayList<Color> shapeFill = new ArrayList<Color>();
        ArrayList<Color> shapeStroke = new ArrayList<Color>();
        Point drawStart, drawEnd;
        
        public static int drawStartX;
		public static int drawStartY;
		public static int drawEndX;
		public static int drawEndY;
 
     // Going to be used to monitor what shape to draw next
		
     		int currentAction = 1;
     		
     		// Default stroke and fill colors
     		
     		Color strokeColor=Color.BLACK;
        // Monitors events on the drawing area of the frame
        
        public DrawOval()
        {
                this.addMouseListener(new MouseAdapter()
                  {
                    public void mousePressed(MouseEvent e)
                    {
                    	
                    	// When the mouse is pressed get x & y position
                    	
                    	drawStart = new Point(e.getX(), e.getY());
                    	drawEnd = drawStart;
                        repaint();
                        }

                    public void mouseReleased(MouseEvent e)
                        {
                    	
                    	  // Create a shape using the starting x & y
                    	  // and finishing x & y positions
                    	
                          Shape aShape = drawEllipse(
                        		  drawStart.x, drawStart.y,
                                              e.getX(), e.getY());
                          
                          // Add shapes, fills and colors to there ArrayLists
                          
                          shapes.add(aShape);
                         
                          shapeStroke.add(strokeColor);
                          
                          drawStart = null;
                          drawEnd = null;
                          
                          // repaint the drawing area
                          
                          repaint();
                        }
                  } );

                this.addMouseMotionListener(new MouseMotionAdapter()
                {
                  public void mouseDragged(MouseEvent e)
                  {
                	  
                	// Get the final x & y position after the mouse is dragged
                	  
                	drawEnd = new Point(e.getX(), e.getY());
                    repaint();
                  }
                } );
        }
        

        public void paint(Graphics g)
        {
        		// Class used to define the shapes to be drawn
        	
                Graphics2D graphSettings = (Graphics2D)g;

                // Antialiasing cleans up the jagged lines and defines rendering rules
                
                graphSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Defines the line width of the stroke
                
                graphSettings.setStroke(new BasicStroke(1));

                // Iterators created to cycle through strokes and fills
                Iterator<Color> strokeCounter = shapeStroke.iterator();
               
                // Eliminates transparent setting below
                
                graphSettings.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 1.0f));
               
                for (Shape s : shapes)
                {
                	
                //	g.clearRect(0, 0, getWidth(), getHeight() );
                
                	// Grabs the next stroke from the color arraylist
                	graphSettings.setPaint(strokeCounter.next());
                	graphSettings.draw(s);
                }
                
                shapes.clear();
                // Guide shape used for drawing
                if (drawStart != null && drawEnd != null)
                {
                	// Makes the guide shape transparent
                    
                    graphSettings.setComposite(AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER, 0.60f));
                	
                    // Make guide shape gray for professional look
                    
                	graphSettings.setPaint(Color.LIGHT_GRAY);
                	
                	// Create a new rectangle using x & y coordinates
                    Shape aShape = drawEllipse(drawStart.x, drawStart.y, drawEnd.x, drawEnd.y);
                        graphSettings.draw(aShape);
                        
                        setStartResultX(drawStart.x);
                        setStartResultY(drawStart.y);
                        setEndResultX(drawEnd.x);
                        setEndResultY(drawEnd.y);
                }
        }

        public Ellipse2D.Float drawEllipse(
                int x1, int y1, int x2, int y2)
        {
                int x = Math.min(x1, x2);
                int y = Math.min(y1, y2);
                int width = Math.abs(x1 - x2);
                int height = Math.abs(y1 - y2);

                return new Ellipse2D.Float(
                        x, y, width, height);
        }
        
    	void setStartResultX (int drawStartX){
    		DrawOval.drawStartX = drawStartX;
    	}
    	
    	public static int getStartResultX(){
    		return drawStartX;
    	}
        
    	void setStartResultY (int drawStartY){
    		DrawOval.drawStartY = drawStartY;
    	}
    	
    	public static int getStartResultY(){
    		return drawStartY;
    	}
        
    	void setEndResultX (int drawEndX){
    		DrawOval.drawEndX = drawEndX;
    	}
    	
    	public static int getEndResultX(){
    		return drawEndX;
    	}
    	
    	void setEndResultY (int drawEndY){
    		DrawOval.drawEndY = drawEndY;
    	}
    	
    	public static int getEndResultY(){
    		return drawEndY;
    	}
        
}
        