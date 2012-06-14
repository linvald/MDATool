package mdatool.gui.shapes.uml;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.jmi.model.Attribute;
import javax.jmi.model.AttributeClass;
import javax.jmi.model.MofClass;
import javax.jmi.model.StructuralFeature;
import javax.jmi.model.VisibilityKindEnum;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;



import mdatool.gui.application.Main;
import mdatool.gui.shapes.basic.Shape;
import mdatool.gui.shapes.basic.EditableTextShape;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class AttributeContainer extends Shape implements PropertyChangeListener{
	
	protected ArrayList editableTextShapes;
	protected UmlShape parent=null;
	protected boolean first = true;
	protected String longestString = "";
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public AttributeContainer(int x, int y, int width, int height,UmlShape parent) {
		//public Shape(int x, int y, int width, int height, Color lineColor, Color fillColor) {
		super(x, y, width, height, Color.gray, Color.blue);
		this.parent = parent;
		//int x, int y, int w, int h, String text, Shape parent
	
		editableTextShapes = new ArrayList();

		parent.addPropertyChangeListener(this);
		this.addPropertyChangeListener(this);
		
		position();
		
	}

	
	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#draw(java.awt.Graphics)
	 */
	public void draw(Graphics g) {
		Color before = g.getColor();
		if (first) {
			parent.move(parent.x, parent.y);
			first = false;
		}
		g.setColor(parent.textColor);
		for (Iterator iter = editableTextShapes.iterator(); iter.hasNext();) {
			Shape element = (Shape) iter.next();
			element.draw(g);
		}
		g.setColor(before);
		g.drawRect(x,y,w,h);
	}
	
	public boolean containsPoint(Point p){
		//Rectangle r = new Rectangle(bounds().x,bounds().y,bounds().width,bounds().height);
		//return r.contains(p.x,p.y);
		
		return bounds().contains(p);
	}
	
	/**
	 * Adding a mofAttribute
	 * @param cl
	 */
	public void addEditableTextShape(Attribute cl){
		System.out.println("AttributeContainer.addEditableTextShape()");
		/*String name = cl.getName();
		String vis = "";
		if(cl.getVisibility()==VisibilityKindEnum.PRIVATE_VIS){
			vis = "-";
		}else if(cl.getVisibility()==VisibilityKindEnum.PUBLIC_VIS){
			vis = "+";	
		}
		//String vis = cl.getVisibility()==VisibilityKindEnum.PRIVATE_VIS ? "-":"+";
		String type = cl.getType().getName();
		String text = vis + name + ":" + type;
		//shape.addPropertyChangeListener(this);
		//int x, int y, int w, int h, String text, Shape parent) 
		if(this.longestString==""){
			this.longestString = text;
		}else if(text.length()>longestString.length()){
			longestString = text;
		}*/
		int total = 0;
		for (Iterator iter = editableTextShapes.iterator(); iter.hasNext();) {
			Shape element = (Shape) iter.next();
			total+=element.getHeight();
		}
		//EditableTextShape ets = new EditableTextShape(x,y+total,w,50,text,this);
		
		
//		** Here I should add AttributeEditableTextShapes
		 AttributeEditableTextShape aets = new AttributeEditableTextShape(parent,cl,x,y+total,w,50, Color.white, Color.black,"",this);
			  
			  
		
		aets.addPropertyChangeListener(this);
		this.editableTextShapes.add(aets);
		position();
		Main.getInstance().getActiveCanvasFrame().repaint();
		this._beanSupport.firePropertyChange("attributeadded",0,1);		
	}
	
	/**
	 * Adding simple strings
	 * @param text
	 */
	public void addEditableTextShape(String text){
		//shape.addPropertyChangeListener(this);
		//int x, int y, int w, int h, String text, Shape parent) 
		if(this.longestString==""){
			this.longestString = text;
		}else if(text.length()>longestString.length()){
			longestString = text;
		}
		int total = 0;
		for (Iterator iter = editableTextShapes.iterator(); iter.hasNext();) {
			Shape element = (Shape) iter.next();
			total+=element.getHeight();
		}
		EditableTextShape ets = new EditableTextShape(x,y+total,w,50,text,this);
		ets.addPropertyChangeListener(this);
		this.editableTextShapes.add(ets);
		position();
		Main.getInstance().getActiveCanvasFrame().repaint();
		this._beanSupport.firePropertyChange("attributeadded",0,1);
		
	}
	
	public String getLongestString(){
		return this.longestString;
	}


	private void addKeyListener(final JTextField t, final JPopupMenu pop) {
		
		t.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				//if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) { //return
					if (t.getText().length() > 0){
						if(t.getName().equals("class")){
					//		className.setText(t.getText());	
						}else if(t.getName().equals("stereo")){
					//		stereoType.setText("<<"+t.getText()+">>");
						}
						
						parent.move(parent.x,parent.y);
						position();
						Main.getInstance().getActiveCanvasFrame().repaint();
					
				//} 
					}if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) { //return
						pop.setVisible(false);
						Main.getInstance().getActiveCanvasFrame().repaint();
					}
			
			}
			public void keyReleased(KeyEvent e) {
				if (t.getText().length() > 0) {}
				//setText(t.getText());
			}
			public void keyTyped(KeyEvent e) {
				if (t.getText().length() > 0) {}
				//setText(t.getText());
			}
		});
	}


	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#handleDoubleClick(java.awt.Point)
	 */
	public void handleDoubleClick(Point p) {}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		if(arg0.getPropertyName().equals("size")){
			position();		
		}
		if(arg0.getPropertyName().equals("textchange")){
			Main.getInstance().getActiveCanvasFrame().repaint();
			this._beanSupport.firePropertyChange(arg0);
		}
		if(arg0.getPropertyName().equals("attributeadded")){
			position();
		}
	}



	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#move(int, int)
	 */
	public void move(int newX, int newY) {
		super.move(newX, newY);
		if(editableTextShapes!=null)
		for (Iterator iter = editableTextShapes.iterator(); iter.hasNext();) {
			Shape element = (Shape) iter.next();
			element.move(newX,newY);
		}
		if (this.editableTextShapes != null ) {
			position();
		}
	}
	
	public void position(){
		w = parent.w;
		int total = 0;
		for (int i = 0; i < editableTextShapes.size(); i++) {
			Shape s = (Shape)editableTextShapes.get(i);
			
			s.x = parent.x;// + ((parent.w-s.w)/2);
			s.y = parent.y+ parent.getheaderHeight()+total;
			total+=s.h;
		}

		
		x = parent.x;
		if(total !=0){
	
			y = parent.y + parent.getheaderHeight() ;
			h = total;
		}else{	
			y = parent.y + total+ parent.getheaderHeight();
			h = 20;	
		}			
	}
	

	
	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.basic.Shape#getContextMenu(java.awt.Point)
	 */
	public JPopupMenu getContextMenu(Point p) {
		if (this.containsPoint(p.x, p.y)) {
			JPopupMenu m = new JPopupMenu("attributes");
			m.add(new JMenuItem("Add attribute"));
			return m;
		} else {
			return null;
		}
	}


	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.basic.Shape#handleSingleClick(java.awt.Point)
	 */
	public void handleSingleClick(Point p) {
		for (Iterator iter = editableTextShapes.iterator(); iter.hasNext();) {
			EditableTextShape ets = (EditableTextShape) iter.next();
			if(ets.containsPoint(p.x,p.y)){
				ets.handleSingleClick(p);	
			}
		}
		System.out.println("Attribute container handler single click");
	}

}
