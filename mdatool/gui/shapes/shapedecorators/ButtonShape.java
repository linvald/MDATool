package mdatool.gui.shapes.shapedecorators;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;

import mdatool.gui.application.Main;
import mdatool.gui.configuration.AddAttributePanel;
import mdatool.gui.shapes.basic.RectShape;
import mdatool.gui.shapes.basic.RoundRectShape;
import mdatool.gui.shapes.uml.UmlShape;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class ButtonShape extends RoundRectShape {

	private UmlShape parent=null;

	/**
	 * @param x1
	 * @param y1
	 * @param w1
	 * @param h1
	 * @param color
	 * @param color2
	 */
	public ButtonShape(int x1, int y1, int w1, int h1, Color color, Color color2, UmlShape parent) {
		super(x1, y1, w1, h1, color, color2);
		//this.lineColor = parent.lineColor; 
		//this.fillColor = parent.fillColor;
		this.parent = parent;
		position();	
	}
	
	
	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.basic.Shape#handleSingleClick(java.awt.Point)
	 */
	public void handleSingleClick(Point p) {
		final AddAttributePanel aw = new AddAttributePanel(parent);
		Main.getInstance().setSettingPanel(aw);
		aw.setVisible(true);
		
		/*aw.addWindowListener(new WindowAdapter(){
					public void windowDeactivated(WindowEvent ev){
						aw.toFront();
					}
					public void windowActivated(WindowEvent e){
						aw.toFront();
					}
					public void windowGainedFocus(WindowEvent e){
						aw.toFront();
					}
					public void windowLostFocus(WindowEvent e){
						aw.toFront();
					}
				});*/
		//aw.setSize(new Dimension(200,200));
		//p.translate(Main.getInstance().getActiveCanvasFrame().getLocationOnScreen().x,Main.getInstance().getActiveCanvasFrame().getLocationOnScreen().y);
		//aw.setLocation(p.x,p.y);
	}

	public void move(int newX, int newY) {
		super.move(newX, newY);
		position();	
	}

	public void position(){
		if(parent!=null){
			x = (parent.x + parent.w)-w;
			y = parent.y + parent.getheaderHeight();
		}		 
	}
	
	/* (non-Javadoc)
	 * @see mdatool.gui.shapes.basic.Shape#draw(java.awt.Graphics)
	 */
	public void draw(Graphics g) {
		//super.draw(g);
		//draw a plus
		Color before = g.getColor();
		g.setColor(Color.black);
		Graphics2D g2 = (Graphics2D)g;
		Stroke bS = g2.getStroke();
		g2.setStroke(new BasicStroke((float)w/4));
		g.drawLine(x+1,y+(h/2)-1,x+w-1,y+(h/2)-1);
		g.drawLine(x+(w/2),y+1,x+(w/2),y+h-1);
		g.setColor(before);
		g2.setStroke(bS);
	}

}
