package mdatool.mof.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofClass;
import javax.jmi.model.MofPackage;
import javax.jmi.model.MofPackageClass;
import javax.jmi.model.NamespaceClass;
import javax.jmi.model.Tag;
import javax.jmi.model.VisibilityKindEnum;
import javax.jmi.reflect.RefPackage;
import javax.jmi.xmi.MalformedXMIException;

import mdatool.gui.application.Main;
import mdatool.mof.common.FilteredCollection;
import mdatool.mof.jmi.SimpleJMIStreamFactory;
import mdatool.mof.modelwrappers.MOFModelWrapper;

import org.netbeans.api.mdr.CreationFailedException;
import org.netbeans.api.mdr.JMIMapper;
import org.netbeans.api.mdr.MDRManager;
import org.netbeans.api.mdr.MDRepository;
import org.netbeans.api.xmi.XMIReader;
import org.netbeans.api.xmi.XMIReaderFactory;
import org.netbeans.api.xmi.XMIWriterFactory;
import org.netbeans.lib.jmi.mapping.JavaMapper;
import org.netbeans.lib.jmi.uml2mof.Transformer;
import org.netbeans.lib.jmi.xmi.XmiConstants;
import org.netbeans.mdr.NBMDRepositoryImpl;
import org.omg.uml.UmlPackage;
import org.omg.uml.foundation.core.Classifier;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.Namespace;

import dk.linvald.logging.Logger;

/**
 * using the <a href="http://mdr.netbeans.org">NetBeans MetaDataRepository</a>.
 */
public class RepositoryManager extends MDRManager{
	
	
	protected final String META_PACKAGE = "UML";
	
	protected final String MOF_META_PACKAGE = "MOF";

	/** name of system-property used to choose storage type */
	private final String STORAGE_FACTORY_PROPERTY =
		"org.netbeans.mdr.storagemodel.StorageFactoryClassName";

	/** default storage type */
	private final String STORAGE_FACTORY_DEFAULT =
		"org.netbeans.mdr.persistence.btreeimpl.btreestorage.BtreeFactory";

	/** default repository name */
	private final String DEFAULT_REPOSITORY_NAME = "MAINREPOSITORY";

	/** repositories */
	public Map repositories = new HashMap();
	
	/** The default main repository */
	protected MDRepository repository;
	
	/** The URL pointing at the MOF UML metamodel - metamodel located in uml14.jar - not uml-1.4.jar */
	protected final URL umlMetaModelURL = RepositoryManager.class.getResource("/umlmodel.xml");
	
	/** The MOF meta model extent of MOF itself */
	private ModelPackage metaModelExtent;
	
	/** The MOF meta model package of UML */
	private MofPackage umlMetaModel;
	
	/** The MOF PrimitiveTypes*/
	private MofPackage primitiveTypes = null;
	
	/** The MOF Metamodel itself */
	private MofPackage mof = null;
	
	private MofPackage corbaIdlTypes = null;
	/** The cased RefPackages*/
	protected HashMap cashedRefPackages = new HashMap(); //mofId, RefPackage
	
	private final Logger logger = new Logger();


	 
	static {
		//System.setProperty("org.netbeans.mdr.persistence.btreeimpl.filename","btree");
		// configure MDR to use an in-memory storage implementation
		//System.setProperty("org.netbeans.mdr.storagemodel.StorageFactoryClassName","org.netbeans.mdr.persistence.memoryimpl.StorageFactoryImpl");
		//use btree implementation of storage
		System.setProperty("org.netbeans.mdr.storagemodel.StorageFactoryClassName", "org.netbeans.mdr.persistence.btreeimpl.btreestorage.BtreeFactory");
		// set the logging so output does not go to standard out
	 	System.setProperty("org.netbeans.lib.jmi.Logger.fileName","mdr.log"); 

	};  
	/** StackOverflow - might have to do with Logger property ?*/
/*	static {    
		 // if running outside of NetBeans IDE ...
		 if (System.getProperty( "org.openide.version" ) == null) {
			 // register StandAloneLookup  
			 System.setProperty( "org.openide.util.Lookup", StandAloneLookup.class.getName() );
		 }
	 }
*/
	
	/**
	 * Constructs a Facade around a netbeans MDR (MetaDataRepository).
	 */
	public RepositoryManager() {
		init();
		loadDefaultMetaModels();	
		getPrimitiveMOFTypes();
	}


