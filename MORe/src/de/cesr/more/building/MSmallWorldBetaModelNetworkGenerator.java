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
 * Created by Sascha Holzhauer on 17.01.2011
 */
package de.cesr.more.building;



import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections15.Factory;

import de.cesr.more.basic.MManager;
import de.cesr.more.building.util.MLattice1DGenerator;
import de.cesr.more.building.util.MoreBetaProvider;
import de.cesr.more.building.util.MoreKValueProvider;
import de.cesr.more.edges.MoreEdge;
import edu.uci.ics.jung.graph.Graph;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @param <V> 
 * @param <E> 
 * @date 17.01.2011
 * 
 */
public class MSmallWorldBetaModelNetworkGenerator<V, E extends MoreEdge<V>> extends MLattice1DGenerator<V, E> {

	MoreBetaProvider<V>		betaProvider;
	MoreKValueProvider<V>	kProvider;
	
	MoreRewireManager<V, E> 	rewireManager;


	/**
	 * @param graphFactory
	 * @param vertexFactory
	 * @param edgeFactory
	 * @param numNodes
	 * @param k_value
	 * @param isToroidal
	 * @param beta
	 */
	public MSmallWorldBetaModelNetworkGenerator(Factory<? extends Graph<V, E>> graphFactory, Factory<V> vertexFactory,
			MoreEdgeFactory<V,E> edgeFactory, int numNodes, final int k_value, boolean isToroidal, final double beta) {
		this(graphFactory, vertexFactory, edgeFactory, numNodes, new MoreKValueProvider<V>() {
			@Override
			public int getKValue(V node) {
				return k_value;
			}

		}, isToroidal, new MoreBetaProvider<V>() {
			@Override
			public double getBetaValue(V node) {
				return beta;
			}
		});
	}

	public MSmallWorldBetaModelNetworkGenerator(Factory<? extends Graph<V, E>> graphFactory, Factory<V> vertexFactory,
			MoreEdgeFactory<V, E> edgeFactory, int numNodes, MoreKValueProvider<V> kProvider, boolean isToroidal,
			MoreBetaProvider<V> betaProvider, MoreRewireManager<V, E> rewireMan) {
		super(graphFactory, vertexFactory, edgeFactory, numNodes, kProvider, isToroidal);
		this.betaProvider = betaProvider;
		this.rewireManager = rewireMan;
	}
	
	/**
	 * @param graphFactory
	 * @param vertexFactory
	 * @param edgeFactory
	 * @param numNodes
	 * @param kProvider
	 * @param isToroidal
	 * @param betaProvider
	 */
	public MSmallWorldBetaModelNetworkGenerator(Factory<? extends Graph<V, E>> graphFactory, Factory<V> vertexFactory,
			MoreEdgeFactory<V, E> edgeFactory, int numNodes, MoreKValueProvider<V> kProvider, boolean isToroidal,
			MoreBetaProvider<V> betaProvider) {
		this(graphFactory, vertexFactory, edgeFactory, numNodes, kProvider, isToroidal, betaProvider,
				new MoreRewireManager<V, E>() {
					@Override
					public V getRewireTarget(Graph<V,E> graph, V source) {
						return new ArrayList<V>(graph.getVertices()).get(
								MManager.getMRandomService().getUniform().nextIntFromTo(0, graph.getVertexCount() - 1));
					}
				});
	}
	
    @Override
	public Graph<V, E> create() {
    	Graph<V, E> graph = super.create();
    	
    	Collection<E> orgEdges = new ArrayList<E>(graph.getEdgeCount());
    	for (E edge : graph.getEdges()) {
    		orgEdges.add(edge);
    	}
    	
    	
    	// rewiring:
    	for (E edge : orgEdges) {
    		// TODO allow for custom random numbers
    		if (MManager.getMRandomService().getUniform().nextDouble() <= betaProvider.getBetaValue(edge.getStart()))  {
    			edge_factory.createEdge(edge.getStart(), rewireManager.getRewireTarget(graph, edge.getStart()), is_directed);
    			graph.removeEdge(edge);
    		}
    	}
    	
        return graph;
    }
}
