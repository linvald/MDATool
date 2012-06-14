/*
 * Created on 07-11-2003
 * 
 */
package mdatool.mof.uml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.omg.uml.foundation.core.ModelElement;

/**
 * @author Jesper Linvald
 * @version
 * 
 * Description:
 */
public abstract class UMLElement {

	private String name;
	private String mofId;
	private ArrayList stereoTypes;
	private HashMap taggedValues;
	
	
	public UMLElement() {

	}

	public UMLElement(String name, String mofId) {
		this.name = name;
		this.mofId = mofId;
		this.stereoTypes = new ArrayList();
		initBase();
	}
	
	public void initBase(){
		this.taggedValues = new HashMap();
		getTaggedValues(null); //TODO: make ModelElement an integral part of the constructor
	}
	
	public void addStereoType(String stereo){
		this.stereoTypes.add(stereo);
	}
	
	public ArrayList getStereoTypes(){
		return stereoTypes;
	}
	
	/**
	 * @return
	 */
	public String getMofId() {
		return mofId;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param l
	 */
	public void setMofId(String l) {
		mofId = l;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	
	public void addTaggedValue(String key, String value){
		this.taggedValues.put(key,value);
	}
	
	/**
	   * Returns the collection of taggedValues for a given modelElement
	   * 
	   * @param object model element
	   * @return Collection of org.omg.uml.foundation.core.TaggedValue
	   * 
	   */
	  public Collection getTaggedValues(Object object) {
		  if ((object == null) || !(object instanceof ModelElement)) {
			  return null;
		  }
		  ModelElement modelElement = (ModelElement) object;
		  return modelElement.getTaggedValue();
	  }
	  
	  public abstract String toVerboseString();
	/**
	 * toString methode: creates a String representation of the object
	 * @return the String representation
	 * @author info.vancauwenberge.tostring plugin
	
	 */
	public String toString(){return getName();}
	public abstract String getElementKind();
}
