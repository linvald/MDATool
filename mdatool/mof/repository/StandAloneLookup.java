package mdatool.mof.repository;

import java.util.*;
import org.netbeans.lib.jmi.mapping.JMIMapperImpl;
import org.netbeans.lib.jmi.xmi.*;
import org.netbeans.mdr.NBMDRManagerImpl;
import org.openide.util.*;

/**
 * A simple implementation of Lookup (service manager) for running outside
 * of NetBeans.
 *
 * @author Mike Williams
 * @version $Id: StandAloneLookup.java,v 1.1 2003/12/07 17:24:24 linvald Exp $
 */
public class StandAloneLookup extends Lookup {
    
	private ArrayList instances = new ArrayList();

	
	public StandAloneLookup() {
		
		System.out.println("StandAloneLookup.StandAloneLookup()");
		instances.add(new NBMDRManagerImpl());
		instances.add(new XMISaxReaderImpl());
		instances.add(new JMIMapperImpl());
		instances.add(new XMIWriterImpl());
	}

	public Object lookup(Class cls) {
		for (Iterator it = instances.iterator(); it.hasNext();) {
			Object instance = it.next();
			if (cls.isAssignableFrom(instance.getClass())) {
				return instance;
			}
		}
		return null;
	}
    
	public Lookup.Result lookup(Lookup.Template template) {
		System.out.println("StandAloneLookup.lookup()");
		return new Result(lookup(template.getType()));
	}
    
	private static class Result extends Lookup.Result {

		private final ArrayList result = new ArrayList();
        
		public Result(Object o) {
			if (o != null) result.add(o);
		}
        
		public java.util.Collection allInstances() {
			return result;
		}
        
		public void addLookupListener(LookupListener l) {
		}
        
		public void removeLookupListener(LookupListener l) {
		}

	}
    
}
