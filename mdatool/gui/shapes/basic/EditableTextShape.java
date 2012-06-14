package mdatool.gui.shapes.basic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import mdatool.gui.application.Main;
import mdatool.gui.configuration.AddAttributePanel;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class EditableTextShape extends Shape {

	protected String text="default";
	protected Font font;
	protected int fontSize, fontHeight, stringLength;
	protected Graphics graphicsForMeausure = null;
	protected Rectangle textBounds = null;
	protected Shape parent = null;


	public EditableTextShape(int x, int y, int w, int h, String text, Shape parent) {
		super(x, y, w, h);
		this.parent = parent;
		//_listeners = new ArrayList();
		font = new Font("Verdana", Font.PLAIN, 12);
		this.text = text;
		this.setWidth(text.length() * 3);
		//this.measureString(Main.getInstance().getActiveCanvasFrame().getGraphics());
	}

	/**
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 * @param color
	 * @param color2
	 */
	public EditableTextShape(int x1,int y1,int w1,int h1,
		Color color,
		Color color2,
		String text,
		Shape parent) {
			
		super(x1, y1, w1, h1, color, color2);
		
		this.parent = parent;
		font = new Font("Verdana", Font.BOLD, 12);
		//this.text = text;
		setText(text);
		//this.measureString(Main.getInstance().getActiveCanvasFrame().getGraphics());
	}

	protected void measureString(Graphics g) {

		//System.out.println("EditableTextShape.measureString() - text:" +text );
		//System.out.println("Graphics:" + g==null);
		//System.out.println("FontMetrics:" + g.getFontMetrics());
		//stringLength = g.getFontMetrics().stringWidth(text);
		
		stringLength = SwingUtilities.computeStringWidth(g.getFontMetrics(), text);
		fontHeight = g.getFontMetrics().getHeight();
		//SwingUtilities.computeStringWidth(g.getFontMetrics(), this.getText());
		this.size(stringLength, fontHeight);

		h = fontHeight;
	}
	
	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#draw(java.awt.Graphics)
	 */
	public void draw(Graphics g) {
		Color before = g.getColor();
		g.setColor(parent.textColor);
		g.setFont(this.font);
		if (graphicsForMeausure == null) {
			graphicsForMeausure = g;
		}
		measureString(graphicsForMeausure);
		g.drawString(text, getX(), getY() + fontHeight);
		g.setColor(before);
	}

	public int getFontHeight() {
		return fontHeight;
	}

	/**
	 * @return
	 */
	public int getStringLength() {
		return stringLength;
	}

	/**
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param font
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * @param i
	 */
	public void setFontHeight(int i) {
		fontHeight = i;
	}

	/**
	 * @param i
	 */
	public void setFontSize(int i) {
		fontSize = i;
	}

	/**
	 * @param i
	 */
	public void setStringLength(int i) {
		stringLength = i;
	}

	/**
	 * @param string
	 */
	public void setText(String s) {
		if(s.length()>0){	
			_beanSupport.firePropertyChange("textchange", text,  s); //old and new
			text = s;

		}

	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#drawSelected(java.awt.Graphics)
	 */
	public void drawSelected(Graphics g) {}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#handleDoubleClick(java.awt.Point)
	 */
	public void handleDoubleClick(Point p) {
		this.setFillColor(Color.green);
	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#getContextMenu(java.awt.Point)
	 */
	public JPopupMenu getContextMenu(Point p) {
		final JPopupMenu pop = new JPopupMenu("pop");
		//final JMenuItem item = new JMenuItem("Rename");
		final JMenu menu = new JMenu("Rename");
		final JTextField t = new JTextField(20);

		t.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) { //return
					if(t.getText().length()>0)
						setText(t.getText());
					pop.setVisible(false);
					Main.getInstance().getActiveCanvasFrame().repaint();
				}else{
					if(t.getText().length()>0)
						setText(t.getText());
				}
			}
			public void keyReleased(KeyEvent e) {
				if(t.getText().length()>0)
					setText(t.getText());
			}
			public void keyTyped(KeyEvent e) {
				if(t.getText().length()>0)
					setText(t.getText());
			}
		});
		menu.add(t);
		t.show();
		pop.add(menu);
		pop.validate();
		return pop;
	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#containsPoint(int, int)
	 */
	public boolean containsPoint(int x, int y) {
		return super.containsPoint(x, y);
	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.Shape#moveBy(int, int)
	 */
	public void move(int dx, int dy) {
		super.move(dx, dy);
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {

	}

	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.basic.Shape#handleSingleClick(java.awt.Point)
	 */
	public void handleSingleClick(Point p) {
		System.out.println("EditableTextShape handle singelClick");
		//assume it is an attribute!!!!
	
		// is it an Attribute or Operation? Inherit from this class to make specific 
		//AddAttributePanel aap = new AddAttributePanel();
		//Main.getInstance().setSettingPanel(aap);
	}

}
