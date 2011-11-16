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
 * Created by Sascha Holzhauer on 14.11.2011
 */
package de.cesr.more.measures.node;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.measures.util.MRService;
import edu.uci.ics.jung.graph.Graph;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 14.11.2011 
 *
 */
public class MVertexSimilarityMeasureCalculator {
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MVertexSimilarityMeasureCalculator.class);
	
	/**
	 * Calculates similarities between vertices.
	 * Mode gives the type of neighboring vertices to use for the calculation, possible values:
	 * ‘out’, ‘in’, ‘all’.
	 * 
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return
	 */
	public static <V, E extends MoreEdge<? super V>> Map<V, Map<V, Double>> getVertexSimilaritiesR(
			final Graph<V, E> graph, String version, String mode) {
		logger.info("Calculate Average Path Length (R) for clusters in a graph containing " + graph.getVertexCount() + " nodes.");
		
		Map<V, Map<V, Double>> similarities = new HashMap<V, Map<V, Double>>();
		
		if (graph.getEdgeCount() == 0)  {
			logger.warn("Graph " + graph + " does not contain any edges");
			return null;
		}
		
		// create id map
		Map<V, Integer> idMap = new HashMap<V, Integer>();
		int i = 0;
		for (V vertex : graph.getVertices()) {
			idMap.put(vertex, new Integer(i++));
		}

		Rengine re = MRService.getRengine();
        
		REXP result;
		MRService.assignGraphObject(re, graph, "g", idMap);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			re.eval("print(g)");
		}
		// LOGGING ->
		
		result = re.eval("similarity." + version + "(g, mode=\"" + mode + "\")");
	
		double[][] results = result.asDoubleMatrix();
		logger.info("Result: " + result);		
		
		for (V outerVertex : idMap.keySet()) {
			for (V innerVertex : idMap.keySet()) {
					if (similarities.get(outerVertex) == null) {
						similarities.put(outerVertex, new HashMap<V, Double>());
					}
					similarities.get(outerVertex).put(innerVertex, new Double(results[idMap.get(outerVertex)][idMap.get(innerVertex)]));
			}
		}
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Similarities: " + similarities);
		}
		// LOGGING ->

		return similarities;
	}
	
	
	/**
	 * Calculates Dice similarities between vertices.
	 * Mode gives the type of neighboring vertices to use for the calculation, possible values:
	 * ‘out’, ‘in’, ‘all’.
	 * @param graph
	 * @param mode
	 * @return
	 */
	public static <V, E extends MoreEdge<? super V>> Map<V, Map<V, Double>> getVertexDiceSimilaritiesR(
			final Graph<V, E> graph, String mode) {
		return getVertexSimilaritiesR(graph, "dice", mode);
	}
	
	/**
	 * Calculates Jaccard similarities.
	 * Mode gives the type of neighboring vertices to use for the calculation, possible values:
	 * ‘out’, ‘in’, ‘all’.
	 * 
	 * @param graph
	 * @param mode
	 * @return
	 */
	public static <V, E extends MoreEdge<? super V>> Map<V, Map<V, Double>> getVertexJaccardSimilaritiesR(
			final Graph<V, E> graph, String mode) {
		return getVertexSimilaritiesR(graph, "jaccard", mode);
	}
}
