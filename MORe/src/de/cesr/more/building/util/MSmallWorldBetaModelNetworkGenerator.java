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
 * Created by Sascha Holzhauer on 17.01.2011
 */
package de.cesr.more.building.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.Factory;
import org.apache.log4j.Logger;

import cern.jet.random.Uniform;
import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MUndirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MoreNetworkBuilder;
import de.cesr.more.manipulate.edge.MDefaultNetworkEdgeModifier;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import de.cesr.more.param.MNetBuildWsPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.rs.building.MDefaultPartnerFinder;
import de.cesr.more.rs.building.MorePartnerFinder;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import edu.uci.ics.jung.graph.Graph;


/**
 * MoRe
 * 
 * TODO test
 * 
 * Uses {@link MLattice1DGenerator} to produce the underlying regular ring.
 * 
 * Regarding the SmallWorldNetworkBuilder one must pay attention because of the network direction.
 * Generally, the small world algorithm considers given k and beta values for the source of a direction.
 * However, in some models we consider the influencer as source and seek to build the network according
 * to the influenced' properties. In these cases,set {@link MNetworkBuildingPa.BUILD_WSSM_CONSIDER_SOURCES}
 * to Boolean.FALSE!
 * 
 * @formatter:off
 * <table>
 * <th>Property</th><th>Value</th>
 * <tr><td>#Vertices</td><td>N (via collection of agents)</td></tr>
 * <tr><td></td><td></td></tr>
 * <tr><td>#Edges:</td><td>Undirected: N*K</td></tr>
 * </table> 
 * 
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * <li>{@link MNetworkBuildingPa.BUILD_WSSM_BETA} (as default for param object)</li>
 * <li>{@link MNetworkBuildingPa.MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG} (as default for param object)</li>
 * <li>{@link MNetworkBuildingPa.MNetworkBuildingPa.BUILD_WSSM_CONSIDER_SOURCES}</li>
 * <li>...</li>
 * </ul>
 * 
 * @author Sascha Holzhauer
 * @author Jung Project
 * @author Nick Collier (Repast Simphony)
 * @param <AgentType> 
 * @param <E> 
 * @date 17.01.2011
 * 
 */
