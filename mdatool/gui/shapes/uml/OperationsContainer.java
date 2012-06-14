package mdatool.gui.shapes.uml;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import mdatool.gui.application.Main;
import mdatool.gui.shapes.basic.Shape;
import mdatool.gui.shapes.basic.EditableTextShape;
import mdatool.gui.shapes.uml.UmlShape;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class OperationsContainer extends Shape implements PropertyChangeListener{
	
	private ArrayList editableTextShapes;
	private UmlShape parent=null;
	private boolean first = true;
	private String longestString = "";
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public OperationsContainer(int x, int y, int width, int height,UmlShape parent) {
		//public Shape(int x, int y, int width, int height, Color lineColor, Color fillColor) {
		super(x, y, width, height);
		this.parent = parent;
		//int x, int y, int w, int h, String text, Shape parent
	
		editableTextShapes = new ArrayList();

		parent.addPropertyChangeListener(this);
		this.addPropertyChangeListener(parent);
		
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
		//g.fillRect(x-20,y,w+40,h);
		for (Iterator iter = editableTextShapes.iterator(); iter.hasNext();) {
			g.setColor(parent.textColor);
			Shape element = (Shape) iter.next();
			element.draw(g);
		}
		g.setColor(parent.lineColor);
		g.drawRect(x,y,w,h);
		g.setColor(before);
		//g.drawRect(x+50,y,w,h);
		//g.drawLine(x,y+h,x+w,y+h);
		//g.setColor(this.fillColor);

	}

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
		
		Main.getInstance().getActiveCanvasFrame().repaint();
		this._beanSupport.firePropertyChange("attributeadded",0,0);

		position();
	}
	
	public String getLongestString(){
		return this.longestString;
	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#getContextMenu(java.awt.Point)
	 */
	public JPopupMenu getContextMenu(Point p) {
		final JPopupMenu pop = new JPopupMenu("pop");
		
		for (Iterator iter = editableTextShapes.iterator(); iter.hasNext();) {
					Shape element = (Shape) iter.next();
					if(element.containsPoint(p.x,p.y)){
						return element.getContextMenu(p);
					}

		}
		
		
		/*if(this.className.containsPoint(p.x,p.y)){
				final JMenu menuClass = new JMenu("Rename class");
				final JTextField tClass = new JTextField(20);
				tClass.setName("class");
				pop.add(menuClass);
				menuClass.add(tClass);
			addKeyListener(tClass,pop);
		}else if(this.stereoType.containsPoint(p.x,p.y)){
			final JMenu menu = new JMenu("Rename stereotype");
			final JTextField t = new JTextField(20);
			t.setName("stereo");
			menu.add(t);
			t.show();
			pop.add(menu);
			addKeyListener(t,pop);
		}*/

		
	//	pop.validate();
		return pop;
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
		int total = 0;
		for (int i = 0; i < editableTextShapes.size(); i++) {
			Shape s = (Shape)editableTextShapes.get(i);
			s.x = parent.x;// + ((parent.w-s.w)/2);
			s.y = parent.y+ parent.getheaderHeight()+parent.getAttributeContainerHeight()+total;
			total+=s.h;
		}
		
		x = parent.x;	
		y = parent.y + parent.getheaderHeight()+parent.getAttributeContainerHeight();
		w = parent.w;
		h = (total==0) ? 20 : total;
		this._beanSupport.firePropertyChange("position",1,2);
	}


	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.basic.Shape#handleSingleClick(java.awt.Point)
	 */
	public void handleSingleClick(Point p) {
	}

}
