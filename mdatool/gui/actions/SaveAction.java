package mdatool.gui.actions;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import mdatool.gui.application.Main;
import mdatool.gui.util.IconUtil;


/**
 * @author Jesper Linvald (jesper@linvald.net)
 * Filename: NewCanvasInternalFrameAction.java
 * Created: 14-02-2003 [23:23:29]
 */
public class SaveAction extends AbstractAction {
	JFileChooser fileChooser;

	protected SaveAction(){
		super("Save");
		putValue(Action.SMALL_ICON, IconUtil.getGeneralIcon( "Save", 24 ));
		fileChooser = new JFileChooser();	
		this.putValue(Action.SHORT_DESCRIPTION,"Save a model");
	}
	public void actionPerformed(ActionEvent e){
		fileChooser.setFileFilter(new FileFilter(){
			public boolean accept(File f) {
				if(f.toString().endsWith("xmi")){
					return true;
				}
				return false;
			}
			public String getDescription() {
				return "xmi";
			}
		});
		
		
		int returnVal = fileChooser.showSaveDialog(Main.getInstance().getActiveCanvasFrame());
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					fileChooser.setVisible(false);
	
					File f = fileChooser.getSelectedFile();
					System.out.println(f.getAbsolutePath());
					String s = (f.getAbsolutePath().lastIndexOf(".xmi")==-1) ? ".xmi" : ""; 
					File withXtension = new File(f.getAbsolutePath()+s); 
					//String fileType = LoadXmiAction.getExtension(f);

		//Main.getInstance().getRepositoryManager().persistModel(Main.getInstance().getRepositoryManager().getModel(),withXtension);
		Main.getInstance().getRepositoryManager().persistModel(Main.getInstance().getRepositoryManager().getModel(Main.getInstance().getActiveInternalFrame().getTitle()),withXtension);
		Main.getInstance().getActiveInternalFrame().setTitle(f.getAbsolutePath());
		Main.getInstance().setStatus("Saved "+withXtension);
	}
}
}
