package mdatool.gui.shapes.basic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyChangeEvent;

import javax.swing.JPopupMenu;


public class RoundRectShape extends Shape {
	   /**
	 * @param x1
	 * @param y1
	 * @param w1
	 * @param h1
	 * @param color
	 * @param color2
	 */
	public RoundRectShape(int x1, int y1, int w1, int h1, Color color, Color color2) {
		super(x1, y1, w1, h1, color, color2);
	}
	

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public RoundRectShape(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	// This class represents rectangle shapes with rounded corners.
	   // (Note that it uses the inherited version of the 
	   // containsPoint(x,y) method, even though that is not perfectly
	   // accurate when (x,y) is near one of the corners.)
	public void draw(Graphics g) {
	 // g.setColor(getColor());
	  g.fillRoundRect(getX(),getY(),getWidth(),getHeight(),getWidth()/3,getHeight()/3);
	  g.setColor(Color.black);
	  g.drawRoundRect(getX(),getY(),getWidth(),getHeight(),getWidth()/3,getHeight()/3);
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