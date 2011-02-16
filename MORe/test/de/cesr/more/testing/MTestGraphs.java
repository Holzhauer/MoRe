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
package de.cesr.more.testing;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections15.Factory;
import org.xml.sax.SAXException;

import de.cesr.more.basic.MEdge;
import de.cesr.more.basic.MoreEdge;
import de.cesr.more.building.MoreEdgeFactory;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.measures.util.MNodeMeasures;
import de.cesr.more.networks.MDirectedNetwork;
import de.cesr.more.networks.MoreNetwork;

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
	
	public static class TestNode implements MoreNodeMeasureSupport{
		
		int id = MTestGraphs.TestAgentId++;
		
		protected MNodeMeasures measures = new MNodeMeasures();
		
		public String toString() {
			return "TestNode" + this.id;
		}

		public int getId() {
			return id;
		}
		
		/**
		 * @see de.cesr.more.measures.node.MoreNodeMeasureSupport#getNetworkMeasureObject(de.cesr.more.networks.MoreNetwork, de.cesr.more.measures.MMeasureDescription)
		 */
		@Override
		public Object getNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
				MMeasureDescription key) {
			return measures.getNetworkMeasureObject(network, key);
		}

		/**
		 * @see de.cesr.more.measures.node.MoreNodeMeasureSupport#setNetworkMeasureObject(de.cesr.more.networks.MoreNetwork, de.cesr.more.measures.MMeasureDescription, java.lang.Object)
		 */
		@Override
		public void setNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
				MMeasureDescription key, Object value) {
			measures.setNetworkMeasureObject(network, key, value);
		}
	}
	
	/**
	 * Vertices or of Type Object.
	 * 
	 * @param num_nodes
	 * @return
	 * Created by Sascha Holzhauer on 03.12.2010
	 */
	public static UndirectedGraph<TestNode, MoreEdge<TestNode>> getCompleteUndirectedGraph(int num_nodes) {	
		UndirectedGraph<TestNode, MoreEdge<TestNode>> completeG = new UndirectedSparseGraph<TestNode, MoreEdge<TestNode>>();
	
		// Build complete graph:
		TestNode[] agents = new TestNode[num_nodes];
		for (int i = 0; i < num_nodes; i++) {
			agents[i] = new MTestGraphs.TestNode();
		}
		for (int i = 0; i < agents.length; i++) {
			for (int j = i; j < agents.length; j++) {
				if (i != j) {
					completeG.addEdge(new MEdge<TestNode>(agents[i], agents[j]), agents[i], agents[j]);
				}
			}
		}
		return completeG;
	}
	
	public static MoreNetwork<TestNode, MoreEdge<TestNode>> getCompleteDirectedMNetwork(int num_nodes) {	
		MoreNetwork<TestNode, MoreEdge<TestNode>> completeG = new MDirectedNetwork(new MoreEdgeFactory<TestNode, MoreEdge<TestNode>>() {

			@Override
			public MoreEdge<TestNode> createEdge(TestNode source, TestNode target, boolean directed) {
				return new MEdge<TestNode>(source, target);
			}
		}, "TestNet");
	
		// Build complete graph:
		TestNode[] agents = new TestNode[num_nodes];
		for (int i = 0; i < num_nodes; i++) {
			agents[i] = new MTestGraphs.TestNode();
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
	public static DirectedGraph<TestNode, MoreEdge<TestNode>> getCompleteDirectedGraph(int num_nodes) {	
		DirectedGraph<TestNode, MoreEdge<TestNode>> completeG = new DirectedSparseGraph<TestNode, MoreEdge<TestNode>>();
	
		// Build complete graph:
		TestNode[] agents = new TestNode[num_nodes];
		for (int i = 0; i < num_nodes; i++) {
			agents[i] = new MTestGraphs.TestNode();
		}
		for (int i = 0; i < agents.length; i++) {
			for (int j = i; j < agents.length; j++) {
				if (i != j) {
					completeG.addEdge(new MEdge<TestNode>(agents[i], agents[j]), agents[i], agents[j]);
					completeG.addEdge(new MEdge<TestNode>(agents[j], agents[i]), agents[j], agents[i]);
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
				public Object create() {
					return new Object();
				}
			}, new Factory<Object>() {
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
