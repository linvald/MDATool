package mdatool.gui.shapes.uml;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import mdatool.gui.application.Main;
import mdatool.gui.shapes.basic.EditableTextShape;
import mdatool.gui.shapes.basic.Shape;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class UmlClassHeader extends Shape implements PropertyChangeListener{
	

	private EditableTextShape stereoType;
	private EditableTextShape className;
	private UmlShape parent=null;
	private boolean first = true;
	private String longestString ="ClassName";
	
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public UmlClassHeader(int x, int y, int width, int height,UmlShape parent) {
		//public Shape(int x, int y, int width, int height, Color lineColor, Color fillColor) {
		super(x, y, width, height);
		this.parent = parent;
		//int x, int y, int w, int h, String text, Shape parent
	
		stereoType = new EditableTextShape(x,y,width,height/2,"<<>>", this);
		className =  new EditableTextShape(x,y,width,height/2,longestString, this);

		parent.addPropertyChangeListener(this);
		stereoType.addPropertyChangeListener(this);
		className.addPropertyChangeListener(this);
		
		position();
		
	}

	
	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#draw(java.awt.Graphics)
	 */
	public void draw(Graphics g) {
		Color before = g.getColor();
		g.setColor(parent.textColor);
		className.draw(g);
		stereoType.draw(g);

		if(first){
			parent.move(parent.x,parent.y);
			first = false;
		}
		g.setColor(before);
	}



	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#getContextMenu(java.awt.Point)
	 */
	public JPopupMenu getContextMenu(Point p) {
		JPopupMenu pop=null;
		if(this.className.containsPoint(p.x,p.y)){
			pop= new JPopupMenu("pop");
				final JMenu menuClass = new JMenu("Rename class");
				final JTextField tClass = new JTextField(20);
				tClass.setName("class");
				pop.add(menuClass);
				menuClass.add(tClass);
			addKeyListener(tClass,pop);
		}else if(this.stereoType.containsPoint(p.x,p.y)){
			pop = new JPopupMenu("pop");
			final JMenu menu = new JMenu("Rename stereotype");
			final JTextField t = new JTextField(20);
			t.setName("stereo");
			menu.add(t);
			t.show();
			pop.add(menu);
			addKeyListener(t,pop);
		}

		if(pop!=null){
			final JMenuItem fit = new JMenuItem("Shrink to to fit");
			fit.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if(parent instanceof UmlShape){
						UmlShape shape = (UmlShape)parent;
						shape.position();
					}
					
				}
			});
			pop.add(fit);
			pop.validate();
			return pop;
		}else	
		return null;
	}

	private void addKeyListener(final JTextField t, final JPopupMenu pop) {
		
		t.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				//if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) { //return
					if (t.getText().length() > 0){
						if(t.getName().equals("class")){
							className.setText(t.getText());	
						}else if(t.getName().equals("stereo")){
							stereoType.setText("<<"+t.getText()+">>");
							
							
						}
						
						parent.move(parent.x,parent.y);
						position();
						Main.getInstance().getActiveCanvasFrame().repaint();
					
				//} 
					}if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) { //return
						pop.setVisible(false);
						Main.getInstance().getActiveCanvasFrame().repaint();
					}
					longestString = (stereoType.getText().length()>className.getText().length() ? stereoType.getText():className.getText());
			
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

	public String getLongestString(){
		return 	longestString;
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
		
		if (className != null) {
			position();
		}
	}
	
	public void position(){
		stereoType.x = parent.x + ((parent.w-stereoType.w)/2);
		stereoType.y = parent.y;
		
		className.x = parent.x + ((parent.w-className.w)/2);
		className.y = stereoType.y + className.h;
		
		int extraSpace = 7;
		
		x = parent.x;
		y = parent.y;
		w = parent.w;
		
		h = stereoType.h + className.h + extraSpace;
	}
	public int getHeaderHight(){
		return h;	
	}
	public void setStereoTypes(Collection s){
		String text = "<<";
		int count = 0;
		if(s.size()>0){
		
		for (Iterator iter = s.iterator(); iter.hasNext();) {
			System.out.println("CLASS:" + iter.next().getClass());
			//org.omg.uml.foundation.core.Stereotype ste = (org.omg.uml.foundation.core.Stereotype)iter.next();
			String stereoType = ((org.omg.uml.foundation.core.Stereotype)s.iterator().next()).getName();
			//String element = (String) iter.next();
			text+= (count==s.size()-1) ? stereoType : stereoType + ",";	
			count++;
	}
		text+=">>";
		this.stereoType.setText( text );
		}
	}


	/**
	 * @param name
	 */
	public void setClassName(String name) {
		this.className.setText(name);
	}

	/**
	 * @return
	 */
	public EditableTextShape getClassName() {
		return className;
	}

	/**
	 * @return
	 */
	public EditableTextShape getStereoType() {
		return stereoType;
	}


	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.basic.Shape#handleSingleClick(java.awt.Point)
	 */
	public void handleSingleClick(Point p) {
	}

}
