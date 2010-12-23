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
 * Created by Sascha Holzhauer on Nov 26, 2010
 */
package de.cesr.more.measures.network.supply.algos;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

import de.cesr.more.basic.MoreEdge;
import de.cesr.more.io.MoreEdgeFactory;
import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.util.Log4jLogger;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * MORe
 * 
 * NOTE: Enable Info-Logging for this class to inspect R console messages!
 *
 * @author Sascha Holzhauer
 * @date Nov 26, 2010 
 *
 */
public class MClusteringCoefficientR extends MAbstractMeasureSupplier implements RMainLoopCallbacks{

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MClusteringCoefficientR.class);
	
	static private MClusteringCoefficientR instance = null;
	
	static private String[] R_ARGS = {""};

	
	private MClusteringCoefficientR() {
	}
	
	static public MClusteringCoefficientR getInstance() {
		if (instance == null) {
			instance = new MClusteringCoefficientR();
		}
		return instance;
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
	
	
	
	public static <V, E extends MoreEdge> double getClusteringCoefficientR(final Graph<V, E> graph) {
		logger.info("Calculate Clustering Coefficient (R) for a graph containing " + graph.getVertexCount() + " nodes.");
		
		Rengine re = getRengine();
		
		logger.debug("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's ready
        if (!re.waitForR()) {
        	logger.error("Cannot load R");
            return 0.0;
        }
        
		REXP result;
		assignGraphObject(re, graph, "g");
		re.eval("print(g)");
		result = re.eval("transitivity(g, type=\"global\")");
		logger.info("Result: " + result);
		return result.asDouble();
	}

	/**
	 * @return
	 * Created by Sascha Holzhauer on 10.12.2010
	 */
	public static Rengine getRengine() {
		Rengine re = Rengine.getMainEngine();
		if (re == null) {
			re = new Rengine(R_ARGS, false, getInstance());
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
		assert false;
	}
}
