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
 * Created by Sascha Holzhauer on 16.11.2010
 */
package de.cesr.more.networks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

import cern.jet.random.Uniform;

import de.cesr.more.building.MoreEdgeFactory;
import de.cesr.more.edges.MoreEdge;
import de.cesr.uranus.core.URandomService;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * MORe
 * 
 * TODO implement!
 *
 * @author Sascha Holzhauer
 * @date 16.11.2010 
 *
 */
public class MDirectedNetwork<V,E extends MoreEdge<V>> extends DirectedSparseGraph<V, E> implements MoreNetwork<V, E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5626154871945249535L;
	protected MoreEdgeFactory<V, E> edgeFactory = null;
	protected String 				name;
	
	protected Uniform 				randomNodeSelectionStream;



	/**
	 * @deprecated (used to build new instances by JUNG...)
	 */
	@Deprecated
	public MDirectedNetwork() {
		this.edgeFactory = null;
		this.name = "NN";
		this.randomNodeSelectionStream = URandomService.getURandomService().getUniform();
	}

	public MDirectedNetwork(MoreEdgeFactory<V, E> edgeFactory, String name) {
		this.edgeFactory = edgeFactory;
		this.name = name;
		this.randomNodeSelectionStream = URandomService.getURandomService().getUniform();
	}

	
	/**
	 * Adds nodes and edges from the given graph to a new {@link MDirectedNetwork}.
	 * Uses the edge factory to create new edges. Does not assign the previous edge weights from the graph.
	 * @param <V>
	 * @param <E>
	 * @param edgeFactory
	 * @param graph
	 * @return the new network
	 */
	public static <V, E extends MoreEdge<V>> MDirectedNetwork<V, E> getNewNetwork(MoreEdgeFactory<V, E> edgeFactory,
			DirectedGraph<V, E> graph, String name) {
		MDirectedNetwork<V, E> net = new MDirectedNetwork<V, E>(edgeFactory, name);
		for (V v : graph.getVertices()) {
			net.addNode(v);
		}
		for (E e : graph.getEdges()) {
			E edge = edgeFactory.createEdge(e.getStart(), e.getEnd(), true);
			net.addEdge(edge, e.getStart(), e.getEnd());
		}
		return net;
	}
	
	/**
	 * Adds nodes and edges from the given graph to a new {@link MDirectedNetwork}.
	 * Uses the edge objects from the given graph.
	 * @param <V>
	 * @param <E>
	 * @param edgeFactory
	 * @param graph
	 * @return the new network
	 */
	public static <V, E extends MoreEdge<V>> MDirectedNetwork<V, E> getNetwork(MoreEdgeFactory<V, E> edgeFactory,
			DirectedGraph<V, E> graph, String name) {
		MDirectedNetwork<V, E> net = new MDirectedNetwork<V, E>(edgeFactory, name);
		for (V v : graph.getVertices()) {
			net.addNode(v);
		}
		for (E e : graph.getEdges()) {
			net.addEdge(e, e.getStart(), e.getEnd());
		}
		return net;
	}
	
	/**
	 * @see de.cesr.more.networks.MoreNetwork#addNode(java.lang.Object)
	 */
	@Override
	public void addNode(V node) {
		this.addVertex(node);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#connect(java.lang.Object, java.lang.Object)
	 */
	@Override
	public E connect(V source, V target) {
		if (edgeFactory == null) {
			throw new IllegalStateException("No edge factory specified");
		}
		E edge = edgeFactory.createEdge(source, target, true);
		this.addEdge(edge, source, target);
		return edge;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#disconnect(java.lang.Object, java.lang.Object)
	 */
	@Override
	public E disconnect(V source, V target) {
		E edge = findEdge(source, target);
		this.removeEdge(edge);
		return edge;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getAdjacent(java.lang.Object)
	 */
	@Override
	public Iterable<V> getAdjacent(V ego) {
		return this.getNeighbors(ego);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getDegree(java.lang.Object)
	 */
	@Override
	public int getDegree(V ego) {
		return getNeighborCount(ego);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getEdge(java.lang.Object, java.lang.Object)
	 */
	@Override
	public E getEdge(V source, V target) {
		return this.findEdge(source, target);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getGraph()
	 */
	@Override
	public Graph<V, E> getJungGraph() {
		return this;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getInDegree(java.lang.Object)
	 */
	@Override
	public int getInDegree(V ego) {
		return this.getPredecessorCount(ego);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getNodes()
	 */
	@Override
	public Iterable<V> getNodes() {
		return this.vertices.keySet();
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getOutDegree(java.lang.Object)
	 */
	@Override
	public int getOutDegree(V ego) {
		return getSuccessorCount(ego);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getRandomSuccessor(java.lang.Object)
	 */
	@Override
	public V getRandomSuccessor(V ego) {
		return new ArrayList<V>(getSuccessors(ego)).get(randomNodeSelectionStream.nextIntFromTo(0, getSuccessorCount(ego)));
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getWeight(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double getWeight(V source, V target) {
		return findEdge(source, target).getWeight();
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#isAdjacent(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isAdjacent(V ego, V alter) {
		return this.isNeighbor(ego, alter);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#isDirected()
	 */
	@Override
	public boolean isDirected() {
		return true;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#normalizeWeights()
	 */
	@Override
	public void normalizeWeights() {
		// determine maximum weight
		double maxWeight = Double.MIN_VALUE;
		for (E edge : getEdges()) {
			maxWeight = Math.max(maxWeight, edge.getWeight());
		}
		
		// divide all weights by maximum weight
		for (E edge : getEdges()) {
			edge.setWeight(edge.getWeight() / maxWeight);
		}
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#numEdges()
	 */
	@Override
	public int numEdges() {
		return this.getEdgeCount();
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#numNodes()
	 */
	@Override
	public int numNodes() {
		return this.getVertexCount();
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#setWeight(java.lang.Object, java.lang.Object, double)
	 */
	@Override
	public void setWeight(V source, V target, double weight) {
		this.findEdge(source, target).setWeight(weight);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getEmptyInstance()
	 */
	@Override
	public MoreNetwork<V, E> getGraphFilteredInstance(Graph<V,E> graph, String newName) {
		MDirectedNetwork<V, E> subnet = new MDirectedNetwork<V, E>(edgeFactory, newName);
		for (V v : graph.getVertices()) {
			if (!this.containsVertex(v)) {
				throw new NoSuchElementException("Original network does not contain " + v);
			}
			subnet.addNode(v);
		}
		for (E e : graph.getEdges()) {
			if (!this.containsEdge(e)) {
				throw new NoSuchElementException("Original network does not contain " + e);
			}
			subnet.addEdge(e, e.getStart(), e.getEnd());
		}
		return subnet;
	}
	
	/**
	 * @see edu.uci.ics.jung.graph.AbstractGraph#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#reverseNetwork()
	 */
	@Override
	public void reverseNetwork() {
		
		if (!this.isDirected()) {
			Collection<E> orgEdges = this.getEdgesCollection();
			for (E edge : orgEdges) {
				this.removeEdge(edge);
			}
			for (E edge : orgEdges) {
				this.connect(edge.getEnd(), edge.getStart());
			}
		}
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getEdgesCollection()
	 */
	@Override
	public Collection<E> getEdgesCollection() {
		return this.getEdges();
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#connect(de.cesr.more.edges.MoreEdge)
	 */
	@Override
	public void connect(E edge) {
		super.addEdge(edge, edge.getStart(), edge.getEnd());
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#removeNode(java.lang.Object)
	 */
	@Override
	public void removeNode(V node) {
		super.removeVertex(node);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#setEdgeFactory(de.cesr.more.building.MoreEdgeFactory)
	 */
	@Override
	public void setEdgeFactory(MoreEdgeFactory<V, E> edgeFac) {
		this.edgeFactory = edgeFac;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#containsNode(java.lang.Object)
	 */
	@Override
	public boolean containsNode(V node) {
		return super.containsVertex(node);
	}
	
	/************************************************
	 * GETTER & SETTER
	 ************************************************/
	
	/**
	 * @return random stream for random node selection
	 */
	public Uniform getRandomNodeSelectionStream() {
		return randomNodeSelectionStream;
	}

	/**
	 * @param randomNodeSelectionStream random stream for random node selection
	 */
	public void setRandomNodeSelectionStream(Uniform randomNodeSelectionStream) {
		this.randomNodeSelectionStream = randomNodeSelectionStream;
	}
}
