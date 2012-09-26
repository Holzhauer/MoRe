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
package de.cesr.more.measures.util;

import java.util.Map;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.util.Log4jLogger;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * MORe
 * 
 * Service class for R calculations
 * - schedules at initialisation stopping R.Engine at the end of simulation 
 * - loads igraph library
 * - instantiates REnging
 *
 * @author Sascha Holzhauer
 * @date 14.11.2011 
 *
 */
public class MRService implements RMainLoopCallbacks{
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MRService.class);
	static private Logger		loggerThreads	= Log4jLogger.getLogger(MRService.class.getName() + ".threads");
	
	static private String[] R_ARGS = {"--no-save"};

	static private MRService instance = null;
	
	/**
	 * Constructor
	 */
	private MRService() {
		// Stop R engine:

		MManager.getSchedule().schedule(
				MScheduleParameters.getScheduleParameter(MScheduleParameters.END_TICK, 0,
						MScheduleParameters.END_TICK, Double.POSITIVE_INFINITY), new MoreAction() {
			@Override
			public void execute() {
						logger.debug("Execute action: " + toString());
						MRService.endEngine();
			}

			@Override
			public String toString() {
						return "Stops REngine" + new Object();
			}
		});
	}
	
	/**
	 * Returns the the current {@link MRService} if existing and creates
	 * a new instance otherwise.
	 * 
	 * @return instance of MRService
	 */
	static public MRService getInstance() {
		if (instance == null) {
			instance = new MRService();
		}
		return instance;
	}
	
	/**
	 * Stops REngine. Scheduled at initialisation for end of simulation.
	 * 
	 */
	static public void endEngine() {
		// <- LOGGING
		if (loggerThreads.isDebugEnabled()) {
			loggerThreads.debug("Stopp REngine...");
		}
		// LOGGING ->
		Rengine re = Rengine.getMainEngine();
		if (re != null) {
			re.end();
		}

		// <- LOGGING
		if (loggerThreads.isDebugEnabled()) {
			loggerThreads.debug("REngine stopped!");
		}
		// LOGGING ->

	}
	
	/**
	 * Assigns a JUNG graph object to an R igraph graph object.
	 * @param re the REngine
	 * @param graph the graph to assign
	 * @param targetSymbol name of R target object
	 */
	public static <V, E extends MoreEdge<? super V>> void assignGraphObject(Rengine re, Graph<V,E> graph, String targetSymbol,
			Map<V, Integer> idMap) {
		logger.info("Create R instance of graph: " + graph);

		// generate edge-list:
		StringBuffer edgelist = new StringBuffer();
		edgelist.append("c(");
		if (idMap != null) {
			for (E e : graph.getEdges()) {
				edgelist.append("" + idMap.get(e.getStart()).intValue() + "," + idMap.get(e.getEnd()).intValue() + ",");
			}
		} else {
			for (E e : graph.getEdges()) {
				edgelist.append("\"" + e.getStart() + "\",\"" + e.getEnd() + "\",");
			}
		}
		edgelist.deleteCharAt(edgelist.length() - 1);
		edgelist.append(")");
		logger.debug("Edgelist: " + edgelist);
		logger.debug(re.eval("print(R.version.string)"));
		logger.debug("Load Library igraph...");
		re.eval("library(igraph)");
		re.eval("el <- matrix(" + edgelist + ", ncol = 2, byrow = TRUE)");
		re.eval(targetSymbol + " <- graph.edgelist( el , directed=" + ((graph instanceof DirectedGraph) ? "TRUE" : "FALSE") + ")");
		logger.debug("Graph object assigned.");
	}
	

	public static <V, E extends MoreEdge<? super V>> void assignGraphObject(Rengine re, Graph<V,E> graph, String targetSymbol) {
		assignGraphObject(re, graph, targetSymbol, null);
	}

	/**
	 * Create a new REngine
	 * @return the new REngine
	 */
	public static Rengine getRengine() {
		Rengine re = Rengine.getMainEngine();
		if (re == null) {
			logger.debug("REngine-Version: " + Rengine.getVersion());
			re = new Rengine(R_ARGS, false, getInstance());
			
			// the engine creates R is a new thread, so we should wait until it's ready
			loggerThreads.debug("Rengine created, waiting for R");
			if (!re.waitForR()) {
				loggerThreads.error("Cannot load R");
	        	throw new IllegalStateException("Cannot load R");
	        }
	        
		}
		logger.debug("Returning Rengine");
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
		return fileChooser.getSelectedFile().getName();
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
