package mdatool.gui.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofClass;
import javax.jmi.model.Namespace;
import javax.swing.AbstractAction;
import javax.swing.Action;

import mdatool.gui.application.Main;
import mdatool.gui.shapes.uml.UmlShape;
import mdatool.gui.util.IconUtil;


/**
 * @author Jesper Linvald (jesper@linvald.net)
 * Filename: NewCanvasInternalFrameAction.java
 * Created: 14-02-2003 [23:23:29]
 */
public class ClassAction extends AbstractAction {
	private ToolBarActionFactory toolBarActionFactory;

	
	protected ClassAction(){
		super("Class");
		putValue(Action.SMALL_ICON, IconUtil.getHomeMadeIcon( "uml", 24 ));
		this.putValue(Action.SHORT_DESCRIPTION,"Add a MOF class");
	}
	public void actionPerformed(ActionEvent e){
		if(Main.getInstance().getActiveCanvasFrame() == null){
			Main.getInstance().setStatus("Open canvas before adding Classes!");
			return;
		}
	
		//createUmlClass(String name, VisibilityKind visibility, boolean isSpecification, boolean isRoot, boolean isLeaf, boolean isAbstract, boolean isActive) 
		UmlShape uml  = new UmlShape(300,100,200,150,Color.black,new Color(Main.getInstance().getUserPrefs().getClassColor()));
		
	
		ModelPackage mofPack = (ModelPackage)Main.getInstance().getRepositoryManager().getMDRepository().getExtent(Main.getInstance().getActiveInternalFrame().getTitle());
		MofClass clazz = Main.getInstance().getActiveCanvasFrame().getMofModelWrapper().createMofClass(mofPack,"ClassName","Default",false,true,false,
									javax.jmi.model.VisibilityKindEnum.PUBLIC_VIS, false);
		
		/*va.lang.String name, java.lang.String annotation, boolean isRoot,boolean isLeaf,
		boolean isAbstract,VisibilityKind visibility, boolean isSingleton)*/
		//MofClass clazz = mofPack.getMofClass().createMofClass("ClassName","Default",false,true,false,javax.jmi.model.VisibilityKindEnum.PUBLIC_VIS, false);
	
		//UmlClass clazz = pack.getCore().getUmlClass().createUmlClass("Default",VisibilityKindEnum.VK_PUBLIC, false, false, true,false,true);
		uml.setClazz(clazz);
		uml.setTextColor(new Color(Main.getInstance().getUserPrefs().getClassTextColor()));

		Main.getInstance().getActiveCanvasFrame().addShape(uml);
		Main.getInstance().getActiveCanvasFrame().repaintShape(uml);
		

		uml.move(20,20);
		System.out.println("ClassAction.actionPerformed() - created MofClass:" + clazz.getName());

	}
}
