/*
 * Created on 14-11-2003
 * 
 */
package mdatool.gui.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.jmi.model.MofClass;
import javax.jmi.model.PrimitiveType;
import javax.jmi.model.VisibilityKindEnum;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import mdatool.gui.application.Main;
import mdatool.gui.shapes.uml.UmlShape;
import mdatool.gui.util.IconUtil;
import mdatool.mof.modelwrappers.MOFModelWrapper;
import mdatool.mof.repository.RepositoryManager;
import mdatool.mof.uml.Relation;

import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Operation;

/**
 * @author Jesper Linvald
 * @version
 * 
 * Description:
 */
public class LoadMOFXmiAction extends AbstractAction {

	JFileChooser fileChooser;
	public final static String XMI = "xmi";
	public final static String ZARGO = "zargo";

	public LoadMOFXmiAction() {
		putValue(Action.SMALL_ICON, IconUtil.getGeneralIcon("Open", 24));
		this.putValue(Action.SHORT_DESCRIPTION, "Load xmi or zargo MOF file");
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Load XMI file");
		fileChooser.setApproveButtonText("Load");
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}

				String extension = LoadMOFXmiAction.getExtension(f);
				if (extension != null) {

					if (extension.equals(LoadMOFXmiAction.XMI)
						|| extension.equals(LoadMOFXmiAction.ZARGO)
						|| f.isDirectory()) {
						return true;
					} else {
						return false;
					}
				}
				return false;
			}
			public String getDescription() {
				return ".xmi or .zargo";
			}
		});

		int returnVal = fileChooser.showOpenDialog(Main.getInstance().getActiveCanvasFrame());
		fileChooser.setVisible(true);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fileChooser.setVisible(false);
			File f = fileChooser.getSelectedFile();
			String fileType = LoadMOFXmiAction.getExtension(f);
			URL modelURL = null;
			//xmifile=file:./resources/ALLUML.xmi
			//xmifile=jar:file:./resources/REA.zargo!/REA.xmi	
			//file:/E:/eclipse/workspace/MDATool/resources/REA.zargo!/REA.zargo.xmi
			if (fileType.equals(LoadMOFXmiAction.XMI)) {

				try {
					modelURL = f.toURL();
				} catch (MalformedURLException e1) {
					System.out.println(e1.getMessage());
					Main.getInstance().setStatus("Couldnt load model:" + e1);
				}
			} else if (fileType.equals(LoadMOFXmiAction.ZARGO)) {
				URL url;
				try {
					File fi = new File(f.toURL() + "!/" + f.getName().replaceAll("zargo", "xmi"));
					modelURL = fi.toURL();
				} catch (MalformedURLException e1) {
					System.err.println("MalformedURLException:" + e1);
				}
			}
			Main.getInstance().setStatus("Loading.....please wait!");
			Main.getInstance().newFrame(modelURL.toExternalForm());
			System.out.println("URL of the new window:" + modelURL.toExternalForm());
			/** Get the manager and load model */
			RepositoryManager repository = Main.getInstance().getRepositoryManager();
		//	MOFModelWrapper u = repository.loadAModel(modelURL, modelURL.toExternalForm()); //loads a model against uml metamodel
		
			MOFModelWrapper u = repository.loadAMOFModel(modelURL, modelURL.toExternalForm());
			
			//Main.getInstance().getTree().loadUmlModel(u);
			Main.getInstance().getTree().loadMofModel(u);
			Main.getInstance().getNavigationTab().setSelectedComponent(
				Main.getInstance().getNavigationScrollPane());

			

			/* 
			RefPackage ref = Main.getInstance().getRepositoryWrapper().getExtentByName(Main.getInstance().getActiveInternalFrame().getTitle());
			
			//Run UML2MOF transformation
			Transformer.execute((UmlPackage)ref,(ModelPackage)Main.getInstance().getRepositoryWrapper().getExtentByName(Main.getInstance().getActiveInternalFrame().getTitle()));
			*/

			//Collection c = u.getMOFClasses();
			Collection c = u.getMOFClasses();
	
			int num = 0;
			int count = 0;
			int y = 20;
			int x = 20;
			for (Iterator iter = c.iterator(); iter.hasNext();) {
				Object o = iter.next();
				MofClass clazz =(MofClass) o;

				y = (count % 4 == 0) ? y + 200 : y;
				x = (count % 4 == 0) ? 20 : x + 300;
				
				//list attributes for fun
				Collection col = clazz.getContents();
				for (Iterator iterator = col.iterator(); iterator.hasNext();) {
					Object element = (Object) iterator.next();
					if(element instanceof javax.jmi.model.Attribute){
						javax.jmi.model.Attribute a = (javax.jmi.model.Attribute)element;
					
						System.out.println("Found right attribute:" + a.getName());
						System.out.println("Its type:" + a.getType().getName());
					}
				}
				
				
				UmlShape uml =
					new UmlShape(
						x,
						y,
						200,
						150,
						Color.black,
						new Color(Main.getInstance().getUserPrefs().getClassColor()));

				uml.setTextColor(new Color(Main.getInstance().getUserPrefs().getClassTextColor()));

				uml.setClassName(clazz.getName());
				uml.setClazz(clazz);
				num++;
				count++;

				if (MOFModelWrapper.getMOFAttributes(clazz).size() > 0) {
					for (Iterator iterator = MOFModelWrapper.getMOFAttributes(clazz).iterator();
						iterator.hasNext();
						) {
						javax.jmi.model.Attribute attribute = (javax.jmi.model.Attribute) iterator.next();
						
						
						Collection el  = attribute.getRequiredElements();
						for (Iterator i = el.iterator();i.hasNext();) {
							Object ob = (Object) i.next();
							if(ob instanceof MofClass){
								MofClass mc = (MofClass)ob;
								System.out.println("LoadMOFXmiAction.actionPerformed() - loaded MofClass where attribute expected....");
							}else if(ob instanceof javax.jmi.model.Attribute){
								javax.jmi.model.Attribute at = (javax.jmi.model.Attribute)ob;
							}
						}
						
						
						String vis="";		
						if(attribute.getVisibility()==VisibilityKindEnum.PRIVATE_VIS){
							vis = "-";
						}else if(attribute.getVisibility()==VisibilityKindEnum.PUBLIC_VIS){
							vis = "+";	
						}
						//String vis = cl.getVisibility()==VisibilityKindEnum.PRIVATE_VIS ? "-":"+";
						/*String type="unknown";
					
						if(attribute.getType()!=null)
							type = attribute.getType().toString();
						String text = vis + attribute.getName() + ":" + type;		
						*/		
					    //uml.addAttribute(text);
						PrimitiveType pt = (PrimitiveType) attribute.getType();
						System.out.println("LoadMOFXmiAction.actionPerformed() - type:" + pt.getName());
					    uml.addAttribute(attribute);
	
						//att.setUserObject(attribute);
					}
				}
				if (MOFModelWrapper.getMOFOperations(clazz) != null) {

					if (MOFModelWrapper.getMOFOperations(clazz).size() > 0) {
						for (Iterator iterator = MOFModelWrapper.getOperations((Classifier)clazz).iterator();
							iterator.hasNext();
							) {
							javax.jmi.model.Operation opp = (javax.jmi.model.Operation) iterator.next();
							uml.addOperation(
								(opp.getVisibility().toString().indexOf("public") == -1)
									? "+"
									: "-" + opp.getName() + "()");
						}
					}
				}
				Main.getInstance().getActiveCanvasFrame().addShape(uml);
				Collection relations = u.getMOFRelations(clazz); //classCast - make MOFspecific
				//TODO:make a set with relationMofIds in order not to create a relation twice
				for (Iterator iterator = relations.iterator(); iterator.hasNext();) {
					Relation element = (Relation) iterator.next();
					uml.addRelation(
						Main.getInstance().getActiveCanvasFrame().getUmlShapeByName(
							element.getTargetClassName()));
					System.out.println(
						"Current class has relation from "
							+ element.getSourceClassName()
							+ " to "
							+ element.getTargetClassName());

				}
			}

			
			
			//Main.getInstance().getStatusBar().blinkProgressBar(Color.red,Color.white,3000,"loaded model");
			Main.getInstance().setStatus("Succesully loaded model!");
			try {
				Main.getInstance().getActiveInternalFrame().setMaximum(true);
			} catch (PropertyVetoException e2) {
				System.err.println("PropertyVetoException:" + e2);
			}
			Main.getInstance().getActiveInternalFrame().setTitle(modelURL.toExternalForm());
			Main.getInstance().setStatus(
				"Added "
					+ Main.getInstance().getActiveCanvasFrame().getShapes().size()
					+ " classes to the canvas...");
	

			//}

		}
	}
	/*
		 * Get the extension of a file.
		 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
}
