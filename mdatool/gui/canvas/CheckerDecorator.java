package mdatool.gui.canvas;

import java.awt.*;

import mdatool.gui.application.Main;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 * Filename: CanvasLinedDecorator.java
 * Created: 22-02-2003 [05:02:32]
 */
public class CheckerDecorator implements ICanvasDecorator{
	
	private int lineDistance=10;
	private Color lineColor = Color.gray;
	
	public CheckerDecorator(){

	}
	public void decorate(Graphics g){
		if(lineDistance>0){
		
		ShapeCanvas c = Main.getInstance().getActiveCanvasFrame();
	
		g.setColor(this.lineColor);
		//vertical
		for (int i = 0; i < c.getHeight(); i++) {
			if(i%this.lineDistance == 0){
				g.drawLine(0,i,(int)c.getWidth(),i);	
			}
		}
		//horizontal
		for (int i = 0; i < c.getWidth(); i++) {
			if(i%this.lineDistance == 0){
				g.drawLine(i,0,i,(int)c.getHeight());	
			}
		}
		}
	}
	/**
	 * Returns the lineDistance.
	 * @return int
	 */
	public int getLineDistance() {
		return lineDistance;
	}

	/**
	 * Sets the lineDistance.
	 * @param lineDistance The lineDistance to set
	 */
	public void setLineDistance(int lineDistance) {
		this.lineDistance = lineDistance;
	}

	/**
	 * Returns the lineColor.
	 * @return Color
	 */
	public Color getLineColor() {
		return lineColor;
	}

	/**
	 * Sets the lineColor.
	 * @param lineColor The lineColor to set
	 */
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}
	/* (non-Javadoc)
	 * @see mdatool.gui.canvas.ICanvasDecorator#getDistance()
	 */
	public int getDistance() {
		return lineDistance;
	}
	/* (non-Javadoc)
	 * @see mdatool.gui.canvas.ICanvasDecorator#setDistance()
	 */
	public void setDistance(int dis) {
		lineDistance = dis;
	}

}
