package mdatool.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import mdatool.gui.application.Main;
import mdatool.gui.util.IconUtil;
import mdatool.mof.modelwrappers.MOFModelWrapper;


/**
 * This class instantiates a canvas and a modelWrapper (which manages a MOF model)
 * @author Jesper Linvald (jesper@linvald.net)
 * Filename: NewCanvasInternalFrameAction.java
 * Created: 14-02-2003 [23:23:29]
 */
public class NewCanvasInternalFrameAction extends AbstractAction {
	

	protected NewCanvasInternalFrameAction(){
		super("New");
		putValue(Action.SMALL_ICON, IconUtil.getGeneralIcon( "New", 24 ));
		this.putValue(Action.SHORT_DESCRIPTION,"New Canvas");
		//putValue(Action.SMALL_ICON, IconUtil.getAnyImageIcon( "../img/New24.gif", 24,NewAction.class ));
	}
	public void actionPerformed(ActionEvent e){
		Main.getInstance().newFrame("");
		Main.getInstance().setStatus("Canvas created...");

		MOFModelWrapper	mof = Main.getInstance().getRepositoryManager().createMOFModel(Main.getInstance().getActiveInternalFrame().getTitle());
		Main.getInstance().getActiveCanvasFrame().setUmlModelWrapper(mof);
	
	
		/*RefPackage ref = Main.getInstance().getRepositoryWrapper().getExtentByName(Main.getInstance().getActiveInternalFrame().getTitle());
		System.out.println("REF retrieved by name:" + ref);
		Main.getInstance().getRepositoryWrapper().persistModel(ref,new File(Main.getInstance().getActiveInternalFrame().getTitle()));*/
	
	}
}
