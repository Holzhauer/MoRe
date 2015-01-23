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
 * Created by Sascha Holzhauer on 29.11.2011
 */
package de.cesr.more.building.network;


import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MUndirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.util.MNetworkBuilderRegistry;
import de.cesr.more.util.io.MGraphMLReader2NodeMap;
import de.cesr.more.util.io.MoreIoUtilities;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import edu.uci.ics.jung.graph.Graph;


/**
 * MORe
 * 
 * First, a new {@link MoreNetwork} is initialised according to {@link MNetworkBuildingPa#BUILD_DIRECTED}.
 * 
 * Using the passed agent collection this network generator assigns each agent to a node defined in the GRAPHML file
 * specified in {@link MNetworkBuildingPa#RESTORE_NETWORK_SOURCE_FILE} according to the agent id. Then, links are
 * created as defined in the GRAPHML file.
 * 
 * Typically, the GRAPHML file is also produced by MoRe using
 * {@link MoreIoUtilities#outputGraph(de.cesr.more.basic.network.MoreNetwork, java.io.File)} .
 * 
 * NOTE: Using this generator requires the agent collection to contain at least the nodes defined in the GRAPHML file.
 * Otherwise, an {@link IllegalStateException} is thrown.
 * 
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link de.cesr.more.param.MNetworkBuildingPa#BUILD_DIRECTED}</li>
 * <li>{@link de.cesr.more.param.MNetworkBuildingPa#RESTORE_NETWORK_SOURCE_FILE}</li>
 * <li>...</li>
 * </ul>
 * 
 * @author Sascha Holzhauer
 * @date 26.03.2014
 * 
 */
public class MRestoreNetworkBuilder<AgentType, EdgeType extends MoreEdge<AgentType>> implements
		MoreNetworkBuilder<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MRestoreNetworkBuilder.class);

	protected String		name;
	
	protected MoreEdgeFactory<AgentType, EdgeType>	edgeFac;

	protected PmParameterManager					pm;
	
	/**
	 * @param areasGeography
	 */
	public MRestoreNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String networkName) {
		this(eFac, networkName, PmParameterManager.getInstance(null));
	}

	/**
	 * @param areasGeography
	 */
	public MRestoreNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String networkName, PmParameterManager pm) {
		this.edgeFac = eFac;
		this.name = networkName;
		this.pm = pm;
	}

	@Override
	public MoreNetwork<AgentType, EdgeType> buildNetwork(Collection<AgentType> agents) {

		checkAgentCollection(agents);

		MoreNetwork<AgentType, EdgeType> network = ((Boolean) pm.getParam(MNetworkBuildingPa.BUILD_DIRECTED)) ?
				new MDirectedNetwork<AgentType, EdgeType>(edgeFac, name) : new MUndirectedNetwork<AgentType, EdgeType>(
						edgeFac, name);

		// put agents into map indexed by their indices:
		BidiMap<AgentType, String> agentIdMap = new DualHashBidiMap<AgentType, String>();

		for (AgentType agent : agents) {
			agentIdMap.put(agent, agent.toString());
			network.addNode(agent);
		}

		// <- LOGGING
		logger.info("Add agents");
		// LOGGING ->

		MGraphMLReader2NodeMap<Graph<AgentType, EdgeType>, AgentType, EdgeType> graphReader;
		try {
			graphReader = new MGraphMLReader2NodeMap<Graph<AgentType, EdgeType>, AgentType, EdgeType>(
						edgeFac, agentIdMap);
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Load network from file " + (String)
						pm.getParam(MNetworkBuildingPa.RESTORE_NETWORK_SOURCE_FILE));
			}
			// LOGGING ->

			graphReader.load(
					((String) pm.getParam(MNetworkBuildingPa.RESTORE_NETWORK_SOURCE_FILE)),
					network.getJungGraph());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		MNetworkBuilderRegistry.registerNetworkBuiler(network, this);

		return network;
	}

	protected void checkAgentCollection(Collection<AgentType> agents) {
		// check agent collection:
		if (!(agents instanceof Set)) {
			Set<AgentType> set = new HashSet<AgentType>();
			set.addAll(agents);
			if (set.size() != agents.size()) {
				logger.error("Agent collection contains duplicate entries of at least one agent " +
							"(Set site: " + set.size() + "; collection size: " + agents.size());
				throw new IllegalStateException("Agent collection contains duplicate entries of at least one agent " +
							"(Set site: " + set.size() + "; collection size: " + agents.size());
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MRestoreNetworkBuilder_" + name;
	}
}