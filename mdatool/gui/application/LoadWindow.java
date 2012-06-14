package mdatool.gui.application;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class LoadWindow extends JWindow {

public LoadWindow(){
	URL iconURL = this.getClass().getResource("Load.gif");
		
		// Build and return a new ImageIcon built from this URL
	ImageIcon i =  new ImageIcon(iconURL);
	JLabel p = new JLabel(i);
	getContentPane().add(p);
	setSize(i.getIconWidth(),i.getIconHeight());
	pack();

}


}
