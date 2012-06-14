/*
 * Created on 07-11-2003
 * 
 */
package mdatool.mof.modelwrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofClass;
import javax.jmi.model.MofPackage;
import javax.jmi.reflect.RefClass;
import javax.jmi.reflect.RefPackage;

import mdatool.mof.common.FilteredCollection;
import mdatool.mof.repository.RepositoryManager;
import mdatool.mof.uml.Relation;

import org.netbeans.api.mdr.MDRepository;
import org.netbeans.api.mdr.events.MDRChangeEvent;
import org.netbeans.api.mdr.events.MDRChangeListener;
import org.omg.uml.UmlPackage;
import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.UmlClass;

/**
 * @author Jesper Linvald
 * @version 1.0
 * 
 * Description: This class works on various UML elements and extract info from them
 * TODO: make singleton
 */
public class UMLModelWrapper implements MDRChangeListener{

	//private static UmlModelWrapper instance = null;	
	private RepositoryManager repositoryWrapper;
	private String modelName;
	private RefPackage refPackage;

	
	private final ModelPackage mof;
	// cache of all stereotypes used in model
	private final HashMap stereotypes = new HashMap();
	// outermost package that is currently being processed
	private MofPackage outermost;
	

	public UMLModelWrapper(RepositoryManager repositoryWrapper, String modelName,RefPackage uml) {
		this.repositoryWrapper = repositoryWrapper;
		this.modelName = modelName;
		this.mof = null;
		this.refPackage = uml;	
		System.out.println("Created new UML model extent: " + modelName);
	//	repository.getRepository().addListener(this);
	}
	
	
	public void createClass(String name){
		RefClass c = this.refPackage.refClass(name);
		this.refPackage.refAllClasses().add(c);
	}
	
	/**
		*  Gets the attributes of the specified Classifier object.
		*
		*@param  object  Classifier object
		*@return  Collection of org.omg.uml.foundation.core.Attribute
		*/
	public static Collection getAttributes(UmlClass object) {
		if ((object == null) || !(object instanceof Classifier)) {
			return null;
		}
		Classifier classifier = (Classifier) object;
		Collection features = new FilteredCollection(classifier.getFeature()) {
			protected boolean accept(Object object) {
				return object instanceof Attribute;
			}
		};
		return features;
	}

	public static Collection getOperations(UmlClass object) {
		if ((object == null) || !(object instanceof Classifier)) {
			return null;
		}
		Classifier classifier = (Classifier) object;
		Collection features = new FilteredCollection(classifier.getFeature()) {
			protected boolean accept(Object object) {
				return object instanceof Operation;
			}
		};

		return features;
	}
	
	public Collection getRelations(UmlClass object){
		if ((object == null)) {
			return null;
		}
		Classifier classifier = (Classifier) object;
		Collection c = ((UmlPackage)repositoryWrapper.getModel(modelName)).getCore().getAParticipantAssociation().getAssociation(classifier);
		ArrayList all = new ArrayList();
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			Object element = (Object) iter.next();
			if(element instanceof AssociationEnd){
				Relation rel = getAssociationData(element);
				all.add(rel);
			}
		}	
				return all;
	}
	
	/**
		* Returns Association information from the perspective of
		* a particular end of the association.
		* 
		* <p>The returned object can answers information about
		* whether the assocation is one2many, many2one, ...
		* </p>
		* 
		* @param object assocation end
		* @return DirectionalAssociationEnd directional association data
		* 
		*/
	   private Relation getAssociationData(Object object) {
		   if ((object == null) || !(object instanceof AssociationEnd)) {
			   return null;
		   }

		   AssociationEnd ae = (AssociationEnd) object;
		   Relation r = new Relation( ae);
		   return r;
	   }
	
	private boolean isNativeNamespace(org.omg.uml.foundation.core.UmlClass clazz){
		//Namespace ns = clazz.getNamespace(); //TODO: filter out native classes such as String etc.
		return false;
	}
	
	public Collection getClasses(){
		Collection c = new ArrayList();
			for (Iterator iter = repositoryWrapper.getUMLModelElements(this.modelName).iterator(); iter.hasNext();) {
				Object element = (Object) iter.next();
				if (element instanceof org.omg.uml.foundation.core.UmlClass) {
					org.omg.uml.foundation.core.UmlClass clazz = (org.omg.uml.foundation.core.UmlClass) element;
					c.add(clazz);
					//create a wrapper Class
				/*	mdatool.mof.uml.Class umlClass = new mdatool.mof.uml.Class(clazz);					
					
					//attributes
					Collection attributes = getAttributes(clazz);
					ArrayList attrib = new ArrayList();
					for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
						Attribute at = (Attribute) iterator.next();
						mdatool.mof.uml.Attribute umlAtt = new mdatool.mof.uml.Attribute(at);
						attrib.add(umlAtt);
					}
					umlClass.setAttributes(attrib);
					
					//operations
					Collection operations = getOperations(clazz);
					if (operations.size() > 0) {
						ArrayList o = new ArrayList();
						for (Iterator iterator = operations.iterator(); iterator.hasNext();) {
							Operation operation = (Operation) iterator.next();			
							mdatool.mof.uml.Operation op = new mdatool.mof.uml.Operation(operation);	
							o.add(op);								
						}
						umlClass.setOperations(o);
					}			
					//resolve relations
					umlClass.setRelations((ArrayList) getRelations(clazz));
					
					
				}*/
				}
			}
			return c;
	}

	public String getModelName(){
		return modelName;
	}

	
	public void addAttribute(UmlClass umlClass, UmlPackage umlPackage, String attributeName){
				   Attribute attribute = umlPackage.getCore().getAttribute().createAttribute();
				   attribute.setName(attributeName);
				   attribute.setOwner(umlClass);
	
	}



	public MDRepository getRepository(){
		return repositoryWrapper.getMDRepository();
	}
	/* (non-Javadoc)
	 * @see org.netbeans.api.mdr.events.MDRChangeListener#change(org.netbeans.api.mdr.events.MDRChangeEvent)
	 */
	public void change(MDRChangeEvent e) {
		System.out.println("MDRChangeEvent from UmlModelWrapper:" + e.getSource());
	}
}
