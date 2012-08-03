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
 * Created by Sascha Holzhauer on 03.02.2010
 */
package de.cesr.more.rs.network;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.rs.building.edge.MRsEdgeFactory;
import de.cesr.more.rs.edge.MRepastEdge;
import edu.uci.ics.jung.graph.Graph;



/**
 * Network Adapter for Repast Simphony models.
 * Encapsulates a ContextJungNetwork.
 * 
 * TODO handle edge objects to deal with weights etc... and allow more complex edge objects. TODO error handling for
 * missing nodes
 * TODO comment
 * 
 * @author Sascha Holzhauer
 * @param <AgentT>
 * @date 03.02.2010
 * @see MRsContextJungNetwork
 * 
 */
public final class MRsEncapsulatedContextJungNetwork<AgentT, EdgeT extends RepastEdge<AgentT> & MoreEdge<AgentT>> implements
		MoreNetwork<AgentT, EdgeT> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MRsEncapsulatedContextJungNetwork.class);
	
	private final ContextJungNetwork<AgentT>	network;
	private final Context<AgentT>						context;
	
	protected MoreEdgeFactory<AgentT, EdgeT> edgeFac;
	
	public static double DEFAULT_EDGE_WEIGHT = 1.0;

	/**
	 * Adds the encapsulated network as projection to the given context.
	 * 
	 * @param network
	 *        the network that is going to be wrapped
	 */
	public MRsEncapsulatedContextJungNetwork(ContextJungNetwork<AgentT> network, Context<AgentT> context) {
		this.network = network;
		this.context = context;
		this.context.addProjection(network);
	}

	@Override
	public EdgeT connect(AgentT source, AgentT target) {
		if (edgeFac == null) {
			this.edgeFac = new MRsEdgeFactory<AgentT, EdgeT>();
		}
		EdgeT edge = this.edgeFac.createEdge(source, target, network.isDirected());
		edge.setWeight(DEFAULT_EDGE_WEIGHT);
		this.connect(edge);
		return edge;
	}


	@Override
	public EdgeT disconnect(AgentT source, AgentT target) {
		EdgeT edge = getEdge(source, target);
		network.removeEdge(edge);
		return edge;
	}


	@Override
	public Iterable<AgentT> getAdjacent(AgentT ego) {
		return network.getAdjacent(ego);
	}


	@Override
	public int getDegree(AgentT ego) {
		return network.getDegree(ego);
	}


	@Override
	public int getInDegree(AgentT ego) {
		return network.getInDegree(ego);
	}


	@Override
	public String getName() {
		return network.getName();
	}


	@Override
	public Iterable<AgentT> getNodes() {
		return network.getNodes();
	}


	@Override
	public int getOutDegree(AgentT ego) {
		return network.getOutDegree(ego);
	}


	@Override
	public Iterable<AgentT> getPredecessors(AgentT ego) {
		return network.getPredecessors(ego);
	}


	@Override
	public Iterable<AgentT> getSuccessors(AgentT ego) {
		return network.getSuccessors(ego);
	}


	@Override
	public double getWeight(AgentT source, AgentT target) {
		return network.getEdge(source, target).getWeight();
	}


	@Override
	public boolean isAdjacent(AgentT ego, AgentT alter) {
		return network.isAdjacent(ego, alter);
	}


	@Override
	public boolean isDirected() {
		return network.isDirected();
	}


	@Override
	public void normalizeWeights() {
		double maxWeight = 0;
		double current = 0;
		for (RepastEdge<AgentT> edge : network.getEdges()) {
			current = Math.abs(edge.getWeight());
			if (maxWeight < current) {
				maxWeight = edge.getWeight();
			}
		}
		for (RepastEdge<AgentT> edge : network.getEdges()) {
			edge.setWeight(edge.getWeight() / maxWeight);
		}
	}


	@Override
	public int numEdges() {
		return network.numEdges();
	}


	@Override
	public int numNodes() {
		return network.size();
	}

	@Override
	public void setWeight(AgentT source, AgentT target, double weight) {
		getEdge(source, target).setWeight(weight);
	}

	@Override
	public AgentT getRandomSuccessor(AgentT ego) {
		// TODO look for reason why normal random successor does not work (see Lara-task)!
		ArrayList<AgentT> neighbours = new ArrayList<AgentT>();
		for (AgentT a : network.getSuccessors(ego)) {
			neighbours.add(a);
		}
		Collections.sort(neighbours, new Comparator<AgentT>() {
			@Override
			public int compare(AgentT a1, AgentT a2) {
				return a1.toString().compareTo(a2.toString());
			}
		});
		return neighbours.get(RandomHelper.nextIntFromTo(0, neighbours.size() - 1));
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getRandomPredecessor(java.lang.Object)
	 */
	@Override
	public AgentT getRandomPredecessor(AgentT ego) {
		// TODO look for reason why normal random successor does not work (see Lara-task)!
		ArrayList<AgentT> neighbours = new ArrayList<AgentT>();
		for (AgentT a : network.getPredecessors(ego)) {
			neighbours.add(a);
		}
		Collections.sort(neighbours, new Comparator<AgentT>() {
			@Override
			public int compare(AgentT a1, AgentT a2) {
				return a1.toString().compareTo(a2.toString());
			}
		});
		return neighbours.get(RandomHelper.nextIntFromTo(0, neighbours.size() - 1));
	}
	
	
	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#isSuccessor(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isSuccessor(AgentT alter, AgentT ego) {
		return network.isSuccessor(alter, ego);
	}

	public ContextJungNetwork<AgentT> getNetwork() {
		return network;
	}

	@Override
	public void addNode(AgentT node) {
		network.addVertex(node);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Graph<AgentT, EdgeT> getJungGraph() {
		return (Graph<AgentT, EdgeT>) network.getGraph();
	}
	@SuppressWarnings("unchecked")
	@Override
	public EdgeT getEdge(AgentT source, AgentT target) {
		RepastEdge<AgentT> e = network.getEdge(source, target);
		
		if (e == null) {
			// <- LOGGING
			logger.error("Requested edge (" + source + " > " + target + ") does not exist!");
			throw new IllegalStateException("Requested edge (" + source + " > " + target + ") does not exist!");
			// LOGGING ->
		}
		
		if (e instanceof MRepastEdge) {
			return (EdgeT) e;
		}
		
		if (edgeFac == null) {
			this.edgeFac = new MRsEdgeFactory<AgentT, EdgeT>();
		}
		EdgeT edge = this.edgeFac.createEdge(source, target, e.isDirected());
		edge.setWeight(e.getWeight());
		this.connect(edge);
		return edge;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getEmptyInstance()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MoreNetwork<AgentT, EdgeT> getGraphFilteredInstance(Graph<AgentT, EdgeT> graph, String newName) {
		ContextJungNetwork<AgentT> jnetwork = new ContextJungNetwork<AgentT>(
				(network.isDirected() ? new DirectedJungNetwork<AgentT>(newName) : new UndirectedJungNetwork<AgentT>(
						getName())), context);
		jnetwork.setGraph(((Graph<AgentT, RepastEdge<AgentT>>) graph));
		MoreNetwork<AgentT, EdgeT> out_net = new MRsEncapsulatedContextJungNetwork<AgentT, EdgeT>(jnetwork, context);
		
		return out_net;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getName();
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#reverseNetwork()
	 */
	@Override
	public void reverseNetwork() {
		if (this.isDirected()){
			Collection<EdgeT> orgEdges = this.getEdgesCollection();
			for (EdgeT edge : orgEdges) {
				network.removeEdge(edge);
			}
			for (EdgeT edge : orgEdges) {
				network.addEdge(edge.getTarget(), edge.getSource());
			}
		}
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getEdges()
	 */
	@Override
	public Collection<EdgeT> getEdgesCollection() {
		return getJungGraph().getEdges();
	}
	
	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#connect(java.lang.Object)
	 */
	public EdgeT addEdge(AgentT source, AgentT target) {
		return this.connect(source, target);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#connect(de.cesr.more.basic.edge.MoreEdge)
	 */
	@Override
	public void connect(EdgeT edge) {
		network.addEdge(edge);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#removeNode(java.lang.Object)
	 */
	@Override
	public void removeNode(AgentT node) {
		network.removeVertex(node);
	}
	

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#containsNode(java.lang.Object)
	 */
	@Override
	public boolean containsNode(AgentT node) {
		for (AgentT n : network.getNodes())  {
			if (n.equals(node)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#setEdgeFactory(de.cesr.more.building.edge.MoreEdgeFactory)
	 */
	@Override
	public void setEdgeFactory(MoreEdgeFactory<AgentT, EdgeT> edgeFac) {
		this.edgeFac = edgeFac;
	}
}
