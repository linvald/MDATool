package mdatool.gui.configuration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class ProjectProperties {

	private static final String BUNDLE_NAME = "mdatool.gui.configuration.configuration"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 * 
	 */
	private ProjectProperties() {}
	/**
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
