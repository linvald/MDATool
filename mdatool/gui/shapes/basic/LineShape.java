package mdatool.gui.shapes.basic;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;

import javax.swing.JPopupMenu;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class LineShape extends Shape {
	
	private Point startPoint, endPoint;
	private float lineThickness;
	private Line2D line;

/*	public LineShape(Point start){
		this.setStartPoint(start);	
	}*/
	
	public LineShape(int x,int y,int w,int h, Point p){
		super(x,y,w,h);
		this.setStartPoint(p);
	}
	
	/**
	 * @param start
	 */
	private void setStartPoint(Point start) {
		this.startPoint = start;
	}
	
	/**
	 * @param end
	 */
	public void setEndPoint(Point end) {
		this.endPoint = end;
		this.line = new Line2D.Double(startPoint, endPoint);
	}
	
	public LineShape(int x,int y,int w,int h,Point start, Point end){
		super(x,y,w,h);
		this.setStartPoint(start);	
		this.setEndPoint(end);
	}
	

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#draw(java.awt.Graphics)
	 */
	public void draw(Graphics g) {
		if(this.startPoint != null && this.endPoint != null){
			//g.setColor(getFillColor());
			g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
		}
		if(this.isSelected()){
			this.drawSelected(g);
		}
	}

	public boolean containsPoint(int x, int y) {
		return line.getBounds().contains(x,y);
			/*	Rectangle _bounds = new Rectangle();
				_bounds.x = Math.min(this.startPoint.x, this.endPoint.x);
				_bounds.y = Math.min(this.startPoint.y, this.endPoint.y);
				_bounds.width = Math.abs(this.endPoint.x - this.startPoint.x);
				_bounds.height = Math.abs(this.endPoint.y - this.startPoint.y);
				return _bounds.contains(x,y) ? true : false;*/
	}

	/**
	 * @return
	 */
	public Point getEndPoint() {
		return endPoint;
	}

	/**
	 * @return
	 */
	public float getLineThickness() {
		return lineThickness;
	}

	/**
	 * @return
	 */
	public Point getStartPoint() {
		return startPoint;
	}

	/**
	 * @param f
	 */
	public void setLineThickness(float f) {
		lineThickness = f;
	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#setLeftPosition(int)
	 */
	public void setLeftPosition(int left) {
		this.startPoint.x = left;
		this.endPoint.x = this.endPoint.x+left;
	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#setTopPosition(int)
	 */
	public void setTopPosition(int top) {
		this.startPoint.y = top;
		this.endPoint.y = this.endPoint.y + top; 
	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#moveBy(int, int)
	 */
	public void moveBy(int dx, int dy) {
		this.startPoint.x = this.startPoint.x+dx;
		this.endPoint.x = this.endPoint.x+dx;
		this.startPoint.y = this.startPoint.y + dy;
		this.endPoint.y = this.endPoint.y + dy;
		this.line.setLine(new Line2D.Double(startPoint.x, startPoint.y, endPoint.x, endPoint.y));
	}
	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#drawSelected(java.awt.Graphics)
	 */
	public void drawSelected(Graphics g) {
		Rectangle r = line.getBounds();
		g.drawRect(r.x,r.y,r.width,r.height);
	}
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
