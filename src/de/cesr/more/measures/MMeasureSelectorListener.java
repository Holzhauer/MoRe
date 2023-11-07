/**
 * Social Network Analysis and Visualization Library
 * for RepastJ Models (SoNetA)
 * 
 * [see license.txt in the root directory of this library
 *  for additional important notes]
 *
 * @author Sascha Holzhauer
 * @date 23.06.2008
 * 
 */
package de.cesr.more.measures;



import java.util.Set;

import de.cesr.more.basic.network.MoreNetwork;



/**
 * This interface needs to be implemented by objects the <code>MeasureChooser</code> interacts with.
 * 
 * @author Sascha Holzhauer
 * @date 23.06.2008
 * 
 */
public interface MMeasureSelectorListener {

	/**
	 * This method is invoked every time the user selects a measure at the <code>MeasureChooser</code>
	 * 
	 * @date 10.07.2008
	 * 
	 * @param bundle the measure bundle that was selected
	 * @param remove true if the selected measure shall be removed from this collection
	 * 
	 * @return true, if the measure could be set (and has not been existing before) or removed (and has been existing)
	 */
	public boolean setMeasureBundle(MMeasureBundle bundle, boolean remove);

	/**
	 * The <code>MeasureChooser</code> calls this method in order to fetch measures that might be removed by the user
	 * for a (user-)specified network.
	 * 
	 * @date 10.07.2008
	 * 
	 * @param network the network for which removable measure shall be fetched
	 * @return a set of all removable measures
	 */
	public Set<MMeasureDescription> getRemovableMeasures(MoreNetwork<?, ?> network);

	/**
	 * The <code>MeasureChooser</code> calls this method in order to fetch measures that might be added by the user for
	 * a (user-)specified network. Only returns measures that have not been added before for the given network.
	 * 
	 * @date 10.07.2008
	 * 
	 * @param network the network for which measure to add shall be fetched
	 * @return a set of all measures that may be added
	 */
	public Set<MMeasureDescription> getAddableMeasures(MoreNetwork<?, ?> network);
}
