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
 * Created by Sascha Holzhauer on 16.11.2010
 */
package de.cesr.more.util;

import java.util.HashMap;
import java.util.Map;

import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.networks.MoreNetwork;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 16.11.2010 
 *
 */
public class MNetworkMeasureStorage {
	
	private Map<MoreNetwork<?, ?>, Map<MMeasureDescription, Object>> measures;
	
	public MNetworkMeasureStorage() {
		measures = new HashMap<MoreNetwork<?, ?>, Map<MMeasureDescription, Object>>();
	}
	
	public void put(MoreNetwork<?, ?> network, MMeasureDescription desc, Object value) {
		if (!measures.containsKey(network)) {
			measures.put(network, new HashMap<MMeasureDescription, Object>());
		}
		measures.get(network).put(desc, value);
	}
	
	public Object get(MoreNetwork<?, ?> network, MMeasureDescription desc) {
		if (!measures.containsKey(network)) {
			throw new IllegalStateException("There is no measure for network " + network.getName());
		}
		if (!measures.get(network).containsKey(desc)) {
			throw new IllegalStateException("There is no value for measure " + desc.toString() + " for network " + network.getName());
		}
		return measures.get(network).get(desc);
	}

}
