package mdatool.gui.configuration;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class DoubleEntryCollection extends JPanel {
	
	private Hashtable _hash;
	private Color _backColor;
	
	public DoubleEntryCollection(Color background){
		_hash = new Hashtable();
		_backColor = background;
		this.setBackground(_backColor);
		initGUI();
	}

	
	private void initGUI() {
		this.setLayout(new GridLayout(_hash.size(),2 ));
		if (_hash.size() > 0) {

			this.removeAll();
			Enumeration e = _hash.keys();
			while (e.hasMoreElements()) {
				add((DoubleEntry) _hash.get(e.nextElement()));
			}
		}
		//doPanelDesign(this);
	}
	
	public void addDoubleEntry(DoubleEntry de){
		_hash.put(de.get_name(),de);
		initGUI();
	}
	
	public DoubleEntry getDoubleEntry(String key){
		return (DoubleEntry)_hash.get(key);
	}
	public Dimension getPreferredSize(){
		return new Dimension(230,20*_hash.size());
	}

	private void doPanelDesign(JPanel panel){
		Border border = BorderFactory.createLineBorder(Color.black);
		panel.setBorder(border );
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DoubleEntryCollection col = new DoubleEntryCollection(Color.white);
		col.addDoubleEntry(new DoubleEntry("text", new JTextField(20)));
		col.addDoubleEntry(new DoubleEntry("num", new JLabel("Hello")));
		f.getContentPane().add(col);

		f.pack();
		f.setVisible(true);
	}
	

}
