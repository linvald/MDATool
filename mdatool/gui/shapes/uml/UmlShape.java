package mdatool.gui.shapes.uml;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.jmi.model.Attribute;
import javax.jmi.model.MofClass;
import javax.jmi.model.StructuralFeature;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import mdatool.gui.application.Main;
import mdatool.gui.shapes.basic.LineShape;
import mdatool.gui.shapes.basic.RectShape;
import mdatool.gui.shapes.basic.Shape;
import mdatool.gui.shapes.shapedecorators.ButtonShape;


/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class UmlShape extends RectShape implements PropertyChangeListener{
	
	private String classTitle = "ClassName";
	private Font f;
	//private EditableTextShape text;
	private int greatestTextWidth = 0;
	
	private UmlClassHeader header;
	private AttributeContainer attributes;
	private OperationsContainer operations;
	private boolean autoScrinkToFit = true;
	private MofClass clazz;
	private ArrayList _relations;
	private ButtonShape addAttributeShape, addOperationShape;
	
	/**
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 * @param color
	 * @param color2
	 */
	public UmlShape(int x1, int y1, int w1, int h1, Color color, Color color2) {
		super(x1, y1, w1, h1, color, color2);
		init();
	}

	/**
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 */
	public UmlShape(int x1, int y1, int w1, int h1) {
		super(x1, y1, w1, h1);
		init();
	}

	public void init(){

		f = new Font("Verdana", Font.PLAIN, 12);
		this.addPropertyChangeListener(this);
		header = new UmlClassHeader(x,y,w,50,this);
		header.addPropertyChangeListener(this);
		this.addShape(header);     
		
		attributes= new AttributeContainer(x,this.y+header.getHeaderHight(),w,100,this);	
		this.addShape(attributes);
		
		operations= new OperationsContainer(x,y+400,w,100,this);
		operations.addPropertyChangeListener(this);
		this.addShape(operations);
		
		this._relations = new ArrayList();
		
		
		addAttributeShape = new ButtonShape((x + this.getWidth()) -15, attributes.y + (attributes.h/2), 10,10,this.lineColor,this.fillColor,this);
		this.addShape(addAttributeShape);
		
		setUpAddButtons();
		//_beanSupport.firePropertyChange("", new Object(), new Object());
	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#draw(java.awt.Graphics)
	 */
	public void draw(Graphics g) {
		//super.draw(g);
		if(Main.getInstance().getUserPrefs().getAutomaticUmlResize())
			position();
		if (filled) {
			if (highlighted)
				g.setColor(fillColor.brighter());
			else
				g.setColor(fillColor);
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

		for (Iterator iter = getShapes().iterator(); iter.hasNext();) {
			Shape childShape = (Shape) iter.next();
			childShape.setTextColor(this.textColor);
			childShape.draw(g);
		}
		if(_relations.size()>0)	
		for (Iterator iter = _relations.iterator(); iter.hasNext();) {
			UmlShape element = (UmlShape) iter.next();
			if(element !=null){
			
			Point firstEnd, secondEnd;
			//make line from this 2 element in x direction
			if(this.getX()>element.getX()){
				firstEnd = new Point(this.getX(),this.getY()+(getHeight()/2));
				secondEnd = new Point(element.getX()+element.getWidth(),element.getY()+(element.getHeight()/2));
			}else{
				firstEnd = new Point(this.getX()+this.getWidth(),this.getY()+(getHeight()/2));
				secondEnd = new Point(element.getX(),element.getY()+(element.getHeight()/2));
			}
			
			LineShape line = new LineShape(0,0,0,0,firstEnd,secondEnd);
			line.draw(g);
			
			

				//Point thirdEnd = new Point(element.getX()+element.getWidth(),element.getY()+(element.getHeight()/2));
				//LineShape line2 = new LineShape(0,0,0,0,secondEnd,thirdEnd);
				//line2.draw(g);
			/*Point firstEnd = new Point(this.getX()+this.getWidth(),this.getY()+(getHeight()/2));
			Point secondEnd = new Point(element.getX()+element.getWidth(),element.getY()+(element.getHeight()/2));
			LineShape line = new LineShape(0,0,0,0,firstEnd,secondEnd);
			line.draw(g);*/
			}
		}
	}

	/**
	 * 
	 */
	public void position() {
		this.operations.position();
		this.attributes.position();
		h = header.h + attributes.h+operations.h;
		String head = this.header.getLongestString();
		String att = this.attributes.getLongestString();
		String op = this.operations.getLongestString();
		
		String longest =   head;
		if(att.length()>longest.length())longest = att;
		if(op.length()>longest.length())longest = op;
		
		
		//int longer = Math.max(head.length(),att.length());
		//int longest = Math.max(longer,op.length());
		this.w = Main.getInstance().getActiveCanvasFrame().measureString(longest)+50;
		this.size(w,h);
	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#moveBy(int, int)
	 */
	public void move(int dx, int dy) {
		super.move(dx, dy);
		for (Iterator iter = getShapes().iterator(); iter.hasNext();) {
			Shape element = (Shape) iter.next();
			element.move(dx, dy);
		}
	}

	public void setUpAddButtons(){
	
	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#getContextMenu()
	 */
	public JPopupMenu getContextMenu(Point p) {
		Shape toHandle = null;
		JPopupMenu pop = new JPopupMenu("UmlShape");
				for (Iterator iter =  getShapes().iterator(); iter.hasNext();) {
					Shape element = (Shape) iter.next();
					if(element.containsPoint(p.x,p.y)){
						if(element.getContextMenu(p)!=null){
							return element.getContextMenu(p);	
						}
					}	
				}
				if(this.containsPoint(p.x,p.y)){
					if(!Main.getInstance().getUserPrefs().getAutomaticUmlResize()){
							JMenuItem item = new JMenuItem("Shrink to fit");
							item.addActionListener(new ActionListener(){
								public void actionPerformed(ActionEvent arg0) {
									position();
								}
			
							});
							pop.add(item);
					}		
				}
		
	
		return pop;
	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#handleDoubleClick(java.awt.Point)
	 */
	public void handleDoubleClick(Point p) {
		Main.getInstance().setStatus("Shape Handle doubleclick");
		Shape toHandle = null;
		for (Iterator iter =  getShapes().iterator(); iter.hasNext();) {
			Shape element = (Shape) iter.next();
			if(element.containsPoint(p.x,p.y)){
				toHandle = element;
				element.handleDoubleClick(p);
			//	Main.getInstance().setStatus("Location  HIT of element:" + element.getLeftPosition() + ","+ element.getTopPosition());
			}else{
			//	Main.getInstance().setStatus("Location of element:" + element.getLeftPosition() + ","+ element.getTopPosition());
			}
		}
		if(toHandle==null){
			//this.setColor(Color.yellow);
		}	
	}

	public void handleSingleClick(Point p){
		Shape toHandle = null;
		for (Iterator iter =  getShapes().iterator(); iter.hasNext();) {
					Shape element = (Shape) iter.next();
					if(element.containsPoint(p.x,p.y)){
						toHandle = element;
						element.handleSingleClick(p);
					}else{
					}
		}
		//if(toHandle==null){
			Main.getInstance().getUmlSettingPanel().showClassSettingPanel(this);
		//}	
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent object) {

		if(object.getPropertyName().equals("textchange")){
			//Main.getInstance().getActiveCanvasFrame().refreshShape(this);

		}else if(object.getPropertyName().equals("size")){
			if(object.getSource() instanceof UmlClassHeader){
			//	this.w = this.header.getWidth();
			}
		}else if(object.getPropertyName().equals("attributeadded")){
			//calculate new size...
			if(Main.getInstance().getUserPrefs().getAutomaticUmlResize())
				position();

		}else if(object.getPropertyName().equals("position")){
			//position();
		}
	}

	/**
	 * @return
	 */
	public int getheaderHeight() {
		return this.header.getHeight();
	}
	public int getAttributeContainerHeight(){
		return this.attributes.getHeight();
	}
	public void addAttribute(String att){
		this.attributes.addEditableTextShape(att);
	}
	public void addAttribute(Attribute sf){
		this.attributes.addEditableTextShape(sf);
	}
	public void addOperation(String op){
		this.operations.addEditableTextShape(op);
	}

	public void setClassName(String name){
		this.header.setClassName(name);
	}
	public String getClassName(){
		return this.header.getClassName().getText();
	}
	public void setStereoTypes(Collection stereoTypes){
		this.header.setStereoTypes(stereoTypes);
		//set the stereotype of the MofClass as well
		//this.clazz.
	}

	/**
	 * 
	 * @return commaseparated list of stereoTypes
	 */
	public String getClassStereoTypes(){
		return this.header.getStereoType().getText();
	}
	public void addRelation(UmlShape shape){
		this._relations.add(shape);
	}

	/**
	 * @return
	 */
	public MofClass getClazz() {
		return clazz;
	}

	/**
	 * @param class1
	 */
	public void setClazz(MofClass class1) {
		clazz = class1;
	}

}