	public void init() {
		logger.set_loggingToFile(false);
		//load the mof uml meta model into out repository	
		try {
			umlMetaModel = loadMetaModel(umlMetaModelURL, getMDRepository());		
		} catch (Exception e) {
			System.err.println("Exception:" + e);		
		}
	}

	/**
	 * Opens the reposistory and prepares it to read in models.
	 * 
	 * <p> All the file reads are done within the context of a transaction 
	 * this seems to speed up the processing. </p>
	 */
	public void open() {
		//MDRManager.getDefault().getDefaultRepository().beginTrans(true);
		getDefaultRepository().beginTrans(true);
	}

	/**
	 * Closes the repository and reclaims all resources.
	 * 
	 * <p> This should only be called after all the models has been read and all querys 
	 * have completed. </p>
	 */
	public void close() {
		//MDRManager.getDefault().getDefaultRepository().endTrans(true);
		getDefaultRepository().endTrans(true);
	}
	
	/**
	 * Should be called when main application shuts down
	 */
	public void shutDownRepositories(){
		MDRManager.getDefault().shutdownAll();
	}

	/**
	 * Loads any mof model into the repository
	 */
	public void loadMofMetaModel(URL modelURL) throws Exception {
		try {
			if(modelURL !=null){
				RefPackage model = loadModel(modelURL, umlMetaModel, getMDRepository());
				cashedRefPackages.put(modelURL.toExternalForm(),model);
			}
		} catch (CreationFailedException cfe) {
			throw new Exception("unable to create metadata repository", cfe);
		} catch (MalformedXMIException mxe) {
			throw new Exception("malformed XMI data", mxe);
		} finally {}
		log("MDR: loaded meta model from " + modelURL.toString());
	}

	/**
	 * Load a model from a XMI file
	 * @param modelURL
	 * @param modelName
	 * @return a MOFModelWrapper wrapping the model
	 */
	public MOFModelWrapper loadAModel(URL modelURL, String modelName){
		RefPackage model = null;
		if(modelURL !=null)
			try {
				model = loadModel(modelURL, umlMetaModel, getMDRepository());
				cashedRefPackages.put(modelURL.toExternalForm(),model);
			} catch (CreationFailedException e) {
				System.err.println("CreationFailedException:" + e);
			} catch (IOException e) {
				System.err.println("IOException:" + e);
			} catch (MalformedXMIException e) {
				System.err.println("MalformedXMIException:" + e);
			}
		//ModelPackage umlPack = (ModelPackage)Main.getInstance().getRepositoryWrapper().getRepository().getExtent(Main.getInstance().getActiveInternalFrame().getTitle());
			MOFModelWrapper w = new MOFModelWrapper(this,modelName, model);
			return w;
	}
	
