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
package de.cesr.more.rs.building;


import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import repast.simphony.space.gis.Geography;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.AgentLabelFactory;
import de.cesr.more.geo.building.edge.MDefaultGeoEdgeFactory;
import de.cesr.more.geo.building.network.MoreGeoNetworkService;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.more.util.io.MGraphMLReader2NodeMap;
import de.cesr.more.util.io.MoreIoUtilities;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import edu.uci.ics.jung.graph.Graph;


/**
 * MORe
 * 
 * First, a new {@link MRsContextJungNetwork} is initialised according to {@link MNetworkBuildingPa#BUILD_DIRECTED}.
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
 * 
 * @param <AgentType>
 * @param <EdgeType>
 * 
 * @author Sascha Holzhauer
 * @date 29.11.2011
 * 
 */
public class MGeoRsRestoreNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType>>
		extends MGeoRsNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MGeoRsRestoreNetworkBuilder.class);

	protected String		name;
	
	protected MoreGeoNetworkService<AgentType, EdgeType>	maintainingNetworkService	= null;

	protected AgentLabelFactory<AgentType>					agentLabelFactory			= null;

	/**
	 * @param edgeFac
	 */
	@SuppressWarnings("unchecked")
	public MGeoRsRestoreNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this((Geography<Object>) PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac);
	}

	/**
	 * @param edgeFac
	 * @param networkName
	 */
	@SuppressWarnings("unchecked")
	public MGeoRsRestoreNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, String networkName) {
		this((Geography<Object>) PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac,
				PmParameterManager.getInstance(null), networkName);
	}

	/**
	 * @param geography
	 * @param edgeFac
	 */
	public MGeoRsRestoreNetworkService(Geography<Object> geography, MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this(geography, edgeFac, PmParameterManager.getInstance(null));
	}

		/**
	 * @param edgeFac
	 * @param pm
	 */
	@SuppressWarnings("unchecked")
	public MGeoRsRestoreNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, PmParameterManager pm) {
		this((Geography<Object>) PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac, pm);
	}

	/**
	 * @param geography
	 * @param edgeFac
	 * @param pm
	 */
	public MGeoRsRestoreNetworkService(Geography<Object> geography, MoreEdgeFactory<AgentType, EdgeType> edgeFac,
			PmParameterManager pm) {
		this(geography, edgeFac, pm, "RestoredNetwork");
	}

	/**
	 * @param geography
	 * @param eFac
	 * @param networkName
	 * @param pm
	 */
	@SuppressWarnings("unchecked")
	public MGeoRsRestoreNetworkService(Geography<Object> geography, MoreEdgeFactory<AgentType, EdgeType> eFac,
			PmParameterManager pm, String networkName) {
		super(geography, eFac, pm);
		this.name = networkName;

		this.agentLabelFactory = new AgentLabelFactory<AgentType>(){
			@Override
			public String getLabel(AgentType agent) {
				return Integer.parseInt(agent.getAgentId()) + "";
			}};
			
		if (pm.getParam(MNetworkBuildingPa.MAINTAINING_NETWORK_SERVICE) != null) {
			Class<?> serviceClass = (Class<?>) pm.getParam(MNetworkBuildingPa.MAINTAINING_NETWORK_SERVICE);
			if (MoreGeoNetworkService.class.isAssignableFrom(serviceClass)) {
				try {
					this.maintainingNetworkService = (MoreGeoNetworkService<AgentType, EdgeType>) (serviceClass)
							.getConstructor(
									MoreEdgeFactory.class, String.class, PmParameterManager.class)
							.newInstance(new MDefaultGeoEdgeFactory<AgentType>(), this.name, this.pm);

					this.maintainingNetworkService.setGeography(this.geography);
					this.maintainingNetworkService.setGeoRequestClass(this.geoRequestClass);

				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
			// TODO error handling
		}
	}

	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(Collection<AgentType> agents) {

		if (context == null) {
			logger.error("Context not set!");
			throw new IllegalStateException("Context not set!");
		}

		if (this.geography == null) {
			logger.error("Geogrpahy not set!");
			throw new IllegalStateException("Geogrpahy not set!");
		}

		checkAgentCollection(agents);

		MRsContextJungNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType>(
				((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ?
						new DirectedJungNetwork<AgentType>(name) :
						new UndirectedJungNetwork<AgentType>(name), context, this.edgeModifier.getEdgeFactory());

		// put agents into map indexed by their indices:
		BidiMap<AgentType, String> agentIdMap = new DualHashBidiMap<AgentType, String>();

		for (AgentType agent : agents) {
			agentIdMap.put(agent, this.agentLabelFactory.getLabel(agent));
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
						PmParameterManager.getParameter(MNetworkBuildingPa.RESTORE_NETWORK_SOURCE_FILE));
			}
			// LOGGING ->

			graphReader.load(
					((String) PmParameterManager.getParameter(MNetworkBuildingPa.RESTORE_NETWORK_SOURCE_FILE)),
					network.getJungGraph());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return network;
	}

	/**
	 * @param agentLabelFactory
	 */
	public void setAgentLabelFactory(AgentLabelFactory<AgentType> agentLabelFactory) {
		this.agentLabelFactory = agentLabelFactory;
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
		return "MGeoRsRestoreNetworkService";
	}

	/**
	 * @see de.cesr.more.manipulate.network.MoreNetworkModifier#addAndLinkNode(de.cesr.more.basic.network.MoreNetwork,
	 *      java.lang.Object)
	 */
	@Override
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network, AgentType node) {
		return this.maintainingNetworkService.addAndLinkNode(network, node);
	}
}