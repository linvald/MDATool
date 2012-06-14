package mdatool.gui.actions;

import mdatool.gui.application.Main;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 * Filename: ToolBarActionFactory.java
 * Created: 22-02-2003 [22:34:26]
 * A class for making Toolbaractions
 */
public class ToolBarActionFactory {
	private static ToolBarActionFactory _factory = new ToolBarActionFactory();
	
	
	private NewCanvasInternalFrameAction newAction;


	private ToolBarActionFactory(){
		
	}
	public static ToolBarActionFactory getToolBarActionFactory(){
		return _factory;	
	}
	public NewCanvasInternalFrameAction makeNewAction(){
		return new NewCanvasInternalFrameAction();	
	}
	public ClassAction makeClassAction(){
		return new ClassAction();	
	}
	public LoadXmiAction makeLoadXmiAction(){
		return new LoadXmiAction();
	}
	public SaveAction makeSaveAction(){
		return new SaveAction();
	}
	public JMIAction makeJMIAction(){
		return new JMIAction();
	}
	public LoadMOFXmiAction makeLoadMOFXmiAction(){
		return new LoadMOFXmiAction();
	}
	
	
	
}
