package mdatool.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class StatusBar extends JPanel {
	
	protected JProgressBar progress;
	private int _currentProgressLength=0;
	private int x,y=0;
	protected String[]panelNames;
	protected JLabel progressLabelName, mouseStatLabel;
	protected JPanel progressBarPanel;
	protected JPanel mouseStats;
	protected Color _background;
	private int numberOfPanels = 2;
	
	public StatusBar(int width, int height,String progressLabelNameString, Color background){
		_background = background;
		this.setLayout(new GridLayout(1,numberOfPanels));
		BorderLayout layout = new BorderLayout();
		progress = new JProgressBar();
		progress.setStringPainted(true);
		progressLabelName = new JLabel(progressLabelNameString);
		progressBarPanel = new JPanel();
		progressBarPanel.setLayout(new BorderLayout());
		progressBarPanel.add("West",progressLabelName);
		progressBarPanel.add("Center",progress);		
		progressBarPanel.setBackground(background);	
		progress.setForeground(Color.red);
		progress.setBackground(_background.brighter());
		progress.setBorder(BorderFactory.createCompoundBorder());
		
		progressBarPanel.setSize(progressBarPanel.getWidth(),height);
		this.add(progressBarPanel);
		doPanelDesign(progressBarPanel);
		
		
		mouseStats = new JPanel();
		mouseStats.setLayout(layout);	
		mouseStatLabel = new JLabel(" Mouse X:" + x + " Y:"+y);
		
		//mouseStats.setSize(mouseStats.getWidth(),progressBarPanel.getHeight());
		mouseStats.add(mouseStatLabel);
		mouseStats.setBackground(background);
		this.add(mouseStats);
		doPanelDesign(mouseStats);
			
		setSize(width, height);


	}
	
	/**
	 * Non blocking
	 */	
	public void blinkProgressBar(Color first, Color second, long howLongMilies, String message){
		Blinker b = new Blinker(first, second, 5000, message);
		new Thread(b).start();
		
	}
	
	public void setProgressMax(int length){
		progress.setMaximum(length);
	}
	public void incrementProgresByOne(){
		_currentProgressLength++;
		if(_currentProgressLength>progress.getMaximum())_currentProgressLength=0;
		progress.setValue(_currentProgressLength);
	}
	public void setProgressLength(int howLong){
		progress.setValue(howLong);
		_currentProgressLength = howLong;
	}
	
	private void doPanelDesign(JPanel panel){
		Border border = BorderFactory.createLoweredBevelBorder();
		panel.setBorder(border );
	}
	
	
	public void setMouseX_Y(int x, int y){
		this.x = x;
		this.y = y;
		mouseStatLabel.setText(" Mouse X:" + x + " Y:"+y);
		mouseStatLabel.invalidate();
	}
	public void setFullProgress(){
		progress.setValue(progress.getMaximum());
	}
	
	public void setProgressNull(){
		progress.setValue(0);
	}
	
	public Dimension getPreferredSize(){
		return new Dimension(this.getWidth(),20);
	}
	public Dimension getMaximumSize(){
		return getPreferredSize();
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("Progress test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().setLayout(new BorderLayout());
		Dimension bounds = new Dimension(500,500);
		f.setSize(bounds);
		f.getContentPane().add(new JButton("just fill"));
		StatusBar bar = new StatusBar(500,30,"Progress:",Color.green);
		
		f.getContentPane().add("South",bar);
		bar.setProgressMax(100);

		f.setVisible(true);
		f.show();
		for (int i = 0; i < 120; i++) {
			try {
				Thread.sleep(50);
				bar.incrementProgresByOne();
			} catch (InterruptedException e) {
				System.err.println("InterruptedException:" + e);
			}
			
		}
		bar.blinkProgressBar(Color.red,Color.green,5000,"loaded");
		System.out.println("Is it blocking?");
	}
	
	class Blinker implements Runnable {
		Color one, two;
		long howLong;String message;
		public Blinker(Color one, Color two, long howLong, String message) {
			this.one = one;
			this.two = two;
			this.howLong = howLong;
			this.message = message;
		}

		public void run() {
			long time = System.currentTimeMillis();
			Color c;
			progress.setString(message);
			while (System.currentTimeMillis() < time + howLong) {
				try {
					Thread.sleep(100);
					c = (System.currentTimeMillis() % 2 == 0) ? one : two;
					progress.setForeground(c);
				} catch (InterruptedException e) {
					System.err.println("InterruptedException:" + e);
				}
			}
		}
	}
}
