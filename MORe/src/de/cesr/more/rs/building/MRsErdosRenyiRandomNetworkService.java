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
 * Created by Sascha Holzhauer on 13.06.2012
 */
package de.cesr.more.rs.building;


import java.util.Collection;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import cern.jet.random.Uniform;
import de.cesr.more.basic.MManager;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.util.MRandomNetworkGenerator;
import de.cesr.more.building.util.MoreLinkProbProvider;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 13.06.2012 
 *
 */
public class MRsErdosRenyiRandomNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType>>
		extends MRsNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MRsErdosRenyiRandomNetworkService.class);

	protected String		name;

	public MRsErdosRenyiRandomNetworkService(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		super(eFac);
		this.name = name;
	}

	/**
	 * @see de.cesr.more.building.network.MoreNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(final Collection<AgentType> agents) {
		
		checkAgentCollection(agents);

		MoreLinkProbProvider<AgentType> linkProbProvider = null;
		if (PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS) != null) {
			final MMilieuNetworkParameterMap netParams = ((MMilieuNetworkParameterMap) PmParameterManager.getParameter(
					MNetworkBuildingPa.MILIEU_NETWORK_PARAMS));
			linkProbProvider =  new MoreLinkProbProvider<AgentType>() {
				@Override
				public double getLinkProb(AgentType node) {
					return 1.0 / (agents.size() - 1) * netParams.getK(node.getMilieuGroup());
				}
			};

			// <- LOGGING
			logger.info("Using MoreLinkProbProvider...");
			// LOGGING ->
		}
		MRandomNetworkGenerator<AgentType> generator = new MRandomNetworkGenerator<AgentType>(1.0 / (agents.size() - 1)
				*
				((Integer) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_RANDOM_AVG_DEGREE)).intValue(),
				false, (Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED), linkProbProvider);

		if (context == null) {
			// <- LOGGING
			logger.error("The context has not bee set!");
			// LOGGING ->
			throw new IllegalStateException("The context has not bee set!");
		}

		MRsContextJungNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType>(
				((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ?
						new DirectedJungNetwork<AgentType>(name) :
						new UndirectedJungNetwork<AgentType>(name), context, this.edgeModifier.getEdgeFactory());

		for (AgentType agent : context) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Add agent " + agent + " to network.");
			}
			// LOGGING ->

			network.addNode(agent);
		}

		return (MoreRsNetwork<AgentType, EdgeType>) generator.createNetwork(network);
	}

	/**
	 * @see de.cesr.more.manipulate.network.MoreNetworkModifier#addAndLinkNode(de.cesr.more.basic.network.MoreNetwork,
	 *      java.lang.Object)
	 */
	@Override
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network, AgentType node) {
		Uniform uniform = MManager.getURandomService().getNewUniformDistribution(
				MManager.getURandomService().getGenerator(
						((String) PmParameterManager.getParameter(MRandomPa.RND_STREAM_RANDOM_NETWORK_BUILDING))));
		double p = 1.0 / (network.numNodes() - 1) *
				((Integer) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_RANDOM_AVG_DEGREE)).intValue();
		network.addNode(node);
		for (AgentType partner : network.getNodes()) {
			if (partner != node) {
				if (uniform.nextDouble() < p) {
					network.connect(node, partner);
				}
				if (uniform.nextDouble() < p
						&& (Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) {
					network.connect(partner, node);
				}
			}
		}
		return true;
	}

	/**
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#setContext(repast.simphony.context.Context)
	 */
	@Override
	public void setContext(Context<AgentType> context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return "MRsErdosRenyiRandomNetworkService";
	}
}
