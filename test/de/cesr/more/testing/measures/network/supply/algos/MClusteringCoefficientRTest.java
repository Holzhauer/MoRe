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
package de.cesr.more.testing.measures.network.supply.algos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.measures.network.supply.algos.MClusteringCoefficientR;
import de.cesr.more.measures.util.MRService;
import de.cesr.more.testing.testutils.MTestGraphs;
import de.cesr.more.testing.testutils.MTestGraphs.MTestNode;
import de.cesr.more.util.Log4jLogger;
import de.cesr.more.util.MSchedule;
import edu.uci.ics.jung.graph.Graph;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 03.12.2010 
 *
 */
public class MClusteringCoefficientRTest {
	
	private Logger logger;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		/**
		 * Logger
		 */
		logger = Log4jLogger.getLogger(MClusteringCoefficientRTest.class);
		MManager.setSchedule(new MSchedule());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link de.cesr.more.measures.network.supply.algos.MClusteringCoefficientR#getClusteringCoefficientR(edu.uci.ics.jung.graph.Graph)}.
	 */
	@Test
	public final void testGetClusteringCoefficientR() {
		logger.info("Test calculating clustering coefficient.");
		Graph<MTestNode, MoreEdge<MTestNode>> g = MTestGraphs.getCompleteUndirectedGraph(5);
		double result;
		result = MClusteringCoefficientR.getClusteringCoefficientOverallR(g);
		
		
		assertEquals("( transitivity is : " + result + ")", result, 1.0, 0.001);
	}

	/**
	 * Test method for {@link de.cesr.more.measures.network.supply.algos.MClusteringCoefficientR#createRGraph(edu.uci.ics.jung.graph.Graph)}.
	 */
	@Test
	public final void testCreateRGraph() {
		logger.info("Test graph assignment.");
		Graph<MTestNode, MoreEdge<MTestNode>> g = MTestGraphs.getCompleteUndirectedGraph(5);
		REXP result;
		
		Rengine re = MRService.getRengine();
		MRService.assignGraphObject(re, g, "g");
		
		result = re.eval("is.list(g)");
		assertTrue(result.asBool().isTRUE());
	}
}
