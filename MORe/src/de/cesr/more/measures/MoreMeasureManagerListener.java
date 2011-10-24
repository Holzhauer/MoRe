/**
 * 
 */
package de.cesr.more.measures;

import de.cesr.more.basic.network.MoreNetwork;


/**
 * Intended for classes that need to be informed of network measure calculation changes, i.e. the measure manager
 * @author holzhauer
 * 
 * TODO parameterize!
 * 
 */
public interface MoreMeasureManagerListener {
	
	/**
	 * Invoked every time a network measure calculation routine was added
	 * @param network the network the measure is associated with
	 * @param measure the measure that was added
	 * Created by Sascha Holzhauer on 09.04.2010
	 */
	public void networkMeasureCalcAdded(MoreNetwork network,
			MMeasureDescription measure);

	/**
	 * Invoked every time a network measure calculation routine was removed
	 * @param network the network the measure is associated with
	 * @param measure the measure that was removed
	 * Created by Sascha Holzhauer on 09.04.2010
	 */
	public void networkMeasureCalcRemoved(MoreNetwork network,
			MMeasureDescription measure);
}
