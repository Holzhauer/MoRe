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
 * Created by Sascha Holzhauer on 05.12.2013
 */
package de.cesr.more.testing.basic;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import repast.simphony.context.DefaultContext;
import de.cesr.more.basic.MManager;
import de.cesr.more.param.MRandomPa;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.uranus.core.UranusRandomService;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 05.12.2013 
 *
 */
public class MManagerTest {

	UranusRandomService	randomService;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MManager.setRootContext(new DefaultContext<Object>());
		randomService = MManager.getURandomService();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRandomStreamInitialisation() {
		// Set up parameters:
		// Set a custom random seed for MRandomPa.RANDOM_SEED
		PmParameterManager.setParameter(MRandomPa.RANDOM_SEED, new Integer(42));

		MManager.init();

		// Random Stream for initialisation should be set whereas other streams should not be affected:
		assertTrue(PmParameterManager.isCustomised(MRandomPa.RANDOM_SEED));
		assertTrue(randomService.isGeneratorRegistered((String) PmParameterManager
				.getParameter(MRandomPa.RND_STREAM)));

		// depending random stream should point to the new one as well:
		assertFalse(PmParameterManager.isCustomised(MRandomPa.RANDOM_SEED_NETWORK_BUILDING));
		assertFalse(PmParameterManager.getParameter(MRandomPa.RND_STREAM_NETWORK_BUILDING) == MRandomPa.RND_STREAM_NETWORK_BUILDING_CUSTOMISED);

		assertFalse(randomService.isGeneratorRegistered(MRandomPa.RND_STREAM_NETWORK_BUILDING_CUSTOMISED));
	}

	@Test
	public void testRandomStreamAgentInitialisation() {
		// Set up parameters:
		// Set a custom random seed for GRandomPa.RANDOM_SEED_NETWORK_BUILDING
		PmParameterManager.setParameter(MRandomPa.RANDOM_SEED_NETWORK_BUILDING, new Integer(42));

		MManager.init();

		// Random Stream for initialisation should be set whereas other streams should not be affected:
		assertTrue(PmParameterManager.isCustomised(MRandomPa.RANDOM_SEED_NETWORK_BUILDING));
		assertEquals(MRandomPa.RND_STREAM_NETWORK_BUILDING_CUSTOMISED,
				PmParameterManager.getParameter(MRandomPa.RND_STREAM_NETWORK_BUILDING));
		assertTrue(randomService.isGeneratorRegistered((String) PmParameterManager
				.getParameter(MRandomPa.RND_STREAM_NETWORK_BUILDING)));
	}
}
