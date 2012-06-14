package mdatool.gui.configuration;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import mdatool.gui.application.Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.prefs.*;
/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class ConfigurationPanel extends JPanel implements ActionListener {

	private UserPreferences _userPrefs;
	protected JPanel _pCanvasConfig, _pPreferences, _pLookAndFeel;
	
	protected JComboBox _lookAndFeelsCombo;
	protected UIManager.LookAndFeelInfo[] _lookAndFeels;
	private JColorChooser _colorChooser;
	
	private DoubleEntryCollection _preferences;
	
	public ConfigurationPanel() {
		_userPrefs = new UserPreferences();
		_preferences = new DoubleEntryCollection(Color.black);
		
		this.setLayout(new BorderLayout());


		_preferences.addDoubleEntry(makeUmlResize());	
		_preferences.addDoubleEntry(makeCanvasGrid());	
		_preferences.addDoubleEntry(makeLookAndFeelPanel());

		
		_preferences.addDoubleEntry(makeColorSelection());
		_preferences.addDoubleEntry(makeColorSelectionForText());
		
		this.add(_preferences, BorderLayout.NORTH);
	}

	public UserPreferences getUserPreferences() {
		return this._userPrefs;
	}


	public DoubleEntry makeUmlResize() {
		JCheckBox b = new JCheckBox();
		b.setBackground(Color.white);
		b.setSelected(_userPrefs.getAutomaticUmlResize());
		b.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					_userPrefs.putAutomaticUmlResize(false);
				} else if (e.getStateChange() == ItemEvent.SELECTED) {
					_userPrefs.putAutomaticUmlResize(true);
				}
			}
		});
		return new DoubleEntry("Auto resize",b);


	}

	public DoubleEntry makeLookAndFeelPanel() {
		_lookAndFeels = UIManager.getInstalledLookAndFeels();
		String[] LAFNames = new String[_lookAndFeels.length];
		for (int i = 0; i < _lookAndFeels.length; i++) {
			LAFNames[i] = _lookAndFeels[i].getName();
		}
		_lookAndFeelsCombo = new JComboBox(LAFNames);
		_lookAndFeelsCombo.setBackground(Color.white);
		
		int index = 0;
		if(_userPrefs.getLookAndFeel()!=null){
			
			for (int i = 0; i < _lookAndFeels.length; i++) {
				String lf = (String)_lookAndFeels[i].getClassName();
				if(lf.equals(_userPrefs.getLookAndFeel())){
					index = i;
				}
			}
		}
		
		_lookAndFeelsCombo.setSelectedIndex(index);
		_lookAndFeelsCombo.addActionListener(this);
		return new DoubleEntry("Look & feel", _lookAndFeelsCombo);
	}

	public DoubleEntry makeCanvasGrid() {
		final SpinnerModel sm =
			new SpinnerNumberModel(
				new Integer(0),
				new Integer(0),
				new Integer(100),
				new Integer(5));
		JSpinner jsp = new JSpinner();
		jsp.setModel(sm);
		if(_userPrefs.getCanvasGridDistance()>=0){
			jsp.setValue(new Integer(_userPrefs.getCanvasGridDistance()));
		}
	
		// add a change listener
		jsp.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				_userPrefs.putCanvasGridDistance(((Integer) sm.getValue()).intValue());
				//update canvas if any
				if(Main.getInstance().getActiveCanvasFrame()!=null){
					//TODO: should affect all active canvases
					Main.getInstance().getActiveCanvasFrame().getCanvasDecorator().setDistance(((Integer) sm.getValue()).intValue());
					Main.getInstance().getActiveCanvasFrame().repaint();
				}
			}
		});
		return new DoubleEntry("Canvas preferences", jsp);
	}
	

	private DoubleEntry makeColorSelection() {

		final JButton colorButton = new JButton();
		colorButton.setSize(new Dimension(20, 20));
		colorButton.setBackground(new Color(_userPrefs.getClassColor()));

		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				Color chosen =
					JColorChooser.showDialog(
						ConfigurationPanel.this,
						"Choose Class Color",
						colorButton.getBackground());
				if (chosen != null) {

					_userPrefs.putClassColor(chosen.getRGB());
					colorButton.setBackground(chosen);
					if (Main.getInstance().getActiveCanvasFrame() != null) {
						Main.getInstance().getActiveCanvasFrame().setAllClassColors(chosen);
						Main.getInstance().getActiveCanvasFrame().repaint();
					}
				}
			}
		});

		//_pCanvasConfig.add(colorButton);
		return new DoubleEntry("Class base color", colorButton);
	}
	private DoubleEntry makeColorSelectionForText() {

			final JButton colorButton = new JButton();
			colorButton.setSize(new Dimension(20, 20));
			colorButton.setBackground(new Color(_userPrefs.getClassTextColor()));

			colorButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					Color chosen =
						JColorChooser.showDialog(
							ConfigurationPanel.this,
							"Choose Class text Color",
							colorButton.getBackground());
					if (chosen != null) {

						_userPrefs.putClassTextColor(chosen.getRGB());
						colorButton.setBackground(chosen);
						if (Main.getInstance().getActiveCanvasFrame() != null) {
							Main.getInstance().getActiveCanvasFrame().setAllClassTextColors(chosen);
							Main.getInstance().getActiveCanvasFrame().repaint();
						}
					}
				}
			});

			//_pCanvasConfig.add(colorButton);
			return new DoubleEntry("Text color", colorButton);
		}
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(300, 500);
		f.getContentPane().add(new ConfigurationPanel());
		f.pack();
		f.setVisible(true);
	}
	private void doPanelDesign(JPanel panel){
		Border border = BorderFactory.createLoweredBevelBorder();
		panel.setBorder(border );
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _lookAndFeelsCombo) {
			try {
				_lookAndFeelsCombo.hidePopup(); //BUG WORKAROUND
				UIManager.setLookAndFeel(_lookAndFeels[_lookAndFeelsCombo.getSelectedIndex()].getClassName());
				SwingUtilities.updateComponentTreeUI(Main.getInstance());
				_userPrefs.putLookAndFeel(_lookAndFeels[_lookAndFeelsCombo.getSelectedIndex()].getClassName());
			} catch (Exception ex) {
				System.out.println(
					"Could not load " + _lookAndFeels[_lookAndFeelsCombo.getSelectedIndex()].getClassName());
			}
		}
	}
}
