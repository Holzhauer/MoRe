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

import de.cesr.more.io.MoreEdgeFactory;
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
public class MDirectedNetwork<V,E> extends DirectedSparseGraph<V, E>implements MoreNetwork<V, E> {

	protected MoreEdgeFactory<V, E> edgeFactory = null;
	
	public MDirectedNetwork(MoreEdgeFactory<V, E> edgeFactory) {
		this.edgeFactory = edgeFactory;
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
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getAdjacent(java.lang.Object)
	 */
	@Override
	public Iterable<V> getAdjacent(V ego) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getDegree(java.lang.Object)
	 */
	@Override
	public int getDegree(V ego) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getEdge(java.lang.Object, java.lang.Object)
	 */
	@Override
	public E getEdge(V source, V target) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getGraph()
	 */
	@Override
	public Graph getGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getInDegree(java.lang.Object)
	 */
	@Override
	public int getInDegree(V ego) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getNodes()
	 */
	@Override
	public Iterable<V> getNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getOutDegree(java.lang.Object)
	 */
	@Override
	public int getOutDegree(V ego) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getRandomSuccessor(java.lang.Object)
	 */
	@Override
	public V getRandomSuccessor(V ego) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getWeight(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double getWeight(V source, V target) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#isAdjacent(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isAdjacent(V ego, V alter) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#isDirected()
	 */
	@Override
	public boolean isDirected() {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#numNodes()
	 */
	@Override
	public int numNodes() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#setWeight(java.lang.Object, java.lang.Object, double)
	 */
	@Override
	public void setWeight(V source, V target, double weight) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getEmptyInstance()
	 */
	@Override
	public MoreNetwork<V, E> getInstanceWithNewGraph(Graph<V,E> graph) {
		// TODO Auto-generated method stub
		return null;
	}

}
