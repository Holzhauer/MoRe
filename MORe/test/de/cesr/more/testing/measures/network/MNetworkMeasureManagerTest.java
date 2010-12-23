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
 * Created by Sascha Holzhauer on 10.12.2010
 */
package de.cesr.more.testing.measures.network;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.more.basic.MEdge;
import de.cesr.more.basic.MNetworkManager;
import de.cesr.more.basic.MoreEdge;
import de.cesr.more.io.MoreEdgeFactory;
import de.cesr.more.measures.MAbstractMeasureManager;
import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.MNetworkMeasureCategory;
import de.cesr.more.measures.MoreMeasureManagerListener;
import de.cesr.more.measures.measures.MAbstractNetworkMeasure;
import de.cesr.more.measures.network.MNetworkMeasureManager;
import de.cesr.more.measures.network.supply.MCentralityNetMSupplier;
import de.cesr.more.measures.util.MAbstractAction;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.networks.MDirectedNetwork;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.standalone.MSchedule;
import de.cesr.more.testing.MTestGraphs;
import de.cesr.more.testing.MTestGraphs.TestNode;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 10.12.2010
 * 
 */
public class MNetworkMeasureManagerTest {

	MNetworkMeasureManager						netMan;
	MoreNetwork<TestNode, MoreEdge<TestNode>>	net;
	MSchedule									schedule;
	MMeasureDescription							centDesc;
	MeasureManagerListenerImpl					listener;
	MarkerNetworkMeasureSupplier				supplier;

	protected static boolean					marker	= false;

	public static class MarkerNetworkMeasureSupplier extends MAbstractMeasureSupplier {
		public static final String	MARKER_MEASURE	= "Marker Measure";

		MMeasureDescription			description;

		public MarkerNetworkMeasureSupplier() {
			description = new MMeasureDescription(MNetworkMeasureCategory.NETWORK_CENTRALITY, MARKER_MEASURE,
					"Marker Measure");

			measures.put(description, new MAbstractNetworkMeasure(description, Double.class) {

				@Override
				public <T, EdgeType> MoreAction getAction(final MoreNetwork<T, EdgeType> network,
						Map<String, Object> parameters) {
					return new MAbstractAction() {
						@Override
						public void execute() {
							MNetworkMeasureManagerTest.setMarkerTrue();
						}

						@Override
						public String toString() {
							return MARKER_MEASURE;
						}
					};
				}
			});
		}
	}

	public static class MeasureManagerListenerImpl implements MoreMeasureManagerListener {

		boolean	added	= false;
		boolean	removed	= false;

		public void reset() {
			added = false;
			removed = false;
		}

		/**
		 * @see de.cesr.more.measures.MoreMeasureManagerListener#networkMeasureCalcAdded(de.cesr.more.networks.MoreNetwork,
		 *      de.cesr.more.measures.MMeasureDescription)
		 */
		@Override
		public void networkMeasureCalcAdded(MoreNetwork network, MMeasureDescription measure) {
			added = true;
		}

		/**
		 * @see de.cesr.more.measures.MoreMeasureManagerListener#networkMeasureCalcRemoved(de.cesr.more.networks.MoreNetwork,
		 *      de.cesr.more.measures.MMeasureDescription)
		 */
		@Override
		public void networkMeasureCalcRemoved(MoreNetwork network, MMeasureDescription measure) {
			removed = true;
		}
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 22.12.2010
	 */
	public static void setMarkerTrue() {
		marker = true;
	}

	/**
	 * @throws java.lang.Exception Created by Sascha Holzhauer on 10.12.2010
	 */
	@Before
	public void setUp() throws Exception {
		schedule = new MSchedule();
		MAbstractMeasureManager.setSchedule(schedule);
		netMan = MNetworkMeasureManager.getInstance();
		net = MDirectedNetwork.getNetwork(new MoreEdgeFactory<TestNode, MoreEdge<TestNode>>() {
			public MoreEdge<TestNode> createEdge(TestNode source, TestNode target, boolean directed) {
				return new MEdge<TestNode>(source, target);
			}
		}, MTestGraphs.getCompleteDirectedGraph(4));
		centDesc = new MMeasureDescription(MCentralityNetMSupplier.MCenShort.NET_CEN_DEGREE.toString());
		this.supplier = new MarkerNetworkMeasureSupplier();
		netMan.addMeasureSupplier(supplier);
		marker = false;
		listener = new MeasureManagerListenerImpl();
	}

