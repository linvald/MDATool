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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofPackage;
import javax.jmi.xmi.XmiReader;
import javax.jmi.xmi.XmiWriter;
import org.netbeans.api.mdr.MDRManager;
import org.netbeans.api.mdr.MDRepository;
import org.omg.uml.UmlPackage;
import org.openide.ErrorManager;
import org.openide.util.Lookup;

/**
 *
 * @author  mm109185
 */
public class Main {
    private static final String MOF_INSTANCE = "MOFInstance";
    private static final String UML_INSTANCE = "UMLInstance";
    private static final String UML_MM = "UML";
    
    public static MDRepository rep;
    public static UmlPackage uml;
    public static ModelPackage mof;
    public static XmiReader reader;
    
    /** Creates a new instance of Main */
    public Main() {
    }
 
    public static void main(String args[]) {
        try {
            rep = MDRManager.getDefault().getDefaultRepository();
            String uri = new File(args[0]).toURL().toString();
            FileOutputStream out = new FileOutputStream(args[1]);
            reader = (XmiReader) Lookup.getDefault().lookup(XmiReader.class);
            XmiWriter writer = (XmiWriter) Lookup.getDefault().lookup(XmiWriter.class);
            init();
            rep.beginTrans(true);
            try {
                reader.read(uri, uml);
                Transformer.execute(uml, mof);
                writer.write(out, mof, null);
            } finally {
                rep.endTrans(true);
                MDRManager.getDefault().shutdownAll();
                out.close();
            }
        } catch (Exception e) {
            ErrorManager.getDefault().notify(ErrorManager.ERROR, e);
        }
    }
    
    private static void init() throws Exception {
        mof = (ModelPackage) rep.getExtent(MOF_INSTANCE);
        uml = (UmlPackage) rep.getExtent(UML_INSTANCE);
        if (mof == null) {
            mof = (ModelPackage) rep.createExtent(MOF_INSTANCE);
        }
        if (uml == null) {
            uml = (UmlPackage) rep.createExtent(UML_INSTANCE, getUmlPackage());
        }
    }
    
    private static MofPackage getUmlPackage() throws Exception {
        ModelPackage umlMM = (ModelPackage) rep.getExtent(UML_MM);
        if (umlMM == null) {
            umlMM = (ModelPackage) rep.createExtent(UML_MM);
        }
        MofPackage result = getUmlPackage(umlMM);
        if (result == null) {
            reader.read(UmlPackage.class.getResource("resources/01-02-15_Diff.xml").toString(), umlMM);
        }
        result = getUmlPackage(umlMM);
        return result;
    }
    
    private static MofPackage getUmlPackage(ModelPackage umlMM) {
        for (Iterator it = umlMM.getMofPackage().refAllOfClass().iterator(); it.hasNext();) {
            MofPackage pkg = (MofPackage) it.next();
            if (pkg.getContainer() == null && "UML".equals(pkg.getName())) {
                return pkg;
            }
        }
        return null;
    }
}
