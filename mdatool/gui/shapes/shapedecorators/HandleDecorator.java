package mdatool.gui.shapes.shapedecorators;

// Handle

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class HandleDecorator
{
	Rectangle boundRect;
	int x, y, w, h;
	int left, right, top, bottom;
	Color lineColor = Color.red;
	Color fillColor = Color.red;
	Color highlightColor = Color.yellow;
	public int dragStartX, dragStartY; 
	boolean filled = false;
	boolean selected = false; 
	boolean highlighted = false;
	static final int LEFTTOP = 0;
	static final int LEFTBOTTOM = 1;
	static final int RIGHTBOTTOM = 2;
	static final int RIGHTTOP = 3;
	int corner;
    
	public HandleDecorator() {}

	public HandleDecorator(int x1, int y1, int w1, int h1) 
	{
		move(x1 - w1/2, y1 - h1/2); 
		size(w1, h1);
	}

	public HandleDecorator(int x1, int y1, int w1, int h1, Color lineColor) 
	{
		move(x1 - w1/2, y1 - h1/2);
		size(w1, h1);
		setLineColor(lineColor);
	}

	   public HandleDecorator(int x1, int y1, int w1, int h1, Color lineColor, 
		Color fillColor) 
	{
		move(x1 - w1/2, y1 - h1/2);
		size(w1, h1);
		setLineColor(lineColor);
		setFillColor(fillColor);
		filled = (fillColor != null);
	}

	public HandleDecorator(int x1, int y1, int w1, int h1, Color lineColor, 
		Color fillColor, int corner) 
	{
		move(x1 - w1/2, y1 - h1/2);
		size(w1, h1);
		setLineColor(lineColor);
		setFillColor(fillColor);
		filled = (fillColor != null);
		this.corner = corner;
	}
    
	public void move(int newX, int newY)
	{
		x = left = newX - w/2;
		y = top = newY - h/2;
		right = left + w;
		bottom = top + h;
		boundRect = new Rectangle (x, y, w, h);
	}

	public void size(int width, int height)
	{
		w = width;
		h = height;
		right = left + width;
		bottom = top + height;
		boundRect = new Rectangle (x, y, w, h);
	}

	public void setLineColor( Color c )
	{
		lineColor = c;
	}

	public void setFillColor( Color c )
	{
		fillColor = c;
	}

	public void setHighlightColor( Color c )
	{
		highlightColor = c;
	}

	public void setFilled( boolean fill )
	{
		filled = fill;
	}

	// declare these simple get methods final 
	// so they can be inlined by compiler for speed
	// final methods can't be overridden by subclasses,
	// so there is no dynamic method lookup
	public final int getX()
	{
		return x;
	}

	public final int getY()
	{
		return y;
	}

	public final int getWidth()
	{
		return w;
	}

	public final int getHeight()
	{
		return h;
	}

	public final Rectangle bounds()
	{
		return boundRect;
	}

	public final boolean getFilled()
	{
		return filled;
	}

	public final boolean inside(int testX, int testY)
	{
		return boundRect.contains(testX, testY);
	}

	public void highlight( boolean state )
	{
		highlighted = state;
	}
    
	public boolean select( MouseEvent e, boolean select )
	{
		if (!select)
			selected = false;
		else if (e.isShiftDown())
			selected = !selected;
		else
			selected = select;

		return selected;
	}

	public final int getCorner()
	{
		return corner;
	}

	public void draw(Graphics g)
	{
		if (filled)
		{    
			g.setColor(fillColor); 
			g.fillRect(x, y, w, h);
		}
			g.setColor(lineColor);
			g.drawRect(x, y, w, h);
		if (highlighted)
		{
			g.setColor(highlightColor);
			g.drawRect(x, y, w, h);
		}
	}

}
