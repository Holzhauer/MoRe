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
import org.apache.log4j.Logger;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.parma.core.PmParameterManager;
import edu.uci.ics.jung.algorithms.generators.GraphGenerator;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;


/**
 * MORe
 * 
 * Considers {@link MNetworkBuildingPa.BUILD_WSSM_CONSIDER_SOURCES} (this class is mainly used by
 * {@link MSmallWorldBetaModelNetworkGenerator}).
 * 
 * NOTE: For undirected networks, the k provider values are not fully respected (because of links from other nodes that
 * increase in-degree)
 * 
 * TODO (see NOTE, also check if it is possible to distribute links across nodes to satisfy k provider values)
 * 
 * @author Sascha Holzhauer
 * @date 24.01.2011
 * 
 */
public class MLattice1DGenerator<V, E extends MoreEdge<? super V>> implements GraphGenerator<V, E> {
	protected int								numVertices;
	protected MoreKValueProvider<V>				kProvider;
	protected boolean							is_toroidal;
	protected boolean							is_directed;
	protected boolean							is_symmetrical;
	protected Factory<? extends MoreNetwork<V, E>>	graph_factory;
	protected Factory<V>						vertex_factory;
	protected MoreNetworkEdgeModifier<V, E>			edge_modifier;
	protected boolean							considerSource;
	private List<V>								v_array;
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MSmallWorldBetaModelNetworkGenerator.class);

	/**
	 * Constructs a generator of square lattices of size {@code latticeSize} with the specified parameters.
	 * 
	 * @param graph_factory
	 *        used to create the {@code Graph} for the lattice
	 * @param vertex_factory
	 *        used to create the lattice vertices (also defines order of vertices)
	 * @param edge_modifier
	 *        used to create the lattice edges
	 * @param numVertices
	 * @param kProvider
	 * @param isToroidal
	 *        if true, the created lattice wraps from top to bottom and left to right
	 * @param is_symmetrical
	 *        if true, for every link another link of reverse direction is created
	 */
	public MLattice1DGenerator(Factory<? extends MoreNetwork<V, E>> graph_factory, Factory<V> vertex_factory,
			MoreNetworkEdgeModifier<V, E> edge_modifier, int numVertices, MoreKValueProvider<V> kProvider,
			boolean isToroidal,
			boolean is_symmetrical) {
		if (numVertices < 2) {
			throw new IllegalArgumentException("Lattice size counts must each be at least 2.");
		}

		this.numVertices = numVertices;
		this.kProvider = kProvider;
		this.is_toroidal = isToroidal;
		this.graph_factory = graph_factory;
		this.vertex_factory = vertex_factory;
		this.edge_modifier = edge_modifier;
		this.is_directed = (graph_factory.create() instanceof DirectedGraph);
		this.is_symmetrical = is_symmetrical;
		this.considerSource = (Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_WSSM_CONSIDER_SOURCES);
	}

	/**
	 * @see edu.uci.ics.jung.algorithms.generators.GraphGenerator#create()
	 */
	public MoreNetwork<V, E> createMoreNetwork() {
		int vertex_count = numVertices;
		MoreNetwork<V, E> graph = graph_factory.create();
		v_array = new ArrayList<V>(vertex_count);
		for (int i = 0; i < vertex_count; i++) {
			V v = vertex_factory.create();
			graph.addNode(v);
			v_array.add(i, v);
		}

		// int start = is_toroidal ? 0 : 1;
		// int end_row = is_toroidal ? lattice_size : lattice_size - 1;

		int start, end;

		// fill in edges
		// clockwise:
		for (int i = 0; i < numVertices; i++) {
			// degree <= |nodes| !
			if (kProvider.getKValue(v_array.get(i)) > graph.numNodes()) {
				String msg = "Degree/K value (" + kProvider.getKValue(v_array.get(i))
						+ ") may not exceed the number of nodes ("
						+ graph.numNodes() + ")";
				logger.error(msg);
				throw new IllegalStateException(msg);
			}
			for (int j = 1; j <= Math.ceil(kProvider.getKValue(v_array.get(i)) * 0.5); j++) {
				if (is_toroidal || (!is_toroidal && i + j < numVertices)) {
					start = this.considerSource ? i : i + j;
					end = this.considerSource ? i + j : i;
					edge_modifier.createEdge(graph, getVertex(getIndex(start)), getVertex(getIndex(end)));
					if (is_symmetrical) {
						edge_modifier.createEdge(graph, getVertex(getIndex(end)), getVertex(getIndex(start)));
					}
				}
			}
		}

		// if the graph is directed, fill in the edges going the other direction...
		if (graph.isDirected()) {
			// counter-clockwise
			for (int i = 0; i < numVertices; i++) {
				for (int j = 1; j <= kProvider.getKValue(v_array.get(i)) * 0.5; j++) {
					if (is_toroidal || (!is_toroidal && i - j >= 0)) {
						start = this.considerSource ? i : i - j;
						end = this.considerSource ? i - j : i;
						edge_modifier.createEdge(graph, getVertex(getIndex(start)), getVertex(getIndex(end)));
						if (is_symmetrical) {
							edge_modifier.createEdge(graph, getVertex(getIndex(end)), getVertex(getIndex(start)));
						}
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

	/**
	 * @see org.apache.commons.collections15.Factory#create()
	 */
	@Override
	public Graph<V, E> create() {
		return createMoreNetwork().getJungGraph();
	}
}