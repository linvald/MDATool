package mdatool.gui.canvas;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.ColorUIResource;

import mdatool.gui.application.Main;
import mdatool.gui.util.IconUtil;

import java.awt.event.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

/* Used by InternalFrameDemo.java. */
public class CanvasInternalFrame extends 
										JInternalFrame 
								 implements 
								 		InternalFrameListener,
								 		PropertyChangeListener,
										ComponentListener{
															
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
	ShapeCanvas canvas;
	private JScrollPane scroll;
	
    public CanvasInternalFrame(String name) {
        super("Untitled_" + (++openFrameCount)+".xmi", 
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
         if(name!="" && name != null){
         	this.setTitle(name);
         }
              
		this.setFrameIcon(IconUtil.getHomeMadeIcon( "uml", 24 ));
		//((javax.swing.plaf.basic.BasicInternalFrameUI) getUI()).getNorthPane().setBackground(Color.lightGray.brighter());
		


		canvas = new ShapeCanvas(1000, 1000);
		initFrame(name);
    }

	private void initFrame(String name){
		canvas.addPropertyChangeListener(Main.getInstance());
				//canvas.setBackground(Color.white);
				canvas.addPropertyChangeListener(this);
				Main.getInstance().setActiveCanvasFrame(canvas);
//				canvas.setPreferredSize(canvas.getPreferredSize());


				//JScrollPane scroll = new JScrollPane(canvas,
				//									JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				//									JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				scroll = new JScrollPane(canvas);									
				//scroll.putClientProperty("canvas", canvas);

				String n = (name=="") ? "Untitled_" + openFrameCount : name;	
				putClientProperty("canvas", canvas);		
		
		

				//Set the window's location.
				setLocation(0,0);
			    scroll.setViewportView(canvas);

			   this.getContentPane().add(scroll);
			   this.addComponentListener(this);
			   this.addInternalFrameListener(this);
	
			   pack();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameOpened(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameOpened(InternalFrameEvent e) {
		Main.getInstance().getActiveCanvasFrame().repaint();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameClosing(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameClosing(InternalFrameEvent e) {
		String frameName = this.getTitle();
		Main.getInstance().getTree().removeNode(frameName);
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameClosed(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameClosed(InternalFrameEvent e) {
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameIconified(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameIconified(InternalFrameEvent e) {
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameDeiconified(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameDeiconified(InternalFrameEvent e) {
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameActivated(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameActivated(InternalFrameEvent e) {
		Main.getInstance().setActiveCanvasFrame((ShapeCanvas)e.getInternalFrame().getClientProperty("canvas"));
		Main.getInstance().getActiveCanvasFrame().repaint();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameDeactivated(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameDeactivated(InternalFrameEvent e) {
	}
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#repaint(java.awt.Rectangle)
	 */
	public void repaint(Rectangle r) {
		super.repaint(r);
		canvas.repaint();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent arg0) {}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent arg0) {}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	public void componentResized(ComponentEvent arg0) {
		canvas.repaint();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent arg0) {}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		
		if(arg0.getPropertyName().equals("preferredSize")){
			scroll.invalidate();
		}
	}


}
