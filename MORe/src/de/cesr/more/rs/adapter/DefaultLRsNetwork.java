/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 03.02.2010
 */
package de.cesr.more.rs.adapter;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.EdgeCreator;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.graph.UndirectedJungNetwork;
import sun.management.Agent;
import de.cesr.more.basic.MoreEdge;
import de.cesr.more.networks.MDirectedNetwork;
import de.cesr.more.networks.MoreNetwork;
import edu.uci.ics.jung.graph.Graph;



/**
 * Network Adapter for Repast Simphony models.
 * Encapsulates a ContextJungNetwork.
 * 
 * TODO handle edge objects to deal with weights etc... and allow more complex edge objects. TODO error handling for
 * missing nodes
 * 
 * @author Sascha Holzhauer
 * @param <AgentT>
 * @date 03.02.2010
 * 
 */
public final class DefaultLRsNetwork<AgentT, EdgeT extends RepastEdge<AgentT> & MoreEdge<AgentT>> implements
		MoreNetwork<AgentT, EdgeT> {

	private ContextJungNetwork<AgentT>	network;
	private Context						context;
	private EdgeCreator<EdgeT, AgentT>	edgeCreator	= null;
	
	public static double DEFAULT_EDGE_WEIGHT = 1.0;

	/**
	 * @param network the network that is going to be wrapped
	 */
	public DefaultLRsNetwork(ContextJungNetwork<AgentT> network, Context context) {
		this.network = network;
		this.context = context;
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#connect(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void connect(AgentT source, AgentT target) {
		if (edgeCreator != null) {
			network.addEdge(edgeCreator.createEdge(source, target, network.isDirected(), DEFAULT_EDGE_WEIGHT));
		} else {
			network.addEdge(new MRepastEdge<AgentT>(source, target, network.isDirected(), DEFAULT_EDGE_WEIGHT));
		}
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#disconnect(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void disconnect(AgentT source, AgentT target) {
		network.removeEdge(getEdge(source, target));
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#getAdjacent(java.lang.Object)
	 */
	@Override
	public Iterable<AgentT> getAdjacent(AgentT ego) {
		return network.getAdjacent(ego);
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#getDegree(java.lang.Object)
	 */
	@Override
	public int getDegree(AgentT ego) {
		return network.getDegree(ego);
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#getInDegree(java.lang.Object)
	 */
	@Override
	public int getInDegree(AgentT ego) {
		return network.getInDegree(ego);
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#getName()
	 */
	@Override
	public String getName() {
		return network.getName();
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#getNodes()
	 */
	@Override
	public Iterable<AgentT> getNodes() {
		return network.getNodes();
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#getOutDegree(java.lang.Object)
	 */
	@Override
	public int getOutDegree(AgentT ego) {
		return network.getOutDegree(ego);
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#getPredecessors(java.lang.Object)
	 */
	@Override
	public Iterable<AgentT> getPredecessors(AgentT ego) {
		return network.getPredecessors(ego);
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#getSuccessors(java.lang.Object)
	 */
	@Override
	public Iterable<AgentT> getSuccessors(AgentT ego) {
		return network.getSuccessors(ego);
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#getWeight(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double getWeight(AgentT source, AgentT target) {
		return network.getEdge(source, target).getWeight();
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#isAdjacent(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isAdjacent(AgentT ego, AgentT alter) {
		return network.isAdjacent(ego, alter);
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#isDirected()
	 */
	@Override
	public boolean isDirected() {
		return network.isDirected();
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#normalizeWeights()
	 */
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

	/**
	 * @see de.cesr.lara.components.LaraNetwork#numEdges()
	 */
	@Override
	public int numEdges() {
		return network.numEdges();
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#numNodes()
	 */
	@Override
	public int numNodes() {
		return network.size();
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#setWeight(java.lang.Object, java.lang.Object, double)
	 */
	@Override
	public void setWeight(AgentT source, AgentT target, double weight) {
		getEdge(source, target).setWeight(weight);
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#getRandomSuccessor(java.lang.Object)
	 */
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
	 * @see de.cesr.lara.components.LaraNetwork#isSuccessor(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isSuccessor(AgentT ego, AgentT alter) {
		return network.isSuccessor(alter, ego);
	}

	public ContextJungNetwork<AgentT> getNetwork() {
		return network;
	}

	public void setEdgeFactory(EdgeCreator<EdgeT, AgentT> edgeCreator) {
		this.edgeCreator = edgeCreator;
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

	@Override
	public EdgeT getEdge(AgentT source, AgentT target) {
		RepastEdge<AgentT> e = network.getEdge(source, target);
		if (e instanceof MRepastEdge) {
			return (EdgeT) e;
		}
		if (edgeCreator != null) {
			return edgeCreator.createEdge(source, target, e.isDirected(), e.getWeight());
		} else {
			return (EdgeT) new MRepastEdge<AgentT>(source, target, e.isDirected(), e.getWeight());
		}
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getEmptyInstance()
	 */
	@Override
	public MoreNetwork<AgentT, EdgeT> getGraphFilteredInstance(Graph<AgentT, EdgeT> graph, String newName) {
		ContextJungNetwork<AgentT> jnetwork = new ContextJungNetwork<AgentT>(
				(network.isDirected() ? new DirectedJungNetwork<AgentT>(newName) : new UndirectedJungNetwork<AgentT>(
						getName())), context);
		jnetwork.setGraph(((Graph<AgentT, RepastEdge<AgentT>>) graph));
		MoreNetwork<AgentT, EdgeT> out_net = new DefaultLRsNetwork<AgentT, EdgeT>(jnetwork, context);
		
		return out_net;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.getName();
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#reverseNetwork()
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
	 * @see de.cesr.more.networks.MoreNetwork#getEdges()
	 */
	@Override
	public Collection<EdgeT> getEdgesCollection() {
		return getJungGraph().getEdges();
	}
}
