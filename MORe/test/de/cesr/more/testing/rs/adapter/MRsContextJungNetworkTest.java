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
 * Created by Sascha Holzhauer on 18.10.2011
 */
package de.cesr.more.testing.rs.adapter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.space.graph.DirectedJungNetwork;
import de.cesr.more.networks.MoreRsNetwork;
import de.cesr.more.rs.adapter.MRepastEdge;
import de.cesr.more.rs.adapter.MRsContextJungNetwork;
import de.cesr.more.testing.MTestGraphs.TestNode;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 18.10.2011 
 *
 */
public class MRsContextJungNetworkTest {
	
	MoreRsNetwork<TestNode, MRepastEdge<TestNode>> network;
	TestNode node1, node2, node3;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Context<TestNode> context = new DefaultContext<TestNode>();
		this.network = new MRsContextJungNetwork<TestNode, MRepastEdge<TestNode>>(
				 new DirectedJungNetwork<TestNode>("Network"), context);
		node1 = new TestNode();
		node2 = new TestNode();
		node3 = new TestNode();
		network.connect(node2, node1);
		network.connect(node2, node3);
	}

	/**
	 * Test method for {@link repast.simphony.context.space.graph.ContextJungNetwork#isPredecessor(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	public void testIsPredecessor() {
		assertTrue(network.isPredecessor(node2, node1));
		assertTrue(network.isPredecessor(node2, node3));
		
		assertFalse(network.isPredecessor(node3, node1));
		assertFalse(network.isPredecessor(node3, node2));
		
		assertFalse(network.isPredecessor(node1, node2));
		assertFalse(network.isPredecessor(node1, node3));
	}

	/**
	 * Test method for {@link repast.simphony.context.space.graph.ContextJungNetwork#isSuccessor(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	public void testIsSuccessor() {
		assertTrue(network.isSuccessor(node1, node2));
		assertTrue(network.isSuccessor(node3, node2));
		
		assertFalse(network.isSuccessor(node1, node3));
		assertFalse(network.isSuccessor(node2, node3));
		
		assertFalse(network.isSuccessor(node2, node1));
		assertFalse(network.isSuccessor(node3, node1));
	}
}