public class MSmallWorldBetaModelNetworkGenerator<AgentType, E extends MoreEdge<AgentType>>
	implements MoreNetworkBuilder<AgentType, E>{

	/**
	 * MORe
	 * 
	 * Parameter provider for {@link MSmallWorldBetaModelNetworkGenerator}.
	 *
	 * @author Sascha Holzhauer
	 * @date 23.11.2011 
	 *
	 * @param <AType>
	 * @param <EType>
	 */
	public static class MSmallWorldBetaModelNetworkGeneratorParams<AType, EType extends MoreEdge<AType>> {

		protected MoreNetwork<AType,EType>					network;
		
		protected boolean									isSymmetrical = false;

		protected MoreNetworkEdgeModifier<AType, EType>		edgeModifier;
		
		protected MoreBetaProvider<AType>				betaProvider;
		protected MoreKValueProvider<AType>				kProvider;
		protected MorePartnerFinder<AType, EType> 			rewireManager;
		
		protected Uniform 									randomDist;
		
		protected PmParameterManager						pm;
		
		public MSmallWorldBetaModelNetworkGeneratorParams(PmParameterManager pm) {
			this.pm = pm;
		}
		
		/**
		 * If random distribution has not been set, it uses
		 * URandomService.getURandomService().getUniform().
		 * 
		 * @return the randomDist
		 */
		public Uniform getRandomDist() {
			if (this.randomDist== null) {
				this.randomDist = MManager.getURandomService().getNewUniformDistribution(
						MManager.getURandomService().getGenerator(
								(String) pm.getParam(MRandomPa.RND_STREAM)));
			}
			return randomDist;
		}

		/**
		 * @param randomDist the randomDist to set
		 */
		public void setRandomDist(Uniform randomDist) {
			this.randomDist = randomDist;
		}
		
		/**
		 * @return the network
		 */
		public MoreNetwork<AType, EType> getNetwork() {
			if (network == null) {
				network = (Boolean) pm.getParam(MNetworkBuildingPa.BUILD_DIRECTED) ? 
						new MDirectedNetwork<AType, EType>(getEdgeModifier().getEdgeFactory(),
								"Network generated by MSmallWorldBetaModelNetworkGenerator") : 
							new MUndirectedNetwork<AType, EType>(getEdgeModifier().getEdgeFactory(), 
						"Network generated by MSmallWorldBetaModelNetworkGenerator");
			}
			return network;
		}

		/**
		 * @param network the network to set
		 */
		public void setNetwork(MoreNetwork<AType, EType> network) {
			this.network = network;
		}

		/**
		 * @return the edgeModifier
		 */
		@SuppressWarnings("unchecked")
		public MoreNetworkEdgeModifier<AType, EType> getEdgeModifier() {
			if (edgeModifier == null) {
				edgeModifier = new MDefaultNetworkEdgeModifier<AType, EType>((MoreEdgeFactory<AType, EType>) 
						new MDefaultEdgeFactory<AType>());
			}
			return edgeModifier;
		}

		/**
		 * @param edgeModifier the edgeModifier to set
		 */
		public void setEdgeModifier(MoreNetworkEdgeModifier<AType, EType> edgeModifier) {
			this.edgeModifier = edgeModifier;
		}

		/**
		 * If the beta provider has not been set yet, it assigns a general provider
		 * using MNetBuildWsPa.BETA.
		 * @return the betaProvider
		 */
		public MoreBetaProvider<AType> getBetaProvider() {
			if (betaProvider == null) {
				betaProvider = new MoreBetaProvider<AType>() {
					double beta = ((Double) pm
							.getParam(MNetBuildWsPa.BETA))
							.doubleValue();
					@Override
					public double getBetaValue(AType node) {
						return beta;
					}
				};
			}
			return betaProvider;
		}

		/**
		 * @param betaProvider the betaProvider to set
		 */
		public void setBetaProvider(MoreBetaProvider<AType> betaProvider) {
			this.betaProvider = betaProvider;
		}

		/**
		 * If the k provider has not been set yet, it assigns a general provider
		 * using MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG.
		 * @return the kProvider
		 */
		public MoreKValueProvider<AType> getkProvider() {
			if (kProvider == null) {
				kProvider =  new MoreKValueProvider<AType>() {
					@Override
					public int getKValue(AType node) {
						return ((Integer) pm.getParam(MNetBuildWsPa.K))
								.intValue();
					}};
			}
			return kProvider;
		}

		/**
		 * @param kProvider the kProvider to set
		 */
		public void setkProvider(MoreKValueProvider<AType> kProvider) {
			this.kProvider = kProvider;
		}

		/**
		 * If the rewire target provider was not set yet, it assigns a provider
		 * that selects a target vertex randomly (using MManager.getMRandomService().getUniform()).
		 * 
		 * @return the rewireManager
		 */
		public MorePartnerFinder<AType, EType> getRewireManager() {
			if (rewireManager == null) {
				rewireManager = new  MDefaultPartnerFinder<AType, EType>() {
					@Override
					public AType findPartner(Graph<AType,EType> graph, AType source, boolean incoming) {
						return new ArrayList<AType>(graph.getVertices()).get(
								MManager.getURandomService().getUniform().nextIntFromTo(0, graph.getVertexCount() - 1));
					}
				};
			}
			return rewireManager;
		}

		/**
		 * @param rewireManager the rewireManager to set
		 */
		public void setRewireManager(
				MorePartnerFinder<AType, EType> rewireManager) {
			this.rewireManager = rewireManager;
		}
		

		/**
		 * @return the isSymmetrical
		 */
		public boolean isSymmetrical() {
			return isSymmetrical;
		}

		/**
		 * Whether or not the generated edges will be symmetrical (if an edge goes from A to B there
		 * is also and edge that goes from B to A). This has no effect on a non-directed network.
		 * Default: false;
		 * @param isSymmetrical
		 */
		public void setSymmetrical(boolean isSymmetrical) {
			this.isSymmetrical = isSymmetrical;
		}

		/**
		 * @return
		 */
		public boolean isConsiderSources() {
			return (Boolean) pm.getParam(MNetworkBuildingPa.BUILD_WSSM_CONSIDER_SOURCES);
		}
	}
	
	
	/**
	 * MORe
	 * 
	 * Helper class to use agent collections with a vertex factory.
	 * @author Sascha Holzhauer
	 * @date 23.11.2011 
	 *
	 */
	protected class SWVertexFactory implements Factory <AgentType> {
		Iterator <AgentType>		agentIterator;

		protected SWVertexFactory(Collection <AgentType> agents) {
			agentIterator = agents.iterator();
		}

		@Override
		public AgentType create() {
			return agentIterator.next();
		}

	}
	
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MSmallWorldBetaModelNetworkGenerator.class);

	protected MoreNetwork<AgentType,E>					network;
	protected boolean									isSymmetrical;
	protected MoreNetworkEdgeModifier<AgentType, E>		edgeModifier;
	protected Factory<AgentType>						vertexFactory;
	
	protected MoreBetaProvider<AgentType>				betaProvider;
	protected MoreKValueProvider<AgentType>				kProvider;
	protected MorePartnerFinder<AgentType, E> 			rewireManager;
	
	protected int 										numNodes;
	protected boolean 									isDirected;
	protected boolean 									isConsiderSources;
	
	Uniform 											randomDist;


	public MSmallWorldBetaModelNetworkGenerator(
			MSmallWorldBetaModelNetworkGeneratorParams<AgentType, E> params) {
		
		this.network 		= params.getNetwork();
		this.isSymmetrical	= params.isSymmetrical();
		this.edgeModifier 	= params.getEdgeModifier();
		
		this.betaProvider 	= params.getBetaProvider();
		this.kProvider		= params.getkProvider();
		this.rewireManager 	= params.getRewireManager();
		
		this.randomDist		= params.getRandomDist();
		
		this.isConsiderSources = params.isConsiderSources();
	}
	
	

	/**
	 * Order of agents in agents also determines the order of vertices in
	 * the underlying lattice structure (via {@link SWVertexFactory}).
	 * @see de.cesr.more.building.network.MoreNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreNetwork<AgentType, E> buildNetwork(
			Collection<AgentType> agents) {
		
		network.setEdgeFactory(edgeModifier.getEdgeFactory());
		
		this.vertexFactory = new SWVertexFactory(agents);
		
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
		
		for (AgentType agent : agents) {
			network.addNode(agent);
		}

		// <- LOGGING
		logger.info("Build network with beta-provider: "
				+ betaProvider
				+ " and initial k-provider: "
				+ kProvider);
		// LOGGING ->

		this.isDirected = network.isDirected();
		numNodes = network.numNodes();
		
		if (numNodes < 10) {
			logger.error("Error creating Watts beta small world network",
					new IllegalArgumentException(
							"Number of nodes must be greater than 9"));
		}

		ArrayList<AgentType> list = new ArrayList<AgentType>(agents);


		// create the lattice
		MLattice1DGenerator<AgentType, E> ringGen = new MLattice1DGenerator<AgentType, E>(
				new Factory<MoreNetwork<AgentType, E>>() {
					@Override
					public MoreNetwork<AgentType, E> create() {
						return network;
					}}, vertexFactory, edgeModifier, numNodes, kProvider, true, isSymmetrical);
		ringGen.create();
		
		List<E> edges = new ArrayList<E>();
		for (E edge : network.getEdgesCollection()) {
			edges.add(edge);
		}
		
		int numOfExpectedEdges = 0;
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			//UNDO
			//logger.debug("Network: " + network.getNetworkInfo());
		}
		// LOGGING ->

		for (AgentType agent : agents) {
			numOfExpectedEdges +=kProvider.getKValue(agent);
			
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Desired k value: " + kProvider.getKValue(agent) + " | Out-Degree: " + network.getOutDegree(agent) + 
						" | In-Degree: " + network.getInDegree(agent));
			}
			// LOGGING ->

			assert kProvider.getKValue(agent) == (this.isConsiderSources ? 
					network.getOutDegree(agent) : network.getInDegree(agent));
		}
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Number of Edges before rewiring: " + network.numEdges()
					+ " (expected: " + (this.isDirected ? numOfExpectedEdges : numOfExpectedEdges / 2) + ")");
		}
		// LOGGING ->
		
		assert network.numEdges() == (this.isDirected ? numOfExpectedEdges : numOfExpectedEdges / 2);
		
		
		// rewiring:
		Set<E> removedEdges = new HashSet<E>();

		for (E edge : edges) {
			rewireEdge(list, removedEdges, edge);
		}
		
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Number of Edges after rewiring: " + network.numEdges()
					+ " (expected: " + (this.isDirected ? numOfExpectedEdges : numOfExpectedEdges / 2) + ")");
		}
		// LOGGING ->
		
		assert network.numEdges() == (this.isDirected ? numOfExpectedEdges : numOfExpectedEdges / 2);

		
		return network;
	}



	/**
	 * @param list
	 * @param removedEdges
	 * @param edge
	 */
	public void rewireEdge(ArrayList<AgentType> list, Set<E> removedEdges, E edge) {
		if (!removedEdges.contains(edge)) {
			AgentType start = this.isConsiderSources ?
					edge.getEnd() : edge.getStart();
			AgentType end = this.isConsiderSources ?
					edge.getStart() : edge.getEnd();

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Beta: " + betaProvider.getBetaValue(start));
			}
			// LOGGING ->

			if (betaProvider.getBetaValue(start) > randomDist.nextDouble()) {
				
				AgentType randomNode;
				
				if (this.rewireManager != null) {
					randomNode = this.rewireManager.findPartner(network.getJungGraph(), start);
				} else {
					int rndIndex = randomDist.nextIntFromTo(0, numNodes - 1);
					randomNode = list.get(rndIndex);
				}
				
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("randomNode: " + randomNode);
				}
				// LOGGING ->

				boolean condition;
				if (this.isConsiderSources) { 
					condition = !start.equals(randomNode)
					&& !network.isSuccessor(randomNode, start);
				} else {
					condition = !start.equals(randomNode)
					&& !network.isSuccessor(start, randomNode);
				}
				
				
				if (randomNode != null && condition) {
					
					network.disconnect(edge.getStart(), edge.getEnd());
					
					if (this.isConsiderSources) { 
						network.connect(start, randomNode);
					} else {
						network.connect(randomNode, start);
					}

					if (this.isDirected && isSymmetrical) {
						// remove the t -> s edge
						E otherEdge = network.getEdge(
								end, start);
						network.disconnect( edge.getEnd(), edge.getStart());
						removedEdges.add(otherEdge);

						// add the randomNode -> s edge
						if (this.isConsiderSources) { 
							network.connect(randomNode, start);
						} else {
							network.connect(start, randomNode);
						}							
					}
				}
			}
		}
	}
}
