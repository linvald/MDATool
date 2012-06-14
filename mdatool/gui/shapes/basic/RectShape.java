package mdatool.gui.shapes.basic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyChangeEvent;

import javax.swing.JPopupMenu;

public class  RectShape extends Shape {
	  /**
	 * @param x1
	 * @param y1
	 * @param w1
	 * @param h1
	 * @param color
	 * @param color2
	 */
	public RectShape(int x1, int y1, int w1, int h1, Color color, Color color2) {
		super(x1, y1, w1, h1, color, color2);
	}

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public RectShape(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	// This class represents rectangle shapes.
	public void draw(Graphics g) {
		/* g.setColor(getColor());
		 g.fillRect(getLeftPosition(),getTopPosition(),getWidth(),getHeight());
		 g.setColor(Color.black);
		 g.drawRect(getLeftPosition(),getTopPosition(),getWidth(),getHeight());*/
		if (filled) {
			if (highlighted){
				g.setColor(fillColor.brighter());
			}
				
			else{
				g.setColor(fillColor);
			}
				
			g.fillRect(x, y, w, h);
		}
		if (selected)
			drawHandleDecorators(x, y, w, h, g);
		else {
			if (highlighted)
				g.setColor(lineColor.brighter());
			else
				g.setColor(lineColor);
			g.drawRect(x, y, w, h);
		}
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