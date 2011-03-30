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

import java.util.Collection;
import java.util.NoSuchElementException;

import de.cesr.more.basic.MoreEdge;
import de.cesr.more.building.MoreEdgeFactory;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

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

	protected MoreEdgeFactory<V, E> edgeFactory = null;
	protected String 				name;

	/**
	 * @deprecated (used to build new instances by JUNG...)
	 */
	public MDirectedNetwork() {
		this.edgeFactory = null;
		this.name = "NN";
	}

	public MDirectedNetwork(MoreEdgeFactory<V, E> edgeFactory, String name) {
		this.edgeFactory = edgeFactory;
		this.name = name;
	}

	
	/**
	 * @param <V>
	 * @param <E>
	 * @param edgeFactory
	 * @param graph
	 * @return
	 * Created by Sascha Holzhauer on 10.12.2010
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
	public void connect(V source, V target) {
		if (edgeFactory == null) {
			throw new IllegalStateException("No edge factory specified");
		}
		this.addEdge(edgeFactory.createEdge(source, target, true), source, target);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#disconnect(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void disconnect(V source, V target) {
		this.removeEdge(findEdge(source, target));
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
	public Graph getJungGraph() {
		return (DirectedSparseGraph) this;
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
		// TODO implement
		return null;
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
		// TODO Auto-generated method stub
		
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
	 * @see de.cesr.more.networks.MoreNetwork#addEdge(de.cesr.more.basic.MoreEdge)
	 */
	@Override
	public void addEdge(E edge) {
		super.addEdge(edge, edge.getStart(), edge.getEnd());
	}
}
