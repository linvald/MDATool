package mdatool.gui.moftree;

import java.util.Collection;
import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import mdatool.mof.modelwrappers.MOFModelWrapper;
import mdatool.mof.uml.Attribute;
import mdatool.mof.uml.Operation;
import mdatool.mof.uml.UMLElement;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class MofTreeModel implements TreeModel{


	// TreeModelListeners
	private Vector listeners = new Vector();
//	private UmlModelWrapper umlModel;	
	private String root = "root";
	private final String operations = "operations";
	private final String attributes = "attributes";
	
	private Collection classes;
	
	public MofTreeModel(String xmiFileName, Collection classes){
		this.root = xmiFileName;
		//this.umlModel = umlModel;
		classes = classes;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return root;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		if (parent instanceof UMLElement) {
			if (parent instanceof mdatool.mof.uml.Class) {
				mdatool.mof.uml.Class clazz = (mdatool.mof.uml.Class) parent;
			//	return UmlModelWrapper.getAttributes(clazz.getClazz()).size()
			//		+ UmlModelWrapper.getOperations(clazz.getClazz()).size();
			return -1;
			}
		}
		if (parent instanceof String) {
			String s = (String) parent;
			if (s.equals(root))
				return classes.size();
		} else {
			return 2;
		}

		return -1;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object parent) {
		if (parent instanceof Operation || parent instanceof Attribute) {
				return true;
						
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener listener) {
		listeners.add( listener );
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener listener) {
		listeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		UMLElement element = (UMLElement)parent;
		if(parent instanceof Class){
			mdatool.mof.uml.Class c = (mdatool.mof.uml.Class)parent;
			return (mdatool.mof.uml.Attribute)c.getAttributes().get(index);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	public int getIndexOfChild(Object arg0, Object arg1) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	public void valueForPathChanged(TreePath arg0, Object arg1) {}



}
