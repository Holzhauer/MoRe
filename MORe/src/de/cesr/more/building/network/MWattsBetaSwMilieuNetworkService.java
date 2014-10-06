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
 * Created by Sascha Holzhauer on 06.03.2014
 */
package de.cesr.more.building.network;


import java.util.Collection;

import org.apache.log4j.Logger;

import cern.jet.random.Uniform;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MUndirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.util.MSmallWorldBetaModelNetworkGenerator;
import de.cesr.more.building.util.MoreBetaProvider;
import de.cesr.more.building.util.MoreKValueProvider;
import de.cesr.more.building.util.MSmallWorldBetaModelNetworkGenerator.MSmallWorldBetaModelNetworkGeneratorParams;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.MDefaultPartnerFinder;
import de.cesr.more.rs.building.MGeoRsWattsBetaSwNetworkService.MSmallWorldBetaModelNetworkGeneratorMilieuParams;
import de.cesr.more.rs.building.MMilieuPartnerFinder;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import edu.uci.ics.jung.graph.Graph;


/**
 * MORe
 * 
 * <table>
 * <th>Parameter</th><th>Value</th>
 * <tr><td>#Vertices</td><td>N (via collection of agents)</td></tr>
 * <tr><td>#Edges:</td><td>Directed: kN</td></tr>
 * <tr><td>Parameter provider</td><td>MSmallWorldBetaModelNetworkGeneratorParams</td></tr>
 * </table>
 * See {@link MSmallWorldBetaModelNetworkGeneratorParams} for further parameters!
 * <br>
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * <li>{@link MNetworkBuildingPa.CONSIDER_SOURCES}</li>
 * <li>{@link MNetBuildWbSwPa.BETA}(used as default {@link MoreBetaProvider} in parameter provider)</li>
 * <li>{@link MNetBuildWbSwPa.K} (used as default {@link MoreKValueProvider} in parameter provider)</li>
 * </ul>
 *
 * @author Sascha Holzhauer
 * @date 06.03.2014
 *
 */
public class MWattsBetaSwMilieuNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MoreEdge<AgentType>>
		extends MWattsBetaSwNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MWattsBetaSwMilieuNetworkService.class);

	protected Uniform		rand;

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public MWattsBetaSwMilieuNetworkService() {
		this((MoreEdgeFactory<AgentType, EdgeType>) new MDefaultEdgeFactory<AgentType>());
	}

	public MWattsBetaSwMilieuNetworkService(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		this(eFac, "Network");
	}

	/**
	 * @param eFac
	 */
	public MWattsBetaSwMilieuNetworkService(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		this(eFac, name, PmParameterManager.getInstance(null));
	}

	/**
	 * @param eFac
	 */
	public MWattsBetaSwMilieuNetworkService(MoreEdgeFactory<AgentType, EdgeType> eFac, String name, 
			PmParameterManager param) {
		super(eFac);
		this.name = name;
		this.pm = param;
	}

	/**
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#buildNetwork(java.util.Collection) Parameters are assigned
	 *      through the parameter framework to allow network builders to be initialised automatically.
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
						new MSmallWorldBetaModelNetworkGeneratorMilieuParams<AgentType, EdgeType>(this.pm);

		params.setNetwork(network);
		params.setEdgeModifier(getEdgeModifier());

		params.setRewireManager(new MDefaultPartnerFinder<AgentType, EdgeType>() {
			MMilieuPartnerFinder<AgentType, EdgeType>	partnerFinder	= new MMilieuPartnerFinder<AgentType, EdgeType>(
																				(MMilieuNetworkParameterMap)
																				pm.getParam(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS));
			@Override
			public AgentType findPartner(Graph<AgentType, EdgeType> graph, AgentType focus) {
				return partnerFinder.findPartner(network.getJungGraph(), focus, params.isConsiderSources());
			}
		});

		MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType> gen = new MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType>(
				params);

		return gen.buildNetwork(agents);
	}
}