	/**
	 * Load a model from a XMI file
	 * @param modelURL
	 * @param modelName
	 * @return a MOFModelWrapper wrapping the model
	 */
	public MOFModelWrapper loadAMOFModel(URL modelURL, String modelName){
		/*1) Instantiate Model package, i.e. go to MOF (package extent) -> Package (class
proxy) -> Model (instance of Package) and perform instantiate action on it. Let the name of the created package be "Model_1". Note that Model_1 is exactly of the same type as the default MOF package, both packages are instances of MOF Model package. The only difference is that Model_1 does not contain any data
(instances) yet while MOF package contains data related to MOF metamodel by default.
*/
		printRepositoryInfo();
		RefPackage model = null;					
		MofPackage mof = getMOFPackage();


				if(mof != null){
					System.out.println("FOUND MOF METAMODEL:" + mof);
				}else{
					System.out.println("COULDNT FIND MOF METAMODEL");
				}
				
				//we use MOF as the meta model
				try {
					model = loadModel(modelURL, mof, getMDRepository());
					cashedRefPackages.put(modelURL.toExternalForm(),model);
					Collection all = model.refAllClasses();
					for (Iterator iter = all.iterator(); iter.hasNext();) {
						Object element = (Object) iter.next();
						System.out.println("Loaded model element: " + element.getClass() );
					}
				} catch (CreationFailedException e) {
					System.out.println(e.getMessage());
				} catch (IOException e) {
					System.out.println(e.getMessage());
				} catch (MalformedXMIException e) {
					System.out.println(e.getMessage());
				}
				
			
			//MOFModelWrapper w = new MOFModelWrapper(this,modelName,(ModelPackage) model);
		MOFModelWrapper w = new MOFModelWrapper(this,modelName, model);
			return w;
	}
	
	
	/**
	 * Loads in the three default MOF packages
	 */
	public void loadDefaultMetaModels(){
		ModelPackage modlpck = (ModelPackage) repository.getExtent("MOF");
		Collection c =  modlpck.getMofPackage().refAllOfClass();
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			MofPackage element = (MofPackage) iter.next();
			if(element.getName().equals("Model")){
				this.mof = element;	
			}else if(element.getName().equals("PrimitiveTypes")){
				this.primitiveTypes = element;	
			}else if(element.getName().equals("CorbaIdlTypes")){
				this.corbaIdlTypes = element;	
			}
		}
	}
	
	public void printRepositoryInfo(){
		System.out.println("Extents: ");
		String[] extents = repository.getExtentNames();
		for (int i = 0; i < extents.length; i++) {
			System.out.println("Extent " + i +" = " + extents[i]);
		}
		System.out.println();
		
		
		
		System.out.println("Trying to locate MOF package:");
		ModelPackage mof = (ModelPackage) repository.getExtent("MOF");
		String s = (mof==null)? " NO " : " Yes " + mof;
		System.out.println("MOF is loaded:" +s  );
		if(mof!=null){
			System.out.println("Size of mof.refAllPackages:"+ mof.getMofPackage().refAllOfClass().size());
			Collection c =  mof.getMofPackage().refAllOfClass();
			
			for (Iterator iter = c.iterator(); iter.hasNext();) {
				MofPackage element = (MofPackage) iter.next();
				System.out.println("MOF PACKAGE:" + element.getName());
				System.out.println("Size of model.getContents:"+element.getContents().size());
				if(element.getName().equals("Model")){
					List l = element.getContents();
					for (Iterator iterator = l.iterator();iterator.hasNext();) {
						Object o = (Object) iterator.next();
						System.out.println("ELEMENT IN CONTENTS:" + o.getClass());
						if(o instanceof MofClass){
							MofClass mc = (MofClass)o;
							System.out.println("MOFCLASS:" + mc.getName());
						}					
					}		
				}
			}
			
		}
	}
	
	public MofPackage getMOFPackage(){
		MofPackage mofPackage = null;
		ModelPackage mof = (ModelPackage) repository.getExtent("MOF");
		Collection c =  mof.getMofPackage().refAllOfClass();
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			MofPackage pck = (MofPackage) iter.next();
			if("Model".equals(pck.getName())){
				mofPackage = pck;
				return mofPackage;
			}
		}
		return mofPackage;
	}
	
	public List getPrimitiveMOFTypes(){
		List prim = new ArrayList();
		MofPackage primitives = this.primitiveTypes;
		Collection c =primitives.getContents();
		for (Iterator iter = c.iterator(); iter.hasNext();) {
					Object pck = (Object) iter.next();
					if(pck instanceof javax.jmi.model.PrimitiveType){
						javax.jmi.model.PrimitiveType pt = (javax.jmi.model.PrimitiveType)pck;
						prim.add(pt);
					}
		}
		return prim;
	}
	
	
	
	/**
	 * Creates a new model extent with a given name and cashes it
	 * @param modelName The name of the model
	 * @return a MOFModelWrapper wrapping the model
	 */
	public MOFModelWrapper createMOFModel( String modelName){
		ModelPackage mp = null;
		try {			
			RefPackage exist = repository.getExtent(modelName);
					if (exist != null) {
						log("MDR: deleting existing model " + modelName);
						exist.refDelete();
					}
			RefPackage model = getMDRepository().createExtent(modelName,mof);
		
			mp =(ModelPackage)model;
			

			/*MofPackage pkg = mp.getMofPackage().createMofPackage(modelName, 
						"", 
						true, 
						false, 
						false, 
						VisibilityKindEnum.PUBLIC_VIS);
			
			NamespaceClass ns =(NamespaceClass) mp.getNamespace().refCreateInstance(new ArrayList());
			pkg.setContainer((javax.jmi.model.Namespace) ns);
			model.refAllPackages().add(pkg);*/			
			cashedRefPackages.put(modelName,model);			
			System.out.println("RepositoryWrapper.createUmlModel() - created refPack:" + model);	
		} catch (CreationFailedException e) {
			System.out.println("Failed in repositorywrapper to create extent:"+ e.getMessage());
			// The model probably exist - load it!
		}
		return new MOFModelWrapper(this,modelName,mp);
	}



	/**
	 * Loads a metamodel into the repository.
	 *
	 *@param  repository   MetaDataRepository
	 *@return MofPackage for newly loaded metamodel
	 * 
	 *@exception  CreationFailedException  
	 *@exception  IOException              
	 *@exception  MalformedXMIException
	 */
	private MofPackage loadMetaModel(URL metaModelURL, MDRepository repository)
		throws CreationFailedException, IOException, MalformedXMIException {

		// Use the metaModelURL as the name for the repository extent.
		// This ensures we can load mutiple metamodels without them colliding.
		metaModelExtent = (ModelPackage) repository.getExtent(metaModelURL.toExternalForm());

		if (metaModelExtent == null) {
			metaModelExtent = (ModelPackage) repository.createExtent(metaModelURL.toExternalForm());
		}

		MofPackage metaModelPackage = findPackage(META_PACKAGE, metaModelExtent);

		if (metaModelPackage == null) {
			XMIReader xmiReader = XMIReaderFactory.getDefault().createXMIReader();
			xmiReader.read(metaModelURL.toExternalForm(), metaModelExtent);

			// locate the UML package definition that was just loaded in
			metaModelPackage = findPackage(META_PACKAGE, metaModelExtent);
		}

		log("MDR: Created MOF metaModel for " +metaModelPackage.getName() + " using URL:" + metaModelURL.toExternalForm() );
		return metaModelPackage;
	}

	/**
	 * Loads a model (xmi file) into the repository and validates the model against
	 * the given metaModel.
	 *
	 *@param  modelURL                     url of model
	 *@param  repository                   netbeans MDR
	 *@param  metaModel                    meta model of model
	 *@return  populated model
	 * 
	 *@exception  CreationFailedException unable to create model in repository
	 *@exception  IOException  unable to read model
	 *@exception  MalformedXMIException model violates metamodel
	 */
	private RefPackage loadModel(URL modelURL, MofPackage metaModel, MDRepository repository)
									throws CreationFailedException, IOException, MalformedXMIException {
		
		log("MDR: loading model(RefPackage) from " + modelURL + " - metamodel is: " + metaModel.getName());

		RefPackage model = repository.getExtent(modelURL.toExternalForm());
		if (model != null) {
			log("MDR: deleting existing model");
			model.refDelete();
		}

		model = repository.createExtent(modelURL.toExternalForm(), metaModel);
		log("MDR: created model extent");

		// read the model
		XMIReader xmiReader = XMIReaderFactory.getDefault().createXMIReader();
		try {
			xmiReader.read(modelURL.toExternalForm(), model);
			log("MDR: Read XMI - " + modelURL.toExternalForm());
		} catch (Exception e) {
			System.err.println("Couldnt read XMI:" + e);
			throw new IOException("Could not read XMI:" +e);
		}

		log("MDR:Succesfully loaded model");
		String [] names = repository.getExtentNames();
		String tmp = "Loaded model extents now:";
		for (int i = 0; i < names.length; i++) {
			tmp += "	[" + (String)names[i] + "]";
		}
		log(tmp);
		return model;
	}

	/**
	 * Searches a meta model for the specified package.
	 *
	 *@param  packageName name of package for which to search
	 *@param  metaModel   meta model to search
	 *@return MofPackage
	 */
	private MofPackage findPackage(String packageName, ModelPackage metaModel) {
		for (Iterator it = metaModel.getMofPackage().refAllOfClass().iterator(); it.hasNext();) {
			javax.jmi.model.ModelElement temp = (javax.jmi.model.ModelElement) it.next();
			//System.out.println("LOOKING FOR "+packageName + " CURRENT:" + temp.getName());
			if (temp.getName().equals(packageName)) {
				return (MofPackage) temp;
			}
		}
		return null;
	}
	
	/**
	 * Get all elements of a particular UML model
	 * @return Collection of all the model elements (refAllOfType)
	 * @param modelName 
	 */
	public Collection getUMLModelElements(String modelName) {
		return ((UmlPackage) getModel(modelName)).getCore().getModelElement().refAllOfType();
	}


	/**
	 * Get a ModelElement from a given Namespace
	 * @param namespace
	 * @param fqn
	 * @param pos
	 * @return a ModelElement
	 */
	private static ModelElement getModelElement(Namespace namespace, String[] fqn, int pos) {
		if ((namespace == null) || (fqn == null) || (pos > fqn.length)) {
			return null;
		}

		if (pos == fqn.length) {
			return namespace;
		}

		Collection elements = namespace.getOwnedElement();
		for (Iterator i = elements.iterator(); i.hasNext();) {
			ModelElement element = (ModelElement) i.next();
			if (element.getName().equals(fqn[pos])) {
				int nextPos = pos + 1;

				if (nextPos == fqn.length) {
					return element;
				}

				if (element instanceof Namespace) {
					return getModelElement((Namespace) element, fqn, nextPos);
				}

				return null;
			}
		}

		return null;
	}

	/**
	 * Get all classes from a particular model
	 * @return Collection of org.omg.uml.foundation.core.UmlClass
	 */
	public Collection getUMLClasses(String modelName) {
		return new FilteredCollection(getUMLModelElements(modelName)) {
		   protected boolean accept(Object object) {
			   return (object instanceof org.omg.uml.foundation.core.UmlClass);
		   }
	   };
	}

	/**
	 * Transforms/maps UML to MOF
	 */
	public void transformUmlToMof(String model) {
		Transformer.execute((UmlPackage) getModel(model), metaModelExtent);		
	}

	
	/**
	 * Generate JMI Interfaces
	 * @param myPackage
	 * @param outputDirName
	 */
	public void generate( RefPackage myPackage, String outputDirName ){ //TODO: should be a file       
		
		File outDir = new File(outputDirName);
		JMIMapper mapper = JMIMapper.getDefault();
		SimpleJMIStreamFactory streamFactory = new SimpleJMIStreamFactory( outDir);
		JavaMapper jm = new JavaMapper(streamFactory,"header");

		try {
			mapper.generate( streamFactory, myPackage );
			log( "JMI interfaces succesfully generated to " + outDir.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("IOException:" + e);
			log("Couldnt generated JMI interfaces:" + e);
		}     
		
	}
	
	/**
	 * Write a model to a XMI file
	 * @param extent - the model
	 * @param f - the file to write to
	 */
	public void persistModel(RefPackage extent, File f) {
		org.netbeans.api.xmi.XMIWriter writer = XMIWriterFactory.getDefault().createXMIWriter();
		String name="";
		Iterator iter=null;

		
		FileOutputStream os;
		try {
			os = new FileOutputStream(f);
			writer.write(os, extent, "1.4");
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException:" + e);
		} catch (IOException e) {
			System.err.println("IOException:" + e);
		}	
	}

	
	/**
	 * Get a ref to a previously loaded model
	 * @param extentNameKey - the name of the model
	 * @return a RefPackage - the model
	 */
	public RefPackage getModel(String extentNameKey) {
		return (RefPackage) cashedRefPackages.get(extentNameKey);
	}
		
	/**
	   * Create a new metadata repository - it is cashed for future reference
	   * @param name the repository name
	   */
	protected MDRepository createRepository(String name) {
		String storageFactoryClassName =
			System.getProperty(STORAGE_FACTORY_PROPERTY, STORAGE_FACTORY_DEFAULT);
		Map params = new HashMap();
		params.put("storage", storageFactoryClassName);
		params.put("fileName", name);
		return new NBMDRepositoryImpl(params);
	}
	
	/**
	 * @return the default main repository
	 * @see org.netbeans.api.mdr.MDRManager#getDefaultRepository()
	 */
	public MDRepository getDefaultRepository() {
		return this.repository;
	}
	
	/** 
	 * @return Either a cashed previously created repository or a new one
	 * @see org.netbeans.api.mdr.MDRManager#getRepository(java.lang.String)
	 */
	public MDRepository getRepository(String name) {
		MDRepository rep = (MDRepository) repositories.get(name);
		if (repository != null)
			return repository;
		rep = createRepository(name);
		repositories.put(name, rep);
		return rep;
	}
	
	/**
	 * Usually we dont load multible repositories so we just return the default main one
	 */
	public MDRepository getMDRepository() {
		if (repository == null) {
			//repository = MDRManager.getDefault().getDefaultRepository();
			repository = createRepository(DEFAULT_REPOSITORY_NAME);
			log("Created main model repository:" + DEFAULT_REPOSITORY_NAME);
		}
		return repository;
	}
	
	/**
	 * @return String[] of model extents
	 * @see super.getRepositoryNames()
	 */
	public String[] getRepositoryNames() {
		return getRepositoryNames();
	}
	
	/**
	 * Logs to logger and Main status
	 * @param object - a String
	 */
	private void log(Object object) {
		if (object instanceof String) {
			Main.getInstance().setStatus((String) object);
			logger.log((String) object);
		} else
			logger.log(object.toString());
	}
	}