	/**
	 * @throws java.lang.Exception Created by Sascha Holzhauer on 10.12.2010
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.MAbstractMeasureSupplier#findMeasure(de.cesr.more.measures.MMeasureDescription)}.
	 */
	@Test
	public final void testFindMeasure() {
		assertEquals(
				"The description of the obtained MoreMeasure should match the description by which it is searched",
				centDesc, netMan.findMeasure(centDesc).getMeasureDescription());
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.network.MNetworkMeasureManager#addMeasureCalculation(de.cesr.more.networks.MoreNetwork, de.cesr.more.measures.MMeasureDescription, java.util.Map)}
	 * .
	 */
	@Test
	public final void testAddMeasureCalculationDefault() {
		// using default parameters:
		netMan.addMeasureCalculation(net, MarkerNetworkMeasureSupplier.MARKER_MEASURE);
		schedule.step(1);
		assertTrue("Added measure should be scheduled", marker);
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.network.MNetworkMeasureManager#addMeasureCalculation(de.cesr.more.networks.MoreNetwork, de.cesr.more.measures.MMeasureDescription, java.util.Map)}
	 * .
	 */
	@Test
	public final void testAddMeasureCalculationCustomParam() {
		// using custom parameters:
		// / empty map:
		Map<String, Object> params = new HashMap<String, Object>();
		netMan.addMeasureCalculation(net, MarkerNetworkMeasureSupplier.MARKER_MEASURE);
		schedule.step(1);
		assertTrue("Added measure should be scheduled", marker);
		marker = false;
		schedule.step(2);
		assertTrue("Added measure should be scheduled", marker);
		netMan.removeMeasureCalculation(net, MarkerNetworkMeasureSupplier.MARKER_MEASURE);

		// / altered default map:
		params = netMan.findMeasure(new MMeasureDescription(MarkerNetworkMeasureSupplier.MARKER_MEASURE))
				.getParameters();
		params.put(MNetworkMeasureManager.ParameterKeys.START.toString(), new Integer(10));
		params.put(MNetworkMeasureManager.ParameterKeys.INTERVAL.toString(), new Double(2.0));
		netMan.addMeasureCalculation(net, MarkerNetworkMeasureSupplier.MARKER_MEASURE, params);
		marker = false;
		schedule.step(2);
		assertFalse("Added measure should not be scheduled", marker);
		schedule.step(9);
		assertFalse("Added measure should not be scheduled", marker);
		schedule.step(10);
		assertTrue("Added measure should be scheduled", marker);
		marker = false;
		schedule.step(11);
		assertFalse("Added measure should not be scheduled", marker);
		marker = false;
		schedule.step(12);
		assertTrue("Added measure should be scheduled", marker);
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.network.MNetworkMeasureManager#getMeasureCalculations(de.cesr.more.networks.MoreNetwork)}
	 * .
	 */
	@Test
	public final void testGetMeasureCalculations() {
		netMan.addMeasureCalculation(net, MCentralityNetMSupplier.MCenShort.NET_CEN_DEGREE.toString());
		assertTrue("Should contain the added Network Measure", netMan.getMeasureCalculations(net).contains(
				new MMeasureDescription(MCentralityNetMSupplier.MCenShort.NET_CEN_DEGREE.toString())));
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.network.MNetworkMeasureManager#removeMeasureCalculation(de.cesr.more.networks.MoreNetwork, de.cesr.more.measures.MMeasureDescription)}
	 * .
	 */
	@Test
	public final void testRemoveMeasureCalculation() {
		int step = 0;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Interval", new Double(1.0));

		netMan.addMeasureCalculation(net, centDesc.getShort(), params);
		schedule.step(step++);
		netMan.removeMeasureCalculation(net, new MMeasureDescription(centDesc.getShort()));
		schedule.step(step++);
		Iterator<TestNode> iter = net.getNodes().iterator();
		assertNull("centrality-degree is wrong for node1", iter.next().getNetworkMeasureObject(net,
				new MMeasureDescription(centDesc.getShort())));
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.MAbstractMeasureManager#addMeasureManagerListener(de.cesr.more.measures.MoreMeasureManagerListener)}
	 * .
	 */
	@Test
	public final void testAddMeasureManagerListener() {
		listener.reset();
		netMan.addMeasureManagerListener(listener);
		netMan.addMeasureCalculation(net, centDesc.getShort());
		assertTrue("Listener should have been informed", listener.added);
		netMan.removeMeasureCalculation(net, centDesc);
		assertTrue("Listener should have been informed", listener.removed);
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.MAbstractMeasureManager#removeMeasureManagerListener(de.cesr.more.measures.MoreMeasureManagerListener)}
	 * .
	 */
	@Test
	public final void testRemoveMeasureManagerListener() {
		listener.reset();
		netMan.addMeasureManagerListener(listener);
		netMan.addMeasureCalculation(net, centDesc.getShort());
		assertTrue("Listener should have been informed", listener.added);
		netMan.removeMeasureCalculation(net, centDesc);
		assertTrue("Listener should have been informed", listener.removed);

		listener.reset();
		netMan.removeMeasureManagerListener(listener);
		netMan.addMeasureCalculation(net, centDesc.getShort());
		assertFalse("Listener should not have been informed", listener.added);
		netMan.removeMeasureCalculation(net, centDesc);
		assertFalse("Listener should not have been informed", listener.removed);

	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.MAbstractMeasureManager#getRemovableMeasures(de.cesr.more.networks.MoreNetwork)}.
	 */
	@Test
	public final void testGetRemovableMeasures() {
		netMan.addMeasureCalculation(net, centDesc.getShort());
		assertTrue("Previously added measure should be removeable", netMan.getRemovableMeasures(net).contains(centDesc));
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.MAbstractMeasureManager#getAddableMeasures(de.cesr.more.networks.MoreNetwork)}.
	 */
	@Test
	public final void testGetAddableMeasures() {
		assertTrue("Should at least contain the measure added at start up", netMan.getAddableMeasures(net).contains(
				new MMeasureDescription(MarkerNetworkMeasureSupplier.MARKER_MEASURE)));
		netMan.addMeasureCalculation(net, MarkerNetworkMeasureSupplier.MARKER_MEASURE);
		assertFalse("Since it was added in meantime it should not be addable now", netMan.getAddableMeasures(net)
				.contains(new MMeasureDescription(MarkerNetworkMeasureSupplier.MARKER_MEASURE)));
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.MAbstractMeasureSupplier#addMeasureSupplier(de.cesr.more.measures.MoreMeasureSupplier)}
	 * .
	 */
	@Test
	public final void testAddMeasureSupplier() {
		assertTrue(netMan.removeMeasureSupplier(supplier));
		assertFalse("Since the supplier was removed in meantime it should not be addable now", netMan
				.getAddableMeasures(net).contains(new MMeasureDescription(MarkerNetworkMeasureSupplier.MARKER_MEASURE)));

		assertTrue(netMan.addMeasureSupplier(supplier));
		assertTrue("Should at least contain the measure added at start up", netMan.getAddableMeasures(net).contains(
				new MMeasureDescription(MarkerNetworkMeasureSupplier.MARKER_MEASURE)));
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 23.12.2010
	 */
	@Test
	public final void testCentralityDegreeMeasure() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(MNetworkMeasureManager.ParameterKeys.INTERVAL.toString(), new Double(1.0));
		netMan.addMeasureCalculation(net, MCentralityNetMSupplier.MCenShort.NET_CEN_DEGREE.toString(), params);
		schedule.step(1);
		schedule.step(2);
		assertEquals("centrality-degree is wrong (should be 6 in a complete, directed network with n=4)", 6.0,
				MNetworkManager.getNetworkMeasure(net,
						new MMeasureDescription(MCentralityNetMSupplier.MCenShort.NET_CEN_DEGREE.toString()))
						.doubleValue(), 0.001);
	}

}
