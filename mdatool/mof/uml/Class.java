/*
 * Created on 07-11-2003
 * 
 */
package mdatool.mof.uml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Jesper Linvald
 * @version
 * 
 * Description:
 */
public class Class extends UMLElement implements XMIWritable {
	
	private ArrayList stereoTypes;
	private ArrayList attributes;
	private ArrayList operations;
	private ArrayList relations;
	private org.omg.uml.foundation.core.UmlClass clazz;
	

	public Class(String name, String mofId) {
		super(name, mofId);
	}
	public Class(org.omg.uml.foundation.core.UmlClass clazz) {
		super(clazz.getName(), clazz.refMofId());
		this.clazz = clazz;

		Collection stereoTypes = clazz.getStereotype();
		
		if(stereoTypes.size()>0){
			for (Iterator iter = stereoTypes.iterator(); iter.hasNext();) {
				org.omg.uml.foundation.core.Stereotype element = (org.omg.uml.foundation.core.Stereotype) iter.next();
				this.addStereoType(element.getName());
		}
			
			
			//String stereoType = ((org.omg.uml.foundation.core.Stereotype)stereoTypes.iterator().next()).getName();
			//setStereoType(stereoType);
		}	
	}

	/* (non-Javadoc)
	 * @see mdatool.mof.uml.XMIWritable#toXMI()
	 */
	public String toXMI() {
		return null;
	}

	/**
	 * @return
	 */
	public ArrayList getAttributes() {
		return attributes;
	}

	/**
	 * @param list
	 */
	public void setAttributes(ArrayList list) {
		attributes = list;
	}

	/**
	 * @return
	 */
	public ArrayList getOperations() {
		return (ArrayList)operations;
	}

	/**
	 * @return
	 */
	public ArrayList getRelations() {
		return relations;
	}

	/**
	 * @param list
	 */
	public void setOperations(ArrayList list) {
		operations = list;
	}

	/**
	 * @param list
	 */
	public void setRelations(ArrayList list) {
		relations = list;
	}

	/**
	 * toString methode: creates a String representation of the object
	 * @return the String representation
	 * @author info.vancauwenberge.tostring plugin
	
	 */
	public String toString() {
		return this.getName();
	}
	/**
	 * @return
	 */
	public org.omg.uml.foundation.core.UmlClass getClazz() {
		return clazz;
	}
	/* (non-Javadoc)
	 * @see mdatool.mof.uml.UMLElement#toVerboseString()
	 */
	public String toVerboseString() {
		StringBuffer buffer = new StringBuffer();
buffer.append("Class[");
buffer.append("name = ").append(super.getName());
buffer.append(", stereotype = ").append(super.getStereoTypes());
buffer.append(", mofid = ").append(super.getMofId());
buffer.append(", attributes = ").append(attributes);
buffer.append(", operations = ").append(operations);
buffer.append(", relations = ").append(relations);
buffer.append("]");
return buffer.toString();
	}
	/* (non-Javadoc)
	 * @see mdatool.mof.uml.UMLElement#getElementKind()
	 */
	public String getElementKind() {
		return "class";
	}

}
