/*
 * Created on 07-11-2003
 * 
 */
package mdatool.mof.modelwrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofClass;
import javax.jmi.model.MofClassClass;
import javax.jmi.model.MofPackage;
import javax.jmi.model.ScopeKindEnum;
import javax.jmi.model.StructuralFeature;
import javax.jmi.model.VisibilityKind;
import javax.jmi.model.VisibilityKindEnum;
import javax.jmi.reflect.RefClass;
import javax.jmi.reflect.RefPackage;

import mdatool.gui.application.Main;
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
 */
public class MOFModelWrapper implements MDRChangeListener{

	//private static UmlModelWrapper instance = null;	
	private RepositoryManager repositoryWrapper;
	private String modelName;
	private RefPackage refPackage;
	private boolean isUml=false;
	
	private final ModelPackage mof;
	// cache of all stereotypes used in model
	private final HashMap stereotypes = new HashMap();
	// outermost package that is currently being processed
	private MofPackage outermost;
	
	public MOFModelWrapper(RepositoryManager repositoryWrapper, String modelName,ModelPackage mof) {
		this.repositoryWrapper = repositoryWrapper;
		this.modelName = modelName;
		this.mof = mof;
		System.out.println("Created new MOFModelWrapper for model: " + modelName);
	//	repository.getRepository().addListener(this);
	}
	public MOFModelWrapper(RepositoryManager repositoryWrapper, String modelName,RefPackage uml) {
		this.isUml = true;
		this.repositoryWrapper = repositoryWrapper;
		this.modelName = modelName;
		this.mof = null;
		this.refPackage = uml;	
		System.out.println("Created new UMLModelWrapper: " + modelName + " with " + uml.refAllPackages().size()+ " packages and " + uml.refAllClasses().size()+ " number of classes");
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
	public static Collection getAttributes(Classifier object) {
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
	public static Collection getMOFAttributes(MofClass object) {
		if ((object == null) || !(object instanceof MofClass)) {
			return null;
		}
				
		MofClass mof = (MofClass) object;
		Collection features = new FilteredCollection(mof.getContents()) {
			protected boolean accept(Object object) {
				return object instanceof javax.jmi.model.Attribute;
			}
		};
		return features;
	}


	public static Collection getOperations(Classifier object) {
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
	
	public static Collection getMOFOperations(MofClass object) {
		if ((object == null) || !(object instanceof MofClass)) {
			return null;
		}
		MofClass mof = (MofClass) object;
		Collection features = new FilteredCollection(mof.getContents()) {
			protected boolean accept(Object object) {
				return object instanceof javax.jmi.model.Operation;
			}
		};

		return features;
	}
	public Collection getRelations(Classifier object){
		if ((object == null)) {
			return null;
		}
		Classifier classifier = (Classifier) object;
		Collection c  = null;
		if(classifier instanceof UmlClass ){
			c = ((UmlPackage)repositoryWrapper.getModel(modelName)).getCore().getAParticipantAssociation().getAssociation(classifier);
		}else{
			//TODO: relations for MofClass
		}
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
	
	public Collection getMOFRelations(MofClass object){
		if ((object == null|| !(object instanceof MofClass))) {
			return null;
		}
		//Classifier classifier = (Classifier) object;
		Collection c = ((ModelPackage)repositoryWrapper.getModel(modelName)).refAllAssociations();
		
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
	
	/**
	 * 
	 * @return Collection of native MOF classes (MOF itself)
	 */
	public Collection getMOFClasses(){
		Collection c = new ArrayList();
		
		Collection classes =  this.refPackage .refAllClasses();
		
			for (Iterator iter = classes.iterator(); iter.hasNext();) {
				Object element = (Object) iter.next();
				System.out.println("Found class:" + element.getClass());
				if(element instanceof MofClassClass){
					MofClassClass mc = (MofClassClass)element;
					Collection c1 = mc.refAllOfClass();
					
					for (Iterator iterator = c1.iterator(); iterator.hasNext();) {
						MofClass e = (MofClass) iterator.next();
						System.out.println("CLAZZZZZZZ:" + e.getName());
						c.add(e);
					}
				}
			}
				return c;			
	}
	
	public Collection getMofModelClasses(){
		Collection clazzes = new FilteredCollection(this.refPackage .refAllClasses()) {
					protected boolean accept(Object object) {
						return object instanceof MofClass;
					}
		};
		return clazzes;
		/*Collection c = new ArrayList();
		RefPackage p = repositoryWrapper.getModel(Main.getInstance().getActiveInternalFrame().getTitle());
		System.out.println("Number of CLASSES in refpackage:" + p.refAllClasses().size());
		return p.refAllClasses();*/
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
	

	
/*	public void addAttribute(UmlClass umlClass, UmlPackage umlPackage, String attributeName){
				   Attribute attribute = umlPackage.getCore().getAttribute().createAttribute();
				   attribute.setName(attributeName);
				   attribute.setOwner(umlClass);
	
	}*/
	public void addAttribute(MofClass mofClass, String attributeName, boolean isPrivate){
		/*createAttribute(java.lang.String name, java.lang.String annotation, ScopeKind scope, 
		 *VisibilityKind visibility, MultiplicityType multiplicity, boolean isChangeable, 
		 *boolean isDerived) */
		VisibilityKindEnum vis = (isPrivate)? VisibilityKindEnum.PRIVATE_VIS:VisibilityKindEnum.PUBLIC_VIS;
		StructuralFeature mofFeature = mof.getAttribute().
											createAttribute(
													attributeName, 
													"Annotation?", 
													ScopeKindEnum.INSTANCE_LEVEL, 
													vis, 
													mof.createMultiplicityType(1, 1, false, true), //lower, upper, isOrdered, isUnique 
													true,  
													false);
		mofFeature.setContainer(mofClass);
	}
	


	public MDRepository getRepository(){
		return repositoryWrapper.getMDRepository();
	}
	/* (non-Javadoc)
	 * @see org.netbeans.api.mdr.events.MDRChangeListener#change(org.netbeans.api.mdr.events.MDRChangeEvent)
	 */
	public void change(MDRChangeEvent e) {
		System.out.println("MDRChangeEvent from MOFModelWrapper:" + e.getSource());
	}
	
	
	/**
	 * @param mofPack
	 * @param className
	 * @param annotation
	 * @param isRoot
	 * @param isLeaf
	 * @param isAbstract
	 * @param visibility
	 * @param isSingleton
	 * @return The newly created MofClass
	 */
	public MofClass createMofClass(ModelPackage mofPack, String className, String annotation,boolean isRoot,boolean isLeaf,boolean isAbstract,VisibilityKind visibility, boolean isSingleton ){	
			/*va.lang.String name, java.lang.String annotation, boolean isRoot,boolean isLeaf,
			boolean isAbstract, visibility, boolean isSingleton)*/
			return mofPack.getMofClass().createMofClass(className,annotation,isRoot,isLeaf,isAbstract,visibility, isSingleton);
	}
	
}
