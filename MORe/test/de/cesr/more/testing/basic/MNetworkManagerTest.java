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
 * Created by Sascha Holzhauer on 03.01.2011
 */
package de.cesr.more.testing.basic;



import static org.junit.Assert.assertEquals;

import org.apache.commons.collections15.Predicate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.MNetworkManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.testing.testutils.MTestGraphs;
import de.cesr.more.testing.testutils.MTestGraphs.MTestNode;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 03.01.2011
 * 
 */
public class MNetworkManagerTest {

	MoreNetwork<MTestNode, MoreEdge<MTestNode>>	network;
	Predicate<MTestNode>							predicate;

	/**
	 * @throws java.lang.Exception Created by Sascha Holzhauer on 03.01.2011
	 */
	@Before
	public void setUp() throws Exception {
		MManager.init();
		network = MTestGraphs.getCompleteDirectedMNetwork(6);

		predicate = new Predicate<MTestNode>() {

			@Override
			public boolean evaluate(MTestNode object) {
				return object.getId() % 2 == 0;
			}
		};
	}

	/**
	 * @throws java.lang.Exception Created by Sascha Holzhauer on 03.01.2011
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.basic.MNetworkManager#storeVertexSubnetwork(de.cesr.more.basic.network.MoreNetwork, org.apache.commons.collections15.Predicate, java.lang.String)}
	 * .
	 */
	@Test
	public final void testStoreVertexSubnetworkMoreNetworkOfVEPredicateOfVString() {
		MNetworkManager.storeVertexSubnetwork(network, predicate,
				"TestGraph_even");
		
		assertEquals(3, MNetworkManager.getNetwork("TestGraph_even").numNodes()) ;
	}

}
