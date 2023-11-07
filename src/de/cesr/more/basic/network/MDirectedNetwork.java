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
package de.cesr.more.basic.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import cern.jet.random.Uniform;
import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MoreNetworkBuilder;
import de.cesr.more.measures.network.supply.algos.MNetworkStatisticsR;
import de.cesr.more.param.MRandomPa;
import de.cesr.parma.core.PmParameterManager;
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
	 * Logger
	 */
	static private Logger								logger				= Logger.getLogger(MDirectedNetwork.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 5626154871945249535L;
	protected MoreEdgeFactory<V, E> edgeFactory = null;
	protected String 				name;
	
	protected Uniform 				randomNodeSelectionStream;

	protected Class<? extends MoreNetworkBuilder<?, ?>>	networkBuilderClass	= MNetworkBuilderNotSpecified.class;


	/**
	 * @deprecated (used to build new instances by JUNG...)
	 */
	@Deprecated
	public MDirectedNetwork() {
		this.edgeFactory = null;
		this.name = "NN";
		this.randomNodeSelectionStream = MManager.getURandomService().getNewUniformDistribution(
				MManager.getURandomService().getGenerator(
						(String) PmParameterManager.getParameter(MRandomPa.RND_STREAM)));
	}

	/**
	 * @param edgeFactory
	 * @param name
	 */
	public MDirectedNetwork(MoreEdgeFactory<V, E> edgeFactory, String name) {
		this.edgeFactory = edgeFactory;
		this.name = name;
		this.randomNodeSelectionStream = MManager.getURandomService().getNewUniformDistribution(
				MManager.getURandomService().getGenerator(
						(String) PmParameterManager.getParameter(MRandomPa.RND_STREAM)));
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
	 * @see de.cesr.more.basic.network.MoreNetwork#addNode(java.lang.Object)
	 */
	@Override
	public void addNode(V node) {
		this.addVertex(node);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#connect(java.lang.Object, java.lang.Object)
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
	 * @see de.cesr.more.basic.network.MoreNetwork#disconnect(java.lang.Object, java.lang.Object)
	 */
	@Override
	public E disconnect(V source, V target) {
		E edge = findEdge(source, target);
		this.removeEdge(edge);
		return edge;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getAdjacent(java.lang.Object)
	 */
	@Override
	public Iterable<V> getAdjacent(V ego) {
		return this.getNeighbors(ego);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getDegree(java.lang.Object)
	 */
	@Override
	public int getDegree(V ego) {
		return getNeighborCount(ego);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getEdge(java.lang.Object, java.lang.Object)
	 */
	@Override
	public E getEdge(V source, V target) {
		return this.findEdge(source, target);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getGraph()
	 */
	@Override
	public Graph<V, E> getJungGraph() {
		return this;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getInDegree(java.lang.Object)
	 */
	@Override
	public int getInDegree(V ego) {
		return this.getPredecessorCount(ego);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getNodes()
	 */
	@Override
	public Iterable<V> getNodes() {
		return this.vertices.keySet();
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getOutDegree(java.lang.Object)
	 */
	@Override
	public int getOutDegree(V ego) {
		return getSuccessorCount(ego);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getRandomSuccessor(java.lang.Object)
	 */
	@Override
	public V getRandomSuccessor(V ego) {
		return new ArrayList<V>(getSuccessors(ego)).get(randomNodeSelectionStream.nextIntFromTo(0, 
				getSuccessorCount(ego)));
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getRandomPredecessor(java.lang.Object)
	 */
	@Override
	public V getRandomPredecessor(V ego) {
		return getPredecessorCount(ego) > 0 ? new ArrayList<V>(getPredecessors(ego)).get(randomNodeSelectionStream.nextIntFromTo(0, 
				getPredecessorCount(ego))): null;
	}
	
	@Override
	public Collection<V> getSuccessors(V ego) {
		checkVertex(ego);
		return super.getSuccessors(ego);
	}

	/**
	 * @param ego
	 */
	protected void checkVertex(V ego) {
		if (!this.containsVertex(ego)) {
			logger.error("Network " + this.getName() + " does not contain vertex " + ego + "!");
			throw new IllegalStateException("Network " + this.getName() + " does not contain vertex " + ego + "!");
		}
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getWeight(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double getWeight(V source, V target) {
		return findEdge(source, target).getWeight();
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#isAdjacent(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isAdjacent(V ego, V alter) {
		return this.isNeighbor(ego, alter);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#isDirected()
	 */
	@Override
	public boolean isDirected() {
		return true;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#normalizeWeights()
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
	 * @see de.cesr.more.basic.network.MoreNetwork#numEdges()
	 */
	@Override
	public int numEdges() {
		return this.getEdgeCount();
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#numNodes()
	 */
	@Override
	public int numNodes() {
		return this.getVertexCount();
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#setWeight(java.lang.Object, java.lang.Object, double)
	 */
	@Override
	public void setWeight(V source, V target, double weight) {
		this.findEdge(source, target).setWeight(weight);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getEmptyInstance()
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
	 * @see de.cesr.more.basic.network.MoreNetwork#reverseNetwork()
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
	 * @see de.cesr.more.basic.network.MoreNetwork#getEdgesCollection()
	 */
	@Override
	public Collection<E> getEdgesCollection() {
		return this.getEdges();
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#connect(de.cesr.more.basic.edge.MoreEdge)
	 */
	@Override
	public void connect(E edge) {
		super.addEdge(edge, edge.getStart(), edge.getEnd());
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#removeNode(java.lang.Object)
	 */
	@Override
	public void removeNode(V node) {
		super.removeVertex(node);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#setEdgeFactory(de.cesr.more.building.edge.MoreEdgeFactory)
	 */
	@Override
	public void setEdgeFactory(MoreEdgeFactory<V, E> edgeFac) {
		this.edgeFactory = edgeFac;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#containsNode(java.lang.Object)
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

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getNetworkBuilderClass()
	 */
	@Override
	public Class<? extends MoreNetworkBuilder<?, ?>> getNetworkBuilderClass() {
		return this.networkBuilderClass;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#setNetworkBuilderClass(java.lang.Class)
	 */
	@Override
	public void setNetworkBuilderClass(Class<? extends MoreNetworkBuilder<?, ?>> builderClass) {
		this.networkBuilderClass = builderClass;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getNetworkInfo()
	 */
	@Override
	public String getNetworkInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Network " + this.getName() + ":" + System.getProperty("line.separator"));
		buffer.append("Number of Nodes: " + this.numNodes() + System.getProperty("line.separator"));
		buffer.append("Number of Edges: " + this.numEdges() + System.getProperty("line.separator"));
		buffer.append("Directedness: " + (this.isDirected() ? " Directed" : "Undirected")
				+ System.getProperty("line.separator"));
		buffer.append("APL: " + MNetworkStatisticsR.getAveragepathLengthR(this.getJungGraph(), false));
		return buffer.toString();
	}
}
