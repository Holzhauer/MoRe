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
 * Created by Sascha Holzhauer on 24.01.2011
 */
package de.cesr.more.building.util;



import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Factory;

import de.cesr.more.building.MoreEdgeFactory;

import edu.uci.ics.jung.algorithms.generators.GraphGenerator;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 24.01.2011
 * 
 */
public class MLattice1DGenerator<V, E> implements GraphGenerator<V, E> {
	protected int								numVertices;
	protected MoreKValueProvider<V>				kProvider;
	protected boolean							is_toroidal;
	protected boolean							is_directed;
	protected Factory<? extends Graph<V, E>>	graph_factory;
	protected Factory<V>						vertex_factory;
	protected MoreEdgeFactory<V,E>				edge_factory;
	private List<V>								v_array;

	/**
	 * Constructs a generator of square lattices of size {@code latticeSize} with the specified parameters.
	 * 
	 * @param graph_factory used to create the {@code Graph} for the lattice
	 * @param vertex_factory used to create the lattice vertices
	 * @param edge_factory used to create the lattice edges
	 * @param lattice_size
	 * @param kProvider
	 * @param isToroidal if true, the created lattice wraps from top to bottom and left to right
	 */
	public MLattice1DGenerator(Factory<? extends Graph<V, E>> graph_factory, Factory<V> vertex_factory,
			MoreEdgeFactory<V,E> edge_factory, int lattice_size, MoreKValueProvider<V> kProvider, boolean isToroidal) {
		if (lattice_size < 2) {
			throw new IllegalArgumentException("Lattice size counts must each be at least 2.");
		}

		this.numVertices = lattice_size;
		this.kProvider = kProvider;
		this.is_toroidal = isToroidal;
		this.graph_factory = graph_factory;
		this.vertex_factory = vertex_factory;
		this.edge_factory = edge_factory;
		this.is_directed = (graph_factory.create() instanceof DirectedGraph);
	}

	/**
	 * @see edu.uci.ics.jung.algorithms.generators.GraphGenerator#create()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Graph<V, E> create() {
		int vertex_count = numVertices;
		Graph<V, E> graph = graph_factory.create();
		v_array = new ArrayList<V>(vertex_count);
		for (int i = 0; i < vertex_count; i++) {
			V v = vertex_factory.create();
			graph.addVertex(v);
			v_array.add(i, v);
		}

		// int start = is_toroidal ? 0 : 1;
		// int end_row = is_toroidal ? lattice_size : lattice_size - 1;

		// fill in edges
		// clockwise:
		for (int i = 1; i <= numVertices; i++) {
			for (int j = 1; j <= kProvider.getKValue(v_array.get(i - 1)); j++) {
				if (is_toroidal || (!is_toroidal && i + j <= numVertices)) {
					graph.addEdge(edge_factory.createEdge(getVertex(getIndex(i)), getVertex(getIndex(i + j)), this.is_directed), 
							getVertex(getIndex(i)), getVertex(getIndex(i + j)));
				}
			}
		}

		// if the graph is directed, fill in the edges going the other direction...
		if (graph instanceof DirectedGraph) {
			// counter-clockwise
			for (int i = 1; i <= numVertices; i++) {
				for (int j = 1; j <= kProvider.getKValue(v_array.get(i - 1)); j++) {
					if (is_toroidal || (!is_toroidal && i - j >= 0)) {
						graph.addEdge(edge_factory.createEdge(getVertex(getIndex(i)), getVertex(getIndex(i - j)), this.is_directed), 
								getVertex(getIndex(i)), getVertex(getIndex(i - j)));
					}
				}
			}
		}

		return graph;
	}

	protected int getIndex(int i) {
		return ((mod(i, numVertices)));
	}

	protected int mod(int i, int modulus) {
		int i_mod = i % modulus;
		return i_mod >= 0 ? i_mod : i_mod + modulus;
	}

	/**
	 * Returns the {@code i}th vertex (counting row-wise).
	 */
	protected V getVertex(int i) {
		return v_array.get(i);
	}
}
