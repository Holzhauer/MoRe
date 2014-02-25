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
 * Created by Sascha Holzhauer on 21.02.2014
 */
package de.cesr.more.testing.param.reader;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildWsPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.reader.MMilieuNetDataCsvReader;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 21.02.2014 
 *
 */
public class MMilieuNetDataCsvReaderTest {

	PmParameterManager	pm;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.pm = PmParameterManager.getNewInstance();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		this.pm = null;
	}

	@Test
	public void testInitParameters() {

		pm.setParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_MILIEUS, "./test/res/SocialNetworkMilieuParameter.csv");
		pm.setParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_DELIMITER, ',');

		new MMilieuNetDataCsvReader(pm).initParameters();

		MMilieuNetworkParameterMap map = (MMilieuNetworkParameterMap) pm
				.getParam(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);

		assertEquals(10, map.getMilieuParam(MNetBuildWsPa.K, 1));
		assertEquals(11, map.getMilieuParam(MNetBuildWsPa.K, 2));
		assertEquals(12, map.getMilieuParam(MNetBuildWsPa.K, 3));
		assertEquals(13, map.getMilieuParam(MNetBuildWsPa.K, 4));

		pm.setParam(MNetBuildWsPa.K, new Integer(4));
		assertEquals(4, map.getMilieuParam(MNetBuildWsPa.K, 5));

		assertEquals(0.01, map.getP_Rewire(2), 0.0001);
		assertEquals(0.1, map.getP_Rewire(3), 0.0001);

		assertEquals("de.cesr.more.util.distributions.MWeibullDistanceDistribution", map.getDistDistributionClass(1));
	}
}
