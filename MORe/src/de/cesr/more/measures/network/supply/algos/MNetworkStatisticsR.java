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
 * Created by Sascha Holzhauer on 23.12.2010
 */
package de.cesr.more.measures.network.supply.algos;

import org.apache.log4j.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.measures.util.MRService;
import de.cesr.more.util.Log4jLogger;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * MORe
 * 
 * TODO write junit
 * TODO write measure supplier
 *
 * @author Sascha Holzhauer
 * @date 23.12.2010 
 *
 */
public class MNetworkStatisticsR {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MNetworkStatisticsR.class);
	
	
	/**
	 * Return {@link Double.NaN} when graph does not contain any edge.
	 * If the graph is unconnected, the length of the missing paths are counted having 
	 * length vcount(graph), one longer than the longest possible geodesic in the network.
	 * 
	 * TODO automatically check whether graph is connected and pass result to R
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return
	 */
	public static <V, E extends MoreEdge<? super V>> double getAveragepathLengthR(final Graph<V, E> graph, boolean considerIsolates) {
		logger.info("Calculate Average Path Length (R) for a graph containing " + graph.getVertexCount() + " nodes.");
		
		if (graph.getEdgeCount() == 0)  {
			logger.warn("Graph " + graph + " does not contain any edges");
			return Double.NaN;
		}
		
		Rengine re = MRService.getRengine();
        
		REXP result;
		MRService.assignGraphObject(re, graph, "g");

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			re.eval("print(g)");
		}
		// LOGGING ->

		result = re.eval("average.path.length(g, directed=" + ((graph instanceof DirectedGraph) ? "TRUE" : "FALSE") + " , " +
				"unconnected=" + (considerIsolates ? "FALSE" : "TRUE")+ ")");
		logger.info("Result: " + result);
		return result.asDouble();
	}

	
	/**
	 * Return {@link Double.NaN} when graph does not contain any edge.
	 * If the graph is unconnected, the length of the missing paths are counted having 
	 * length vcount(graph), one longer than the longest possible geodesic in the network.
	 * 
	 * TODO automatically check whether graph is connected and pass result to R
	 * @param <V>
	 * @param <E>
	 * @param graph
	 * @return
	 */
	public static <V, E extends MoreEdge<? super V>> double[] getAveragepathLengthClustersR(final Graph<V, E> graph, boolean considerIsolates) {
		logger.info("Calculate Average Path Length (R) for clusters in a graph containing " + graph.getVertexCount() + " nodes.");
		
		if (graph.getEdgeCount() == 0)  {
			logger.warn("Graph " + graph + " does not contain any edges");
			return null;
		}
		
		Rengine re = MRService.getRengine();
        
		REXP result;
		MRService.assignGraphObject(re, graph, "g");

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			re.eval("print(g)");
		}
		// LOGGING ->

		
		// identify clusters
		
		
		// calculate APL for every cluster
		re.eval("moreClusterAVP <- function(graph, directed, unconnected) {" +
				"	cls = clusters(graph, mode=\"weak\")" +
				"	for (i in 1:cls$no) {" +
				"		subg = subgraph(graph, which(cls$membership %in% c(i - 1)) - 1)" +
				"		result[i] <- average.path.length(subg, directed, unconnected)" +
				"}" +
				"result" +
				"}");
		
		result = re.eval("moreClusterAVP(g, directed=" + ((graph instanceof DirectedGraph) ? "TRUE" : "FALSE") + " , " +
				"unconnected=" + (considerIsolates ? "FALSE" : "TRUE")+ ")");
		
		logger.info("Result: " + result);		
		return result.asDoubleArray();
	}
}
