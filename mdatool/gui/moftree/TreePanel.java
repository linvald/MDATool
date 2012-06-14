package mdatool.gui.moftree;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.jmi.model.MofClass;
import javax.jmi.model.MofClassClass;
import javax.jmi.model.StructuralFeature;
import javax.jmi.reflect.RefPackage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.netbeans.api.mdr.MDRepository;

import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.Operation;
import org.omg.uml.foundation.core.UmlClass;

import mdatool.gui.application.Main;
import mdatool.gui.configuration.ProjectProperties;
//import mdatool.mof.uml.Attribute;
//import mdatool.mof.uml.Operation;
import mdatool.mof.modelwrappers.MOFModelWrapper;
import mdatool.mof.repository.RepositoryManager;
/*
 * Created on 01-11-2003 by jesper
 * This class should 
 */
/**
 * @author jesper
 */
public class TreePanel extends JPanel {
	private JTree tree;
	private JScrollPane scrollTree;

	private URL modelURL = null;
	private RepositoryManager repository = null;
	private final String XMI_FILE_URL = ProjectProperties.getString("xmifile");
	private DefaultTreeModel treeModel = null;
	private DefaultMutableTreeNode root;
	private MOFModelWrapper uml;
	private ArrayList _rootNodes = new ArrayList(); //add nodes to remove them
	private DefaultTreeModel _treeModel;
	
	public TreePanel(MOFModelWrapper uml) {
		initTree();
	}
	
