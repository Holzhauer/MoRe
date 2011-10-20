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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;
import org.apache.log4j.Logger;

import cern.jet.random.Uniform;

import repast.simphony.context.Context;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.building.MRsEdgeFactory;
import de.cesr.more.building.MoreEdgeFactory;
import de.cesr.more.networks.MoreRsNetwork;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.adapter.MRepastEdge;
import de.cesr.more.rs.adapter.MRsContextJungNetwork;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.uranus.core.URandomService;
import edu.uci.ics.jung.algorithms.util.Indexer;

/**
 * MORe
 * 
 * @author holzhauer
 * @author Jung Project
 * @author Nick Collier (Repast Simphony)
 * 
 * @date 24.06.2011
 * 
 */
public class MRsWattsBetaSwBuilder<AgentType, EdgeType extends MRepastEdge<AgentType>>
		implements MoreRsNetworkBuilder<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MRsWattsBetaSwBuilder.class);

	private Context<AgentType> context;
	private MoreEdgeFactory<AgentType, EdgeType> eFac;

	private double beta;
	private int outdegree, numNodes;
	private boolean isDirected;
	
	protected Uniform randomDist;
	/**
	 * 
	 */
	public MRsWattsBetaSwBuilder() {
		this(new MRsEdgeFactory<AgentType, EdgeType>());
	}

	/**
	 * @param eFac
	 */
	public MRsWattsBetaSwBuilder(MRsEdgeFactory<AgentType, EdgeType> eFac) {
		this.eFac = eFac;
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
						"Network") : new UndirectedJungNetwork<AgentType>(
						"Network"), context);
		network.setEdgeFactory(eFac);

		for (AgentType agent : agents) {
			network.addNode(agent);
		}

		// <- LOGGING
		logger.info("Build network with beta: "
				+ PmParameterManager
						.getParameter(MNetworkBuildingPa.BUILD_WSSM_BETA)
				+ " and initial degree: "
				+ PmParameterManager
						.getParameter(MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG));
		// LOGGING ->

		this.beta = ((Double) PmParameterManager
				.getParameter(MNetworkBuildingPa.BUILD_WSSM_BETA))
				.doubleValue();
		this.outdegree = ((Integer) PmParameterManager
				.getParameter(MNetworkBuildingPa.BUILD_WSSM_INITIAL_OUTDEG))
				.intValue();
		this.isDirected = ((Boolean) PmParameterManager
				.getParameter(MNetworkBuildingPa.BUILD_DIRECTED));

		if (outdegree % 2 != 0) {
			logger.error("Error creating WattsBetaSmallWorldGenerator",
					new IllegalArgumentException(
							"All nodes must have an even degree."));
		}
		if (beta > 1.0 || beta < 0.0) {
			logger.error("Error creating WattsBetaSmallWorldGenerator",
					new IllegalArgumentException(
							"Beta must be between 0 and 1."));
		}

		// degree <= |nodes| !
		if (this.outdegree > network.numNodes()) {
			String msg = "Degree (" + this.outdegree
					+ ") may not exceed the number of nodes ("
					+ network.numNodes() + ")";
			logger.error(msg);
			throw new IllegalStateException(msg);
		}

		network = createNetwork(network);
		return network;
	}

	/**
	 * Generates a beta-network from a 1-lattice according to the parameters
	 * given.
	 * Rewiring checks against self-loops and already existing edges (but does
	 * not search for another target node in case there is already an edge to
	 * the target node chosen at first).
	 * 
	 * @return a beta-network model that is potentially a small-world
	 */
	public MoreRsNetwork<AgentType, EdgeType> createNetwork(
			MoreRsNetwork<AgentType, EdgeType> network) {
		numNodes = network.size();
		if (numNodes < 10)
			logger.error("Error creating Watts beta small world network",
					new IllegalArgumentException(
							"Number of nodes must be greater than 10"));

		Set<AgentType> set = new HashSet<AgentType>();
		for (AgentType node : network.getNodes()) {
			set.add(node);
		}
		BidiMap<AgentType, Integer> map = Indexer.create(set);

		boolean isDirected = network.isDirected();
		int numKNeighbors = outdegree / 2;

		// create the lattice
		for (int i = 0; i < numNodes; i++) {
			for (int s = 1; s <= numKNeighbors; s++) {
				AgentType source = map.getKey(i);
				int upI = upIndex(i, s);
				AgentType target = map.getKey(upI);
				network.addEdge(source, target);
				if (this.isDirected && isDirected) {
					network.addEdge(target, source);
				}
			}
		}

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Number of Edges: " + network.numEdges()
					+ " (expected: " + (this.isDirected ? network.numNodes() * outdegree : network.numNodes() * outdegree / 2) + ")");
		}
		// LOGGING ->
		assert network.numEdges() == (this.isDirected ? network.numNodes() * outdegree : network.numNodes() * outdegree / 2);

		List<RepastEdge<AgentType>> edges = new ArrayList<RepastEdge<AgentType>>();
		for (RepastEdge<AgentType> edge : network.getEdges()) {
			edges.add(edge);
		}

		Set<RepastEdge<AgentType>> removedEdges = new HashSet<RepastEdge<AgentType>>();

		for (RepastEdge<AgentType> edge : edges) {
			if (!removedEdges.contains(edge)) {
				if (beta > randomDist.nextDouble()) {
					int rndIndex = randomDist.nextIntFromTo(0, numNodes - 1);
					AgentType randomNode = map.getKey(rndIndex);
					AgentType source = edge.getSource();
					if (!source.equals(randomNode)
							&& !network.isPredecessor(source, randomNode)) {
						network.removeEdge(edge);						
						network.addEdge(source, randomNode);

						if (this.isDirected && isDirected) {
							// remove the t -> s edge
							AgentType target = edge.getTarget();
							RepastEdge<AgentType> otherEdge = network.getEdge(
									target, source);
							network.removeEdge(otherEdge);
							removedEdges.add(otherEdge);

							// add the randomNode -> s edge
							network.addEdge(randomNode, source);
						}
					}
				}
			}
		}
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Number of Edges after rewiring: " + network.numEdges()
					+ " (expected: " + (this.isDirected ? network.numNodes() * outdegree : network.numNodes() * outdegree / 2) + ")");
		}
		// LOGGING ->
		assert network.numEdges() == (this.isDirected ? network.numNodes() * outdegree : network.numNodes() * outdegree / 2);
		
		return network;
	}

	/**
	 * Determines the index of the neighbor ksteps above
	 * 
	 * @param numSteps
	 *            is the number of steps away from the current index that is
	 *            being considered.
	 * @param currentIndex
	 *            the index of the selected vertex.
	 */
	private int upIndex(int currentIndex, int numSteps) {

		int value = currentIndex + numSteps;
		if (value > numNodes - 1)
			return value % numNodes;
		return value;
	}
	
	/*******************************************
	 * GETTER & SETTER
	 *******************************************/
	
	public Uniform getRandomDist() {
		return randomDist;
	}

	public void setRandomDist(Uniform randomDist) {
		this.randomDist = randomDist;
	}
}
