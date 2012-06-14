/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.lib.jmi.uml2mof;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.jmi.model.AliasType;
import javax.jmi.model.Association;
import javax.jmi.model.Classifier;
import javax.jmi.model.CollectionType;
import javax.jmi.model.DirectionKind;
import javax.jmi.model.DirectionKindEnum;
import javax.jmi.model.EnumerationType;
import javax.jmi.model.Import;
import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofClass;
import javax.jmi.model.MofException;
import javax.jmi.model.MofPackage;
import javax.jmi.model.MultiplicityType;
import javax.jmi.model.NameNotFoundException;
import javax.jmi.model.PrimitiveType;
import javax.jmi.model.PrimitiveTypeClass;
import javax.jmi.model.Reference;
import javax.jmi.model.ScopeKindEnum;
import javax.jmi.model.StructuralFeature;
import javax.jmi.model.StructureField;
import javax.jmi.model.StructureType;
import javax.jmi.model.Tag;
import javax.jmi.model.VisibilityKindEnum;
import javax.jmi.reflect.RefObject;
import org.omg.uml.behavioralelements.commonbehavior.UmlException;
import org.omg.uml.modelmanagement.UmlPackage;
import org.omg.uml.foundation.core.AStereotypeExtendedElement;
import org.omg.uml.foundation.core.AssociationEnd;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.BehavioralFeature;
import org.omg.uml.foundation.core.DataType;
import org.omg.uml.foundation.core.Dependency;
import org.omg.uml.foundation.core.Element;
import org.omg.uml.foundation.core.Enumeration;
import org.omg.uml.foundation.core.EnumerationLiteral;
import org.omg.uml.foundation.core.GeneralizableElement;
import org.omg.uml.foundation.core.Generalization;
import org.omg.uml.foundation.core.Method;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Namespace;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.Parameter;
import org.omg.uml.foundation.core.Stereotype;
import org.omg.uml.foundation.core.TaggedValue;
import org.omg.uml.foundation.core.UmlAssociation;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.datatypes.AggregationKind;
import org.omg.uml.foundation.datatypes.AggregationKindEnum;
import org.omg.uml.foundation.datatypes.ChangeableKindEnum;
import org.omg.uml.foundation.datatypes.Multiplicity;
import org.omg.uml.foundation.datatypes.MultiplicityRange;
import org.omg.uml.foundation.datatypes.OrderingKindEnum;
import org.omg.uml.foundation.datatypes.ParameterDirectionKind;
import org.omg.uml.foundation.datatypes.ParameterDirectionKindEnum;
import org.omg.uml.modelmanagement.ElementImport;
import org.openide.ErrorManager;

/**
 *
 * @author  mm109185
 */
public class Transformer {
    // stereotypes
    private static final String STEREOTYPE_METAMODEL = "metamodel";
    private static final String STEREOTYPE_IMPLICIT = "implicit";
    private static final String STEREOTYPE_REFERENCE = "reference";

    private static final String STEREOTYPE_ALIAS = "alias";
    private static final String STEREOTYPE_EXCEPTION = "exception";
    private static final String STEREOTYPE_STRUCTURE = "structure";
    private static final String STEREOTYPE_COLLECTION = "collection";
    private static final String STEREOTYPE_IMPORTS = "import";
    private static final String STEREOTYPE_CLUSTERS = "clustering";
    private static final String STEREOTYPE_SPECIALIZES = "subtyping";
    private static final String STEREOTYPE_ENUMERATION = "enumeration";
    
    // tags
//    private static final String TAG_CLUSTERED_IMPORT = "org.omg.uml2mof.clusteredImport";
    private static final String TAG_IMPLICIT_REFERENCES = "org.omg.uml2mof.hasImplicitReferences";
    private static final String TAG_SINGLETON = "org.omg.uml2mof.isSingleton";
    private static final String TAG_ATTR_DERIVED = "org.omg.uml2mof.isDerived";
    private static final String TAG_UNIQUE = "org.omg.uml2mof.isUnique";
    private static final String TAG_REFERENCED_END = "org.omg.uml2mof.referencedEnd";
    private static final String TAG_MULTIPLICITY = "org.omg.uml2mof.multiplicity";
    private static final String TAG_ORDERED = "org.omg.uml2mof.isOrdered";

