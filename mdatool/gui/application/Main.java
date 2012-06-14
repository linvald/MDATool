//http://www.google.dk/search?q=cache:iyw2XN33HScJ:www.webcubed.com/java2/+java+shape+handle&hl=da&ie=UTF-8
package mdatool.gui.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import mdatool.gui.StatusBar;
import mdatool.gui.StatusPanel;
import mdatool.gui.actions.ShowPreferenceFrameAction;
import mdatool.gui.actions.ToolBarActionFactory;
import mdatool.gui.canvas.CanvasInternalFrame;
import mdatool.gui.canvas.ShapeCanvas;
import mdatool.gui.configuration.ConfigurationPanel;
import mdatool.gui.configuration.UMLSettingPanel;
import mdatool.gui.configuration.UserPreferences;
import mdatool.gui.moftree.TreePanel;
import mdatool.gui.shapes.uml.UmlShape;
import mdatool.mof.repository.RepositoryManager;

import org.netbeans.api.mdr.MDRepository;


public final class Main extends JFrame implements PropertyChangeListener{
	
	//singleton
	private static final Main _instance = new Main();
	protected int _iFrameCount;
	private Container desktop;
	private StatusPanel _status;
	protected StatusBar statusBar;
	private ShapeCanvas _selectedFrame = null;

	private JSplitPane _splitHorizontal,_splitVertical;
	private JPanel _panelLeftSide, _panelToolbar, _panelCenter,_panelTop, _bottomPanel;
	private JToolBar _toolBar;

	private Dimension _screenSize;
	private JTabbedPane _tabLeft;
	private JPopupMenu tabPopup;
	private Map _canvasMap;
	private JDesktopPane _desktop;
	protected ConfigurationPanel _config;
	protected UMLSettingPanel _umlSettingPanel;
	protected TreePanel _tree;
	private LoadWindow load;
 
	private JScrollPane navigationPane, _umlSettingScroll;
	
	private MDRepository _repository;
	private RepositoryManager _repositoryManager;
	
	private JTabbedPane _bottomTab;
	private JPanel settingPanel;
	protected Main() {
		super("MOF Transformation editor");
		load = new LoadWindow();
		load.setLocation(((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2)-load.getWidth()/2,
		((int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2)-load.getHeight());
		load.setVisible(true);
		setIconImage(new ImageIcon(Main.class.getResource("class.gif")).getImage());		
		initialize();
	}
    


	/**
	 * Initialize a default repository with MOF model as default extent
	 */
	private void initRepository() {
		this._repositoryManager = new RepositoryManager();				
	}
	
	public MDRepository getRepository(){
		return _repositoryManager.getMDRepository();
	}
	
	public RepositoryManager getRepositoryManager(){
		return this._repositoryManager;
	}

	//singleton 
	public static Main getInstance(){
		return _instance;
	}
 

	
	private void initialize(){
		//_canvasMap = new HashMap();

		_config = new ConfigurationPanel();
		//_config.setBackground(Color.white);
		_screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//setBounds(0, 0, _screenSize.width, _screenSize.height);
		_desktop = new JDesktopPane();
		        
		desktop = getContentPane();
		
		BorderLayout layout = new BorderLayout();
		desktop.setLayout(layout);
		desktop.setBackground(Color.yellow)	;
		//desktop.setSize(_screenSize);
		setupToolBarPanel();  
  
		setJMenuBar(createMenuBar());
        
		//Quit this app when the big window closes.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(getRepositoryManager()!=null)
					getRepositoryManager().shutDownRepositories();
				System.exit(0);
			}
		});
   
		_panelTop = new JPanel();
		_panelTop.setLayout(new BorderLayout());
		_panelTop.setBackground(Color.green);
		_panelTop.setSize(_screenSize.width,_screenSize.height - 170);
		_panelTop.setMinimumSize(new Dimension(300,300));
		
		_status = new StatusPanel();
		_status.addStatusLine("Ready...");
		_status.setMaximumSize(new Dimension((int)this._screenSize.getWidth(),300));
		_status.setPreferredSize(new Dimension((int)this._screenSize.getWidth(),100));
		
		_bottomTab = new JTabbedPane();
		_bottomTab.setTabPlacement(JTabbedPane.BOTTOM);
		statusBar = new StatusBar(500,30,"Progress:",Color.getHSBColor(154f,155f,197f));
		
		_bottomPanel = new JPanel();
		_bottomPanel.setLayout(new GridLayout(2,1));
		_bottomTab.addTab("Status", _status);
		
		
		getContentPane().add("South",statusBar);
		
