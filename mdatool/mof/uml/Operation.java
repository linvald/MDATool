/*
 * Created on 07-11-2003
 * 
 */
package mdatool.mof.uml;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Jesper Linvald
 * @version
 * 
 * Description:
 * 
 * name =
 */
public class Operation extends UMLElement {
	
	

	private String visibility;
	private String returnType;
	private ArrayList parameters;
	private StringBuffer codeBody;
	private String fullSignaure;
	private org.omg.uml.foundation.core.Operation operation;
	
	/**
	 * @param operation
	 */
	public Operation(org.omg.uml.foundation.core.Operation operation) {
		super(operation.getName(),operation.refMofId());
		this.operation = operation;
		init();
		
		
	}


	private void init(){	
		this.initParameters();
		this.fullSignaure = getOperationSignature(this.operation);
		setVisibility(operation.getVisibility().toString());
	}

	/**
	 * 
	 */
	private void initParameters() {
		this.parameters = new ArrayList();
		
		if (operation.getParameter().size()>0) {
			for (Iterator i = operation.getParameter().iterator(); i.hasNext();) {
				Object b = (Object) i.next();
				if (b instanceof org.omg.uml.foundation.core.Parameter) {
					org.omg.uml.foundation.core.Parameter param =
						(org.omg.uml.foundation.core.Parameter) b;
					if (param.getName().equals("return")) {
						setReturnType(param.getType().getName());
					} else {
						//System.out.println("PARAM:" + param.getType().getName());
						//System.out.println("PARAM:" + param.getName());
						addParameter(new Parameter(param.getType().getName(), param.getName()));
					}
				}
			}
		}

	}

	/**
	 * @return
	 */
	public StringBuffer getCodeBody() {
		return codeBody;
	}

	/**
	 * @return
	 */
	public ArrayList getParameters() {
		return parameters;
	}

	/**
	 * @return
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @return
	 */
	public String getVisibility() {
		return visibility;
	}

	/**
	 * @param buffer
	 */
	public void setCodeBody(StringBuffer buffer) {
		codeBody = buffer;
	}

	/**
	 * @param list
	 */
	public void setParameters(ArrayList list) {
		parameters = list;
	}

	/**
	 * @param string
	 */
	public void setReturnType(String string) {
		returnType = string;
	}

	/**
	 * @param string
	 */
	public void setVisibility(String string) {
		visibility = string;
	}
	
	public void addParameter(Parameter p){
		parameters.add(p);
	}

	/**
	 * @return
	 */
	public String getFullSignaure() {
		return fullSignaure;
	}

	/**
	 * @param string
	 */
	public void setFullSignaure(String string) {
		fullSignaure = string;
	}
	/**
		   * returns a string representation for the Java signature for
		   * a given operation. Note: The return type is not included (any more)!
		   * 
		   * @param model element representing the operation
		   * @return String representation of the operation signature
		   */
		  private String getOperationSignature(org.omg.uml.foundation.core.Operation o) {
			  if ((o == null) || !(o instanceof org.omg.uml.foundation.core.Operation)) {
				  return null;
			  }
			  Iterator it = o.getParameter().iterator();
			  if (!it.hasNext()){
				  return o.getName() + "()";
			  }

			  StringBuffer sb = new StringBuffer(); 
			  sb.append(o.getName());
			  sb.append("(");
			  for (Iterator iter = this.parameters.iterator(); iter.hasNext();) {
				Parameter element = (Parameter) iter.next();
				sb.append(element.getType()).append(" "+ element.getName()).append((","));
			}
			  sb.append(")");

			  return sb.toString();
		  }

	
	/* (non-Javadoc)
	 * @see mdatool.mof.uml.UMLElement#toVerboseString()
	 */
	public String toVerboseString() {
		StringBuffer buffer = new StringBuffer();
	buffer.append("Operation[");
	buffer.append("name = ").append(super.getName());
	buffer.append(", mofid = ").append(super.getMofId());
	buffer.append(", signature = ").append(this.getFullSignaure());
	buffer.append(", visibility = ").append(visibility);
	buffer.append(", returnType = ").append(returnType);
	buffer.append(", parameters = ").append(parameters);
	buffer.append(", codeBody = ").append(codeBody);
	buffer.append("]");
	return buffer.toString();
	}


	/* (non-Javadoc)
	 * @see mdatool.mof.uml.UMLElement#getElementKind()
	 */
	public String getElementKind() {
		return "operation";
	}
		  
		  
}
