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

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.MoreEdge;
import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
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
public class MNetworkStatisticsR extends MAbstractMeasureSupplier implements RMainLoopCallbacks{

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MNetworkStatisticsR.class);
	
	static private MNetworkStatisticsR instance = null;
	
	static private String[] R_ARGS = {"--no-save"};

	
	private MNetworkStatisticsR() {
		// Stop R engine:
		logger.info("Schedule stopping REngine at end");
		MManager.getSchedule().schedule(MScheduleParameters.getScheduleParameter(MScheduleParameters.END_TICK, 1,
				MScheduleParameters.END_TICK, MScheduleParameters.LAST_PRIORITY), new MoreAction() {
					@Override
					public void execute() {
						logger.info("Stop REngine.");
						MNetworkStatisticsR.endEngine();
					}
					
					@Override
					public String toString() {
						return "Stops REngine";
					}
				});
		logger.info("Schedule: " + MManager.getSchedule().getScheduleInfo());
		logger.info("Instance created");
	}
	
	static public MNetworkStatisticsR getInstance() {
		if (instance == null) {
			logger.info("Create new instance");
			instance = new MNetworkStatisticsR();
		}
		return instance;
	}
	
	/**
	 * Stops REngine.
	 * 
	 * Created by Sascha Holzhauer on 10.01.2011
	 */
	static public void endEngine() {
		Rengine re = Rengine.getMainEngine();
		if (re != null) {
			re.end();
		}
	}
	
	public static <V, E extends MoreEdge> void assignGraphObject(Rengine re, Graph<V,E> graph, String targetSymbol) {
		logger.info("Create R instance of graph: " + graph);
		
		REXP x = null;
		// generate edgelist:
		StringBuffer edgelist = new StringBuffer();
		edgelist.append("c(");
		for (E e : graph.getEdges()) {
			edgelist.append("\"" + e.getStart() + "\",\"" + e.getEnd() + "\",");
		}
		edgelist.deleteCharAt(edgelist.length() - 1);
		edgelist.append(")");
		logger.debug("Edgelist: " + edgelist);
		logger.debug("Load Library igraph...");
		re.eval("library(igraph)");
		re.eval("el <- matrix(" + edgelist + ", ncol = 2, byrow = TRUE)");
		re.eval(targetSymbol + " <- graph.edgelist( el , directed=" + ((graph instanceof DirectedGraph) ? "TRUE" : "FALSE") + ")");
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
	 * Created by Sascha Holzhauer on 23.12.2010
	 */
	public static <V, E extends MoreEdge> double getAveragepathLengthR(final Graph<V, E> graph, boolean considerIsolates) {
		logger.info("Calculate Average Path Length (R) for a graph containing " + graph.getVertexCount() + " nodes.");
		
		if (graph.getEdgeCount() == 0)  {
			logger.warn("Graph " + graph + " does not contain any edges");
			return Double.NaN;
		}
		
		Rengine re = getRengine();
        
		REXP result;
		assignGraphObject(re, graph, "g");

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
	 * Created by Sascha Holzhauer on 23.12.2010
	 */
	public static <V, E extends MoreEdge> double[] getAveragepathLengthClustersR(final Graph<V, E> graph, boolean considerIsolates) {
		logger.info("Calculate Average Path Length (R) for clusters in a graph containing " + graph.getVertexCount() + " nodes.");
		
		if (graph.getEdgeCount() == 0)  {
			logger.warn("Graph " + graph + " does not contain any edges");
			return null;
		}
		
		Rengine re = getRengine();
        
		REXP result;
		assignGraphObject(re, graph, "g");

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
	
	/**
	 * @return
	 * Created by Sascha Holzhauer on 10.12.2010
	 */
	public static Rengine getRengine() {
		Rengine re = Rengine.getMainEngine();
		if (re == null) {
			logger.debug("REngine-Version: " + Rengine.getVersion());
			re = new Rengine(R_ARGS, false, getInstance());
			
			// the engine creates R is a new thread, so we should wait until it's ready
			logger.debug("Rengine created, waiting for R");

			if (!re.waitForR()) {
	        	logger.error("Cannot load R");
	        	throw new IllegalStateException("Cannot load R");
	        }
			logger.debug("New REngine instantiated: " + re);
		}
		return re;
	}
	

	/**
	 * @see org.rosuda.JRI.RMainLoopCallbacks#rBusy(org.rosuda.JRI.Rengine, int)
	 */
	@Override
	public void rBusy(Rengine arg0, int which) {
		if (which == 1) {
			logger.info("R Engine works ...");
		}
		if (which == 0) {
			logger.info("... finished.");
		}
		assert false;
	}

	/**
	 * @see org.rosuda.JRI.RMainLoopCallbacks#rChooseFile(org.rosuda.JRI.Rengine, int)
	 */
	@Override
	public String rChooseFile(Rengine arg0, int arg1) {
		JFileChooser fileChooser = new JFileChooser();
		// TODO fileChooser.
		return null;
	}

	/**
	 * @see org.rosuda.JRI.RMainLoopCallbacks#rFlushConsole(org.rosuda.JRI.Rengine)
	 */
	@Override
	public void rFlushConsole(Rengine arg0) {
		logger.warn("Flushed");
	}

	/**
	 * @see org.rosuda.JRI.RMainLoopCallbacks#rLoadHistory(org.rosuda.JRI.Rengine, java.lang.String)
	 */
	@Override
	public void rLoadHistory(Rengine arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see org.rosuda.JRI.RMainLoopCallbacks#rReadConsole(org.rosuda.JRI.Rengine, java.lang.String, int)
	 */
	@Override
	public String rReadConsole(Rengine arg0, String arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.rosuda.JRI.RMainLoopCallbacks#rSaveHistory(org.rosuda.JRI.Rengine, java.lang.String)
	 */
	@Override
	public void rSaveHistory(Rengine arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see org.rosuda.JRI.RMainLoopCallbacks#rShowMessage(org.rosuda.JRI.Rengine, java.lang.String)
	 */
	@Override
	public void rShowMessage(Rengine arg0, String message) {
		logger.warn(message);
	}

	/**
	 * @see org.rosuda.JRI.RMainLoopCallbacks#rWriteConsole(org.rosuda.JRI.Rengine, java.lang.String, int)
	 */
	@Override
	public void rWriteConsole(Rengine arg0, String message, int level) {
		if (level == 0) {
			logger.info(message);
		}
		else if (level == 1) {
			logger.warn(message);
		}
	}
}
