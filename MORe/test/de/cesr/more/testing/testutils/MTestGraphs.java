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
 * Created by Sascha Holzhauer on 03.12.2010
 */
package de.cesr.more.testing.testutils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections15.Factory;
import org.xml.sax.SAXException;

import repast.simphony.context.Context;
import de.cesr.more.basic.edge.MEdge;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.MNodeMeasures;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.rs.building.MoreMilieuAgent;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.io.GraphMLReader;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 03.12.2010 
 *
 */
public class MTestGraphs {
	
	final static private String FILENAME = "./test/graphs/SmallWorld02.graphml";
	
	static int TestAgentId = 0;
	
	public static class MTestNode implements MoreNodeMeasureSupport, MoreMilieuAgent {
		
		int id = MTestGraphs.TestAgentId++;
		
		protected MNodeMeasures measures = new MNodeMeasures();
		
		@Override
		public String toString() {
			return "MTestNode" + this.id;
		}

		public int getId() {
			return id;
		}
		
		/**
		 * @see de.cesr.more.measures.node.MoreNodeMeasureSupport#getNetworkMeasureObject(de.cesr.more.basic.network.MoreNetwork, de.cesr.more.measures.MMeasureDescription)
		 */
		@Override
		public Number getNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
				MMeasureDescription key) {
			return measures.getNetworkMeasureObject(network, key);
		}

		/**
		 * @see de.cesr.more.measures.node.MoreNodeMeasureSupport#setNetworkMeasureObject(de.cesr.more.basic.network.MoreNetwork, de.cesr.more.measures.MMeasureDescription, java.lang.Object)
		 */
		@Override
		public void setNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
				MMeasureDescription key, Number value) {
			measures.setNetworkMeasureObject(network, key, value);
		}

		/**
		 * @see de.cesr.more.rs.building.MoreMilieuAgent#getMilieuGroup()
		 */
		@Override
		public int getMilieuGroup() {
			return 1;
		}

		/**
		 * @see de.cesr.more.rs.building.MoreMilieuAgent#getParentContext()
		 */
		@Override
		public Context<?> getParentContext() {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * @see de.cesr.more.rs.building.MoreMilieuAgent#getAgentId()
		 */
		@Override
		public String getAgentId() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	/**
	 * Vertices or of Type Object.
	 * 
	 * @param num_nodes
	 * @return
	 * Created by Sascha Holzhauer on 03.12.2010
	 */
	public static UndirectedGraph<MTestNode, MoreEdge<MTestNode>> getCompleteUndirectedGraph(int num_nodes) {	
		UndirectedGraph<MTestNode, MoreEdge<MTestNode>> completeG = new UndirectedSparseGraph<MTestNode, MoreEdge<MTestNode>>();
	
		// Build complete graph:
		MTestNode[] agents = new MTestNode[num_nodes];
		for (int i = 0; i < num_nodes; i++) {
			agents[i] = new MTestGraphs.MTestNode();
		}
		for (int i = 0; i < agents.length; i++) {
			for (int j = i; j < agents.length; j++) {
				if (i != j) {
					completeG.addEdge(new MEdge<MTestNode>(agents[i], agents[j]), agents[i], agents[j]);
				}
			}
		}
		return completeG;
	}
	
	public static MoreNetwork<MTestNode, MoreEdge<MTestNode>> getCompleteDirectedMNetwork(int num_nodes) {	
		MoreNetwork<MTestNode, MoreEdge<MTestNode>> completeG = new MDirectedNetwork(new MoreEdgeFactory<MTestNode, MoreEdge<MTestNode>>() {

			@Override
			public MoreEdge<MTestNode> createEdge(MTestNode source, MTestNode target, boolean directed) {
				return new MEdge<MTestNode>(source, target);
			}
		}, "TestNet");
	
		// Build complete graph:
		MTestNode[] agents = new MTestNode[num_nodes];
		for (int i = 0; i < num_nodes; i++) {
			agents[i] = new MTestGraphs.MTestNode();
		}
		for (int i = 0; i < agents.length; i++) {
			for (int j = i; j < agents.length; j++) {
				if (i != j) {
					completeG.connect(agents[i], agents[j]);
				}
			}
		}
		completeG.getName();
		return completeG;
	}
	
	/**
	 * Vertices or of Type Object.
	 * 
	 * @param num_nodes
	 * @return
	 * Created by Sascha Holzhauer on 03.12.2010
	 */
	public static DirectedGraph<MTestNode, MoreEdge<MTestNode>> getCompleteDirectedGraph(int num_nodes) {	
		DirectedGraph<MTestNode, MoreEdge<MTestNode>> completeG = new DirectedSparseGraph<MTestNode, MoreEdge<MTestNode>>();
	
		// Build complete graph:
		MTestNode[] agents = new MTestNode[num_nodes];
		for (int i = 0; i < num_nodes; i++) {
			agents[i] = new MTestGraphs.MTestNode();
		}
		for (int i = 0; i < agents.length; i++) {
			for (int j = i; j < agents.length; j++) {
				if (i != j) {
					completeG.addEdge(new MEdge<MTestNode>(agents[i], agents[j]), agents[i], agents[j]);
					completeG.addEdge(new MEdge<MTestNode>(agents[j], agents[i]), agents[j], agents[i]);
				}
			}
		}
		return completeG;
	}
	
	/**
	 * Returns a graph that is stored in graphML-format.
	 * @return
	 * Created by Sascha Holzhauer on 03.12.2010
	 * @throws IOException 
	 */
	public static Graph getSmallWorldGraph() throws IOException {
		Graph readGraph = null;
		GraphMLReader<Graph<Object, Object>, Object, Object> graphReader;
		try {
			graphReader = new GraphMLReader<Graph<Object, Object>, Object, Object>(new Factory<Object>() {
				@Override
				public Object create() {
					return new Object();
				}
			}, new Factory<Object>() {
					@Override
					public Object create() {
						return new Object();
					}
			});
	
			readGraph = new UndirectedSparseGraph<Object, Object>();
			graphReader.load(FILENAME, readGraph);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readGraph;
	}
}
