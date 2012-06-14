package mdatool.gui.shapes.basic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyChangeEvent;

import javax.swing.JPopupMenu;

public class OvalShape extends Shape {
	
	
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public OvalShape(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	public void draw(Graphics g) {
	  //g.setColor(getColor());
	  g.fillOval(getX(),getY(),getWidth(),getHeight());
	  g.setColor(Color.black);
	  g.drawOval(getX(),getY(),getWidth(),getHeight());
   }
   public boolean containsPoint(int x, int y) {
		 // Check whether (x,y) is inside this oval, using the
		 // mathematical equation of an ellipse.
	  double rx = getWidth()/2.0;   // horizontal radius of ellipse
	  double ry = getHeight()/2.0;  // vertical radius of ellipse 
	  double cx = getX() + rx;   // x-coord of center of ellipse
	  double cy = getY() + ry;    // y-coord of center of ellipse
	  if ( (ry*(x-cx))*(ry*(x-cx)) + (rx*(y-cy))*(rx*(y-cy)) <= rx*rx*ry*ry )
		 return true;
	  else
		return false;
   }
/* (non-Javadoc)
 * @see mdatool.gui.shapes.Shape#drawSelected(java.awt.Graphics)
 */
public void drawSelected(Graphics g) {}
/* (non-Javadoc)
 * @see mdatool.gui.shapes.Shape#handleDoubleClick(java.awt.Point)
 */
public void handleDoubleClick(Point p) {}
/* (non-Javadoc)
 * @see mdatool.gui.shapes.Shape#getContextMenu(java.awt.Point)
 */
public JPopupMenu getContextMenu(Point p) {
	return null;
}

/* (non-Javadoc)
 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
 */
public void propertyChange(PropertyChangeEvent arg0) {}

/* (non-Javadoc)
 * @see mdatool.gui.shapes.basic.Shape#handleSingleClick(java.awt.Point)
 */
public void handleSingleClick(Point p) {
}



   
}
