package mdatool.gui.configuration;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.jmi.model.Attribute;
import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofClass;
import javax.jmi.model.PrimitiveType;
import javax.jmi.model.ScopeKindEnum;
import javax.jmi.model.StructuralFeature;
import javax.jmi.model.VisibilityKindEnum;
import javax.swing.*;

import mdatool.gui.application.Main;
import mdatool.gui.shapes.uml.UmlShape;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class AttributeDoubleEntryCollection extends DoubleEntryCollection 
											implements 
												ActionListener, ItemListener, KeyListener{
	/*createAttribute
	 * (java.lang.String name, java.lang.String annotation, 
	 * ScopeKind scope, VisibilityKind visibility, MultiplicityType multiplicity, 
	 * boolean isChangeable, boolean isDerived) 
	 */
	protected Attribute _attribute = null;
	protected UmlShape _umlShape;
	protected DoubleEntry _deAttributeName, _deAnnotation, _deVisibility, 
			_deIsChangeable, _deMultiplicity, _deType, _deSubmit;
	protected JTextField _tAttributeName, _tAnnotation;
	protected JComboBox _cVisibility, _cMultiplicity, _cType;
	protected JCheckBox _cIsChangeable;
	protected JButton _bSubmit;


	/** Default visibility */
	protected VisibilityKindEnum _visibility = VisibilityKindEnum.PUBLIC_VIS; 
	
	protected PrimitiveType _primitiveType = null;
	
	protected MofClass _clazz = null;



	public AttributeDoubleEntryCollection(Color background,UmlShape clazz) {
		super(background);
		this._umlShape = clazz;
		this._clazz = clazz.getClazz();
		initAttribute(_clazz);
		setUp();	
	}
	
	public AttributeDoubleEntryCollection(Color background,UmlShape clazz, StructuralFeature feature) {
		super(background);
		this._umlShape = clazz;
		_attribute = (Attribute)feature;
		this._clazz = clazz.getClazz();
		initAttribute(_clazz);
		setUp();	
	}
	
	private void initAttribute(MofClass clazz){
		if (_attribute == null) {

			ModelPackage mof = (ModelPackage) clazz.refOutermostPackage();
			_attribute =
					mof
						.getAttribute()
						.createAttribute(
							"",
							"",
							ScopeKindEnum.INSTANCE_LEVEL,
							_visibility,
							mof.createMultiplicityType(1, 1, false, true),//lower, upper, isOrdered, isUnique
							true, false);

			_attribute.setType(_primitiveType);
			_attribute.setContainer(_clazz);
		}														
	}
	
	public void setAttribute(Attribute attribute){
		this._attribute = attribute;											
		this._attribute.setContainer(_clazz);
	}
	

	
	private void setUp() {
	
		/** Attribute name */
		this._tAttributeName = new JTextField();
		_tAttributeName.addActionListener(this);
		_tAttributeName.setText(_attribute.getName());
		_deAttributeName = new DoubleEntry("Name", _tAttributeName);
		this.addDoubleEntry(_deAttributeName);
	
		/** Annotation */
		_tAnnotation = new JTextField();
		_tAnnotation.setText(_attribute.getAnnotation());
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
		String[] multItems = new String[]{"Todo"};
		_cMultiplicity = new JComboBox(multItems);
		//_cMultiplicity.setSelectedItem(_attribute.getMultiplicity()); //TODO:
		_cMultiplicity.addItemListener(this);
		_deMultiplicity = new DoubleEntry("Multiplicity",_cMultiplicity );
	
		/** Setup _cIsChangeable */
		_cIsChangeable = new JCheckBox();
		_cIsChangeable.setSelected(_attribute.isChangeable());
		_deIsChangeable = new DoubleEntry("isChangeable",_cIsChangeable);
		
		/** Setup submit button */
		_bSubmit = new JButton("Submit");
		_bSubmit.addActionListener(this);
		_deSubmit = new DoubleEntry("Add", _bSubmit);
		
		addDoubleEntry(_deAttributeName);
		addDoubleEntry(_deAnnotation);
		addDoubleEntry(_deType);
		addDoubleEntry(_deVisibility);
		addDoubleEntry(_deMultiplicity);
		addDoubleEntry(_deIsChangeable);
		//addDoubleEntry(_deSubmit);
	
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src == _tAttributeName){
			_attribute.setName(_tAttributeName.getText());
		}else if(src == _tAnnotation){
			_attribute.setAnnotation(_tAnnotation.getText());
		}else if(src == _bSubmit){
			setValues();
			this._umlShape.addAttribute(this._attribute);
		}
		setValues();
	}


	private void setValues() {
		String name = _tAttributeName.getText();
		String annotation = _tAnnotation.getText();
		PrimitiveType type = ((PrimitiveTypeWrap)_cType.getSelectedItem()).pt;
		VisibilityKindEnum visibility = (VisibilityKindEnum)_cVisibility.getSelectedItem();
		boolean changeable = _cIsChangeable.isSelected();
		//_cMultiplicity
		this._attribute.setName(name);
		this._attribute.setAnnotation(annotation);
		this._attribute.setType(type);
		this._attribute.setVisibility(visibility);
		this._attribute.setChangeable(changeable);
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource()== _cType ){
			PrimitiveTypeWrap ptw = (PrimitiveTypeWrap)e.getItem();
			_primitiveType = ptw.pt;
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
					public String toString(){return name;}
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
