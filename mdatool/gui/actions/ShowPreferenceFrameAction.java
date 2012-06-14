package mdatool.gui.actions;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofClass;
import javax.jmi.model.MofClassClass;
import javax.jmi.reflect.RefPackage;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.*;
import javax.swing.*;

import org.omg.uml.UmlPackage;
import org.omg.uml.foundation.core.UmlClass;
import org.omg.uml.foundation.core.UmlClassClass;
import org.omg.uml.foundation.datatypes.VisibilityKindEnum;
import org.omg.uml.modelmanagement.UmlPackageClass;

import mdatool.gui.application.Main;
import mdatool.gui.configuration.ConfigurationPanel;
import mdatool.gui.shapes.uml.UmlShape;
import mdatool.gui.util.IconUtil;


/**
 * @author Jesper Linvald (jesper@linvald.net)
 * Filename: NewCanvasInternalFrameAction.java
 * Created: 14-02-2003 [23:23:29]
 */
public class ShowPreferenceFrameAction extends AbstractAction {
	private ToolBarActionFactory toolBarActionFactory;

	
	public ShowPreferenceFrameAction(){
		super("Preferences");
		//putValue(Action.SMALL_ICON, IconUtil.getHomeMadeIcon( "uml", 24 ));
		//this.putValue(Action.SHORT_DESCRIPTION,"Add a MOF class");
	}
	public void actionPerformed(ActionEvent e){
		final JFrame frame = new JFrame("Preferences");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(Main.getInstance().getConfigurationPanel());
		frame.pack();
		frame.setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2);
		frame.addWindowListener(new WindowAdapter(){
			public void windowDeactivated(WindowEvent ev){
				frame.toFront();
			}
		});

		frame.setVisible(true);
	}
}
