package mdatool.gui.shapes.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JPopupMenu;

import mdatool.gui.shapes.shapedecorators.HandleDecorator;

public abstract class Shape implements PropertyChangeListener {

	// A class representing shapes that can be displayed on a ShapeCanvas.

	private int id;
	private boolean isSelected;
	private ArrayList shapes = new ArrayList();
	
	protected PropertyChangeSupport _beanSupport;
	//private ArrayList _listeners;

	public Color lineColor = Color.black;
	public Color fillColor = Color.blue;
	public Color highlightColor = Color.yellow;
	public Color textColor = Color.white;

	protected boolean filled = false;
	protected boolean selected = false;
	protected boolean highlighted = false;

	private Rectangle boundRect;
	private ArrayList handles;
	private HandleDecorator pickedHandleDecorator = null;
	private static final int LEFTTOP = 0;
	private static final int LEFTBOTTOM = 1;
	private static final int RIGHTBOTTOM = 2;
	private static final int RIGHTTOP = 3;

	private int left, right, top, bottom;
	public int x, y, w, h;
	
	public int dragStartX, dragStartY; 

	public Shape(int x, int y, int width, int height) {
		_beanSupport = new PropertyChangeSupport(this);
		initPropertyChangeSupport();
		createHandleDecorators();
		move(x, y);
		size(width, height);
		boundRect = new Rectangle(x, y, w, h);
	}
	public Shape(int x, int y, int width, int height, Color lineColor) {
		_beanSupport = new PropertyChangeSupport(this);
		initPropertyChangeSupport();
		createHandleDecorators();
		move(x, y);
		size(width, height);
		setLineColor(lineColor);
		boundRect = new Rectangle(x, y, w, h);
	}

	public Shape(int x, int y, int width, int height, Color lineColor, Color fillColor) {
		_beanSupport = new PropertyChangeSupport(this);
		initPropertyChangeSupport();
		createHandleDecorators();
		move(x, y);
		size(width, height);
		setLineColor(lineColor);
		setFillColor(fillColor);
		filled = (fillColor != null);
		boundRect = new Rectangle(x, y, w, h);
	}

	public void move(int newX, int newY) {
		x = left = newX;
		y = top = newY;
		right = left + w;
		bottom = top + h;
		boundRect = new Rectangle(x, y, w, h);
		updateHandleDecorators();
	}
	
	private void initPropertyChangeSupport(){
		//_listeners = new ArrayList();
		//this._beanSupport = new PropertyChangeSupport(this);
	}
	
	public void setLineColor(Color c) {
		lineColor = c;
	}
	
	public void setTextColor(Color c){
		this.textColor = c;
	}

	public void setFillColor(Color c) {
		fillColor = c;
	}

	public void setHighlightColor(Color c) {
		highlightColor = c;
	}

	public void setFilled(boolean fill) {
		filled = fill;
	}
	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	public final int getWidth() {
		return w;
	}

	public final int getHeight() {
		return h;
	}


	public final boolean getFilled() {
		return filled;
	}
	public void highlight(boolean state) {
		highlighted = state;
	}
	

	public boolean containsPoint(int x, int y) {
		return bounds().contains(new Point(x,y));
		//return boundRect.contains(x, y);
	}

	public abstract void draw(Graphics g);
	//public abstract void drawSelected(Graphics g);
	public abstract void handleDoubleClick(Point p);
	public abstract void handleSingleClick(Point p);
	public abstract JPopupMenu getContextMenu(Point p);
	public void addShape(Shape s) {
		shapes.add(s);
	}
	public ArrayList getShapes() {
		return shapes;
	}

	public void setWidth(int width) {
		this._beanSupport.firePropertyChange("width",w, width);
		this.w = width;
	}
	
	public void setHeight(int width) {
		this._beanSupport.firePropertyChange("height",w, width);
		this.h = width;
	}


