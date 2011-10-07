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
 * Created by Sascha Holzhauer on 11.01.2011
 */
package de.cesr.more.testing.measures.network.supply.algos;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.more.basic.MManager;
import de.cesr.more.measures.network.supply.algos.MNetworkStatisticsR;
import de.cesr.more.standalone.MSchedule;
import de.cesr.more.testing.MTestGraphs;
import de.cesr.more.util.Log4jLogger;
import edu.uci.ics.jung.graph.Graph;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 11.01.2011 
 *
 */
public class MNetworkStatisticsRTest {

	
	private Logger logger;
	
	/**
	 * @throws java.lang.Exception
	 * Created by Sascha Holzhauer on 11.01.2011
	 */
	@Before
	public void setUp() throws Exception {
		/**
		 * Logger
		 */
		logger = Log4jLogger.getLogger(MNetworkStatisticsRTest.class);
		MManager.setSchedule(new MSchedule());
	}

	/**
	 * @throws java.lang.Exception
	 * Created by Sascha Holzhauer on 11.01.2011
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link de.cesr.more.measures.network.supply.algos.MNetworkStatisticsR#getAveragepathLengthR(edu.uci.ics.jung.graph.Graph)}.
	 */
	@Test
	public final void testGetAveragepathLengthR() {
		logger.info("Test calculating average path length.");
		Graph g = MTestGraphs.getCompleteUndirectedGraph(5);
		double result = MNetworkStatisticsR.getAveragepathLengthR(g, false);
		assertEquals("(Calculated Average Path length is : " + result + ")", result, 1.0, 0.001);
	}

}
