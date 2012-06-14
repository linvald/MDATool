package mdatool.gui.canvas;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.applet.Applet;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import mdatool.gui.application.Main;
import mdatool.gui.shapes.*;
import mdatool.gui.shapes.basic.Shape;
import mdatool.gui.shapes.basic.*;
import mdatool.gui.shapes.uml.UmlShape;
import mdatool.mof.modelwrappers.MOFModelWrapper;
import mdatool.mof.repository.RepositoryManager;
import mdatool.mof.uml.UMLElement;

public class ShapeCanvas extends JPanel implements 
												   MouseListener, 
												   MouseMotionListener, 
												   PropertyChangeListener{
												 //  Scrollable {

	// This class represents a canvas that can display colored shapes and
	// let the user drag them around.  It uses an off-screen images to 
	// make the dragging look as smooth as possible.
	
	private int _zoomLevel = 1; //100%
	private int id;
	public static int canvasCount;
	private JPopupMenu _contextMenu;
	BufferedImage offScreenCanvas = null; // off-screen image used for double buffering
	Graphics offScreenGraphics; // graphics context for drawing to offScreenCanvas
	private Dimension area; //indicates area taken up by graphics
	ArrayList shapes = new ArrayList();
	Color currentColor = Color.red; // current color; when a shape is created, this is its color
	private int canvasWidth, canvasHeight = 500;
	JMenuItem _save;
	private boolean formingNewShape = false;
	private boolean reshaping = false;
	private int downX, downY;
	
	private Shape pickedShape = null;
	private Shape oldPickedShape = null;
	private Shape highlightedShape = null;
	private int step = 12;
	private int maxUnitIncrement = 5;
	private ICanvasDecorator _decorator;
	private MOFModelWrapper _umlWrapper;
	
	//private Repository _repository;
	
	public ShapeCanvas() {
		super(true);
		initCanvas();
		
	}

	/**
	 * 
	 */
	private void initDecorator() {
		//TODO: get preferred Decorator
		_decorator = new CheckerDecorator(); 
		_decorator.setDistance(Main.getInstance().getUserPrefs().getCanvasGridDistance());
	}

	public ShapeCanvas(int canvasWidth, int canvasHeight) {
	
		super(true);
		//this.setBounds(0,0,canvasWidth, canvasHeight);
		//this.setBackground(Color.yellow);
		initCanvas();
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.setSize(canvasWidth, canvasHeight);
		this.setDoubleBuffered(true);
		setPreferredSize(new Dimension(canvasWidth,canvasHeight));
		//this.doLayout();
	}

	public static int getCanvasNewNumber() {
		return ShapeCanvas.canvasCount;
	}
	
	private void initCanvas() {
		setBackground(Color.lightGray.brighter());
		addMouseListener(this);
		addMouseMotionListener(this);
		addPropertyChangeListener(this);
		setAutoscrolls(true);
	
		this.setupContextMenu();
		ShapeCanvas.canvasCount++;
		this.id = canvasCount++;
		initDecorator();
	}
	

	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g2 = (Graphics2D) gr;
		Rectangle c = Main.getInstance().getActiveInternalFrame().getVisibleRect();
		//g2.setClip(c.x,c.y,c.width,c.height);
		/*if (offScreenCanvas == null) {
			offScreenCanvas =
				new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
		}*/
		this._decorator.decorate(g2);
		//offScreenGraphics = (Graphics2D) offScreenCanvas.getGraphics();
		for (Iterator iter = shapes.iterator(); iter.hasNext();) {
			Shape shape = (Shape) iter.next();
			shape.draw(g2);
		}
		

	}

	public void paint(Graphics g){
		paintComponent(g);
	}

	void makeOffScreenCanvas(Graphics g) {
		if(offScreenCanvas==null){
			offScreenCanvas = new BufferedImage(canvasWidth,canvasHeight,BufferedImage.TYPE_INT_RGB);
		}	
		offScreenGraphics = (Graphics2D)offScreenCanvas.getGraphics();	

		for (Iterator iter = shapes.iterator(); iter.hasNext();) {
			Shape shape = (Shape) iter.next();
				shape.draw(offScreenGraphics);
		}
	}

	public void repaintShape(Shape s){
		for (Iterator iter = shapes.iterator(); iter.hasNext();) {
					Shape shape = (Shape) iter.next();
					if(shape.equals(s))
						shape.draw(this.getGraphics());
		}
	}
	
	public void replaceDrawing(ArrayList newDrawing) {
		this.shapes = newDrawing;
	}

	public synchronized void addShape(Shape shape) {
		shape.addPropertyChangeListener(this);
	
		if(shape.getX()+shape.getWidth()>canvasWidth){
			this.setCanvasWidth(shape.getX()+shape.getWidth());
			System.out.println("Resizing because shape.x > canvas.width:" + canvasWidth);
		}
		if(shape.getY()>canvasHeight){
			this.setCanvasHeight(shape.getY()+shape.getHeight());
			System.out.println("Resizing because shape.y > canvas.height:"+canvasHeight);
		}
		shapes.add(shape);
		doLayout();
		repaint();
		
	}
	
	public UmlShape getUmlShapeByName(String name){
		for (Iterator iter = shapes.iterator(); iter.hasNext();) {
					Shape shape = (Shape) iter.next();
					if(shape instanceof UmlShape){
						UmlShape uml = (UmlShape) shape;
						if(uml.getClassName().equals(name)){
							return uml;
						}
					}
		}
		return null;
	}
	
	public boolean highlightShape(MouseEvent e, int x, int y) {
		Shape lastHighlightedShape;
		boolean highlightChange = false;

		lastHighlightedShape = highlightedShape;
		if (lastHighlightedShape != null) {
			if (!lastHighlightedShape.containsPoint(x, y)) {
				lastHighlightedShape.highlight(false);
				lastHighlightedShape = highlightedShape = null;
				highlightChange = true;
			}
		}
		for (int i = shapes.size() - 1; i >= 0; i--) {
			Shape currentShape = (Shape) shapes.get(i);
			if (currentShape.containsPoint(x, y)) {
				highlightedShape = currentShape;
				highlightChange = (highlightedShape != lastHighlightedShape);
				if (highlightChange) {
					currentShape.highlight(true);
					if (lastHighlightedShape != null)
						lastHighlightedShape.highlight(false);
				}
				break;
			}
		}
		if (highlightChange)
			repaint();	
		return highlightChange;
	}
	
	public boolean selectShape(MouseEvent e, int x, int y) {
		boolean selected = false;

		if (pickedShape != null) {
			reshaping = pickedShape.selectHandleDecorators(e, x, y);
			if (reshaping)
				return reshaping;
		}

		if (oldPickedShape != null) {
			oldPickedShape.select(e, false);
			oldPickedShape = null;
		}

		for (int i = shapes.size() - 1; i >= 0; i--) {
			Shape currentShape = (Shape) shapes.get(i);
			if (currentShape.containsPoint(x, y)) {
				pickedShape = oldPickedShape = currentShape;
				pickedShape.dragStartX = pickedShape.getX();
				pickedShape.dragStartY = pickedShape.getY();
				selected = pickedShape.select(e, true);
				highlightShape(e,x,y);
				this.scrollRectToVisible(new Rectangle(currentShape.x,currentShape.y,currentShape.w+100,currentShape.h+100));
				break;
			}
		}
		if (!selected) {
			pickedShape = null;
		}

		repaint();
		return selected;
	}
	
	public boolean selectElementByName(String name){
		for (Iterator iter = shapes.iterator(); iter.hasNext();) {
			Object element = (Object) iter.next();
			if(element instanceof UmlShape){
				UmlShape uml = (UmlShape) element;
				if(uml.getClassName().equals(name)){
					//(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger, int button) 
					selectShape(new MouseEvent(this,0,0,0,uml.x,uml.y,0,false,1),uml.x,uml.y);
					return true;
				}
			}
		}
		return false;
	}

	
	// --------------------  dragging ---------------------------

	int lastX; // 	 During dragging, these record the x and y coordinates of the
	int lastY; //    previous position of the mouse.

	public synchronized void mousePressed(MouseEvent evt) {
		// User has pressed the mouse.  Find the shape that the user has clicked on, if
		// any.  If there is a shape at the position when the mouse was clicked, then
		// start dragging it.  If the user was holding down the shift key, then bring
		// the dragged shape to the front, in front of all the other shapes.
		
		boolean selected;
		Color newFillColor, newLineColor;
		float red, green, blue;
		Shape newShape;
		int x, y;
		x = evt.getX();
		y = evt.getY();
		//reshaping = true;
		downX = x;
		downY = y;
		
		selectShape(evt, x, y);

		for (Iterator iter = shapes.iterator(); iter.hasNext();) {
			Shape shape = (Shape) iter.next();
			if (shape.containsPoint(x, y)) {

				if (evt.isShiftDown()) { // Bring the shape to the front by moving it to
					shapes.remove(shape); //       the end of the list of shapes.
					shapes.add(shape);
					repaint(); // repaint canvas to show shape in front of other shapes
				}
				return;
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		int dragW, dragH, newWidth, newHeight, newX, newY;
		Shape newShape;
		boolean shiftHeld;

		// if nothing's picked, there's nothing to drag
		if (pickedShape == null)
			return;

		int x, y;
		int vX = 0, vY = 0;
		x = e.getX();
		y = e.getY();

		shiftHeld = e.isShiftDown();

		dragW = x - downX;
		dragH = y - downY;
  
		if (reshaping) {
			if (pickedShape != null) {
				pickedShape.moveHandleDecorator(x, y);
				repaint();
			}
			return;
		}

			if (shiftHeld){ // constrain movement to one axis				
				if (Math.abs(dragW) > Math.abs(dragH)) {
					newX = pickedShape.dragStartX + dragW;
					newY = pickedShape.dragStartY;
				} else {
					newX = pickedShape.dragStartX;
					newY = pickedShape.dragStartY + dragH;
				}
			} else {
				newX = pickedShape.dragStartX + dragW;
				newY = pickedShape.dragStartY + dragH;
			}
			pickedShape.move(newX, newY);
			
			Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
			if(e.getX()+pickedShape.getWidth()>this.canvasWidth){
				this.setCanvasWidth(canvasWidth+50);
				//this.setBounds(0,0,(int)this.getSize().getWidth()+30,(int)this.getSize().getHeight());
			}
			if(e.getY()+pickedShape.getHeight()>this.canvasHeight){
				this.setCanvasHeight(canvasHeight+50);
			}
			scrollRectToVisible(r);
			doLayout();
			repaint();
			return;
	}
	
	public synchronized void mouseReleased(MouseEvent e) {
			
		/*	formingNewShape = false;
			final int W = 100;
			final int H = 100;
			boolean changed = false;

				int x = e.getX();
				int y = e.getY();
				if (x < 0) x = 0;
				if (y < 0) y = 0;
				
				//Rectangle rect = new Rectangle(x, y, , h);
				//scrollRectToVisible(rect);
				
				//TODO: deal with the area - never sized!!
				int this_width = (x + W + 2);
				if (this_width > this.getWidth()) {
					//area.width = this_width; 
					System.out.println("Enlarging width");
					changed=true;
				}

				int this_height = (y + H + 2);
				if (this_height > this.getHeight()) {
					//area.height = this_height; 
					changed=true;
				}
		//	}
			if (changed) {
				//Update client's preferred size because
				//the area taken up by the graphics has
				//gotten larger or smaller (if cleared).
				Dimension s = new Dimension(this_width,this_height);
			
				//setPreferredSize(s);
				this.setSize(s);
				//Let the scroll pane know to update itself
				//and its scrollbars.
				Main.getInstance().getActiveInternalFrame().revalidate();
				//Main.getInstance().getActiveCanvasFrame().revalidate();
				revalidate();
			}*/
			repaint();
	}

	public void mouseEntered(MouseEvent evt) {}
	public void mouseExited(MouseEvent evt) {}
	
	public void mouseMoved(MouseEvent evt) {
		int x, y;
		x = evt.getX();
		y = evt.getY();
		highlightShape(evt, x, y);
		firePropertyChange("mouse",x,y);
	return;
	}
	

	public void mouseClicked(MouseEvent evt) {
		int x = evt.getX(); // x-coordinate of point where mouse was clicked
		int y = evt.getY(); // y-coordinate of point 

		if (shapes.size() > 0) {
			Shape shape = null;
			Shape hit = null;
			for (Iterator iter = shapes.iterator(); iter.hasNext();) {
				shape = (Shape) iter.next();
				if (shape.containsPoint(x, y)) {
					hit = shape;
					hit.handleSingleClick(evt.getPoint());
					if (evt.getClickCount() == 2) {
						shape.handleDoubleClick(evt.getPoint());
					}
					if (evt.getButton() == 3) {
						if (shape != null) {					
							showShapeContextMenu(shape, evt.getPoint());
						}else{
							showContextMenu(evt.getX(),evt.getY());
						}
					}
				} else {
					shape.select(evt, false);
					if (evt.getModifiers() == InputEvent.BUTTON3_DOWN_MASK) {
						showContextMenu(evt.getX(), evt.getY());
					}
				}
			}
			if (hit == null && evt.getButton() == 3) {
				//we clicked canvas
				showContextMenu(evt.getX(), evt.getY());
			}
		}
		repaint();
	}

	
	public void refreshShape(Shape s){
		s.move(s.getX(), s.getY());
					repaint();
	}

	/**
	 * @return
	 */
	public int getCanvasHeight() {
		return canvasHeight;
	}

	/**
	 * @return
	 */
	public int getCanvasWidth() {
		return canvasWidth;
	}

	public ICanvasDecorator getCanvasDecorator(){
		return this._decorator;
	}
	/**
	 * @param i
	 */
	public void setCanvasHeight(int i) {
		canvasHeight = i;
		setPreferredSize(new Dimension(canvasWidth,canvasHeight));
		this.doLayout();
	}

	/**
	 * @param i
	 */
	public void setCanvasWidth(int i) {
		canvasWidth = i;
		setPreferredSize(new Dimension(canvasWidth,canvasHeight));
		this.doLayout();
	}

	public void setupContextMenu() {
		this._contextMenu = new JPopupMenu();

		JMenu shapes = new JMenu("UML");

		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new MenuActionListener());
		save.setName("save");

		//JMenuItem shapes = new JMenuItem("shapes");

		JMenuItem clazz = new JMenuItem("Class");
		clazz.setName("Class");
		clazz.addActionListener(new MenuActionListener());
		shapes.add(clazz);

		_contextMenu.add(shapes);
		_contextMenu.addSeparator();
		_contextMenu.add(save);
		//this.add(this._contextMenu);

	}
	class MenuActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source instanceof JMenuItem) {
				JMenuItem item = (JMenuItem) source;
				if (item.getName().equals("Class")) {
					//Main.getInstance().setStatus("Creating a class");
					UmlShape uml = new UmlShape(50,50,200,150);
					//uml.setLeft(50);
					//uml.setTop(50);
					addShape(uml);

				}

				if (item.getName().equals("save")) {
					JFileChooser jfc = new JFileChooser();
					jfc.setSize(500, 250);
					jfc.setDialogTitle("Save as XMI");
					int choice = jfc.showSaveDialog(null);
				}
			}
		}
	} // End MenuActionListener
	/**
	 * @see gui.IContextMenu#showContextMenu()
	 */
	public void showContextMenu(int locX, int locY) {
		_contextMenu.show(this, locX, locY);
		repaint();

	}
	public void showShapeContextMenu(Shape s, Point p) {
		JPopupMenu menu = s.getContextMenu(p);
		menu.show(this, p.x, p.y);
		repaint();
	}

	/**
	 * @return
	 */
	public int getId() {
		return canvasCount;
	}

	/**
	 * @return
	 */
	public ArrayList getShapes() {
		return shapes;
	}

	/**
	 * @param list
	 */
	public void setShapes(ArrayList list) {
		shapes = list;
	}


	public int measureString(String text) {
		Graphics g = (this.offScreenGraphics==null)?this.getGraphics() : this.offScreenGraphics;
		int stringLength = g.getFontMetrics().stringWidth(text);
		
		return SwingUtilities.computeStringWidth(g.getFontMetrics(), text);
	}
	public int measureStringHeight() {
		return this.offScreenGraphics.getFontMetrics().getHeight();
	}
	
	

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		if(arg0.getPropertyName().equals("textchange")){
			System.out.println("TextChange");
		}
		for (Iterator iter = shapes.iterator(); iter.hasNext();) {
			Shape element = (Shape) iter.next();
			element.move(element.getX(),element.getY());
		}
		this.repaint();
	}

	public Dimension getPreferredSize() {	
				return new Dimension(this.canvasWidth,this.canvasHeight);
		}
		public Dimension getMinimumSize(){
		   return getPreferredSize();
		 }

	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
	 */
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect,
											int orientation,
											int direction) {
		if (orientation == SwingConstants.HORIZONTAL) {
			return visibleRect.width - maxUnitIncrement;
		} else {
			return visibleRect.height - maxUnitIncrement;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect,
											int orientation,
											int direction) {
		//Get the current position.
	   int currentPosition = 0;
	   if (orientation == SwingConstants.HORIZONTAL) {
		   currentPosition = visibleRect.x;
	   } else {
		   currentPosition = visibleRect.y;
	   }

	   //Return the number of pixels between currentPosition
	   //and the nearest tick mark in the indicated direction.
	   if (direction < 0) {
		   int newPosition = currentPosition -
							(currentPosition / maxUnitIncrement)
							 * maxUnitIncrement;
		   return (newPosition == 0) ? maxUnitIncrement : newPosition;
	   } else {
		   return ((currentPosition / maxUnitIncrement) + 1)
				  * maxUnitIncrement
				  - currentPosition;
	   }
	}

	public void setAllClassColors(Color color){
		for (Iterator iter = shapes.iterator(); iter.hasNext();) {
			Shape	shape = (Shape) iter.next();
					shape.setFillColor(color);		
		}
	}

	/**
	 * @param chosen
	 */
	public void setAllClassTextColors(Color chosen) {
		for (Iterator iter = shapes.iterator(); iter.hasNext();) {
			Shape	shape = (Shape) iter.next();
					shape.setTextColor(chosen);		
		}
	}
	
	public void setUmlModelWrapper(MOFModelWrapper w){
		this._umlWrapper = w;
	}
	public MOFModelWrapper getMofModelWrapper(){
		return this._umlWrapper;
	}
	
}