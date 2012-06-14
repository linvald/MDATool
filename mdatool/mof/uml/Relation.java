/*
 * Created on 07-11-2003
 * 
 */
package mdatool.mof.uml;

import java.util.Collection;
import java.util.Iterator;

import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.datatypes.MultiplicityRange;

/**
 * @author Jesper Linvald
 * 
 * Description:
 * @TODO: handle aggregation
 */
public class Relation extends UMLElement {
	
	private AssociationEnd source;
	private AssociationEnd target;
	private String stereoType;
	

	public Relation( AssociationEnd source) {
		super(source.getAssociation().getName(), source.refMofId());
		this.source =source;
		this.target = getOtherEnd();
		init();
	}

	public void init(){
		setStereoType((source.getAssociation().getStereotype().size()>0) ? ((org.omg.uml.foundation.core.Stereotype)source.getAssociation().getStereotype().iterator().next()).getName() : "No");
	}

	protected AssociationEnd getOtherEnd() {
		   Collection ends = source.getAssociation().getConnection();
		   for (Iterator i = ends.iterator(); i.hasNext(); ){
			   AssociationEnd ae = (AssociationEnd)i.next();
			   if (!source.equals(ae)){
				   return ae;
			   }
		   }
        
		   return null;
	   }

	private boolean isMany(AssociationEnd ae){
		Collection ranges = ae.getMultiplicity().getRange();
        
		for (Iterator i = ranges.iterator(); i.hasNext() ; ){
			MultiplicityRange range = (MultiplicityRange)i.next();
			if ( range.getUpper() > 1 ){
				return true;
			}       
			int rangeSize = range.getUpper() - range.getLower();
			if (rangeSize < 0){
				return true;
			}
         
		}     
		return false;
	}
	
	public boolean isOne2Many() {
		return !isMany(source) && isMany(getOtherEnd());
	}

	public boolean isMany2Many() {
		return isMany(source) && isMany(getOtherEnd());
	}

	public boolean isOne2One() {
		return !isMany(source) && !isMany(getOtherEnd());
	}

	/**
	 * @return
	 */
	public String getStereoType() {
		return stereoType;
	}


	public boolean isSourceCardinalityMany() {
		return isMany(source);
	}


	public boolean isTargetCardinalityMany() {
		return isMany(getOtherEnd());
	}


	/**
	 * @param string
	 */
	public void setStereoType(String string) {
		stereoType = string;
	}

	/**
	 * @return
	 */
	public AssociationEnd getSource() {
		return source;
	}

	/**
	 * @return
	 */
	public AssociationEnd getTarget() {
		return target;
	}

	/**
	 * @param end
	 */
	public void setSource(AssociationEnd end) {
		source = end;
	}

	/**
	 * @param end
	 */
	public void setTarget(AssociationEnd end) {
		target = end;
	}
	
	public String getSourceClassName(){
		return source.getParticipant().getName();
	}
	public String getTargetClassName(){
		return target.getParticipant().getName();
	}

	/* (non-Javadoc)
	 * @see mdatool.mof.uml.UMLElement#toVerboseString()
	 */
	public String toVerboseString() {
		StringBuffer buffer = new StringBuffer();
buffer.append("Relation[");
buffer.append("name = ").append(getName());
buffer.append(", mofid = ").append(super.getMofId());
buffer.append(", source = ").append(getSourceClassName()).append(this.isSourceCardinalityMany() ? "(many)" : "(one)");
buffer.append(", target = ").append(getTargetClassName()).append(this.isTargetCardinalityMany() ? "(many)" : "(one)");;
buffer.append(", stereoType = ").append(stereoType);
		
buffer.append("]");
return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see mdatool.mof.uml.UMLElement#getElementKind()
	 */
	public String getElementKind() {
		return "erlation";
	}

}
