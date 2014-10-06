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
 * Created by Sascha Holzhauer on 14.05.2012
 */
package de.cesr.more.rs.building;

import java.util.Collection;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MNetworkService;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 14.05.2012 
 *
 */
public class MRsCompleteNetworkBuilder<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType>>
	extends MNetworkService<AgentType, EdgeType> implements MoreRsNetworkBuilder<AgentType, EdgeType> {
	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MGeoRsCompleteNetworkService.class);
	
	String name;
	
	/**
	 * The context the network belongs to.
	 */
	protected Context<AgentType>						   context;
	
	/**
	 * Uses "Network" as name.
	 * @param eFac
	 */
	public MRsCompleteNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		this(eFac, "Network");
	}
	
	/**
	 * @param eFac
	 */
	public MRsCompleteNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		super(eFac);
		this.name = name;
	}
	
	/**
	 * @see de.cesr.more.building.network.MoreNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(
			Collection<AgentType> agents) {
		if (context == null) {
			// <- LOGGING
			logger.error("The context has not been set!");
			// LOGGING ->
			throw new IllegalStateException("The context has not bee set!");
		}
		
		checkAgentCollection(agents);

		MRsContextJungNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType >(
				((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ?
						new DirectedJungNetwork<AgentType>(name) :
						new UndirectedJungNetwork<AgentType>(name), context, this.edgeModifier.getEdgeFactory());

		for (AgentType agent : agents) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Add agent " + agent + " to network.");
			}
			// LOGGING ->

			network.addNode(agent);
			context.add(agent);
			
			// connect this agent with every already added other (undirected):
			for (AgentType other : network.getNodes()) {
				if (other != agent) {
					createEdge(network, agent, other);

					if ((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) {
						createEdge(network, other, agent);
					}

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(agent + "> connect to (and from if directed): " + other);
					}
					// LOGGING ->
				}
			}
		}
		return network;
	}


	/**
	 * @see de.cesr.more.manipulate.network.MoreNetworkModifier#addAndLinkNode(de.cesr.more.basic.network.MoreNetwork, java.lang.Object)
	 */
	@Override
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network,
			AgentType node) {
		network.addNode(node);
		context.add(node);
		
		// connect this agent with every already added other (undirected):
		for (AgentType other : network.getNodes()) {
			if (other != node) {
				createEdge(network, node, other);

				if ((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) {
					createEdge(network, other, node);
				}

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(node + "> connect to (and from if directed): " + other);
				}
				// LOGGING ->
			}
		}
		return true;
	}
	
	/**
	 * Set the (root) context the network shall span
	 * 
	 * @param context
	 */
	@Override
	public void setContext(Context<AgentType> context) {
		this.context = context;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MGeoRsCompleteNetworkBuilder";
	}
}
