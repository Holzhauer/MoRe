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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections15.Predicate;
import org.apache.log4j.Logger;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.util.Log4jLogger;
import de.cesr.more.util.MDbNetworkDataWriter;
import de.cesr.more.util.MNetworkClusterMeasureStorage;
import de.cesr.more.util.MNetworkMeasureStorage;
import de.cesr.more.util.MoreRunIdProvider;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;

/**
 * Manages networks and network measures
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
	static MNetworkClusterMeasureStorage measureClusterStorage;
	
	static {
		networks = new HashMap<String, MoreNetwork<?,?>>();
		measureStorage = new MNetworkMeasureStorage();
		measureClusterStorage = new MNetworkClusterMeasureStorage();
	}
	/**
	 * Add a new {@link MoreNetwork} to the network manager
	 * @param network the network to add
	 * @param name the network's identifier
	 */
	public static void setNetwork(MoreNetwork<?, ?> network, String name) {
		networks.put(name, network);
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Networks:\n" + networks);
		}
		// LOGGING ->

	}
	
	/**
	 * @param name
	 *        network's identifier
	 * @return true if a network with the given name is registed.
	 */
	public static boolean isNetworkRegistered(String name) {
		return networks.containsKey(name);
	}

	/**
	 * @param name name of requested network
	 * @return
	 */
	public static MoreNetwork<?,?> getNetwork(String name) {
		if (networks.get(name) == null) {
			throw new IllegalArgumentException("The network \"" + name + "\" is not registered at the MNetworkManager!");
		}
		return networks.get(name);
	}

	/**
	 * Creates a new MoreNetwork from the given network that contains only the vertices the given predicate renders
	 * true. The resulting network (the given network is not altered) is stored at this network manager using the given
	 * name.
	 * 
	 * @param <V>
	 *        vertex type
	 * @param <E>
	 *        edge type
	 * @param in_network
	 *        original network
	 * @param predicate
	 *        needs to return true for vertices the resulting network shall contain
	 * @param newname
	 *        an identifier for the new subnetwork (used to store and as network name)
	 * @return the resulting network
	 */
	public static <V, E extends MoreEdge<? super V>> MoreNetwork<V,E> storeVertexSubnetwork(MoreNetwork<V, E> in_network, Predicate<V> predicate, String newname) {		
		VertexPredicateFilter<V, E> filter = new VertexPredicateFilter<V, E>(predicate);
		
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
	
	public static void writeNetworkMeasuresToDb(String network, String externalVersion, MoreRunIdProvider prov, int tick) {
		writeNetworkMeasuresToDb(network, externalVersion, prov, tick, true);
	}

	public static void writeNetworkMeasuresToDb(String network, String externalVersion, MoreRunIdProvider prov,
			int tick, boolean closeConnection) {
		MDbNetworkDataWriter dbWriter = new MDbNetworkDataWriter(network, externalVersion, prov);
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("MeasureStorage: \n" + measureStorage);
		}
		// LOGGING ->

		for (Entry<MMeasureDescription, Number> e : measureStorage.getAllMeasures(getNetwork(network)).entrySet()) {
			dbWriter.addValue(e.getKey().getShort(), e.getValue().toString());
		}
		dbWriter.addValue("tick", "" + tick );
		dbWriter.writeData(closeConnection);
	}

	public static void writeNetworkClusterMeasuresToDb(String network, String externalVersion, MoreRunIdProvider prov, int tick) {
		MDbNetworkDataWriter dbWriter = new MDbNetworkDataWriter(network, externalVersion, prov);
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("MeasureClusterStorage: \n" + measureClusterStorage);
		}
		// LOGGING ->

		for (Entry<MMeasureDescription, Number> e : measureClusterStorage.getAllMeasures(network).entrySet()) {
			dbWriter.addValue(e.getKey().getShort(), e.getValue().toString());
		}
		dbWriter.addValue("tick", "" + tick );
		dbWriter.writeData();
	}
	
	/**
	 * @param <V>
	 * @param <E>
	 * @param in_network
	 * @param predicate
	 * @param newname
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <V, E extends MoreEdge<? super V>> MoreNetwork<V,E> storeVertexSubnetwork(String in_network, Predicate<V> predicate, String newname) {
		if (!networks.containsKey(in_network)) {
			throw new IllegalArgumentException("The network \"" + in_network + "\" is not registered at the MNetworkManager!");
		}
		return storeVertexSubnetwork((MoreNetwork<V,E>)getNetwork(in_network), predicate, newname);
	}
	
	public static <V, E extends MoreEdge<? super V>> Number getNetworkMeasure(MoreNetwork<V, E> network, MMeasureDescription desc) {
		return measureStorage.get(network, desc);
	}
	
	public static <V, E extends MoreEdge<? super V>> List<Number> getNetworkMeasure(String network, MMeasureDescription desc) {
		List<Number> numbers = new ArrayList<Number>();
		for (String name : measureClusterStorage.getNetworkNames()) {
			if (name.startsWith(network)) {
				numbers.add(measureClusterStorage.get(network, desc));
			}
		}
		return numbers;
	}

	public static <V, E extends MoreEdge<? super V>> void setNetworkMeasure(MoreNetwork<V, E> network, MMeasureDescription desc, Number value) {
		measureStorage.put(network, desc, value);
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Content of Measure Storage:\n" + measureStorage.toString());
		}
		// LOGGING ->

	}
	
	public static <V, E extends MoreEdge<? super V>> void setNetworkClusterMeasure(String network, MMeasureDescription desc, Number value) {
		measureClusterStorage.put(network, desc, value);
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Content of Cluster Measure Storage:\n" + measureClusterStorage.toString());
		}
		// LOGGING ->
	}
	
	public static void reset() {
		networks = new HashMap<String, MoreNetwork<?,?>>();
		measureStorage = new MNetworkMeasureStorage();
		measureClusterStorage = new MNetworkClusterMeasureStorage();
	}
}