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
import de.cesr.more.param.MNetBuildBhPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.reader.MMilieuNetLinkDataCsvReader;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 21.02.2014 
 *
 */
public class MMilieuNetLinkDataCsvReaderTest {

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

		pm.setParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_MILIEULINKS,
				"./test/res/SocialNetworkMilieuLinksParameter.csv");
		pm.setParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_DELIMITER, ',');

		new MMilieuNetLinkDataCsvReader(pm).initParameters();

		MMilieuNetworkParameterMap map = (MMilieuNetworkParameterMap) pm
				.getParam(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);

		assertEquals(0.25, map.getP_Milieu(1, 1), 0.0001);
		assertEquals(0.3, map.getP_Milieu(3, 1), 0.0001);
		assertEquals(0.2, map.getP_Milieu(3, 2), 0.0001);
		assertEquals(0.25, map.getP_Milieu(1, 1), 0.0001);
		assertEquals(0.25, map.getP_Milieu(1, 1), 0.0001);

		pm.setParam(MNetBuildBhPa.P_MILIEUS, new Double(0.99));
		assertEquals(0.25, map.getP_Milieu(1, 1), 0.00001);
	}
}