    private static final String TAG_RAISED_EXCEPTIONS = "org.netbeans.uml2mof.raisedExceptions";
    private static final String TAG_ALIAS_FOR = "org.netbeans.uml2mof.aliasFor";
    private static final String TAG_ASSOC_DERIVED = "org.omg.uml2mof.isDerived";
    
    // special attributes
    private static final String ATTR_ITEMS = "items";
    
    private static final ErrorManager logger = ErrorManager.getDefault().getInstance("org.netbeans.lib.uml2mof.Logger");
    
    // source extent
    private final org.omg.uml.UmlPackage uml;
    // target extent
    private final ModelPackage mof;
    // cache of all stereotypes used in model
    private final HashMap stereotypes = new HashMap();
    // mapped elements (used for late resolution of supertypes, types and imports) (key=source, value=target)
    private final HashMap elements = new HashMap();
    // imports that should be resolved (key=imported element, value=list of imports)
//    private final HashMap imports = new HashMap();
    // registry of fully qualified names to resolve (key=object referencing elements identified by FQNs, value = list of FQNs)
    private final HashMap fqnsToResolve = new HashMap();
    // implicit references that should be added to the correct containers
    private final HashSet references = new HashSet();
    // PrimitiveTypes package
    private MofPackage primitivesPackage;
    // table of primitive types (key=type name, value=type)
    private HashMap primitives;
    // packages that need to import PrimitiveTypes package
    private final HashSet ptImports = new HashSet();
    // tags that are attached to more than one element
    private final HashMap multiTags = new HashMap();
    
    // indicates, whether the package that is being processed has implicit references
    private boolean implicitReferences;
    // outermost package that is currently being processed
    private MofPackage outermost;
    
    /** Creates a new instance of Transformer */
    private Transformer(org.omg.uml.UmlPackage uml, ModelPackage mof) {
        this.uml = uml;
        this.mof = mof;
        // cache all the stereotypes
        for (Iterator it = uml.getCore().getStereotype().refAllOfClass().iterator(); it.hasNext();) {
            Stereotype temp = (Stereotype) it.next();
            stereotypes.put(temp.getName(), temp);
        }
    }
    
    public static void execute(org.omg.uml.UmlPackage uml, ModelPackage mof) {
        new Transformer(uml, mof).transform(); 
    }
    
    private void transform() {
        // get all outermost elements, and find all outermost models extended by metamodel stereotype
        for (Iterator it = uml.getCore().getNamespace().refAllOfType().iterator(); it.hasNext();) {
            Namespace temp = (Namespace) it.next();
            if (temp.getNamespace() == null) {
                lookForModel(temp);
            }
        }
        resolveTypes();
        resolveFQNs();
        resolveReferences();
        resolveImports();
    }
    
