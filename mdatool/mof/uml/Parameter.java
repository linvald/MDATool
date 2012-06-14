package mdatool.mof.uml;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class Parameter extends UMLElement {

	private String type, name;
	
	public Parameter(String type, String name){
		this.type = type;
		this.name = name;
	}
	

	/**
	 * @return
	 */
	public String getName() {
		return name;
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
	public void setName(String string) {
		name = string;
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
buffer.append("Parameter[");
buffer.append("type = ").append(type);
buffer.append(", name = ").append(name);
buffer.append("]");
return buffer.toString();
	}


	/* (non-Javadoc)
	 * @see mdatool.mof.uml.UMLElement#getElementKind()
	 */
	public String getElementKind() {
		return "parameter";
	}
}
