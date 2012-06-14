package mdatool.gui;


import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import mdatool.gui.application.Main;

import java.awt.event.*;

import java.awt.*;



public class StatusPanel extends JPanel{


	private DefaultListModel _model;
	private JList _list;
	private JScrollPane _scroll;
	private JViewport _viewPort;

	
	public StatusPanel() {   
          
              this._model  = new DefaultListModel();
              this._list = new JList(this._model);
              this._scroll = new JScrollPane(this._list);
              this._scroll.setWheelScrollingEnabled(true);
          	  this._viewPort = _scroll.getViewport();
              
             this.setLayout(new BorderLayout());
             
             this.addComponentListener(new ComponentAdapter() {      
             	public void componentResized(ComponentEvent e) {
             		if(e.getComponent().getClass() == this.getClass()){   
             			 setSize(e.getComponent().getSize().width,e.getComponent().getSize().height);    
             			_scroll.setSize(e.getComponent().getSize().width,e.getComponent().getSize().height);
             			_list.setSize(e.getComponent().getSize().width,e.getComponent().getSize().height);
             			setLastInFocus();
             		}
             		      
             	} 
             	public void componentMoved(ComponentEvent e) { 
             		   
             		             			 setSize(e.getComponent().getSize().width,e.getComponent().getSize().height);    
             			_scroll.setSize(e.getComponent().getSize().width,e.getComponent().getSize().height);
             			_list.setSize(e.getComponent().getSize().width,e.getComponent().getSize().height);
             			setLastInFocus();	
             	}   
             });
     
           	this.add(this._scroll);
            this.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
          
            this.setVisible(true);
            // this.show();
	}
    public void setStatusLabelColor(Color color){
    	setBackground(color);	
    }
	public void setLastInFocus(){
		//this._viewPort.setViewPosition(new Point(0,this._list.getModel().getSize()*this._list.getCellBounds(0,0).height));
		this._list.setSelectedIndex(0);	
	}
	public void addStatusLine(String line){
	// Append an item
    //int pos = this._list.getModel().getSize();
    //prepend
    _model.add(0, line);
    setLastInFocus();
    	
	}
	public Dimension getPreferredSize(){
		return new Dimension(this.getWidth(),150);
	}
	public Dimension getMaximumSize(){
		return getPreferredSize();
	}
     
}