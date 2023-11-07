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
 * Created by Sascha Holzhauer on 20.03.2014
 */
package de.cesr.more.testing.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.random.MersenneTwister;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.more.param.MNetBuildHdffPa;
import de.cesr.more.util.distributions.MLinearLocalWeibullDistanceDistribution;
import de.cesr.more.util.distributions.MRealDistribution;
import de.cesr.more.util.distributions.MWeibullDistanceDistribution.MWeibullDistanceDistParams;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 20.03.2014 
 *
 */
public class MLinearLocalWeibullDistanceDistributionTest  {

	MRealDistribution		linearLocal;

	public final String		OUTPUT_FILE_DENSITY		= "./logs/linearLocal/LinearLocalWeibullDistribution_density.csv";
	public final String		OUTPUT_FILE_CUMULATIV	= "./logs/linearLocal/LinearLocalWeibullDistribution_cumulativ.csv";
	public final String		OUTPUT_FILE_RANDOM		= "./logs/linearLocal/LinearLocalWeibullDistribution_random.csv";
	public final String		OUTPUT_FILE_INVERSE		= "./logs/linearLocal/LinearLocalWeibullDistribution_inverse.csv";

	public final double		DIAMETER			= 1000.0;

	public final boolean	OUTPUT				= true;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.linearLocal = new MLinearLocalWeibullDistanceDistribution(new MersenneTwister());
		this.linearLocal.setParameter(MWeibullDistanceDistParams.SHAPE,
				((Double) PmParameterManager.getParameter(MNetBuildHdffPa.DIST_PARAM_A)));
		this.linearLocal.setParameter(MWeibullDistanceDistParams.SCALE,
				((Double) PmParameterManager.getParameter(MNetBuildHdffPa.DIST_PARAM_B)));
		this.linearLocal.setParameter(MWeibullDistanceDistParams.XMIN,
				((Double) PmParameterManager.getParameter(MNetBuildHdffPa.DIST_PARAM_XMIN)));
		this.linearLocal.setParameter(MWeibullDistanceDistParams.PLOCAL,
				((Double) PmParameterManager.getParameter(MNetBuildHdffPa.DIST_PARAM_PLOCAL)));
		this.linearLocal.setParameter(MWeibullDistanceDistParams.XMAX, DIAMETER);
		this.linearLocal.init();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link de.cesr.more.util.distributions.MWeibullDistanceDistribution#density(double)}.
	 */
	@Test
	public void testDensity() {
		double density = 0.0;

		try {
			FileWriter fileWriter = new FileWriter(new File(OUTPUT_FILE_DENSITY), false);
			for (int i = 0; i < 1000; i++) {
				density = this.linearLocal.density(i / 10.0);
				assertTrue(density <= 1);
				if (OUTPUT) {
					fileWriter.append(i / 10.0 + ", " + density + "\n");
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


		assertEquals((Double) PmParameterManager.getParameter(MNetBuildHdffPa.DIST_PARAM_PLOCAL),
				this.linearLocal.cumulativeProbability((Double) PmParameterManager
						.getParameter(MNetBuildHdffPa.DIST_PARAM_XMIN)),
				0.00001);


		try {
			FileWriter fileWriter = new FileWriter(new File(OUTPUT_FILE_RANDOM), false);
			for (int i = 0; i < 10000; i++) {
				sample = this.linearLocal.sample();
				assertTrue("" + sample, sample <= DIAMETER);
				if (OUTPUT) {
					fileWriter.append(i + ", " + sample + "\n");
				}
			}
			fileWriter.close();

			fileWriter = new FileWriter(new File(OUTPUT_FILE_CUMULATIV), false);
			for (int i = 0; i < 10000; i++) {
				if (OUTPUT) {
					fileWriter.append(i / 10.0 + ", " + this.linearLocal.cumulativeProbability(i / 10.0) + "\n");
				}
			}
			fileWriter.close();

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link de.cesr.more.util.distributions.MLinearLocalWeibullDistanceDistribution#inverseCumulativeProbability(double)}
	 * .
	 */
	@Test
	public void testInverseCumulativeProbabilityDouble() {
		double sample = 0.0;
		try {
			FileWriter fileWriter = new FileWriter(new File(OUTPUT_FILE_INVERSE), false);
			for (int i = 0; i < 10000; i++) {
				sample = this.linearLocal.inverseCumulativeProbability(i / 10000.0);
				assertTrue("" + sample, sample <= DIAMETER);
				if (OUTPUT) {
					fileWriter.append(i / 10000.0 + ", " + sample + "\n");
				}
			}
			fileWriter.close();

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
