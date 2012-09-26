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
 * Created by Sascha Holzhauer on 03.09.2012
 */
package de.cesr.more.testing.rs.building.geo;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import repast.simphony.context.DefaultContext;
import repast.simphony.space.gis.DefaultGeography;
import repast.simphony.space.gis.Geography;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.cesr.more.basic.MNetworkTools;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.MGeoRsRestoreNetworkBuilder;
import de.cesr.more.rs.building.MGeoRsWattsBetaSwBuilder;
import de.cesr.more.rs.building.edge.MRsEdgeFactory;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.testing.testutils.MTestGraphs.MTestNode;
import de.cesr.more.util.io.MoreIoUtilities;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 03.09.2012 
 *
 */
public class MGeoRsRestoreNetworkBuilderTest {

	Collection<MTestNode>	agents	= new ArrayList<MTestNode>();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		for (int i = 0; i < 100; i++) {
			agents.add(new MTestNode());
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		agents = null;
	}

	/**
	 * Test method for {@link de.cesr.more.rs.building.MGeoRsRestoreNetworkBuilder#buildNetwork(java.util.Collection)}.
	 */
	@Test
	public void testBuildNetwork() {
		MGeoRsWattsBetaSwBuilder<MTestNode, MRepastEdge<MTestNode>> netBuilder =
				new MGeoRsWattsBetaSwBuilder<MTestNode, MRepastEdge<MTestNode>>();
		Geography<Object> geography = new DefaultGeography<Object>("TestGeogrpahy");
		netBuilder.setGeography(geography);
		netBuilder.setContext(new DefaultContext<MTestNode>());

		for (MTestNode agent : agents) {
			geography.move(agent, new Point(new Coordinate(0, 0), new PrecisionModel(), 1));
		}
		MoreNetwork<MTestNode, MRepastEdge<MTestNode>> networkOne = netBuilder.buildNetwork(agents);

		MoreIoUtilities.outputGraph(networkOne, new File("./TestSwGraph.graphml"));

		PmParameterManager.setParameter(MNetworkBuildingPa.RESTORE_NETWORK_SOURCE_FILE, "./TestSwGraph.graphml");
		MGeoRsRestoreNetworkBuilder<MTestNode, MRepastEdge<MTestNode>> networkBuilder2 =
				new MGeoRsRestoreNetworkBuilder<MTestNode, MRepastEdge<MTestNode>>(
						new MRsEdgeFactory<MTestNode, MRepastEdge<MTestNode>>(), "TestNetwork");
		networkBuilder2.setGeography(new DefaultGeography<Object>("TestGeogrpahy"));
		networkBuilder2.setContext(new DefaultContext<MTestNode>());
		MoreNetwork<MTestNode, MRepastEdge<MTestNode>> networkTwo = networkBuilder2.buildNetwork(agents);

		assertTrue(MNetworkTools.isStructurallyEqual(networkOne, networkTwo));

	}

}
