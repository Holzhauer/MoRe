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
 * Created by Sascha Holzhauer on 21.03.2014
 */
package de.cesr.more.testing.util;


import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.random.MersenneTwister;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.more.param.MNetBuildHdffPa;
import de.cesr.more.util.distributions.MIntegerDistribution;
import de.cesr.more.util.distributions.MPascalDistribution;
import de.cesr.more.util.distributions.MPascalDistribution.MPascalDistributionParam;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 21.03.2014 
 *
 */
public class MPascalDistributionTest {

	MIntegerDistribution	pascal;

	public final String		OUTPUT_FILE_DENSITY		= "./logs/pascal/PascalDistribution_density.csv";
	public final String		OUTPUT_FILE_CUMULATIV	= "./logs/pascal/PascalDistribution_cumulativ.csv";
	public final String		OUTPUT_FILE_RANDOM		= "./logs/pascal/PascalDistribution_random.csv";

	public final boolean	OUTPUT					= true;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.pascal = new MPascalDistribution(new MersenneTwister());
		this.pascal.setParameter(MPascalDistributionParam.R,
				((Double) PmParameterManager.getParameter(MNetBuildHdffPa.K_PARAM_A)));
		this.pascal.setParameter(MPascalDistributionParam.P,
				((Double) PmParameterManager.getParameter(MNetBuildHdffPa.K_PARAM_B)));
		this.pascal.init();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		this.pascal = null;
	}

	/**
	 * Test method for {@link de.cesr.more.util.distributions.MWeibullDistanceDistribution#density(double)}.
	 */
	@Test
	public void testDensity() {
		double density = 0.0;
		try {
			FileWriter fileWriter = new FileWriter(new File(OUTPUT_FILE_DENSITY), false);
			for (int i = 0; i < 100; i++) {
				density = this.pascal.probability(i);
				assertTrue(density <= 1);
				if (OUTPUT) {
					fileWriter.append(i + ", " + density + "\n");
				}
			}
			fileWriter.close();

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.util.distributions.MWeibullDistanceDistribution#cumulativeProbability(double)}.
	 */
	@Test
	public void testCumulativeProbabilityDouble() {
		double sample = 0.0;
		try {
			FileWriter fileWriter = new FileWriter(new File(OUTPUT_FILE_RANDOM), false);
			for (int i = 0; i < 100000; i++) {
				sample = this.pascal.sample();
				if (OUTPUT) {
					fileWriter.append(i + ", " + sample + "\n");
				}
			}
			fileWriter.close();

			fileWriter = new FileWriter(new File(OUTPUT_FILE_CUMULATIV), false);
			for (int i = 0; i < 100; i++) {
				if (OUTPUT) {
					fileWriter.append(i / 10.0 + ", " + this.pascal.cumulativeProbability(i) + "\n");
				}
			}
			fileWriter.close();

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
