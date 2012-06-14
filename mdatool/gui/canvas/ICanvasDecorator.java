package mdatool.gui.canvas;
import java.awt.*;
/**
 * @author Jesper Linvald (jesper@linvald.net)
 * Filename: ICanvasDecorator.java
 * Created: 22-02-2003 [05:04:54]
 */
public interface ICanvasDecorator {
	void decorate(Graphics g);
	int getDistance();
	void setDistance(int dis);
}
