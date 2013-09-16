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
 * Created by Sascha Holzhauer on 27.06.2013
 */
package de.cesr.more.measures.network.supply.algos;


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
 * @date 27.06.2013 
 *
 */
public class MNetworkModularityR {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MNetworkModularityR.class);

	public static <V, E extends MoreEdge<? super V>> double getModularityR(final Graph<V, E> graph) {
		logger.info("Calculate Modularity (R) for a graph containing " + graph.getVertexCount() + " nodes.");

		if (graph.getEdgeCount() == 0) {
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

		logger.info("Calculate Modularity...");
		re.eval("comunity =	edge.betweenness.community(g)");
		result = re.eval("modularity(comunity)");
		logger.info("Result: " + result);
		return result.asDouble();
	}
}
