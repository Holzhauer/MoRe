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
package de.cesr.more.building.network;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.Factory;
import org.apache.log4j.Logger;
import cern.jet.random.Uniform;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.util.MLattice1DGenerator;
import de.cesr.more.building.util.MoreBetaProvider;
import de.cesr.more.building.util.MoreKValueProvider;
import de.cesr.more.building.util.MoreRewireTargetProvider;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.uranus.core.URandomService;
import edu.uci.ics.jung.algorithms.util.Indexer;
import edu.uci.ics.jung.graph.Graph;




/**
 * MoRe
 * 
 * Uses {@link MLattice1DGenerator} to produce the underlying regular ring.
 * 
 * TODO:
 * Regarding the SmallWorldNetworkBuilder one must pay attention because of the network direction. Generally, the small world
		algorithm considers given k and beta values for the source of a direction. However, in KUBUS we consider the influencer as source and
		seek to build the network according to the influenced' properties.</p>
		
		<p class="text">Since also for rewiring <code>MSmallWorldBetaModelNetworkGenerator</code> considers the beta value of the source vertex,
		using a custom <code>MoreEdgeFactory</code> as proxy that takes the influenced as source and the influencer as target from the underlying
		<code>MSmallWorldBetaModelNetworkGenerator</code> and creates the edge the other way around, i.e. from the influenced to the influenced,
		does not work. Instead, we build up the entire network in the other direction and reverse the direction of every single edge afterwards.
		For creating the geography edge representative we use the correct direction from influenced to influenced instantaneous.
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

	public static class MSmallWorldBetaModelNetworkGeneratorParams<AgentType, E extends MoreEdge<AgentType>> {

		MoreNetwork<AgentType,E>				network;
		
		boolean									isSymmetrical = false;

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

		MoreEdgeFactory<AgentType, E>			edgeFactory;
		
		MoreBetaProvider<AgentType>				betaProvider;
		MoreKValueProvider<AgentType>			kProvider;
		MoreRewireTargetProvider<AgentType, E> 	rewireManager;
		
		Uniform 								randomDist;
		
		/**
		 * @return the randomDist
		 */
		public Uniform getRandomDist() {
			return randomDist;
		}

		/**
		 * If random distribution has not been set, it uses
		 * URandomService.getURandomService().getUniform().
		 * 
		 * @param randomDist the randomDist to set
		 */
		public void setRandomDist(Uniform randomDist) {
			if (randomDist== null) {
				randomDist = URandomService.getURandomService().getUniform();
			}
			this.randomDist = randomDist;
		}

		public MSmallWorldBetaModelNetworkGeneratorParams() {
		}
		
		/**
		 * @return the network
		 */
		public MoreNetwork<AgentType, E> getNetwork() {
			if (network == null) {
				network = new MDirectedNetwork<AgentType, E>(getEdgeFactory(), "Network generated by MSmallWorldBetaModelNetworkGenerator");
			}
			return network;
		}

		/**
		 * @param network the network to set
		 */
		public void setNetwork(MoreNetwork<AgentType, E> network) {
			this.network = network;
		}

		/**
		 * @return the edgeFactory
		 */
		@SuppressWarnings("unchecked")
		public MoreEdgeFactory<AgentType, E> getEdgeFactory() {
			if (edgeFactory == null) {
				edgeFactory = (MoreEdgeFactory<AgentType, E>) new MDefaultEdgeFactory<AgentType>();
			}
			return edgeFactory;
		}

		/**
		 * @param edgeFactory the edgeFactory to set
		 */
		public void setEdgeFactory(MoreEdgeFactory<AgentType, E> edgeFactory) {
			this.edgeFactory = edgeFactory;
		}

		/**
		 * If the beta provider has not been set yet, it assign a general provider
		 * using MNetworkBuildingPa.BUILD_WSSM_BETA.
		 * @return the betaProvider
		 */
		public MoreBetaProvider<AgentType> getBetaProvider() {
			if (betaProvider == null) {
				betaProvider = new MoreBetaProvider<AgentType>() {
					double beta = ((Double) PmParameterManager
							.getParameter(MNetworkBuildingPa.BUILD_WSSM_BETA))
							.doubleValue();
					@Override
					public double getBetaValue(AgentType node) {
						return beta;
					}
				};
			}
			return betaProvider;
		}

		/**
		 * @param betaProvider the betaProvider to set
		 */
		public void setBetaProvider(MoreBetaProvider<AgentType> betaProvider) {
			this.betaProvider = betaProvider;
		}

		/**
		 * If the k provider has not been set yet, it assign a general provider
		 * using MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG.
		 * @return the kProvider
		 */
		public MoreKValueProvider<AgentType> getkProvider() {
			if (kProvider == null) {
				kProvider =  new MoreKValueProvider<AgentType>() {
					@Override
					public int getKValue(AgentType node) {
						return ((Integer) PmParameterManager
								.getParameter(MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG))
								.intValue();
					}};
			}
			return kProvider;
		}

		/**
		 * @param kProvider the kProvider to set
		 */
		public void setkProvider(MoreKValueProvider<AgentType> kProvider) {
			this.kProvider = kProvider;
		}

		/**
		 * If the rewire target provider was not set yet, it assign a provider
		 * that selects a target vertex randomly (using MManager.getMRandomService().getUniform()).
		 * 
		 * @return the rewireManager
		 */
		public MoreRewireTargetProvider<AgentType, E> getRewireManager() {
			if (rewireManager == null) {
				rewireManager = new MoreRewireTargetProvider<AgentType, E>() {
					@Override
					public AgentType getRewireTarget(Graph<AgentType,E> graph, AgentType source) {
						return new ArrayList<AgentType>(graph.getVertices()).get(
								MManager.getMRandomService().getUniform().nextIntFromTo(0, graph.getVertexCount() - 1));
					}
				};
			}
			return rewireManager;
		}

		/**
		 * @param rewireManager the rewireManager to set
		 */
		public void setRewireManager(
				MoreRewireTargetProvider<AgentType, E> rewireManager) {
			this.rewireManager = rewireManager;
		}
	}
	
	
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
	protected MoreEdgeFactory<AgentType, E>				edgeFactory;
	protected Factory<AgentType>						vertexFactory;
	
	protected MoreBetaProvider<AgentType>				betaProvider;
	protected MoreKValueProvider<AgentType>				kProvider;
	protected MoreRewireTargetProvider<AgentType, E> 	rewireManager;
	
	protected int 										numNodes;
	protected boolean 									isDirected;
	
	Uniform 											randomDist;


	public MSmallWorldBetaModelNetworkGenerator(
			MSmallWorldBetaModelNetworkGeneratorParams<AgentType, E> params) {
		
		this.network 		= params.getNetwork();
		this.isSymmetrical	= params.isSymmetrical();
		this.edgeFactory 	= params.getEdgeFactory();
		
		this.betaProvider 	= params.getBetaProvider();
		this.kProvider		= params.getkProvider();
		this.rewireManager 	= params.getRewireManager();
		
		this.randomDist		= params.getRandomDist();
	}
	
	

	/**
	 * @see de.cesr.more.building.network.MoreNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreNetwork<AgentType, E> buildNetwork(
			Collection<AgentType> agents) {
		
		network.setEdgeFactory(edgeFactory);
		
		this.vertexFactory = new SWVertexFactory(agents);

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
		
		if (numNodes < 10)
			logger.error("Error creating Watts beta small world network",
					new IllegalArgumentException(
							"Number of nodes must be greater than 10"));

		Set<AgentType> set = new HashSet<AgentType>();
		for (AgentType node : network.getNodes()) {
			set.add(node);
		}
		BidiMap<AgentType, Integer> map = Indexer.create(set);


		// create the lattice
		MLattice1DGenerator<AgentType, E> latticeGen = new MLattice1DGenerator<AgentType, E>(
				new Factory<Graph<AgentType, E>>() {
					@Override
					public Graph <AgentType, E> create() {
						return network.getJungGraph();
					}}, vertexFactory, edgeFactory, numNodes, kProvider, true, isSymmetrical);
		latticeGen.create();
		
		List<E> edges = new ArrayList<E>();
		for (E edge : network.getEdgesCollection()) {
			edges.add(edge);
		}

		
		int numOfExpectedEdges = 0;
		for (AgentType agent : agents) {
			numOfExpectedEdges +=kProvider.getKValue(agent);
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
			if (!removedEdges.contains(edge)) {
				if (betaProvider.getBetaValue(edge.getStart()) > randomDist.nextDouble()) {
					int rndIndex = randomDist.nextIntFromTo(0, numNodes - 1);
					AgentType randomNode = map.getKey(rndIndex);
					AgentType source = edge.getStart();
					if (!source.equals(randomNode)
							&& !network.isSuccessor(randomNode, source)) {
						network.disconnect(source, edge.getEnd());						
						network.connect(source, randomNode);

						if (this.isDirected && isSymmetrical) {
							// remove the t -> s edge
							AgentType target = edge.getEnd();
							E otherEdge = network.getEdge(
									target, source);
							network.disconnect(target, source);
							removedEdges.add(otherEdge);

							// add the randomNode -> s edge
							network.connect(randomNode, source);
						}
					}
				}
			}
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
}