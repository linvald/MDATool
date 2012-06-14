package mdatool.mof.common;

import java.util.*;

/**
	 *  Filters a collection of objects so that the collection
	 * contains only those objects that pass the 'accept' test.
	 * 
	 * <p>It is useful for filtering the results of a query.</p>
	 */

	public abstract class FilteredCollection extends ArrayList {
		/**
		 *  Constructor for the FilterCollection object
		 *
		 *@param  c  Description of the Parameter
		 */
		public FilteredCollection(Collection c) {
			for (Iterator i = c.iterator(); i.hasNext();) {
				Object object = i.next();
				if (accept(object)) {
					add(object);
				}
			}
		}

		/**
		 *  Description of the Method
		 *
		 *@param  object  Description of the Parameter
		 *@return         Description of the Return Value
		 */
		protected abstract boolean accept(Object object);
	}
