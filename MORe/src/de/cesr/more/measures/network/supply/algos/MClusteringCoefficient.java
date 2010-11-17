/**
 * RS_SoNetA
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 12.04.2010
 */
package de.cesr.more.measures.network.supply.algos;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.uci.ics.jung.graph.Graph;
import de.cesr.more.util.Log4jLogger;

/**
 * RS_SoNetA
 *
 * @author Sascha Holzhauer
 * @date 12.04.2010 
 *
 */
public class MClusteringCoefficient {
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MClusteringCoefficient.class);

	/**
	 * Calculates the clustering coefficient (also known as transitivity) for the given graph.
	 * 
	 * Vertices with less than two neighbours contribute 0.0.
	 * See, for instance, Vega-Redondo2007, p. 33
	 * 
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return the clustering coefficient
	 * Created by Sascha Holzhauer on 12.04.2010
	 */
	public static <V, E> double getClusteringCoefficientPerNode(Graph<V, E> graph) {
		logger.info("Calculate Clustering Coefficient per Node for a graph containing " + graph.getVertexCount() + " nodes.");
		double sum = 0.0;
		double vsum;
		for (V v : graph.getVertices()) {
			vsum = 0.0;
			Collection<V> nb = graph.getNeighbors(v);
			ArrayList<V> neighbours = new ArrayList<V>(nb);
			if (neighbours.size() >= 2) {
				for (int i = 0; i < neighbours.size(); i++) {
					for (int j = 0; j < i; j++) {
						if (graph.isNeighbor(neighbours.get(i), neighbours.get(j))) {
							vsum++;
						}
					}
				}
				sum = sum + (vsum / ((neighbours.size() * (neighbours.size() - 1)) / 2));
			}
		}
		return sum / graph.getVertexCount();
	}
	
	/**
	 * Calculates the clustering coefficient (also known as transitivity) for the given graph.
	 * 
	 * Vertices with less than two neighbours contribute not at all.
	 * See, for instance, Vega-Redondo2007, p. 33
	 * 
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return the clustering coefficient
	 * Created by Sascha Holzhauer on 12.04.2010
	 */
	public static <V, E> double getClusteringCoefficientPerNodeWithNeighbors(Graph<V, E> graph) {
		logger.info("Calculate Clustering Coefficient per Node with Neighbors for a graph containing " + graph.getVertexCount() + " nodes.");
		double sum = 0.0;
		double vsum;
		int lonelyCounter = 0;
		for (V v : graph.getVertices()) {
			vsum = 0.0;
			Collection<V> nb = graph.getNeighbors(v);
			ArrayList<V> neighbours = new ArrayList<V>(nb);
			for (int i = 0; i < neighbours.size(); i++) {
				for (int j = 0; j < i; j++) {
					if (graph.isNeighbor(neighbours.get(i), neighbours.get(j))) {
						vsum++;
					}
				}
			}

			if (neighbours.size() > 1) {
				sum = sum + (vsum / ((neighbours.size() * (neighbours.size() - 1)) / 2));
			}
			else {
				lonelyCounter++;
			}
		}
		return sum / (graph.getVertexCount() - (double)lonelyCounter);
	}
	
	/**
	 * Calculates the clustering coefficient (also known as transitivity) for the given graph.
	 * 
	 * TODO check why this leads to different results than R implementation!
	 * > the formula in Vega-Rodondo2007 seems to be wrong!
	 * 
	 * The ration of all triples and all triangles - gives more weight to high-degree nodes.
	 * Straight implementation of e.g. Vega-Rodondo2007 p. 34
	 * (Vertices with less than two neighbours contribute not at all.)
	 * 
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return the clustering coefficient
	 * Created by Sascha Holzhauer on 12.04.2010
	 */
	public static <V, E> double getClusteringCoefficientOverallRatio(Graph<V, E> graph) {
		logger.info("Calculate Clustering Coefficient overall ration for a graph containing " + graph.getVertexCount() + " nodes.");
		double vsum = 0.0;
		int counter = 0;
		
		Collection<V> a = graph.getVertices();
		ArrayList<V> all = new ArrayList<V>(a);
		
		for (int i = 0; i < all.size(); i++) {
			for (int j = i + 1; j < all.size(); j++) {
				for (int k = j + 1; k < all.size(); k++) {
					if (graph.isNeighbor(all.get(i), all.get(j)) && graph.isNeighbor(all.get(i), all.get(k)) ) {
						counter++;
						if (graph.isNeighbor(all.get(j), all.get(k))) {
							vsum++;
						}
					}
				}
			}
		}
		return vsum / (double) counter;
	}
	
	/**
	 * Calculates the clustering coefficient (also known as transitivity) for the given graph.
	 * 
	 * The algorithm conforms the one implemented in igraph (R library). It counts the overall number of
	 * triples and calculated the ratio of triples and triangles.
	 * 
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return the clustering coefficient
	 * Created by Sascha Holzhauer on 12.04.2010
	 */
	public static <V, E> double getClusteringCoefficientR(final Graph<V, E> graph) {
		logger.info("Calculate Clustering Coefficient (R) for a graph containing " + graph.getVertexCount() + " nodes.");
		int triangels = 0;
		int triples = 0;
		
		Collection<V> a = graph.getVertices();
		ArrayList<V> sorted = new ArrayList<V>(a);
		Collections.sort(sorted, new Comparator<V>() {
			@Override
			public int compare(V arg0, V arg1) {
				return graph.getNeighborCount(arg0) - graph.getNeighborCount(arg1);
			}
		});
		Map<V, V> neighbors = new HashMap<V, V>();
		
		Map<V, Integer> ranks = new HashMap<V, Integer>();
		for (int i = 0 ; i < sorted.size(); i++) {
			ranks.put(sorted.get(i), new Integer(i));
		}
		
		for (V node : sorted) {
			triples += graph.getNeighborCount(node) * (graph.getNeighborCount(node) - 1);
			for (V neighbor : graph.getNeighbors(node)) {
				neighbors.put(neighbor, node);
			}
			for (V neighbor : graph.getNeighbors(node)) {
				if (ranks.get(neighbor).intValue() > ranks.get(node).intValue()) {
					for (V neighbor2 : graph.getNeighbors(neighbor)) {
						if (neighbors.get(neighbor2) == node) {
							triangels++;
						}	
					}
				}
			}
		}
		if (triples == 0) {
			return 0.0;
		}
		return (double) triangels / (double) triples * 2.0;
	}
	
	/**
	 * Calculates the clustering coefficient (also known as transitivity) for the given graph.
	 * 
	 * 
	 * The ration of all triples and all triangles - gives more weight to high-degree nodes.
	 * Straight forward according to Goyal2009 p. 19
	 * (Vertices with less than two neighbours contribute not at all.)
	 * Corresponds to R implementation.
	 * 
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return the clustering coefficient
	 * Created by Sascha Holzhauer on 12.04.2010
	 */
	public static <V, E> double getClusteringCoefficientWeightedRatio(Graph<V, E> graph) {
		logger.info("Calculate Clustering Coefficient weighted ration for a graph containing " + graph.getVertexCount() + " nodes.");
		int triples = 0, triangles = 0;
		
		Collection<V> a = graph.getVertices();
		ArrayList<V> all = new ArrayList<V>(a);
		ArrayList<V> least2Neibors = new ArrayList<V>();
		for (V v : all) {
			if (graph.getNeighborCount(v) > 1) {
				least2Neibors.add(v);
			}
		}
		
		for (V node : least2Neibors) {
			for (V neighbor1 : graph.getNeighbors(node)) {
				for (V neighbor2 : graph.getNeighbors(node)) {
					if (graph.isNeighbor(neighbor1, neighbor2)) {
						triples++;
					}
				}
			}
			triangles += graph.getNeighborCount(node) * (graph.getNeighborCount(node) - 1);
		}
		return (double) triples / (double) triangles;
	}
	
	/**
	 * Does not consider the ego itself...
	 * As described e.g. in Watts1998, corresponds to Vega-Rodondo2007 (less efficient)
	 * (Vertices with less than two neighbours contribute 0.0.)
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return
	 * Created by Sascha Holzhauer on 06.05.2010
	 */
	public static <V, E> double getClusteringCoefficientWS(Graph<V, E> graph) {
		logger.info("Calculate Clustering Coefficient after Watts/Strogatz for a graph containing " + graph.getVertexCount() + " nodes.");
		double sum = 0.0;
		double vsum = 0.0;
		for (V v : graph.getVertices()) {
			for (V n : graph.getNeighbors(v)) {
				for (V m : graph.getNeighbors(v)) {
					if ( true ) {
						if (graph.isNeighbor(m, n)) vsum++;
					}
				}
			}
			sum +=  vsum / (double)((graph.getNeighborCount(v)) * ((double) graph.getNeighborCount(v) - 1.0));
			vsum = 0;
		}
		return sum / (double) graph.getVertexCount();
	}
}
