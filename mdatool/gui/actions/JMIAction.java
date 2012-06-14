package mdatool.gui.actions;

import javax.jmi.reflect.RefPackage;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.*;

import org.omg.uml.modelmanagement.UmlPackage;

import mdatool.gui.application.Main;
import mdatool.gui.util.IconUtil;
import mdatool.mof.modelwrappers.MOFModelWrapper;


/**
 * This class instantiates a canvas and a modelWrapper (which manages a MOF model)
 * @author Jesper Linvald (jesper@linvald.net)
 * Filename: NewCanvasInternalFrameAction.java
 * Created: 14-02-2003 [23:23:29]
 */
public class JMIAction extends AbstractAction {
	

	protected JMIAction(){
		super("New");
		putValue(Action.SMALL_ICON, IconUtil.getHomeMadeIcon( "generate", 24 ));
		this.putValue(Action.SHORT_DESCRIPTION,"Generate JMI interfaces for model");
	}
	
	public void actionPerformed(ActionEvent e){
		RefPackage model = Main.getInstance().getRepositoryManager().getModel(Main.getInstance().getActiveInternalFrame().getTitle());
		Main.getInstance().setStatus("Model class size we try to generate JMI for:" + model);
		Main.getInstance().getRepositoryManager().generate(model,"JMIInterfaces");
	}
}
