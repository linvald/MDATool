/*
 * Created on 04-12-2003
 * 
 */
package mdatool.gui.configuration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.jmi.model.Attribute;
import javax.jmi.model.ModelPackage;
import javax.jmi.model.PrimitiveType;
import javax.jmi.model.ScopeKindEnum;
import javax.jmi.model.StructuralFeature;
import javax.jmi.model.VisibilityKindEnum;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mdatool.gui.application.Main;
import mdatool.gui.shapes.uml.UmlShape;



/**
 * @author Jesper Linvald
 * @version
 * 
 * Description:
 */
public class AddAttributePanel extends JPanel 
							   implements ActionListener, ItemListener, KeyListener{



	private DoubleEntryCollection dec;
	private JComboBox _cVisibility, _cType, _cMultiplicity; 
	private UmlShape _class;
	private StructuralFeature _structuralFeature;
	
	protected Attribute _attribute = null;
	protected UmlShape _umlShape;
	protected DoubleEntry _deAttributeName, _deAnnotation, _deVisibility, 
			_deIsChangeable, _deMultiplicity, _deType, _deSubmit;
	protected JTextField _tAttributeName, _tAnnotation;
	protected JButton _ok;
	protected JCheckBox _cIsChangeable;
	
	/** Default visibility */
	private VisibilityKindEnum _visibility = VisibilityKindEnum.PUBLIC_VIS; 
	
	private PrimitiveType _primitiveType = null;

	/**
	 * Constructor for creating new Attribute 
	 */
	public AddAttributePanel(UmlShape umlShape){
		_class = umlShape;
		dec = new DoubleEntryCollection(Color.white);
		add(dec, BorderLayout.CENTER);
		setUp();	
	}

	
	/**
	 * Constructor for modifying/displaying Attribute
	 * @param shape
	 * @param _structuralFeature
	 */
	public AddAttributePanel(UmlShape umlShape, StructuralFeature _structuralFeature) {
		_class = umlShape;
		this._structuralFeature = _structuralFeature;
		dec = new DoubleEntryCollection(Color.white);
		add(dec, BorderLayout.CENTER);
		setUp();
		initValues();
	}
	
	private void setUp() {
		//TODO: addActionListener
		JLabel header = new JLabel("Modify attribute on " + _class.getClassName());

		_ok = new JButton("OK");
		_ok.addActionListener(this);

		/** Attribute name */
		this._tAttributeName = new JTextField();
		_tAttributeName.addActionListener(this);
		_deAttributeName = new DoubleEntry("Name", _tAttributeName);

		/** Annotation */
		_tAnnotation = new JTextField();
		_tAnnotation.addActionListener(this);
		_deAnnotation = new DoubleEntry("Annotation", _tAnnotation);

		/** Setup primitive types */
		_cType = new JComboBox();
		ArrayList pt = (ArrayList) Main.getInstance().getRepositoryManager().getPrimitiveMOFTypes();
		for (int i = 0; i < pt.size(); i++) {
			PrimitiveType element = (PrimitiveType) pt.get(i);
			PrimitiveTypeWrap ptw = new PrimitiveTypeWrap(element, element.getName());
			_cType.addItem(ptw);
		}
		_cType.addItemListener(this);
		_deType = new DoubleEntry("Type", _cType);

		/** Setup visibility */
		_cVisibility = new JComboBox();
		_cVisibility.addItem(VisibilityKindEnum.PRIVATE_VIS);
		_cVisibility.addItem(VisibilityKindEnum.PROTECTED_VIS);
		_cVisibility.addItem(VisibilityKindEnum.PUBLIC_VIS);
		_cVisibility.addItemListener(this);
		_deVisibility = new DoubleEntry("Visibility", _cVisibility);

		/** Setup multiplicity*/
		String[] multItems = new String[] { "Todo" };
		_cMultiplicity = new JComboBox(multItems);
		//_cMultiplicity.setSelectedItem(_attribute.getMultiplicity()); //TODO:
		_cMultiplicity.addItemListener(this);
		_deMultiplicity = new DoubleEntry("Multiplicity", _cMultiplicity);

		/** Setup _cIsChangeable */
		_cIsChangeable = new JCheckBox();
		_deIsChangeable = new DoubleEntry("isChangeable", _cIsChangeable);

		dec.addDoubleEntry(_deAttributeName);
		dec.addDoubleEntry(_deAnnotation);
		dec.addDoubleEntry(_deType);
		dec.addDoubleEntry(_deVisibility);
		dec.addDoubleEntry(_deMultiplicity);
		dec.addDoubleEntry(_deIsChangeable);

		setLayout(new BorderLayout());
		add(header, BorderLayout.NORTH);
		add(dec, BorderLayout.CENTER);
		add(_ok, BorderLayout.SOUTH);
		setSize(dec.getSize());
	}
	
	

	/**
	 * Init the GUI values with the Attributes
	 */
	private void initValues() {
		if (_structuralFeature != null) {
			//_cMultiplicity
			this._tAttributeName.setText(_structuralFeature.getName());
			this._tAnnotation.setText(_structuralFeature.getAnnotation());
			PrimitiveTypeWrap ptw = new PrimitiveTypeWrap((PrimitiveType)_structuralFeature.getType(),_structuralFeature.getName());
			System.out.println("Nameeeee:" +_structuralFeature.getType());
			this._cType.setSelectedItem(ptw);
			this._cVisibility.setSelectedItem(_structuralFeature.getVisibility());
			this._cIsChangeable.setSelected(_structuralFeature.isChangeable());
		}
	}
	
	/**
	 * Assign values to the MOF Attribute
	 */
	private void setValues() {
		if (this._attribute != null) {

			String name = _tAttributeName.getText();
			String annotation = _tAnnotation.getText();
			PrimitiveType type = ((PrimitiveTypeWrap) _cType.getSelectedItem()).pt;
			
			VisibilityKindEnum visibility = (VisibilityKindEnum) _cVisibility.getSelectedItem();
			boolean changeable = _cIsChangeable.isSelected();
			//_cMultiplicity
			this._attribute.setName(name);
			this._attribute.setAnnotation(annotation);
			this._attribute.setType(type);
			this._attribute.setVisibility(visibility);
			this._attribute.setChangeable(changeable);
		}
	}
	
	
	
	/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == _tAttributeName) {
				_attribute.setName(_tAttributeName.getText());
			} else if (src == _tAnnotation) {
				_attribute.setAnnotation(_tAnnotation.getText());
			} else if (src == _ok) {
				if (_class.getClazz() != null) {
					ModelPackage mof = (ModelPackage) _class.getClazz().refOutermostPackage();
					_structuralFeature =
						mof
							.getAttribute()
							.createAttribute(
								_tAttributeName.getText(),
								_tAnnotation.getText(),
								ScopeKindEnum.INSTANCE_LEVEL,
								_visibility,
								mof.createMultiplicityType(1, 1, false, true),//lower, upper, isOrdered, isUnique
								true, false);
					_structuralFeature.setType(((PrimitiveTypeWrap)this._cType.getSelectedItem()).pt);
					_structuralFeature.setContainer(_class.getClazz());
					_class.addAttribute((Attribute)_structuralFeature);
				//	Main.getInstance().showClassSettings(_class);
				}else{
					
				}
			}
		}

	
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource()== _cType ){
			PrimitiveTypeWrap ptw = (PrimitiveTypeWrap)e.getItem();
			_primitiveType = ptw.pt;
			//_primitiveType = (PrimitiveType)e.getItem();
			System.out.println(_primitiveType.getName());

		}else if(e.getSource() == _cVisibility){
			_visibility = (VisibilityKindEnum)e.getItem();
		}else if(e.getSource() == _cMultiplicity){
			//mof.createMultiplicityType(1, 1, false, true), //lower, upper, isOrdered, isUnique 
		}
		setValues();
	}

	class PrimitiveTypeWrap{
					public PrimitiveType pt; public String name;
					public PrimitiveTypeWrap(PrimitiveType pt, String name){
						this.pt = pt; this.name = name;
					}
					public String toString(){
						return name;
					}
					public boolean equals(Object other){
						if(other instanceof PrimitiveTypeWrap){
							PrimitiveTypeWrap ptw = (PrimitiveTypeWrap)other;
							if(ptw.name.equals(this.name) && ptw.pt.equals(pt)){
								return true;
							}
						}
						return false;
					}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {}
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {}
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
		Object src = e.getSource();
		if (src == _tAttributeName) {
			_attribute.setName(_tAttributeName.getText());
		} else if (src == _tAnnotation) {
			_attribute.setAnnotation(_tAnnotation.getText());
		}
	}

}
