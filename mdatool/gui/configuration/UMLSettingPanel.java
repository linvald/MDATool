/*
 * Created on 01-12-2003
 * 
 */
package mdatool.gui.configuration;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mdatool.gui.shapes.uml.UmlShape;

/**
 * @author Jesper Linvald
 * @version
 * 
 * Description: A Panel that will display either Class, Attribute or operation settings
 * depending on a shape
 */
public class UMLSettingPanel extends JPanel{


	public UMLSettingPanel(){
		//setLayout(new GridLayout(1,1));
	}

	public void showClassSettingPanel(final UmlShape uml){
		this.removeAll();
		
		final JTextField tClassName = new JTextField();
		tClassName.setText(uml.getClassName());
		tClassName.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e) {
				uml.setClassName(tClassName.getText());
			}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				uml.setClassName(tClassName.getText());
			}
			
		});
		
		String commaString = uml.getClassStereoTypes();
		String withOutSharp = commaString.substring(2,commaString.length()-2);
		JComboBox assStereoCombo = new JComboBox(withOutSharp.split(","));
		
		//TODO: add actionListener
		JButton addStereoTypeButton = new JButton("Add stereotype");
		
		JButton addAttribute = new JButton("Add attribute");
		
		DoubleEntry className = new DoubleEntry("Classname",tClassName);
		DoubleEntry assStereo = new DoubleEntry("Assigned Stereotypes",assStereoCombo);
		DoubleEntry addStereo = new DoubleEntry("Add Stereotype",addStereoTypeButton);
		DoubleEntry addAttrib = new DoubleEntry("Add attribute",addAttribute);
		
		DoubleEntryCollection decStereoTypes = new DoubleEntryCollection(Color.white);
		decStereoTypes.addDoubleEntry(className);
		decStereoTypes.addDoubleEntry(assStereo);
		decStereoTypes.addDoubleEntry(addStereo);
		decStereoTypes.addDoubleEntry(addAttrib);
		add(decStereoTypes);
	}
	
	public JPanel getAttributeSettingPanel(){
		return null;
	}
	public JPanel getOperationSettingPanel(){
		return null;
	}
	public JPanel getRelationSettingPanel(){
		return null;
	}
}
