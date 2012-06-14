package mdatool.gui.configuration;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class DoubleEntry extends JPanel{

		private JLabel _name;
		private Component _component;
		
		/**
		 * The component should have a listener attached...
		 */
		public DoubleEntry(String name, Component c){
			this._name = new JLabel(name);
			this._component = c;
			init();
		}
		
		private void init(){
			this.setLayout(new GridLayout(1,2));
			this.setBackground(Color.white);
			add(_name);
			add(_component);
			doPanelDesign(_name);
			doPanelDesign(this);
		}
	
	private void doPanelDesign(JComponent c){
		Border border = BorderFactory.createLineBorder(Color.black);
		c.setBorder(border );
	}
	
	public Dimension getPreferredSize(){
		return new Dimension(230,20);
	}
	
		public Component get_component() {
			return _component;
		}


		public String get_name() {
			return _name.getText();
		}
		
		public String toString(){
			return "Key:"+this.get_name() + " componenet:" + _component.getClass();
		}
		
		public static void main(String[] args) {
			JFrame f = new JFrame();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			final JTextField t = new JTextField();
			t.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					System.out.println(t.getText());
				}
			});
			DoubleEntry de = new DoubleEntry("Color", t);
			f.getContentPane().add(de);
			f.pack();
			f.setVisible(true);
			
		}
}
