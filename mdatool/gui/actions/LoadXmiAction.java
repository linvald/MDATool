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
import java.util.List;

import org.netbeans.lib.jmi.uml2mof.Transformer;
import org.omg.uml.UmlPackage;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.Parameter;
import org.omg.uml.foundation.datatypes.VisibilityKind;
import org.omg.uml.foundation.datatypes.VisibilityKindEnum;

import javax.jmi.model.ModelPackage;
import javax.jmi.reflect.RefPackage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import mdatool.gui.application.Main;
import mdatool.gui.shapes.uml.UmlShape;
import mdatool.gui.util.IconUtil;
//import mdatool.mof.uml.Attribute;
//import mdatool.mof.uml.Operation;
import mdatool.mof.modelwrappers.MOFModelWrapper;
import mdatool.mof.repository.RepositoryManager;
import mdatool.mof.uml.Relation;

/**
 * @author Jesper Linvald
 * @version
 * 
 * Description:
 */
public class LoadXmiAction extends AbstractAction {

	JFileChooser fileChooser;
	public final static String XMI = "xmi";
	public final static String ZARGO = "zargo";

	public LoadXmiAction() {
		putValue(Action.SMALL_ICON, IconUtil.getGeneralIcon("Open", 24));
		this.putValue(Action.SHORT_DESCRIPTION, "Load xmi or zargo UML file");
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

				String extension = LoadXmiAction.getExtension(f);
				if (extension != null) {

					if (extension.equals(LoadXmiAction.XMI)
						|| extension.equals(LoadXmiAction.ZARGO)
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
			String fileType = LoadXmiAction.getExtension(f);
			URL modelURL = null;
			//xmifile=file:./resources/ALLUML.xmi
			//xmifile=jar:file:./resources/REA.zargo!/REA.xmi	
			//file:/E:/eclipse/workspace/MDATool/resources/REA.zargo!/REA.zargo.xmi
			if (fileType.equals(LoadXmiAction.XMI)) {

				try {
					modelURL = f.toURL();
				} catch (MalformedURLException e1) {
					System.out.println(e1.getMessage());
					Main.getInstance().setStatus("Couldnt load model:" + e1);
				}
			} else if (fileType.equals(LoadXmiAction.ZARGO)) {
				URL url;
				try {
					File fi = new File(f.toURL() + "!/" + f.getName().replaceAll("zargo", "xmi"));
					modelURL = fi.toURL();
				} catch (MalformedURLException e1) {
					System.err.println("MalformedURLException:" + e1);
				}
			}
			Main.getInstance().setStatus("Loading.....please wait!");

			/** Get the manager and load model */
			RepositoryManager repository = Main.getInstance().getRepositoryManager();
			MOFModelWrapper u = repository.loadAModel(modelURL, modelURL.toExternalForm()); //loads a model against uml metamodel
		//	MOFModelWrapper u = repository.loadAMOFModel(modelURL, modelURL.toExternalForm());

			Main.getInstance().getTree().makeUMLModelTree(u);
			Main.getInstance().getNavigationTab().setSelectedComponent(
				Main.getInstance().getNavigationScrollPane());

			Main.getInstance().newFrame(modelURL.toExternalForm());

			/* 
			RefPackage ref = Main.getInstance().getRepositoryWrapper().getExtentByName(Main.getInstance().getActiveInternalFrame().getTitle());
			
			//Run UML2MOF transformation
			Transformer.execute((UmlPackage)ref,(ModelPackage)Main.getInstance().getRepositoryWrapper().getExtentByName(Main.getInstance().getActiveInternalFrame().getTitle()));
			*/

			Collection c = u.getClasses();

			int num = 0;
			int count = 0;
			int y = 20;
			int x = 20;
			for (Iterator iter = c.iterator(); iter.hasNext();) {
				org.omg.uml.foundation.core.UmlClass clazz =
					(org.omg.uml.foundation.core.UmlClass) iter.next();

				y = (count % 4 == 0) ? y + 200 : y;
				x = (count % 4 == 0) ? 20 : x + 300;

				UmlShape uml =
					new UmlShape(
						x,
						y,
						200,
						150,
						Color.black,
						new Color(Main.getInstance().getUserPrefs().getClassColor()));

				uml.setTextColor(new Color(Main.getInstance().getUserPrefs().getClassTextColor()));

				if (clazz.getStereotype() != null) {
					//uml.setClassStereoType(clazz.getStereoType());
					uml.setStereoTypes(clazz.getStereotype());
				}
				uml.setClassName(clazz.getName());

				num++;
				count++;

				if (MOFModelWrapper.getAttributes(clazz).size() > 0) {
					for (Iterator iterator = MOFModelWrapper.getAttributes(clazz).iterator();
						iterator.hasNext();
						) {
						Attribute attribute = (Attribute) iterator.next();
						String sign = "";
						
						VisibilityKind vk = attribute.getVisibility();
						if(vk== VisibilityKindEnum.VK_PACKAGE){
							sign ="~";
						}else if(vk == VisibilityKindEnum.VK_PRIVATE){
							sign = "-";
						}else if(vk == VisibilityKindEnum.VK_PROTECTED){
							sign = "#";
						}else if(vk == VisibilityKindEnum.VK_PUBLIC){
							sign = "+";
						}
						uml.addAttribute(sign + attribute.getName());
						//att.setUserObject(attribute);
					}
				}
				if (MOFModelWrapper.getOperations(clazz) != null) {

					if (MOFModelWrapper.getOperations(clazz).size() > 0) {
						for (Iterator iterator = MOFModelWrapper.getOperations(clazz).iterator();
							iterator.hasNext();
							) {
							Operation opp = (Operation) iterator.next();
							String sign = "";
							VisibilityKind vk = opp.getVisibility();
													if(vk== VisibilityKindEnum.VK_PACKAGE){
														sign ="~";
													}else if(vk == VisibilityKindEnum.VK_PRIVATE){
														sign = "-";
													}else if(vk == VisibilityKindEnum.VK_PROTECTED){
														sign = "#";
													}else if(vk == VisibilityKindEnum.VK_PUBLIC){
														sign = "+";
													}
							List params = opp.getParameter();
							String sParam = "(";
							String sReturn = "";
							for (int i = 0; i < params.size(); i++) {
								Parameter p = (Parameter)params.get(i);
								if(p.getName().equals("return")){
									sReturn = ":" + p.getType().getName();
								}else if(i==params.size()-1 ){
									sParam+= p.getType().getName() + " " + p.getName();
								}else{
									sParam+= p.getType().getName() + " " + p.getName() +", ";
								}
							
							}
							uml.addOperation(sign +  opp.getName() +sParam +")"+sReturn);
						}
					}
				}
				Main.getInstance().getActiveCanvasFrame().addShape(uml);
				Collection relations = u.getRelations(clazz);
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
