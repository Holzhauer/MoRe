/**
 * This file is part of
 * 
 * MORe - Managing Ongoing Relationships
 *
 * Copyright (C) 2010 Center for Environmental Systems Research, Kassel, Germany
 * 
 * MORe - Managing Ongoing Relationships is free software: You can redistribute 
 * it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *  
 * MORe - Managing Ongoing Relationships is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Center for Environmental Systems Research, Kassel
 * 
 * Created by Sascha Holzhauer on 02.05.2011
 */
package de.cesr.more.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.networks.MoreNetwork;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 02.05.2011 
 *
 */
public class MNetworkClusterMeasureStorage {
	private Map<String, Map<MMeasureDescription, Number>> measures;
	
	public MNetworkClusterMeasureStorage() {
		measures = new HashMap<String, Map<MMeasureDescription, Number>>();
	}
	
	public void put(String network, MMeasureDescription desc, Number value) {
		if (!measures.containsKey(network)) {
			measures.put(network, new HashMap<MMeasureDescription, Number>());
		}
		measures.get(network).put(desc, value);
	}
	
	public Number get(String network, MMeasureDescription desc) {
		if (!measures.containsKey(network)) {
			throw new IllegalStateException("There is no measure for network " + network);
		}
		if (!measures.get(network).containsKey(desc)) {
			throw new IllegalStateException("There is no value for measure " + desc.toString() + " for network " + network);
		}
		return measures.get(network).get(desc);
	}
	
	public Set<String> getNetworkNames() {
		return measures.keySet();
	}
	
	/**
	 * Returns an unmodifiable map of all measures of the given network.
	 * 
	 * @param network
	 * @return
	 * Created by Sascha Holzhauer on Jan 3, 2011
	 */
	public Map<MMeasureDescription, Number> getAllMeasures(String network) {
		if (! measures.containsKey(network)) {
			throw new IllegalStateException("No measures defined for the given network (" + network + ")");
		}
		return Collections.unmodifiableMap(measures.get(network));
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return measures.toString();
	}
}
