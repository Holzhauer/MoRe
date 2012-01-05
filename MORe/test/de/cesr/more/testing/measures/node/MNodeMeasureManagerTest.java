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
 * Created by Sascha Holzhauer on 02.01.2012
 */
package de.cesr.more.testing.measures.node;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MEdge;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.MoreMeasureManagerListener;
import de.cesr.more.measures.network.MNetworkMeasureCategory;
import de.cesr.more.measures.network.MNetworkMeasureManager;
import de.cesr.more.measures.node.MAbstractNodeMeasure;
import de.cesr.more.measures.node.MNodeMeasureManager;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.measures.node.supply.MCentralityNodeMSupplier;
import de.cesr.more.measures.util.MAbstractAction;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.testing.testutils.MTestGraphs;
import de.cesr.more.testing.testutils.MTestGraphs.TestNode;
import de.cesr.more.util.MSchedule;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 02.01.2012 
 *
 */
public class MNodeMeasureManagerTest {
	
	MNodeMeasureManager							nodeMan;
	MoreNetwork<TestNode, MoreEdge<TestNode>>	net;
	TestNode									node;
	MSchedule									schedule;
	MMeasureDescription							centDesc;
	MMeasureManagerListener						listener;
	MarkerNodeMeasureSupplier					supplier;
	
	protected static boolean					marker	= false;
	
	/**
	 * 
	 */
	public static void setMarkerTrue() {
		marker = true;
	}
	
	public static class MarkerNodeMeasureSupplier extends MAbstractMeasureSupplier {
		public static final String	MARKER_MEASURE	= "Marker Measure";

		MMeasureDescription			description;

