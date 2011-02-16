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
import java.util.Map.Entry;

import org.apache.commons.collections15.Predicate;
import org.apache.log4j.Logger;

import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.util.Log4jLogger;
import de.cesr.more.util.MDbWriter;
import de.cesr.more.util.MNetworkMeasureStorage;
import de.cesr.more.util.MoreRunIdProvider;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;
import edu.uci.ics.jung.graph.Graph;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 16.11.2010 
 *
 */
public class MNetworkManager {
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MNetworkManager.class);
	
	static Map<String, MoreNetwork<?,?>> networks;
	static MNetworkMeasureStorage measureStorage;
	
	static {
		networks = new HashMap<String, MoreNetwork<?,?>>();
		measureStorage = new MNetworkMeasureStorage();
	}
	/**
	 * Add a new {@link MoreNetwork} to the network manager
	 * @param network the network to add
	 * @param name the network's identifier
	 * 
	 * Created by Sascha Holzhauer on 03.01.2011
	 */
	public static void setNetwork(MoreNetwork<?, ?> network, String name) {
		networks.put(name, network);
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Networks:\n" + networks);
		}
		// LOGGING ->

	}
	
	public static MoreNetwork<?,?> getNetwork(String name) {
		if (networks.get(name) == null) {
			throw new IllegalArgumentException("The network \"" + name + "\" is not registered at the MNetworkManager!");
		}
		return networks.get(name);
	}
	
	/**
	 * TODO test!
	 * 
	 * @param <V>
	 * @param <E>
	 * @param in_network
	 * @param predicate
	 * @param newname an identifier for the new subnetwork
	 * @return
	 * Created by Sascha Holzhauer on 16.11.2010
	 */
	public static <V, E> MoreNetwork<V,E> storeVertexSubnetwork(MoreNetwork<V, E> in_network, Predicate<V> predicate, String newname) {		
		VertexPredicateFilter<V, E> filter = new VertexPredicateFilter(predicate);
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("In.network: " + in_network);
		}
		// LOGGING ->

		MoreNetwork<V, E> out_network = in_network.getGraphFilteredInstance(filter.transform(in_network.getJungGraph()), newname);
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Put as " + newname + ": " + out_network);
		}
		// LOGGING ->

		networks.put(newname, out_network);
		return out_network;
	}
	
	public static void writeNetworkMeasuresToDb(String network, String externalVersion, int paramID, MoreRunIdProvider prov) {
		MDbWriter dbWriter = new MDbWriter(getNetwork(network), externalVersion, paramID, prov);
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("MeasureStorage: \n" + measureStorage);
		}
		// LOGGING ->

		for (Entry<MMeasureDescription, Number> e : measureStorage.getAllMeasures(getNetwork(network)).entrySet()) {
			dbWriter.addValue(e.getKey().getShort(), e.getValue().toString());
		}
		dbWriter.writeData();
	}
	
	/**
	 * @param <V>
	 * @param <E>
	 * @param in_network
	 * @param predicate
	 * @param newname
	 * @return
	 * Created by Sascha Holzhauer on 03.01.2011
	 */
	public static <V, E> MoreNetwork<V,E> storeVertexSubnetwork(String in_network, Predicate<V> predicate, String newname) {
		if (!networks.containsKey(in_network)) {
			throw new IllegalArgumentException("The network \"" + in_network + "\" is not registered at the MNetworkManager!");
		}
		return storeVertexSubnetwork((MoreNetwork<V,E>)getNetwork(in_network), predicate, newname);
	}
	
	public static <V, E> Number getNetworkMeasure(MoreNetwork<V, E> network, MMeasureDescription desc) {
		return measureStorage.get(network, desc);
	}

	public static <V, E> void setNetworkMeasure(MoreNetwork<V, E> network, MMeasureDescription desc, Number value) {
		measureStorage.put(network, desc, value);
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Content of Measure Storage:\n" + measureStorage.toString());
		}
		// LOGGING ->

	}
}
