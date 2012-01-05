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
 * Created by holzhauer on 24.06.2011
 */
package de.cesr.more.rs.building;

import java.util.Collection;
import org.apache.log4j.Logger;

import cern.jet.random.Uniform;

import repast.simphony.context.Context;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.util.MSmallWorldBetaModelNetworkGenerator;
import de.cesr.more.building.util.MoreBetaProvider;
import de.cesr.more.building.util.MoreKValueProvider;
import de.cesr.more.building.util.MSmallWorldBetaModelNetworkGenerator.MSmallWorldBetaModelNetworkGeneratorParams;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.edge.MRsEdgeFactory;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.uranus.core.URandomService;

/**
 * MORe
 * 
 * TODO implement MoreGeoRsNetworkService
 * TODO parameter description
 * 
 * @author holzhauer
 * @author Jung Project
 * @author Nick Collier (Repast Simphony)
 * 
 * @date 24.06.2011
 * 
 */
public class MGeoRsWattsBetaSwBuilder<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType>>
		extends MGeoRsNetworkService<AgentType, EdgeType> {
	
	
	/**
	 * MORe
	 *
	 * Uses providers that use the milieu network parameter map.
	 * 
	 * @author Sascha Holzhauer
	 * @date 29.12.2011 
	 *
	 * @param <AgentT>
	 * @param <EdgeT>
	 */
	static class MSmallWorldBetaModelNetworkGeneratorMilieuParams<AgentT extends MoreMilieuAgent, EdgeT extends MRepastEdge<AgentT>>
		extends MSmallWorldBetaModelNetworkGeneratorParams<AgentT, EdgeT>{
		
		/**
		 * If the k provider has not been set yet, it assign a provider
		 * usingMNetworkBuildingPa.MILIEU_NETWORK_PARAMS or calls super.getKValueProvider() if 
		 * MNetworkBuildingPa.MILIEU_NETWORK_PARAMS is null.
		 * 
		 * @return the kProvider
		 */
		public MoreKValueProvider<AgentT> getkProvider() {
			if (kProvider == null) {
				if (PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS) != null) {
					final MMilieuNetworkParameterMap netParams = ((MMilieuNetworkParameterMap) PmParameterManager.getParameter(
							MNetworkBuildingPa.MILIEU_NETWORK_PARAMS));
					kProvider =  new MoreKValueProvider<AgentT>() {
						@Override
						public int getKValue(AgentT node) {
							return netParams.getK(node.getMilieuGroup());
						}};
				} else {
					super.getkProvider();
				}
			}
			return kProvider;
		}

		/**
		 * If the beta provider has not been set yet, it assign a provider
		 * using MNetworkBuildingPa.MILIEU_NETWORK_PARAMS or calls super.getBetaProvider() if 
		 * MNetworkBuildingPa.MILIEU_NETWORK_PARAMS is null.
		 * @return the betaProvider
		 */
		public MoreBetaProvider<AgentT> getBetaProvider() {
			if (betaProvider == null) {
				if (PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS) != null) {
					final MMilieuNetworkParameterMap netParams = ((MMilieuNetworkParameterMap) PmParameterManager.getParameter(
							MNetworkBuildingPa.MILIEU_NETWORK_PARAMS));
					betaProvider = new MoreBetaProvider<AgentT>() {
						@Override
						public double getBetaValue(AgentT node) {
							return netParams.getP_Rewire(node.getMilieuGroup());
						}
					};
				} else {
					super.getBetaProvider();
				}
			}
			return betaProvider;
		}
	}

	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MGeoRsWattsBetaSwBuilder.class);

	private Context<AgentType> context;
	private MoreEdgeFactory<AgentType, EdgeType> eFac;
	
	protected Uniform randomDist;
	protected String name;
	
	/**
	 * 
	 */
	public MGeoRsWattsBetaSwBuilder() {
		this(new MRsEdgeFactory<AgentType, EdgeType>());
	}

	/**
	 * @param eFac
	 */
	public MGeoRsWattsBetaSwBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		super(eFac);
		this.eFac = eFac;
		this.randomDist = URandomService.getURandomService().getUniform();
	}

	/**
	 * @param eFac
	 */
	public MGeoRsWattsBetaSwBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		super(eFac);
		this.eFac = eFac;
		this.name = name;
		this.randomDist = URandomService.getURandomService().getUniform();
	}
	
	@Override
	public void setContext(Context<AgentType> context) {
		this.context = context;
	}

	/**
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#buildNetwork(java.util.Collection)
	 *      Parameters are assigned through the parameter framework to allow
	 *      network builders to be initialises automatically.
	 */
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(
			Collection<AgentType> agents) {
		
		if (context == null) {
			logger.error("Context not set!");
			throw new IllegalStateException("Context not set!");
		}
		
		MoreRsNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType>(
				((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ? new DirectedJungNetwork<AgentType>(
						this.name) : new UndirectedJungNetwork<AgentType>(
						this.name), context);
		
		MSmallWorldBetaModelNetworkGeneratorParams<AgentType, EdgeType> params = 
			new MSmallWorldBetaModelNetworkGeneratorMilieuParams<AgentType, EdgeType>();
		
		params.setNetwork(network);
		params.setEdgeFactory(eFac);
		params.setRandomDist(randomDist);
		
		MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType> gen = new MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType>(params);
		
		return (MoreRsNetwork<AgentType, EdgeType>) gen.buildNetwork(agents);
	}

	/**
	 * @see de.cesr.more.manipulate.network.MoreNetworkModifier#addAndLinkNode(de.cesr.more.basic.network.MoreNetwork, java.lang.Object)
	 */
	@Override
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network, AgentType node) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return random distribution
	 */
	public Uniform getRandomDist() {
		return randomDist;
	}

	/**
	 * Sets the random distribution for this network builder (uniform)
	 * @param randomDist
	 */
	public void setRandomDist(Uniform randomDist) {
		this.randomDist = randomDist;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "MGeoRsWattsBetaSwBuilder";
	}
}
