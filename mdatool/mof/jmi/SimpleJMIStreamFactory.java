package mdatool.mof.jmi;

import java.io.*;
import java.util.*;

import javax.jmi.model.*;
import javax.jmi.reflect.*;
import org.netbeans.api.mdr.JMIMapper;
import org.netbeans.api.mdr.JMIStreamFactory;
import org.netbeans.api.mdr.MDRManager;
import org.netbeans.api.mdr.MDRepository;


	/**
	 * Simple implementation of JMIStreamFactory over java.io
	 */
   public class SimpleJMIStreamFactory extends JMIStreamFactory {
		File root;
        
		public SimpleJMIStreamFactory( File root ) {
			super();
			this.root = root;
		}

		/**
		 * Create a stream for output of a generated JMI class
		 * @param pkg list of package names
		 * @param className name of generated class
		 */
		public OutputStream createStream( List pkg, String className,String extension ) throws IOException{
			System.out.println("SimpleJMIStreamFactory.createStream()");
			File outFile = root;
			for (Iterator i = pkg.iterator(); i.hasNext();) {
				String subDir = (String) i.next();
				outFile = new File( outFile, subDir );
			}
			outFile = new File( outFile, className + ".java" );
			outFile.getParentFile().mkdirs();
			return new PrintStream( new FileOutputStream( outFile ));
		}

	/**
 * Create a stream for output of a generated JMI class
 * @param pkg list of package names
 * @param className name of generated class
 */
public OutputStream createStream( List pkg, String className ) throws IOException{
	System.out.println("SimpleJMIStreamFactory.createStream()");
	File outFile = root;
	for (Iterator i = pkg.iterator(); i.hasNext();) {
		String subDir = (String) i.next();
		outFile = new File( outFile, subDir );
	}
	outFile = new File( outFile, className + ".java" );
	outFile.getParentFile().mkdirs();
	return new PrintStream( new FileOutputStream( outFile ));
}

        
	}