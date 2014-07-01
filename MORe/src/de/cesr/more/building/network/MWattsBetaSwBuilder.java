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
 * Created by holzhauer on 22.11.2011
 */
package de.cesr.more.building.network;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MUndirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.util.MSmallWorldBetaModelNetworkGenerator;
import de.cesr.more.building.util.MSmallWorldBetaModelNetworkGenerator.MSmallWorldBetaModelNetworkGeneratorParams;
import de.cesr.more.building.util.MoreBetaProvider;
import de.cesr.more.building.util.MoreKValueProvider;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 *
 * @formatter:off
 *
 * <table>
 * <th>Parameter</th><th>Value</th>
 * <tr><td>#Vertices</td><td>N (via collection of agents)</td></tr>
 * <th>Property</th><th>Value</th>
 * <tr><td>#Edges:</td><td>Directed: kN</td></tr>
 * <tr><td>Parameter provider</td><td>MSmallWorldBetaModelNetworkGeneratorParams</td></tr>
 * </table>
 * See {@link MSmallWorldBetaModelNetworkGeneratorParams} for further parameters!
 * <br>
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * <li>{@link MNetworkBuildingPa.BUILD_WSSM_BETA}(used as default {@link MoreBetaProvider} in parameter provider)</li>
 * <li>{@link MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG} (used as default {@link MoreKValueProvider} in parameter provider)</li>
 * </ul>
 *
 * TODO undirected?
 *
 * @author holzhauer
 * @date 22.11.2011
 *
 */
public class MWattsBetaSwBuilder<AgentType, EdgeType extends MoreEdge<AgentType>>
	extends MNetworkService<AgentType, EdgeType> {

		/**
		 * @formatter:on
		 * Logger
		 */
	static private Logger	logger	= Logger.getLogger(MWattsBetaSwBuilder.class);

	protected String		name;

	/**
		 *
		 */
	@SuppressWarnings("unchecked")
	public MWattsBetaSwBuilder() {
		this((MoreEdgeFactory<AgentType, EdgeType>) new MDefaultEdgeFactory<AgentType>());
	}

	public MWattsBetaSwBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		this(eFac, "Network");
	}

	/**
	 * @param eFac
	 */
	public MWattsBetaSwBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		this(eFac, name, PmParameterManager.getInstance(null));
	}

	/**
	 * @param eFac
	 */
	public MWattsBetaSwBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name, PmParameterManager param) {
		super(eFac);
		this.name = name;
		this.pm = param;
	}

	/**
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#buildNetwork(java.util.Collection) Parameters are assigned
	 *      through the parameter framework to allow network builders to be initialises automatically.
	 */
	@Override
	public MoreNetwork<AgentType, EdgeType> buildNetwork(
			Collection<AgentType> agents) {

		// <- LOGGING
		logger.info("Building Small-World network for " + agents.size() + " agents...");
		// LOGGING ->

		checkAgentCollection(agents);

		final MoreNetwork<AgentType, EdgeType> network = ((Boolean) this.pm
				.getParam(MNetworkBuildingPa.BUILD_DIRECTED)) ?
				new MDirectedNetwork<AgentType, EdgeType>(getEdgeFactory(),
						name) : new MUndirectedNetwork<AgentType, EdgeType>(getEdgeFactory(), name);

		final MSmallWorldBetaModelNetworkGeneratorParams<AgentType, EdgeType> params =
				new MSmallWorldBetaModelNetworkGeneratorParams<AgentType, EdgeType>(this.pm);

		params.setNetwork(network);
		params.setEdgeModifier(getEdgeModifier());

		MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType> gen = new MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType>(
				params);

		return gen.buildNetwork(agents);
	}

	/**
	 * @see de.cesr.more.manipulate.network.MoreNetworkModifier#addAndLinkNode(de.cesr.more.basic.network.MoreNetwork,
	 *      java.lang.Object)
	 */
	@Override
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network,
			AgentType node) {

		MSmallWorldBetaModelNetworkGeneratorParams<AgentType, EdgeType> params =
				new MSmallWorldBetaModelNetworkGeneratorParams<AgentType, EdgeType>(this.pm);

		params.setNetwork(network);
		params.setEdgeModifier(getEdgeModifier());


		MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType> gen = new MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType>(
				params);

		List<EdgeType> edges = new ArrayList<EdgeType>();
		ArrayList<AgentType> agents = new ArrayList<AgentType>();
		Set<EdgeType> removedEdges = new HashSet<EdgeType>();

		for (AgentType agent : network.getNodes()) {
			agents.add(agent);
		}

		network.addNode(node);

		// request random node to connect with
		AgentType initialPartner = params.getRewireManager().findPartner(network.getJungGraph(), node);
		if (params.isConsiderSources()) {
			params.getEdgeModifier().createEdge(network, initialPartner, node);
		} else {
			params.getEdgeModifier().createEdge(network, node, initialPartner);
		}

		// request neighbors of this node to connect with
		Iterator<AgentType> initialAgentNeighbours;
		if (params.isConsiderSources()) {
			initialAgentNeighbours = network.getSuccessors(initialPartner).iterator();
		} else {
			initialAgentNeighbours = network.getPredecessors(initialPartner).iterator();
		}

		// there is already one link to initial partner...:
		for (int i = 1; i < params.getkProvider().getKValue(node) && initialAgentNeighbours.hasNext(); i++) {
			AgentType next = initialAgentNeighbours.next();
			if (next != node) {
				if (params.isConsiderSources()) {
					edges.add(params.getEdgeModifier().createEdge(network, next, node));
				} else {
					edges.add(params.getEdgeModifier().createEdge(network, node, next));
				}
			}
		}

		// randomly rewire these links
		for (EdgeType edge : edges) {
			gen.rewireEdge(agents, removedEdges, edge);
		}

		// add additional global links if required (in case initialPartner's milieu k is smaller than node one's)
		int missing = params.getkProvider().getKValue(node)
				- (params.isConsiderSources() ?
						network.getInDegree(node) : network.getOutDegree(node));
		if (missing > 0) {
			for (int j = 0; j < missing; j++) {
				AgentType partner;
				do {
					partner = params.getRewireManager().findPartner(network.getJungGraph(), node);

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Found partner: " + partner + " (node to connect: " + node + ")");
					}
					// LOGGING ->

				} while (partner == node || network.isSuccessor(partner, node));

				if (params.isConsiderSources()) {
					params.getEdgeModifier().createEdge(network, partner, node);
				} else {
					params.getEdgeModifier().createEdge(network, node, partner);
				}
			}
		}

		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MWattsBetaSW Network Service";
	}
}
