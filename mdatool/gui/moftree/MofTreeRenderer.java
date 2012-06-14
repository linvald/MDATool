package mdatool.gui.moftree;

import java.awt.*;
import java.io.File;
import java.net.URL;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.*;

import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Operation;

import mdatool.mof.uml.UMLElement;

public class MofTreeRenderer extends DefaultTreeCellRenderer {
	ImageIcon icon;

	public MofTreeRenderer() {
		icon = new ImageIcon("img/class.gif");
	}

	public Component getTreeCellRendererComponent(
		JTree tree,
		Object value,
		boolean sel,
		boolean expanded,
		boolean leaf,
		int row,
		boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		setTheIcon(value);
		return this;
	}

	protected void setTheIcon(Object value) {
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (node.getUserObject() instanceof org.omg.uml.foundation.core.UmlClass) {

				setIcon(new ImageIcon(MofTreeRenderer.class.getResource("img/package.gif")));
				//setToolTipText("Element type:" + elm.getComment());
			}
			if (node.getUserObject() instanceof TreePanel.SimpleWrap) {
				TreePanel.SimpleWrap o = (TreePanel.SimpleWrap) node.getUserObject();
				if (o.getWrapped() instanceof org.omg.uml.foundation.core.Operation) {
					setIcon(new ImageIcon(MofTreeRenderer.class.getResource("img/tab.gif")));
				} else if (o.getWrapped() instanceof org.omg.uml.foundation.core.Attribute) {
					setIcon(new ImageIcon(MofTreeRenderer.class.getResource("img/other.gif")));
				} else if(o.getWrapped() instanceof org.omg.uml.foundation.core.UmlClass) {
					setIcon(new ImageIcon(MofTreeRenderer.class.getResource("img/class.gif")));
				}
			}else if(node.getUserObject() instanceof String){
				setIcon(new ImageIcon(MofTreeRenderer.class.getResource("img/package.gif"))); 
			}
			
			else {
				setIcon(new ImageIcon(MofTreeRenderer.class.getResource("img/code.gif")));
				//	setToolTipText("Element type:" + elm.getComment());
			}
			/*else {
						setIcon(new ImageIcon(MofTreeRenderer.class.getResource("img/x.gif")));
					}*/
		} 
	}
}