		public MarkerNodeMeasureSupplier() {
			description = new MMeasureDescription(MNetworkMeasureCategory.NETWORK_CENTRALITY, MARKER_MEASURE,
					"Marker Measure");

			measures.put(description, new MAbstractNodeMeasure(description, Double.class) {

				@Override
				public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> MoreAction getAction(MoreNetwork<T, E> network,
						Map<String, Object> parameters) {
					return new MAbstractAction() {
						@Override
						public void execute() {
							MNodeMeasureManagerTest.setMarkerTrue();
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
	

	public static class MMeasureManagerListener implements MoreMeasureManagerListener {

		boolean	added	= false;
		boolean	removed	= false;

		public void reset() {
			added = false;
			removed = false;
		}

		/**
		 * @see de.cesr.more.measures.MoreMeasureManagerListener#networkMeasureCalcAdded(de.cesr.more.basic.network.MoreNetwork,
		 *      de.cesr.more.measures.MMeasureDescription)
		 */
		@Override
		public void networkMeasureCalcAdded(MoreNetwork<?,?> network, MMeasureDescription measure) {
			added = true;
		}

		/**
		 * @see de.cesr.more.measures.MoreMeasureManagerListener#networkMeasureCalcRemoved(de.cesr.more.basic.network.MoreNetwork,
		 *      de.cesr.more.measures.MMeasureDescription)
		 */
		@Override
		public void networkMeasureCalcRemoved(MoreNetwork<?,?> network, MMeasureDescription measure) {
			removed = true;
		}
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MManager.setSchedule(new MSchedule());
		schedule = (MSchedule) MManager.getSchedule();
		nodeMan = MNodeMeasureManager.getInstance();
		net = MDirectedNetwork.getNetwork(new MoreEdgeFactory<TestNode, MoreEdge<TestNode>>() {
			@Override
			public MoreEdge<TestNode> createEdge(TestNode source, TestNode target, boolean directed) {
				return new MEdge<TestNode>(source, target);
			}
		}, MTestGraphs.getCompleteDirectedGraph(4), "TestNet");
		node = net.getNodes().iterator().next();
		centDesc = new MMeasureDescription(MCentralityNodeMSupplier.Short.NODE_CEN_INDEGREE_NN.getName());
		this.supplier = new MarkerNodeMeasureSupplier();
		nodeMan.addMeasureSupplier(supplier);
		marker = false;
		listener = new MMeasureManagerListener();
	}


	
	/**
	 * Test method for
	 * {@link de.cesr.more.measures.MAbstractMeasureSupplier#findMeasure(de.cesr.more.measures.MMeasureDescription)}.
	 */
	@Test
	public final void testFindMeasure() {
		assertEquals(
				"The description of the obtained MoreMeasure should match the description by which it is searched",
				centDesc, nodeMan.findMeasure(centDesc).getMeasureDescription());
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.network.MNetworkMeasureManager#addMeasureCalculation(de.cesr.more.basic.network.MoreNetwork, de.cesr.more.measures.MMeasureDescription, java.util.Map)}
	 * .
	 */
	@Test
	public final void testAddMeasureCalculationDefault() {
		// using default parameters:
		nodeMan.addMeasureCalculation(net, MarkerNodeMeasureSupplier.MARKER_MEASURE);
		schedule.step(1);
		assertTrue("Added measure should be scheduled", marker);
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.network.MNetworkMeasureManager#addMeasureCalculation(de.cesr.more.basic.network.MoreNetwork, de.cesr.more.measures.MMeasureDescription, java.util.Map)}
	 * .
	 */
	@Test
	public final void testAddMeasureCalculationCustomParam() {
		marker = false;
		// using custom parameters:
		// / empty map:
		Map<String, Object> params = new HashMap<String, Object>();
		nodeMan.addMeasureCalculation(net, MarkerNodeMeasureSupplier.MARKER_MEASURE);
		schedule.step(1);
		assertTrue("Added measure should be scheduled", marker);
		marker = false;
		schedule.step(2);
		assertTrue("Added measure should be scheduled", marker);
		nodeMan.removeMeasureCalculation(net, MarkerNodeMeasureSupplier.MARKER_MEASURE);

		// / altered default map:
		params = nodeMan.findMeasure(new MMeasureDescription(MarkerNodeMeasureSupplier.MARKER_MEASURE))
				.getParameters();
		params.put(MNetworkMeasureManager.ParameterKeys.START.toString(), new Integer(10));
		params.put(MNetworkMeasureManager.ParameterKeys.INTERVAL.toString(), new Double(2.0));
		nodeMan.addMeasureCalculation(net, MarkerNodeMeasureSupplier.MARKER_MEASURE, params);
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
	 * {@link de.cesr.more.measures.network.MNetworkMeasureManager#getMeasureCalculations(de.cesr.more.basic.network.MoreNetwork)}
	 * .
	 */
	@Test
	public final void testGetMeasureCalculations() {
		nodeMan.addMeasureCalculation(net, MCentralityNodeMSupplier.Short.NODE_CEN_INDEGREE_NN.getName());
		assertTrue("Should contain the added Network Measure", nodeMan.getMeasureCalculations(net).contains(
				new MMeasureDescription(MCentralityNodeMSupplier.Short.NODE_CEN_INDEGREE_NN.getName())));
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.network.MNetworkMeasureManager#removeMeasureCalculation(de.cesr.more.basic.network.MoreNetwork, de.cesr.more.measures.MMeasureDescription)}
	 * .
	 */
	@Test
	public final void testRemoveMeasureCalculation() {
		int step = 0;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Interval", new Double(1.0));

		nodeMan.addMeasureCalculation(net, centDesc.getShort(), params);
		schedule.step(step++);
		nodeMan.removeMeasureCalculation(net, new MMeasureDescription(centDesc.getShort()));
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
		nodeMan.addMeasureManagerListener(listener);
		nodeMan.addMeasureCalculation(net, centDesc.getShort());
		assertTrue("Listener should have been informed", listener.added);
		nodeMan.removeMeasureCalculation(net, centDesc);
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
		nodeMan.addMeasureManagerListener(listener);
		nodeMan.addMeasureCalculation(net, centDesc.getShort());
		assertTrue("Listener should have been informed", listener.added);
		nodeMan.removeMeasureCalculation(net, centDesc);
		assertTrue("Listener should have been informed", listener.removed);

		listener.reset();
		nodeMan.removeMeasureManagerListener(listener);
		nodeMan.addMeasureCalculation(net, centDesc.getShort());
		assertFalse("Listener should not have been informed", listener.added);
		nodeMan.removeMeasureCalculation(net, centDesc);
		assertFalse("Listener should not have been informed", listener.removed);

	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.MAbstractMeasureManager#getRemovableMeasures(de.cesr.more.basic.network.MoreNetwork)}.
	 */
	@Test
	public final void testGetRemovableMeasures() {
		nodeMan.addMeasureCalculation(net, centDesc.getShort());
		assertTrue("Previously added measure should be removeable", nodeMan.getRemovableMeasures(net).contains(centDesc));
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.MAbstractMeasureManager#getAddableMeasures(de.cesr.more.basic.network.MoreNetwork)}.
	 */
	@Test
	public final void testGetAddableMeasures() {
		assertTrue("Should at least contain the measure added at start up", nodeMan.getAddableMeasures(net).contains(
				new MMeasureDescription(MarkerNodeMeasureSupplier.MARKER_MEASURE)));
		nodeMan.addMeasureCalculation(net, MarkerNodeMeasureSupplier.MARKER_MEASURE);
		assertFalse("Since it was added in meantime it should not be addable now", nodeMan.getAddableMeasures(net)
				.contains(new MMeasureDescription(MarkerNodeMeasureSupplier.MARKER_MEASURE)));
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.measures.MAbstractMeasureSupplier#addMeasureSupplier(de.cesr.more.measures.MoreMeasureSupplier)}
	 * .
	 */
	@Test
	public final void testAddMeasureSupplier() {
		assertTrue(nodeMan.removeMeasureSupplier(supplier));
		assertFalse("Since the supplier was removed in meantime it should not be addable now", nodeMan
				.getAddableMeasures(net).contains(new MMeasureDescription(MarkerNodeMeasureSupplier.MARKER_MEASURE)));

		assertTrue(nodeMan.addMeasureSupplier(supplier));
		assertTrue("Should at least contain the measure added at start up", nodeMan.getAddableMeasures(net).contains(
				new MMeasureDescription(MarkerNodeMeasureSupplier.MARKER_MEASURE)));
	}

	/**
	 * 
	 */
	@Test
	public final void testCentralityDegreeMeasure() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(MNetworkMeasureManager.ParameterKeys.INTERVAL.toString(), new Double(1.0));
		nodeMan.addMeasureCalculation(net, MCentralityNodeMSupplier.Short.NODE_CEN_INDEGREE_NN.getName(), params);
		schedule.step(1);
		schedule.step(2);
		assertEquals("centrality-indegree is wrong (should be 3 in a complete, directed network with n=4)", 3.0,
				node.getNetworkMeasureObject(net, new MMeasureDescription(MCentralityNodeMSupplier.Short.NODE_CEN_INDEGREE_NN.getName())).doubleValue(),
				0.001);
	}
}