		_splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT,_panelTop, _bottomTab);
		_splitVertical.setOneTouchExpandable(true);
		_splitVertical.setDividerLocation(this.getHeight()-100);
	
		getContentPane().add(_splitVertical);
					
		//HORIZONTAL
		_tabLeft = new JTabbedPane();
				
		_panelLeftSide = new JPanel();
		_panelLeftSide.setBackground(Color.white);
		_panelLeftSide.setLayout(new GridLayout(1,1));
		navigationPane = new JScrollPane(_panelLeftSide);
		//_tabLeft.addTab("Conf",new JScrollPane(_config));
		_tabLeft.addTab("MOF", navigationPane);
		
		_umlSettingPanel = new UMLSettingPanel();
		
		settingPanel = new JPanel();
		settingPanel.setLayout(new BorderLayout());
		settingPanel.add(_umlSettingPanel,BorderLayout.NORTH);
		_umlSettingScroll = new JScrollPane(settingPanel);
		_bottomTab.addTab("UML",_umlSettingScroll );
		
		_panelCenter = new JPanel();
		_panelCenter.setBackground(Color.white);
		_panelCenter.setLayout(new GridLayout(1,1));
		_panelCenter.add(_desktop);
		
		_splitHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,_tabLeft, _panelCenter);
		Dimension leftMinimum = new Dimension(200,400);
		_panelLeftSide.setMinimumSize(leftMinimum);
		Dimension centerMinimum = new Dimension(400,400);
		_panelCenter.setMinimumSize(centerMinimum);
		_splitHorizontal.setOneTouchExpandable(true);
		_splitHorizontal.setDividerLocation(150);
		_splitHorizontal.setPreferredSize(new Dimension(400, 700));
		_panelTop.add(_splitHorizontal,BorderLayout.CENTER);	
				
		Dimension statusSize = new Dimension(getWidth(), 150);
		Dimension leftSize = new Dimension(30, getHeight());

		_status.setMinimumSize(statusSize);
		_status.setPreferredSize(statusSize);

		navigationPane.setMinimumSize(leftSize);
		navigationPane.setPreferredSize(leftSize);
	

		//_desktop.setMinimumSize(desktopSize);
		//_desktop.setPreferredSize(desktopSize);

		Dimension preferred = new Dimension(_screenSize.width, 50);
		_status.setSize(preferred);
		_bottomPanel.setPreferredSize(preferred);
		_bottomPanel.setMinimumSize(preferred);

		_status.setMinimumSize(preferred);
		_status.setStatusLabelColor(Color.white);

		setSize(_screenSize.width, _screenSize.height);
		setBounds(0, 0, _screenSize.width, _screenSize.height);
		setState(JFrame.MAXIMIZED_BOTH);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		

	}
   	 	
	private void setupToolBarPanel(){
		ToolBarActionFactory factory = ToolBarActionFactory.getToolBarActionFactory();
		_panelToolbar = new JPanel(new GridLayout(1,1));
		_panelToolbar.setSize(desktop.getWidth(),20);
		_panelToolbar.setBackground(Color.blue);
   		
		_toolBar = new JToolBar();
		_panelToolbar.setBounds(0,0,_screenSize.width,20);
		_toolBar.setBorderPainted(true);
		
		_toolBar.setBorder(BorderFactory.createLineBorder(Color.black));
		_toolBar.setBorder(BorderFactory.createEtchedBorder());
		
		
		
		_toolBar.setBounds(0,0,desktop.getWidth(),ImageObserver.HEIGHT);
		_toolBar.add(factory.makeLoadXmiAction());
		_toolBar.add(factory.makeLoadMOFXmiAction());
		_toolBar.add(factory.makeNewAction());
		_toolBar.add(factory.makeSaveAction());
		_toolBar.add(factory.makeJMIAction());
		
		_toolBar.addSeparator();
		
		_toolBar.add(factory.makeClassAction());
		_panelToolbar.add(_toolBar,BorderLayout.WEST);
		_panelToolbar.setVisible(true);
		desktop.add(_panelToolbar,BorderLayout.NORTH);	
		setLookAndFeel();
	}
   	
	/**
	 * Method getActiveCanvasFrame.
	 * @return UMLCanvasFrame
	 */
	public ShapeCanvas getActiveCanvasFrame(){
		return _selectedFrame;
		
	}
	public JInternalFrame getActiveInternalFrame(){
		return _desktop.getSelectedFrame();
	}
	public void setActiveCanvasFrame(ShapeCanvas active){
			_selectedFrame = active;
	} 
	public JDesktopPane getDesktop(){
		return this._desktop; 
	}
    
    public void setSettingPanel(JPanel p){
		this.settingPanel.removeAll();
		this.settingPanel.add(p, BorderLayout.NORTH);
    }
    
    public void showClassSettings(UmlShape s){
		this.settingPanel.removeAll();
		UMLSettingPanel us= new  UMLSettingPanel();
		this.settingPanel.add(us);
		us.showClassSettingPanel(s);
    }
    
	public void setStatus(String s){
		this._bottomTab.setSelectedComponent(this._status);
		_status.addStatusLine(s);	
	}
	
	private void setLookAndFeel() {
		_desktop.setBackground(Color.lightGray);
		_desktop.setToolTipText("Click new icon to open canvas");
		try {

			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("InternalFrame.activeTitleBackground",new ColorUIResource( Color.red));
			UIManager.put("InternalFrame.activeTitleForeground",new ColorUIResource( Color.white));
			UIManager.put("InternalFrame.inactiveTitleBackground", new ColorUIResource(Color.yellow));
			UIManager.put("InternalFrame.inactiveTitleForeground", new ColorUIResource(Color.white));
		} catch (Exception e) {
			System.err.println("Createframe...");
			e.printStackTrace();
		}
		try {
			UIManager.setLookAndFeel(this._config.getUserPreferences().getLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);

		} catch (Exception ex) {
			System.out.println("Could not load look and feel " + ex);
		}
	}
	
	
	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenu menuView = new JMenu("View");
		JMenuItem menuItem = new JMenuItem( ToolBarActionFactory.getToolBarActionFactory().makeNewAction());
		JMenuItem itemConf = new JMenuItem(new ShowPreferenceFrameAction());
		
		menuFile.add(menuItem); 
		menuFile.add(itemConf);      
		menuBar.add(menuFile);
		menuBar.add(menuView);

		return menuBar;
	}
	
	public void newFrame(String name) {
		CanvasInternalFrame frame = new CanvasInternalFrame(name);
	
			  frame.setVisible(true); //necessary as of 1.3
			  _desktop.add(frame);
			  try {
				  frame.setSelected(true);
				  frame.setMaximum(true);
			  } catch (java.beans.PropertyVetoException e) {}
			  
	}
	
	public UMLSettingPanel getUmlSettingPanel(){
		//select the Tab - this is probably what the user wants :)
		this._bottomTab.setSelectedComponent(this._umlSettingScroll);
		return _umlSettingPanel;
	}
	
	public JPanel getTreePanel(){
		return _panelLeftSide;
	}
    
	public UserPreferences getUserPrefs(){
		return this._config.getUserPreferences();
	}
	public ConfigurationPanel getConfigurationPanel(){
		return this._config;
	}
    
    public Dimension getPreferredSize(){
    	 return this._screenSize;
    }
        
	public static void main(String[] args) {
		//setDefaultLookAndFeelDecorated(true);
		final Main frame = Main.getInstance();
		frame.pack();
		frame.setVisible(true); 
		frame.startBootSequence();
		
	}
	/**
	 * start booting after the gui has loaded
	 */
	private void startBootSequence() {
		
		JWindow f = new JWindow();
		f.setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2);
		f.setBackground(Color.white);
		JLabel l = new JLabel("Loading....please wait");
		setCursor(Cursor.WAIT_CURSOR );
		l.setBackground(Color.white);
		f.setBackground(Color.white);
		f.setSize(new Dimension(200,50));

		f.getContentPane().add(l);		
		f.pack();
		f.setVisible(true);
		this.setEnabled(false);
		initRepository(); 
		_tree =new TreePanel();
		//_tree.loadMofTree();
		getTreePanel().add(_tree);
		_splitHorizontal.setDividerLocation(150);
		f.setVisible(false);
		f = null;
		this.setEnabled(true);
		this.setState(JFrame.MAXIMIZED_BOTH);
		setCursor(Cursor.DEFAULT_CURSOR );
		load.setVisible(false);
	}
	public void setTree(TreePanel tree){
		
		/*getTreePanel().removeAll();
		_tree = tree;
		
		getTreePanel().add(_tree);
		_tree.repaint();
		_tree.invalidate();
		getTreePanel().invalidate();
					getTreePanel().repaint();
	*/
		
	}
public TreePanel getTree(){
	return _tree;
}


	public JScrollPane getNavigationScrollPane(){
		return navigationPane;
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		if(arg0.getPropertyName().equals("mouse")){
			this.statusBar.setMouseX_Y(Integer.parseInt(arg0.getOldValue().toString()),Integer.parseInt(arg0.getNewValue().toString()));
		}
	}
	/**
	 * @return
	 */
	public StatusBar getStatusBar() {
		return statusBar;
	}



	/**
	 * 
	 */
	public JTabbedPane getNavigationTab() {
		return this._tabLeft;
	}

}
