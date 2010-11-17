/**
 * Social Network Analysis and Visualization Library
 * for RepastJ Models (SoNetA)
 * 
 * [see license.txt in the root directory of this library
 *  for additional important notes]
 *
 * @author Sascha Holzhauer
 * @date 24.05.2008
 * 
 */
package de.cesr.more.measures.util;

import java.util.HashMap;

import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.networks.MoreNetwork;


/**
 * Extension for <code>NetworkChangeNode</code>s to store network measures.
 * 
 * @author Sascha Holzhauer
 * @date 24.05.2008
 */
public class MNodeMeasures {
	
	protected HashMap<MoreNetwork<? extends MoreNodeMeasureSupport, ?>,HashMap<MMeasureDescription, Object>> objectMeasures;
	protected Double temp;
	
	
	/**
	 * Instantiates an new <code>HashMap</code>
	 */
	public MNodeMeasures() {
		objectMeasures = new HashMap<MoreNetwork<? extends MoreNodeMeasureSupport, ?>, 
			HashMap<MMeasureDescription, Object>>();
	}
	
	/**
	 * Stores the measure value of the given measure key for the give network in a map 
	 * @date 10.07.2008
	 *
	 * @param network The network that measure is calculated for
	 * @param key The Key of the measure
	 * @param value the value to set
	 */
	public void setNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network, 
			MMeasureDescription key, Object value) {
		if (!objectMeasures.containsKey(network)) {
			objectMeasures.put(network, new HashMap<MMeasureDescription, Object>());
		}
		objectMeasures.get(network).put(key, value);
	}
	
	/**
	 * Returns the stored value belonging to the given measure key for the given network
	 * @date 10.07.2008
	 *
	 * @param network The network for which the returned measure value shall be calculated for
	 * @param key The key of the measure
	 * @return the value of that measure for the given network
	 */
	public Object getNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network, 
			MMeasureDescription key) {
		if (objectMeasures.containsKey(network)) {		
			return objectMeasures.get(network).get(key);
		}
		else {
			return null;
		}
	}
}
