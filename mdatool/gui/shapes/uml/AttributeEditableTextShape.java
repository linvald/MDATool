package mdatool.gui.shapes.uml;

import java.awt.Color;
import java.awt.Point;

import javax.jmi.model.Attribute;
import javax.jmi.model.StructuralFeature;
import javax.jmi.model.VisibilityKindEnum;

import mdatool.gui.application.Main;
import mdatool.gui.configuration.AddAttributePanel;
import mdatool.gui.shapes.basic.EditableTextShape;
import mdatool.gui.shapes.basic.Shape;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class AttributeEditableTextShape extends EditableTextShape {

	protected Attribute _structuralFeature;
	protected UmlShape _umlShape;
	protected AttributeContainer _container;
	
	/**
	 * @param parent
	 * @param cl
	 * @param x
	 * @param i
	 * @param w
	 * @param j
	 * @param text
	 * @param container
	 */
	public AttributeEditableTextShape(UmlShape parent, Attribute cl,int x1, int y1, int w1, int h1, Color color, Color color2, String text, AttributeContainer container) {
		super(x1, y1, w1, h1, color, color2, text, parent);
		this._structuralFeature = cl;
		this._umlShape = parent;
		this._container = container;
		format();
	}



	/**
	 * @param x1
	 * @param y1
	 * @param w1
	 * @param h1
	 * @param color
	 * @param color2
	 * @param text
	 * @param parent
	 */
	public AttributeEditableTextShape(int x1, int y1, int w1, int h1, Color color, Color color2, String text, Shape parent) {
		super(x1, y1, w1, h1, color, color2, text, parent);
	}

	private void format(){
		String name = _structuralFeature.getName();
		String vis = "";
		if(_structuralFeature.getVisibility()==VisibilityKindEnum.PRIVATE_VIS){
			vis = "-";
		}else if(_structuralFeature.getVisibility()==VisibilityKindEnum.PUBLIC_VIS){
			vis = "+";	
		}
		//String vis = cl.getVisibility()==VisibilityKindEnum.PRIVATE_VIS ? "-":"+";
		//String type = _structuralFeature.getType().getName();
		String text = vis + name + ":" + "type";
		this.text = text;
	
		super.setStringLength( text.length());
		//shape.addPropertyChangeListener(this);
		//int x, int y, int w, int h, String text, Shape parent) 
		/*if(this.longestString==""){
			this.longestString = text;
		}else if(text.length()>longestString.length()){
			longestString = text;
		}
		int total = 0;
		for (Iterator iter = editableTextShapes.iterator(); iter.hasNext();) {
			Shape element = (Shape) iter.next();
			total+=element.getHeight();
		}*/
	}
	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.basic.Shape#handleSingleClick(java.awt.Point)
	 */
	public void handleSingleClick(Point p) {
		//super.handleSingleClick(p);
		//System.out.println("AttributeEditableTextShape.handleSingleClick()");
		//System.out.println("The attribute:" + this._structuralFeature.getName());
		AddAttributePanel aap = new AddAttributePanel((UmlShape)parent, _structuralFeature);
		Main.getInstance().setSettingPanel(aap);
	}

}
