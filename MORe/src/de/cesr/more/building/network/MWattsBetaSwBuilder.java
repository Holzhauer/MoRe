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


import java.util.Collection;

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
 * <tr><td>#Vertices</td><td>N (via collection of agents; must be quadratic)</td></tr>
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
		super(eFac);
		this.name = name;
	}

	/**
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#buildNetwork(java.util.Collection) Parameters are assigned
	 *      through the parameter framework to allow network builders to be initialises automatically.
	 */
	@Override
	public MoreNetwork<AgentType, EdgeType> buildNetwork(
			Collection<AgentType> agents) {

		checkAgentCollection(agents);

		MoreNetwork<AgentType, EdgeType> network = ((Boolean) PmParameterManager
				.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ?
				new MDirectedNetwork<AgentType, EdgeType>(getEdgeFactory(),
						name) : new MUndirectedNetwork<AgentType, EdgeType>(getEdgeFactory(), name);

		MSmallWorldBetaModelNetworkGeneratorParams<AgentType, EdgeType> params =
				new MSmallWorldBetaModelNetworkGeneratorParams<AgentType, EdgeType>();

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
		// TODO Auto-generated method stub
		return false;
	}
}