    private void resolveTypes() {
        info("*** Resolving types...");
        for (Iterator it = elements.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getKey() instanceof GeneralizableElement && entry.getValue() instanceof javax.jmi.model.GeneralizableElement) {
                GeneralizableElement ge = (GeneralizableElement) entry.getKey();
                Collection supertypes = ((javax.jmi.model.GeneralizableElement) entry.getValue()).getSupertypes();
                for (Iterator g = ge.getGeneralization().iterator(); g.hasNext();) {
                    Generalization temp = (Generalization) g.next();
                    supertypes.add(elements.get(temp.getParent()));
                }
            }
            if (entry.getKey() instanceof AssociationEnd) {
                ((javax.jmi.model.AssociationEnd) entry.getValue()).setType(mapType(((AssociationEnd) entry.getKey()).getParticipant(), entry.getValue()));
            } else if (entry.getKey() instanceof Attribute) {
                ((javax.jmi.model.TypedElement) entry.getValue()).setType(mapType(((Attribute) entry.getKey()).getType(), entry.getValue()));
            } else if (entry.getKey() instanceof Parameter) {
                ((javax.jmi.model.TypedElement) entry.getValue()).setType(mapType(((Parameter) entry.getKey()).getType(), entry.getValue()));
            }
        }
    }
    
    private void resolveFQNs() {
        info("*** Resolving FQNs");
        for (Iterator it = fqnsToResolve.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getKey() instanceof javax.jmi.model.Operation) {
                mapByFQNs(((javax.jmi.model.Operation) entry.getKey()).getExceptions(),(Collection) entry.getValue());
            } else if (entry.getKey() instanceof AliasType) {
                ((AliasType) entry.getKey()).setType((javax.jmi.model.Classifier) resolveFQN((String) ((Collection) entry.getValue()).iterator().next()));
            } else if (entry.getKey() instanceof Reference) {
                ((Reference) entry.getKey()).setReferencedEnd((javax.jmi.model.AssociationEnd) resolveFQN((String) ((Collection) entry.getValue()).iterator().next()));
            }
        }
    }
    
    private void resolveReferences() {
        info("*** Resolving references");
        for (Iterator it = elements.values().iterator(); it.hasNext();) {
            Object temp = it.next();
            if (temp instanceof Reference) {
                Reference ref = (Reference) temp;
                javax.jmi.model.AssociationEnd end = ref.getReferencedEnd();
                ref.setChangeable(end.isChangeable());
                ref.setMultiplicity(end.getMultiplicity());
                ref.setType(end.getType());
            }
        }
        for (Iterator it = references.iterator(); it.hasNext();) {
            Reference ref = (Reference) it.next();
            ref.setContainer(ref.getExposedEnd().getType());
            ref.setType(ref.getReferencedEnd().getType());
        }
    }
    
    private void resolveImports() {
        info("*** Resolving imports");
        for (Iterator it = ptImports.iterator(); it.hasNext();) {
            MofPackage pkg = (MofPackage) it.next();
            Import i = mof.getImport().createImport("PrimitiveTypes", "", VisibilityKindEnum.PUBLIC_VIS, false);
            i.setContainer(pkg);
            i.setImportedNamespace(primitivesPackage);
        }
        
        for (Iterator it = uml.getCore().getDependency().refAllOfClass().iterator(); it.hasNext();) {
            Dependency dep = (Dependency) it.next();
            Object client = elements.get(dep.getClient().iterator().next());
            Object supplier = elements.get(dep.getSupplier().iterator().next());
            if (client != null && supplier != null && client instanceof MofPackage && supplier instanceof MofPackage) {
                if (isOfType(dep, STEREOTYPE_IMPORTS) || isOfType(dep, STEREOTYPE_CLUSTERS)) {
                    Import i = mof.getImport().createImport(dep.getName(), getAnnotation(dep), VisibilityKindEnum.PUBLIC_VIS, isOfType(dep, STEREOTYPE_CLUSTERS));
                    i.setContainer((MofPackage) client);
                    i.setImportedNamespace((MofPackage) supplier);
                } else if (isOfType(dep, STEREOTYPE_SPECIALIZES)) {
                    ((MofPackage) client).getSupertypes().add((MofPackage) supplier);
                }
            }
        }
    }
    
    private void lookForModel(Namespace ns) {
        if (ns instanceof UmlPackage && isOfType(ns, STEREOTYPE_METAMODEL)) {
            info("*** Outermost package found: " + ns.getName());
            transform((UmlPackage) ns, null);
        } else {
            for (Iterator it = ns.getOwnedElement().iterator(); it.hasNext();) {
                Object temp = it.next();
                if (temp instanceof Namespace) lookForModel((Namespace) temp);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    // Transformation Methods
    ///////////////////////////////////////////////////////////////////////////////
    private void transformPackage(UmlPackage model, MofPackage container) {
        if (elements.containsKey(model)) return;

        // create corresponding MofPackage
        // [PENDING] visibility is always set to PUBLIC_VIS
        info("transforming package: " + model.getName());
        MofPackage pkg = mof.getMofPackage().createMofPackage(model.getName(), getAnnotation(model), model.isRoot(), model.isLeaf(), model.isAbstract(), VisibilityKindEnum.PUBLIC_VIS);
        pkg.setContainer(container);
        elements.put(model, pkg);
        
        if (container == null) {
            implicitReferences = true;
            outermost = pkg;
            implicitReferences = "true".equals(getTagValue(model, TAG_IMPLICIT_REFERENCES, "true"));
        }
        
        // iterate through tagged values
        mapTags(model, pkg);
        
        // iterate through all imports
//        mapContent(model.getElementImport(), pkg);
        
        // iterate through content
        mapContent(model.getOwnedElement(), pkg);
    }
    
//    private void transform(ElementImport element, MofPackage container) {
//        if (elements.containsKey(element)) return;
//        
//        String name = element.getAlias();
//        Import i = mof.getImport().createImport(element.getAlias(), getAnnotation(element), VisibilityKindEnum.PUBLIC_VIS, clusteredImports.contains(element.getAlias()));
//        i.setContainer(outermost);
//        ModelElement imported = element.getImportedElement();
//        ArrayList list = (ArrayList) imports.get(imported);
//        if (list == null) {
//            list = new ArrayList();
//            imports.put(imported, list);
//        }
//        list.add(i);
//        elements.put(element, i);
//    }
    
    private void transformClass(UmlClass cls, javax.jmi.model.Namespace container) {
        if (elements.containsKey(cls)) return;
        
        javax.jmi.model.Namespace mofElement;
        if (isOfType(cls, STEREOTYPE_STRUCTURE)) {
            mofElement = mof.getStructureType().createStructureType(cls.getName(), getAnnotation(cls), true, true, false, VisibilityKindEnum.PUBLIC_VIS);
        } else if (isOfType(cls, STEREOTYPE_EXCEPTION)) {
            mofElement = mof.getMofException().createMofException(cls.getName(), getAnnotation(cls), ScopeKindEnum.CLASSIFIER_LEVEL, VisibilityKindEnum.PUBLIC_VIS);
        } else if (isOfType(cls, STEREOTYPE_COLLECTION)) {
            mofElement = mof.getCollectionType().createCollectionType(cls.getName(), getAnnotation(cls), true, true, false, VisibilityKindEnum.PUBLIC_VIS, null);
        } else if (isOfType(cls, STEREOTYPE_ENUMERATION)) {
            mofElement = mof.getEnumerationType().createEnumerationType(cls.getName(), getAnnotation(cls), true, true, false, VisibilityKindEnum.PUBLIC_VIS, null);
        } else {
            mofElement = mof.getMofClass().createMofClass(cls.getName(), getAnnotation(cls), cls.isRoot(), cls.isLeaf(), cls.isAbstract(), VisibilityKindEnum.PUBLIC_VIS, "true".equals(getTagValue(cls, TAG_SINGLETON, null)));
        }
        elements.put(cls, mofElement);
        mofElement.setContainer(container);
        mapTags(cls, mofElement);
        mapContent(cls.getOwnedElement(), mofElement);
        mapContent(cls.getFeature(), mofElement);
    }
    
//    private void transform(UmlException ex, javax.jmi.model.Namespace container) {
//        if (elements.containsKey(ex)) return;
//        
//        MofException mofEx = mof.getMofException().createMofException(ex.getName(), getAnnotation(ex), ScopeKindEnum.CLASSIFIER_LEVEL, VisibilityKindEnum.PUBLIC_VIS);
//        elements.put(ex, mofEx);
//        mofEx.setContainer(container);
//        mapTags(ex, mofEx);
//        mapContent(ex.getOwnedElement(), mofEx);
//        mapContent(ex.getFeature(), mofEx);
//    }
    
    private void transformEnum(Enumeration enume, javax.jmi.model.Namespace container) {
        if (elements.containsKey(enume)) return;
     
        EnumerationType mofEnum = mof.getEnumerationType().createEnumerationType(enume.getName(), getAnnotation(enume), true, true, false, VisibilityKindEnum.PUBLIC_VIS, null);
        elements.put(enume, mofEnum);
        mofEnum.setContainer(container);
        mapTags(enume, mofEnum);
        
        for (Iterator it = enume.getLiteral().iterator(); it.hasNext();) {
            EnumerationLiteral literal = (EnumerationLiteral) it.next();
            mofEnum.getLabels().add(literal.getName());
        }
    }
    
    private void transformDataType(DataType type, javax.jmi.model.Namespace container) {
        if (elements.containsKey(type)) return;
        
        if (isOfType(type, STEREOTYPE_ALIAS)) {
            AliasType alias = mof.getAliasType().createAliasType(type.getName(), getAnnotation(type), true, true, false, VisibilityKindEnum.PUBLIC_VIS);
            mapTags(type, alias);
            elements.put(type, alias);
            alias.setContainer(container);
            fqnsToResolve.put(alias, getTagValues(type, TAG_ALIAS_FOR));
        }
    }
    
    private void transformAssoc(UmlAssociation assoc, javax.jmi.model.Namespace container) {
        if (elements.containsKey(assoc)) return;
        if (isOfType(assoc, STEREOTYPE_IMPLICIT)) return;
        
        Association mofAssoc = mof.getAssociation().createAssociation(getName(assoc.getName()), getAnnotation(assoc), assoc.isRoot(), assoc.isLeaf(), assoc.isAbstract(), VisibilityKindEnum.PUBLIC_VIS, "true".equals(getTagValue(assoc, TAG_ASSOC_DERIVED, null)));
        elements.put(assoc, mofAssoc);
        mofAssoc.setContainer(container);
        mapTags(assoc, mofAssoc);
        mapContent(assoc.getConnection(), mofAssoc);
    }
    
    private void transformAssocEnd(AssociationEnd assocEnd, javax.jmi.model.Namespace container) {
        if (elements.containsKey(assocEnd)) return;
        
        boolean changeable = assocEnd.getChangeability() == null || ChangeableKindEnum.CK_CHANGEABLE.equals(assocEnd.getChangeability());
        javax.jmi.model.AssociationEnd mofEnd = mof.getAssociationEnd().createAssociationEnd(assocEnd.getName(), getAnnotation(assocEnd), true /*assocEnd.isNavigable()*/, getAggregation(assocEnd.getAggregation()), getMultiplicity(assocEnd.getMultiplicity(), OrderingKindEnum.OK_ORDERED.equals(assocEnd.getOrdering()), true), changeable);
        elements.put(assocEnd, mofEnd);
        mofEnd.setContainer(container);
        mapTags(assocEnd, mofEnd);
        
        if (implicitReferences && assocEnd.isNavigable()) {
            Reference ref = mof.getReference().createReference(mofEnd.getName(), mofEnd.getAnnotation(), ScopeKindEnum.INSTANCE_LEVEL, VisibilityKindEnum.PUBLIC_VIS, mofEnd.getMultiplicity(), mofEnd.isChangeable());
            ref.setReferencedEnd(mofEnd);
            references.add(ref);
        }
    }
    
    private void transformAttr(Attribute attr, MofClass container) {
        if (elements.containsKey(attr)) return;
        
        StructuralFeature mofFeature;
        if (isOfType(attr, STEREOTYPE_REFERENCE)) {
            mofFeature = mof.getReference().createReference(attr.getName(), getAnnotation(attr), ScopeKindEnum.INSTANCE_LEVEL, VisibilityKindEnum.PUBLIC_VIS, null, true);
            fqnsToResolve.put(mofFeature, getTagValues(attr, TAG_REFERENCED_END));
        } else {
            boolean changeable = attr.getChangeability() == null || ChangeableKindEnum.CK_CHANGEABLE.equals(attr.getChangeability());
            mofFeature = mof.getAttribute().createAttribute(getName(attr.getName()), getAnnotation(attr), getScope(attr.getOwnerScope()), VisibilityKindEnum.PUBLIC_VIS, getMultiplicity(attr.getMultiplicity(), getOrdering(attr), "true".equals(getTagValue(attr, TAG_UNIQUE, null))), changeable, "true".equals(getTagValue(attr, TAG_ATTR_DERIVED, null)));
        }
        elements.put(attr, mofFeature);
        mofFeature.setContainer(container);
        mapTags(attr, mofFeature);
    }
    
    private void transformOperation(Operation oper, MofClass container) {
        if (elements.containsKey(oper)) return;
        
        javax.jmi.model.Operation mofOper = mof.getOperation().createOperation(oper.getName(), getAnnotation(oper), getScope(oper.getOwnerScope()), VisibilityKindEnum.PUBLIC_VIS, oper.isQuery());
        elements.put(oper, mofOper);
        mofOper.setContainer(container);
        mapTags(oper, mofOper);
        mapContent(oper.getParameter(), mofOper);
        List exceptions = getTagValues(oper, TAG_RAISED_EXCEPTIONS);
        if (exceptions.size() == 1) {
            StringTokenizer st = new StringTokenizer((String) exceptions.get(0), ",");
            if (st.countTokens() > 1) {
                exceptions.clear();
                while (st.hasMoreTokens()) {
                    exceptions.add(st.nextToken());
                }
            }
        }
        fqnsToResolve.put(mofOper, exceptions);
    }
    
    private void transformEParam(Attribute attr, MofException ex) {
        if (elements.containsKey(attr)) return;
        
        javax.jmi.model.Parameter mofParam = mof.getParameter().createParameter(attr.getName(), getAnnotation(attr), DirectionKindEnum.OUT_DIR, getMultiplicity(attr.getMultiplicity(), getOrdering(attr), "true".equals(getTagValue(attr, TAG_UNIQUE, null))));
        elements.put(attr, mofParam);
        mofParam.setContainer(ex);
        mapTags(attr, mofParam);
    }
    
    private void transformOParam(Parameter param, javax.jmi.model.Operation mofOper) {
        if (elements.containsKey(param)) return;
        
        javax.jmi.model.Parameter mofParam = mof.getParameter().createParameter(param.getName(), getAnnotation(param), getDirection(param.getKind()), getMultiplicity(getTagValue(param, TAG_MULTIPLICITY, "1"), "true".equals(getTagValue(param, TAG_ORDERED, null)), "true".equals(getTagValue(param, TAG_UNIQUE, null))));
        elements.put(param, mofParam);
        mofParam.setContainer(mofOper);
        mapTags(param, mofParam);
    }
    
    private void transformStructField(Attribute attr, StructureType struct) {
        if (elements.containsKey(attr)) return;
        
        StructureField mofField = mof.getStructureField().createStructureField(attr.getName(), getAnnotation(attr));
        elements.put(attr, mofField);
        mofField.setContainer(struct);
        mapTags(attr, mofField);
    }
    
    private void transformCollection(Attribute attr, CollectionType coll) {
        if (elements.containsKey(attr) || !ATTR_ITEMS.equals(attr.getName())) return;
        mapTags(attr, coll);
        coll.setMultiplicity(getMultiplicity(attr.getMultiplicity(), OrderingKindEnum.OK_ORDERED.equals(attr.getOrdering()), "true".equals(getTagValue(attr, TAG_UNIQUE, null))));
        elements.put(attr, coll);
    }
    
    private void transform(Object obj, javax.jmi.model.Namespace container) {
        if (obj instanceof UmlPackage) {
            transformPackage((UmlPackage) obj, (MofPackage) container);
        } else if (obj instanceof UmlAssociation) {
            transformAssoc((UmlAssociation) obj, container);
        } else if (obj instanceof AssociationEnd) {
            transformAssocEnd((AssociationEnd) obj, container);
        } else if (obj instanceof Attribute) {
            Attribute attr = (Attribute) obj;
            if (container instanceof MofClass) {
                transformAttr(attr, (MofClass) container);
            } else if (container instanceof CollectionType) {
                transformCollection(attr, (CollectionType) container);
            } else if (container instanceof MofException) {
                transformEParam(attr, (MofException) container);
            } else if (container instanceof StructureType) {
                transformStructField(attr, (StructureType) container);
            } else if (container instanceof EnumerationType) {
                String name = attr.getName();
                List labels = ((EnumerationType) container).getLabels();
                if (!labels.contains(name)) {
                    labels.add(attr.getName());
                }
            } else {
                warning("Ignoring attribute: " + attr.getName() + " from container: " + container.getName());
            }
        } else if (obj instanceof Enumeration) {
            transformEnum((Enumeration) obj, container);
        } else if (obj instanceof UmlClass) {
            transformClass((UmlClass) obj, container);
        } else if (obj instanceof DataType) {
            transformDataType((DataType) obj, container);
        } else if (obj instanceof Parameter) {
            transformOParam((Parameter) obj, (javax.jmi.model.Operation) container);
        } else if (obj instanceof Operation) {
            transformOperation((Operation) obj, (MofClass) container);
        } else if (obj instanceof Generalization) {
            // ignore
        } else {
            // element does not fit any specific transofm methods => ignore it
            warning("Ignoring instance of " + obj.getClass().getName() + "; container instance of " + container.getClass().getName());
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////
    // Utility Methods
    //////////////////////////////////////////////////////////////////////////////////
    
    private String getAnnotation(Object umlElement) {
        // [PENDING] implement correctly
        return "";
    }
    
    private String getTagValue(ModelElement element, String tagName, String defaultValue) {
        for (Iterator it = element.getTaggedValue().iterator(); it.hasNext();) {
            TaggedValue tag = (TaggedValue) it.next();
            String temp = getTagName(tag);
            if (tagName.equals(temp)) {
                return (String) tag.getDataValue().iterator().next();
            }
        }
        return defaultValue;
    }
    
    private String getTagName(TaggedValue tag) {
        String temp = tag.getName();
        if (temp == null && tag.getType() != null) {
            // this is a workaround for a bug in Poseidon's tag serialization
            // (in version 1.5 it uses TagType instead of Name)
            temp = tag.getType().getTagType();
            if (temp == null || temp.indexOf('.') < 0) {
                temp = tag.getType().getName();
            }
        }
        return temp;
    }        
    
    private ArrayList getTagValues(ModelElement element, String tagName) {
        for (Iterator it = element.getTaggedValue().iterator(); it.hasNext();) {
            TaggedValue tag = (TaggedValue) it.next();
            String temp = getTagName(tag);
            if (tagName.equals(temp)) {
                return new ArrayList(tag.getDataValue());
            }
        }
        return new ArrayList();
    }
    
    private void mapTags(ModelElement umlElement, javax.jmi.model.ModelElement mofElement) {
        // iterate through tagged values
        for (Iterator it = umlElement.getTaggedValue().iterator(); it.hasNext();) {
            TaggedValue tag = (TaggedValue) it.next();
            String tagName = getTagName(tag);
            mapTag(tagName, tag, mofElement);
        }
    }

    private void mapTag(String tagName, TaggedValue tag, javax.jmi.model.ModelElement mofElement) {
        if (tagName != null 
          && !tagName.equals("element.uuid") 
          && !tagName.startsWith("org.omg.uml2mof.") 
          && !tagName.startsWith("org.netbeans.uml2mof.")) {
            info("copying tag: " + tagName);
            int hash = tagName.indexOf('#');
            String name = tagName;
            Collection value = tag.getDataValue();
            value.remove("");
            Tag mofTag = null;
            if (hash > -1) {
                name = name.substring(0, hash);
                mofTag = (Tag) multiTags.get(tagName);
            }
            if (mofTag == null) {
                mofTag = mof.getTag().createTag(name, getAnnotation(tag), name, null);
                mofTag.setContainer(outermost);
                if (hash > -1) {
                    multiTags.put(tagName, mofTag);
                }
            }
            mofTag.getValues().addAll(value);
            mofTag.getElements().add(mofElement);
            elements.put(tag, mofTag);
        }
    }
    
    private void mapContent(Collection content, javax.jmi.model.Namespace container) {
        for (Iterator it = content.iterator(); it.hasNext();) {
            transform(it.next(), container);
        }
    }        
    
    private javax.jmi.model.AggregationKind getAggregation(AggregationKind aggr) {
        if (AggregationKindEnum.AK_AGGREGATE.equals(aggr)) {
            return javax.jmi.model.AggregationKindEnum.SHARED;
        } else if (AggregationKindEnum.AK_COMPOSITE.equals(aggr)) {
            return javax.jmi.model.AggregationKindEnum.COMPOSITE;
        } else {
            return javax.jmi.model.AggregationKindEnum.NONE;
        }
    }
    
    private MultiplicityType getMultiplicity(Multiplicity m, boolean isOrdered, boolean isUnique) {
        int upper = 1;
        int lower = 1;
        if (m != null) {
            MultiplicityRange range = (MultiplicityRange) m.getRange().iterator().next();
            lower = range.getLower();
            upper = range.getUpper();
        }
        return mof.createMultiplicityType(lower, upper, isOrdered, isUnique);
    }
    
    private MultiplicityType getMultiplicity(String m, boolean isOrdered, boolean isUnique) {
        StringTokenizer st = new StringTokenizer(m, ".");
        int lower = 0;
        int upper;
        try {
            lower = getBound(st.nextToken());
            upper = getBound(st.nextToken());
        } catch (NoSuchElementException e) {
            // ignore
            switch (lower) {
                case 0: upper = -1; break;
                case -1: lower = 0; upper = -1; break;
                default: upper = lower;
            }
        }
        return mof.createMultiplicityType(lower, upper, isOrdered, isUnique);
    }
    
    private int getBound(String bound) {
        try {
            return Integer.parseInt(bound);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private String getName(String name) {
        if (name.startsWith("/")) {
            return name.substring(1).trim();
        }
        return name;
    }
    
    private ScopeKindEnum getScope(org.omg.uml.foundation.datatypes.ScopeKind s) {
        if (org.omg.uml.foundation.datatypes.ScopeKindEnum.SK_CLASSIFIER.equals(s)) {
            return ScopeKindEnum.CLASSIFIER_LEVEL;
        } else {
            return ScopeKindEnum.INSTANCE_LEVEL;
        }
    }
    
    private boolean getOrdering(Attribute attr) {
        String o = getTagValue(attr, TAG_ORDERED, null);
        if (o == null) {
            return OrderingKindEnum.OK_ORDERED.equals(attr.getOrdering());
        } else {
            return "true".equals(o);
        }
    }

    private DirectionKindEnum getDirection(ParameterDirectionKind dk) {
        if (ParameterDirectionKindEnum.PDK_RETURN.equals(dk)) {
            return DirectionKindEnum.RETURN_DIR;
        } else if (ParameterDirectionKindEnum.PDK_INOUT.equals(dk)) {
            return DirectionKindEnum.INOUT_DIR;
        } else if (ParameterDirectionKindEnum.PDK_OUT.equals(dk)) {
            return DirectionKindEnum.OUT_DIR;
        } else {
            return DirectionKindEnum.IN_DIR;
        }
    }
    
    private boolean isOfType(ModelElement element, String stereotype) {
        return element.getStereotype().contains(stereotypes.get(stereotype));
    }
    
    private javax.jmi.model.Classifier mapType(ModelElement element, Object obj) {
        javax.jmi.model.Classifier result = null;
        if (element instanceof DataType) {
            initPrimitives();
            ptImports.add(((RefObject) obj).refOutermostComposite());
            result = (javax.jmi.model.Classifier) primitives.get(element.getName());
        }
        if (result == null) {
            result = (javax.jmi.model.Classifier) elements.get(element);
        }
        return result;
    }
    
    private void initPrimitives() {
        if (primitives == null) {
            primitives = new HashMap();
            primitivesPackage = mof.getMofPackage().createMofPackage("PrimitiveTypes", "", true, true, false, VisibilityKindEnum.PUBLIC_VIS);
            createPrimitive("Integer");
            createPrimitive("Long");
            createPrimitive("Float");
            createPrimitive("Double");
            createPrimitive("Boolean");
            createPrimitive("String");
        }
    }
    
    private void createPrimitive(String name) {
        PrimitiveType pt = mof.getPrimitiveType().createPrimitiveType(name, "", true, true, false, VisibilityKindEnum.PUBLIC_VIS);
        primitives.put(name, pt);
        pt.setContainer(primitivesPackage);
    }
    
    private void mapByFQNs(Collection target, Collection source) {
        for (Iterator it = source.iterator(); it.hasNext();) {
            String fqn = (String) it.next();
            Object result = resolveFQN(fqn);
            if (result == null) {
                warning("Fully qualified name not resolved: " + fqn);
            } else {
                target.add(result);
            }
        }
    }
    
    private javax.jmi.model.ModelElement resolveFQN(String fqn) {
        javax.jmi.model.ModelElement result = null;
        StringTokenizer st = new StringTokenizer(fqn, ".:");
        String name = st.nextToken();
        if ("PrimitiveTypes".equals(name)) initPrimitives();
        for (Iterator c = mof.getMofPackage().refAllOfClass().iterator(); c.hasNext();) {
            result = (javax.jmi.model.ModelElement) c.next();
            if (result.getContainer() == null && name.equals(result.getName())) {
                break;
            } else {
                result = null;
            }
        }
        while (result != null && st.hasMoreTokens()) {
            try {
                result = ((javax.jmi.model.Namespace) result).lookupElement(st.nextToken());
            } catch (NameNotFoundException ex) {
                result = null;
            } catch (ClassCastException ex) {
                result = null;
            }
        }
        return result;
    }
    
    private static void warning(String text) {
        logger.log(ErrorManager.WARNING, text);
    }
    
    private static void info(String text) {
        logger.log(ErrorManager.INFORMATIONAL, text);
    }
}
