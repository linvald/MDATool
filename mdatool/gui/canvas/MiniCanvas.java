package mdatool.gui.canvas;

import javax.swing.JPanel;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *  TODO:
 */
public class MiniCanvas extends JPanel {
	protected ShapeCanvas _canvas;

	public MiniCanvas(ShapeCanvas canvas) {
		this._canvas = canvas;
		initCanvas();
	}

	private void initCanvas() {}
	public void paintComponent() {}
}
