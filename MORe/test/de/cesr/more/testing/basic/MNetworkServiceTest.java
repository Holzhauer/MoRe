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
 * Created by holzhauer on 12.10.2011
 */
package de.cesr.more.testing.basic;

import static org.junit.Assert.*;

import org.apache.commons.collections15.Factory;
import org.junit.Before;
import org.junit.Test;

import de.cesr.more.basic.MNetworkService;
import de.cesr.more.building.MDefaultEdgeFactory;
import de.cesr.more.building.MoreEdgeFactory;
import de.cesr.more.edges.MoreEdge;
import de.cesr.more.io.GraphMLReaderWithEdges;
import de.cesr.more.networks.MDirectedNetwork;
import de.cesr.more.networks.MoreNetwork;
import edu.uci.ics.jung.graph.Graph;

/**
 * MORe
 *
 * @author holzhauer
 * @date 12.10.2011 
 *
 */
public class MNetworkServiceTest {
	
	static final String GRAPH_FILENAME = "./test/res/MergeTestNetwork01yn.graphml";
	static final String GRAPH_FILENAME_RESULTING = "./test/res/MergeTestNetwork02yn.graphml";
	
	MoreNetwork<TestAgent, MoreEdge<TestAgent>> network;
	MoreNetwork<TestAgent, MoreEdge<TestAgent>> resultingNetwork;
	
	static class TestAgent {
		static int identifier = 0; 
		int id;

		public TestAgent() {
			this.id = identifier++;
		}
		
		public TestAgent(int id) {
			this.id =id;
		}

		@Override
		public String toString() {
			return new Integer(this.id).toString();
		}
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object other) {
			if (other instanceof TestAgent) {
				return this.id == ((TestAgent)other).id;
			} else {
				return false;
			}
		}
		
		public static void reset() {
			identifier = 0;
		}
		
		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return this.id;
		}
	}
	
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MoreEdgeFactory<TestAgent, MoreEdge<TestAgent>> edgeFactory = new MDefaultEdgeFactory<MNetworkServiceTest.TestAgent>();
		network = new MDirectedNetwork<TestAgent, MoreEdge<TestAgent>>(edgeFactory, "TestNetwork");
		
		resultingNetwork = new MDirectedNetwork<TestAgent, MoreEdge<TestAgent>>(edgeFactory, "TestNetwork");
		Factory<TestAgent> nodeFactory = new Factory<TestAgent>() {

			@Override
			public TestAgent create() {
				return new TestAgent();
			}
		};
		
		GraphMLReaderWithEdges<Graph<TestAgent, MoreEdge<TestAgent>>, TestAgent, MoreEdge<TestAgent>> graphReader = 
				new GraphMLReaderWithEdges<Graph<TestAgent, MoreEdge<TestAgent>>, TestAgent, MoreEdge<TestAgent>>(nodeFactory,
				edgeFactory);
		
		graphReader.load(GRAPH_FILENAME, network.getJungGraph());
		TestAgent.reset();
		graphReader.load(GRAPH_FILENAME_RESULTING, resultingNetwork.getJungGraph());
	}

	@Test
	public void testStructurallyEqual() {
		assertTrue(MNetworkService.isStructurallyEqual(network, network));
		assertTrue(MNetworkService.isStructurallyEqual(resultingNetwork, resultingNetwork));
	}
	
	@Test
	public void test() {
		MNetworkService.aggregateNodes(network, new TestAgent(0), new TestAgent(3));
		assertTrue(MNetworkService.isStructurallyEqual(network, resultingNetwork));
	}

}
