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
 * Created by Sascha Holzhauer on 13.01.2011
 */
package de.cesr.more.measures.network.supply.algos;



import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Hypergraph;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @param <V> vertex type
 * @date 13.01.2011
 * 
 */
public class MInDegreeScorer<V> implements VertexScorer<V, Integer> {

	/**
	 * The graph for which scores are to be generated.
	 */
	protected Hypergraph<V, ?>	graph;

	/**
	 * Creates an instance for the specified graph.
	 * 
	 * @param graph the input graph
	 */
	public MInDegreeScorer(Hypergraph<V, ?> graph) {
		this.graph = graph;
	}

	/**
	 * In case the graph is an instance of {@link DirectedGraph} it
	 * returns the in-degree of the vertex. Otherwise it returns the degree.
	 * 
	 * @return the (out-)degree of the vertex
	 */
	@SuppressWarnings("unchecked")
	public Integer getVertexScore(V v) {
		if (graph instanceof DirectedGraph) {
			return ((DirectedGraph) graph).inDegree(v);
		} else {
			return graph.degree(v);
		}
	}
}