	public void size(int width, int height) {
		this._beanSupport.firePropertyChange("size",new Dimension(w,h), new Dimension(width,height));
		w = width;
		h = height;
		right = left + width;
		bottom = top + height;
		boundRect = new Rectangle(x, y, w, h);
		updateHandleDecorators();
	
	}

	public final Rectangle bounds() {
		Rectangle r = new Rectangle(x,y,w,h);
		return r;
		//return boundRect;
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param i
	 */
	public void setId(int i) {
		id = i;
	}

	/**************************
	 * 
	 * 		H A N D L E S
	 * 
	 * 
	 * ************************/
	public boolean select(MouseEvent e, boolean select) {
		if (!select)
			selected = false;
		else if (e.isShiftDown())
			selected = !selected;
		else
			selected = select;

		return selected;
	}

	void createHandleDecorators() {
		handles = new ArrayList(4);

		handles.add(createHandleDecorator(left, top, LEFTTOP));
		handles.add(createHandleDecorator(left, bottom, LEFTBOTTOM));
		handles.add(createHandleDecorator(right, bottom, RIGHTBOTTOM));
		handles.add(createHandleDecorator(right, top, RIGHTTOP));
	}

	HandleDecorator createHandleDecorator(int x, int y, int corner) {
		HandleDecorator HandleDecorator =
			new HandleDecorator(x, y, 5, 5, this.lineColor, this.fillColor, corner);
		return HandleDecorator;
	}

	void updateHandleDecorators() {

		HandleDecorator handleDecorator;

		Iterator iter = handles.iterator();

		handleDecorator = (HandleDecorator) iter.next();
		handleDecorator.move(left, top);

		handleDecorator = (HandleDecorator) iter.next();
		handleDecorator.move(left, bottom);

		handleDecorator = (HandleDecorator) iter.next();
		handleDecorator.move(right, bottom);

		handleDecorator = (HandleDecorator) iter.next();
		handleDecorator.move(right, top);
	}

	public void moveHandleDecorator(int x1, int y1) {
		int newX = 0, newY = 0, newW = 0, newH = 0;

		if (pickedHandleDecorator != null) {
			switch (pickedHandleDecorator.getCorner()) {
				case LEFTTOP :
					newX = x1;
					newW = right - x1;
					newY = y1;
					newH = bottom - y1;
					break;

				case LEFTBOTTOM :
					newX = x1;
					newW = right - x1;
					newY = top;
					newH = y1 - top;
					break;

				case RIGHTBOTTOM :
					newX = left;
					newW = x1 - left;
					newY = top;
					newH = y1 - top;
					break;

				case RIGHTTOP :
					newX = left;
					newW = x1 - left;
					newY = y1;
					newH = bottom - y1;
					break;
			}
			size(newW, newH);
			move(newX, newY);
		}
	}

	public void drawHandleDecorators(int x1, int y1, int w1, int h1, Graphics g) {
		g.setColor(this.lineColor);
		g.drawRect(x, y, w, h);

		for (Iterator iter = handles.iterator(); iter.hasNext();) {
			HandleDecorator element = (HandleDecorator) iter.next();
			element.draw(g);
		}
	}

	public boolean selectHandleDecorators(MouseEvent e, int x, int y) {
		boolean HandleDecoratorSelected = false;
		for (int i = 0; i < handles.size(); i++) {
			HandleDecorator HandleDecorator = (HandleDecorator) handles.get(i);
			if (HandleDecorator.inside(x, y)) {
				HandleDecoratorSelected = true;
				pickedHandleDecorator = HandleDecorator;
				HandleDecorator.select(e, true);
			} else
				HandleDecorator.select(e, false);
		}
		return HandleDecoratorSelected;
	}
	
//******** Bean methods ****************
public void addPropertyChangeListener(PropertyChangeListener l) {
	_beanSupport.addPropertyChangeListener(l);
}

public void removePropertyChangeListener(PropertyChangeListener l) {
	_beanSupport.removePropertyChangeListener(l);
}

} // end of class Shape
