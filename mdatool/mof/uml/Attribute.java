/*
 * Created on 07-11-2003
 * 
 */
package mdatool.mof.uml;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Jesper Linvald
 * @version
 * 
 * Description:
 */
public class Attribute extends UMLElement {
	
	private String visibility;
	private String type;
	private org.omg.uml.foundation.core.Attribute attribute;

	public Attribute() {}
	
	public Attribute(org.omg.uml.foundation.core.Attribute attribute) {
		super(attribute.getName(), attribute.refMofId());
		this.attribute = attribute;
		setVisibility(attribute.getVisibility().toString());
		if (attribute.getStereotype().size() > 0){
			Collection stereoTypes = attribute.getStereotype();
		
			for (Iterator iter = stereoTypes.iterator(); iter.hasNext();) {
				org.omg.uml.foundation.core.Stereotype element = (org.omg.uml.foundation.core.Stereotype) iter.next();
				this.addStereoType(element.getName());
			}
		}
		
		
			/*setStereoType(
				((org.omg.uml.foundation.core.Stereotype) attribute
					.getStereotype()
					.iterator()
					.next())
					.getName());*/
		if (attribute.getType().getName() != null) {
			setType(attribute.getType().getName());
		}
	}

	/**
	 * @param name
	 * @param mofId
	 */
	public Attribute(String name, String mofId) {
		super(name, mofId);
	}


	/**
	 * @return
	 */
	public String getVisibility() {
		return visibility;
	}

	/**
	 * @param string
	 */
	public void setVisibility(String string) {
		visibility = string;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param string
	 */
	public void setType(String string) {
		type = string;
	}


	/* (non-Javadoc)
	 * @see mdatool.mof.uml.UMLElement#toVerboseString()
	 */
	public String toVerboseString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Attribute[");
		buffer.append("name = ").append(super.getName());
		buffer.append(", mofid = ").append(super.getMofId());
		buffer.append(", visibility = ").append(visibility);
		buffer.append(", type = ").append(type);
		buffer.append("]");
		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see mdatool.mof.uml.UMLElement#getElementKind()
	 */
	public String getElementKind() {
		return "attribute";
	}
}