	public TreePanel() {
		initTree();
		loadMofTree();
		for (Iterator iter = _rootNodes.iterator(); iter.hasNext();) {
				DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) iter.next();
				root.add(dmt);
		}
	}

	public void initTree() {
		setLayout(new GridLayout(1, 1));
		root = new DefaultMutableTreeNode("Meta models");
		_treeModel = new DefaultTreeModel(root);
		tree = new JTree(_treeModel);
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setShowsRootHandles(true);
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new MofTreeRenderer());
		tree.addTreeSelectionListener(new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent event) {
				if (Main.getInstance().getActiveCanvasFrame() != null) {

					boolean selected =
						Main.getInstance().getActiveCanvasFrame().selectElementByName(
							event.getPath().getLastPathComponent().toString());
					if (!selected) {
						//select parent
						TreePath path = event.getPath();
						Main.getInstance().getActiveCanvasFrame().selectElementByName(
							path.getPathComponent(path.getPathCount() - 2).toString());
					}
				}
			}
		}); // end addTreeSelectionListener

		scrollTree = new JScrollPane(tree);
		Dimension minimumSize = new Dimension(200, 400);
		scrollTree.setMinimumSize(minimumSize);
		add(scrollTree);
	}


	public void loadMofTree() {
		setupTreeForMofModel(root);
		this.invalidate();
		this.doLayout();
		this.repaint();
	}


	public void makeModelTree(MOFModelWrapper mof) {
		System.out.println("TreePanel.makeModelTree() -  modelWrapper:" + mof);
		DefaultMutableTreeNode subRoot = new DefaultMutableTreeNode(mof.getModelName());	
		Collection c = mof.getMOFClasses();
		DefaultMutableTreeNode clazzNode, operationNode, attributeNode = null;
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			MofClass clazz = (MofClass )iter.next();
			SimpleWrap SW = new SimpleWrap(clazz);
			clazzNode = new DefaultMutableTreeNode(clazz.getName());
			subRoot.add(clazzNode);
			clazzNode.setUserObject(SW);
			if (MOFModelWrapper.getMOFAttributes(clazz).size() > 0) {
				System.out.println("FOUND ATTRIBUTE");
				for (Iterator iterator =MOFModelWrapper.getMOFAttributes(clazz).iterator(); iterator.hasNext();) {
					StructuralFeature attribute = (StructuralFeature) iterator.next();
					System.out.println("NAME:"+attribute.getName());
					SimpleWrap SI = new SimpleWrap(attribute);
					attributeNode = new DefaultMutableTreeNode(SI);
					clazzNode.add(attributeNode);
					attributeNode.setUserObject(SI);
				}
			} else {
				System.out.println("Class have no attributes...");
			}
			if (MOFModelWrapper.getMOFOperations(clazz) != null) {

				if (MOFModelWrapper.getMOFOperations(clazz).size() > 0) {

					for (Iterator iterator = MOFModelWrapper.getMOFOperations(clazz).iterator();
						iterator.hasNext();
						) {
						javax.jmi.model.Operation opp = (javax.jmi.model.Operation ) iterator.next();
						SimpleWrap SU = new SimpleWrap(opp);
						operationNode = new DefaultMutableTreeNode(SU);
						operationNode.setUserObject(SU);
						clazzNode.add(operationNode);
					}
				} else {
					System.out.println("Class have no attributes...");
				}
			}
		}
		//topRoot.add(subRoot);
		
		this._rootNodes.add(subRoot);
	}
	public void makeUMLModelTree(MOFModelWrapper mof) {
			DefaultMutableTreeNode subRoot = new DefaultMutableTreeNode(mof.getModelName());	
			Collection c = mof.getClasses();
			DefaultMutableTreeNode clazzNode, operationNode, attributeNode = null;
			for (Iterator iter = c.iterator(); iter.hasNext();) {
				UmlClass clazz = (UmlClass )iter.next();
				SimpleWrap SW = new SimpleWrap(clazz);
				clazzNode = new DefaultMutableTreeNode(clazz.getName());
				subRoot.add(clazzNode);
				clazzNode.setUserObject(SW);
				if (MOFModelWrapper.getAttributes((Classifier)clazz).size() > 0) {
					for (Iterator iterator =MOFModelWrapper.getAttributes((Classifier)clazz).iterator(); iterator.hasNext();) {
						Attribute attribute = (Attribute) iterator.next();
						SimpleWrap SI = new SimpleWrap(attribute);
						attributeNode = new DefaultMutableTreeNode(SI);
						clazzNode.add(attributeNode);
						attributeNode.setUserObject(SI);
					}
				} else {
					System.out.println("Class have no attributes...");
				}
				if (MOFModelWrapper.getOperations((Classifier)clazz) != null) {

					if (MOFModelWrapper.getOperations((Classifier)clazz).size() > 0) {

						for (Iterator iterator = MOFModelWrapper.getOperations((Classifier)clazz).iterator();
							iterator.hasNext();
							) {
							Operation opp = (Operation) iterator.next();
							SimpleWrap SU = new SimpleWrap(opp);
							operationNode = new DefaultMutableTreeNode(SU);
							operationNode.setUserObject(SU);
							clazzNode.add(operationNode);
						}
					} else {
						System.out.println("Class have no attributes...");
					}
				}
			}
			//topRoot.add(subRoot);
		
			this._rootNodes.add(subRoot);
		}


	public void setupTreeForMofModel(DefaultMutableTreeNode topRoot) {
		DefaultMutableTreeNode subRoot = new DefaultMutableTreeNode("MOF UML");
		
		
		//Collection c = uml.getClasses();
		DefaultMutableTreeNode clazzNode, operationNode, attributeNode = null;
		MDRepository mdr = Main.getInstance().getRepository();

		RefPackage refP = mdr.getExtent("MOF");
		Collection all = refP.refAllClasses();
		for (Iterator iter = all.iterator(); iter.hasNext();) {
			Object cl = (Object) iter.next();
			String name = cl.getClass().getName();
			String shorty = name.substring(name.lastIndexOf(".")+1);
			String strip = shorty.substring(0,shorty.lastIndexOf("$"));
			clazzNode = new DefaultMutableTreeNode(strip);
			addAttributes(cl,clazzNode);
			
			subRoot.add(clazzNode);
		}
		this._rootNodes.add(subRoot);
		//return subRoot;
		//topRoot.add(subRoot);
	}
	
	private void addAttributes(Object o, DefaultMutableTreeNode parent) {
		Field[] fields = o.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = (Field) fields[i];
			System.out.println(f);
			parent.add(new DefaultMutableTreeNode(f.getName() + ":" + f.getType()));
		}
		Field[] fields2 = o.getClass().getFields();
		for (int i = 0; i < fields2.length; i++) {
			Field f = (Field) fields2[i];
			System.out.println(f);
			parent.add(new DefaultMutableTreeNode(f.getName() + ":" + f.getType()));
		}
		Method[] methods = o.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method m = (Method) methods[i];
			String s = m.getModifiers() == Member.PUBLIC ? "+" : "-";
			s += " " + m.getReturnType().getName() +" "+ m.getName() + "(";
			Class[] params = m.getParameterTypes();
			if (params.length > 0) {

				for (int j = 0; j < params.length; j++) {

	
					if (j == params.length - 1) {
						s += "arg" +j+ ")";
					} else {
						s += "arg" + j + ", ";
					}
				}
			} else {
				s += ")";
			}
			parent.add(new DefaultMutableTreeNode(s));
		}
	}
	
	public JTree getTree() {
		return this.tree;
	}
	public static void main(String[] args) {
		JFrame f = new JFrame("test");
		f.setSize(500, 500);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.show();

	}

	/**
	 * @param frameName
	 */
	public void removeNode(String frameName) {
		int count = 0;
		ArrayList copy = _rootNodes;
		for (Iterator iter = copy.iterator(); iter.hasNext();) {
			DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) iter.next();
			Object uO = dmt.getUserObject();
			if(uO instanceof String){
				String s = (String)uO;
				if(s.equals(frameName)) {
					_treeModel.removeNodeFromParent(dmt);
					Object o = this._rootNodes.remove(count);
					System.out.println("Removed " + o);
					refreshTree(copy);
					break;
				}
			}
			count++;
		}
		
		
	}
	public void refreshTree(ArrayList nodes){
		
		if(_treeModel==null){
					_treeModel = new DefaultTreeModel(root);
				}

				int count2 = 0; 
				for (Iterator iter2 = nodes.iterator(); iter2.hasNext();) {
					DefaultMutableTreeNode dmt2 = (DefaultMutableTreeNode) iter2.next();		
					this._treeModel.insertNodeInto(dmt2,root,count2);
					count2++;
				}

				tree.invalidate();
				this.invalidate();
				this.doLayout();
				this.repaint();
	}
	class SimpleWrap{
		Object toWrap;
		public SimpleWrap(Object o){
			toWrap = o;
		}
		public Object getWrapped(){
			return toWrap;
		}
		public String toString(){
			if(toWrap instanceof Attribute){
				return ((Attribute)toWrap).getName();
			}else if(toWrap instanceof Operation){
				return ((Operation)toWrap).getName();
			}else if(toWrap instanceof UmlClass){
				return ((UmlClass)toWrap).getName();
			}
			return "";
		}
	}
	/**
	 * @param u
	 */
	public void loadMofModel(MOFModelWrapper u) {
		if(root == null){
					initTree();
				}
				makeModelTree(u);
				System.out.println("Treemodel is null:" + treeModel == null);
				if(_treeModel==null){
					_treeModel = new DefaultTreeModel(root);
				}
				int count = 0;
				for (Iterator iter = _rootNodes.iterator(); iter.hasNext();) {
					DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) iter.next();		
					this._treeModel.insertNodeInto(dmt,root,count);
					count++;
				}

				tree.invalidate();
				this.invalidate();
				this.doLayout();
				this.repaint();
	}
}
