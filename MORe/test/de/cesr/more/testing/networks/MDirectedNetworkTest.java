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
package de.cesr.more.testing.networks;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import de.cesr.more.basic.edge.MEdge;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.testing.testutils.MTestGraphs;
import de.cesr.more.testing.testutils.MTestGraphs.MTestNode;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 18.10.2011 
 *
 */
public class MDirectedNetworkTest {
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MDirectedNetworkTest.class);
	
	MoreNetwork<MTestNode, MoreEdge<MTestNode>> network;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		network = MDirectedNetwork.getNewNetwork(new MoreEdgeFactory<MTestNode, MoreEdge<MTestNode>>() {
			@Override
			public MoreEdge<MTestNode> createEdge(MTestNode source, MTestNode target, boolean directed) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Edge created...");
				}
				// LOGGING ->

				return new MEdge<MTestNode>(source, target, true, 3.0);
			}
		}, MTestGraphs.getCompleteDirectedGraph(4), "TestNet");
	}

	/**
	 * Test method for {@link de.cesr.more.basic.network.MDirectedNetwork#normalizeWeights()}.
	 */
	@Test
	public void testNormalizeWeights() {
		double sum = 0.0;
		for (MoreEdge<MTestNode> edge : network.getEdgesCollection()) {
			sum += edge.getWeight();
		}
		assertEquals(3.0, sum / network.numEdges(), 0.01);
		
		network.normalizeWeights();
		sum = 0.0;
		for (MoreEdge<MTestNode> edge : network.getEdgesCollection()) {
			sum += edge.getWeight();
		}
		assertEquals(1.0, sum / network.numEdges(), 0.01);
	}
}
