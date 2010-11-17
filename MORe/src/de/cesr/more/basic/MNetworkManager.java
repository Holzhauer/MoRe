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
package de.cesr.more.basic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Predicate;

import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.util.MNetworkMeasureStorage;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 16.11.2010 
 *
 */
public class MNetworkManager {
	
	static Map<String, MoreNetwork<?,?>> networks;
	static MNetworkMeasureStorage measureStorage;
	
	static {
		networks = new HashMap<String, MoreNetwork<?,?>>();
		measureStorage = new MNetworkMeasureStorage();
	}
	
	public static MoreNetwork<?,?> getNetwork(String name) {
		return networks.get(name);
	}
	
	/**
	 * TODO test!
	 * 
	 * @param <V>
	 * @param <E>
	 * @param in_network
	 * @param predicate
	 * @return
	 * Created by Sascha Holzhauer on 16.11.2010
	 */
	public static <V, E> MoreNetwork<V,E> storeVertexSubnetwork(MoreNetwork<V, E> in_network, Predicate<V> predicate, String newname) {
		VertexPredicateFilter<V, E> filter = new VertexPredicateFilter(predicate);
		MoreNetwork<V, E> out_network = in_network.getInstanceWithNewGraph(filter.transform(in_network.getGraph()));
		networks.put(newname, out_network);
		return out_network;
	}
	
	public static <V, E> Object getNetworkMeasure(MoreNetwork<V, E> network, MMeasureDescription desc) {
		return measureStorage.get(network, desc);
	}

	public static <V, E> void setNetworkMeasure(MoreNetwork<V, E> network, MMeasureDescription desc, Object value) {
		measureStorage.put(network, desc, value);
	}
}
