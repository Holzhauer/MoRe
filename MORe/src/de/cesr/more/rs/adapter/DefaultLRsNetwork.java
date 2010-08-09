/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 03.02.2010
 */
package de.cesr.more.rs.adapter;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.RepastEdge;
import de.cesr.more.networks.MoreNetwork;



/**
 * Network Adapter for Repast Simphony models
 * 
 * @author Sascha Holzhauer
 * @param <AgentT>
 * @date 03.02.2010
 * 
 */
public final class DefaultLRsNetwork<AgentT> implements MoreNetwork<AgentT> {

	private ContextJungNetwork<AgentT>	network;

	/**
	 * @param network
	 *            the network that is going to be wrapped
	 */
	public DefaultLRsNetwork(ContextJungNetwork<AgentT> network) {
		this.network = network;
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#connect(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void connect(AgentT source, AgentT target) {
		network.addEdge(source, target);
	}

	/**
	 * @see de.cesr.lara.components.LaraNetwork#disconnect(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void disconnect(AgentT source, AgentT target) {
		network.removeEdge(network.getEdge(source, target));
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
		network.getEdge(source, target).setWeight(weight);
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

}
