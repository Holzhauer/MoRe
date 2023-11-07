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
 * Created by holzhauer on 06.10.2011
 */
package de.cesr.more.testing.rs.building.geo;

import static org.junit.Assert.*;

import java.util.Collection;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.cesr.more.geo.MTorusCoordinate;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.rs.geo.util.MGeoTorusDistanceQuery;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author holzhauer
 * @date 06.10.2011 
 *
 */
public class MGeoDistanceQueryTest {
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MGeoDistanceQueryTest.class);

	static final int NUM_AGENTS = 10;

	Collection<Object> agents;
	Geography<Object> geography;
	GeometryFactory geoFactory ;
	
	TestAgent a100, a001, a050;

	static class TestAgent {
		String id;

		public TestAgent(int id) {
			this.id = new Integer(id).toString();
		}

		@Override
		public String toString() {
			return this.id;
		}
	}

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		Context<Object> context = new DefaultContext<Object>();
		this.geoFactory = new GeometryFactory(new PrecisionModel(),
				new Integer(32632)); // WGS84 UTM 32N
		GeographyParameters<Object> geoParams = new GeographyParameters<Object>();
		geoParams.setCrs("EPSG:32632");
		this.geography = GeographyFactoryFinder.createGeographyFactory(null)
				.createGeography("Geography", context, geoParams);

		
		a100 = new TestAgent(100);
		a001 = new TestAgent(101);
		a050 = new TestAgent(150);
		
		PmParameterManager.setParameter(MBasicPa.TORUS_FIELD_UPPER_X,  new Double(100.0));
		PmParameterManager.setParameter(MBasicPa.TORUS_FIELD_UPPER_Y,  new Double(100.0));
		
		PmParameterManager.setParameter(MBasicPa.TORUS_FIELD_LOWER_X,  new Double(0.0));
		PmParameterManager.setParameter(MBasicPa.TORUS_FIELD_LOWER_Y,  new Double(0.0));
		
		double upper_x = ((Double)PmParameterManager.getParameter(MBasicPa.TORUS_FIELD_UPPER_X)).doubleValue();
		
		geography.move(a100,
				geoFactory.createPoint(new MTorusCoordinate(upper_x, 1)));
		geography.move(a001,
				geoFactory.createPoint(new MTorusCoordinate(1, 1)));
		geography.move(a050,
				geoFactory.createPoint(new MTorusCoordinate(upper_x/2, 1)));
	}
	
	@Test
	public void test() {
		logger.info(geography.getGeometry(a100).distance(geography.getGeometry(a001)));
		logger.info(geography.getGeometry(a100).distance(geography.getGeometry(a050)));
		logger.info(geography.getGeometry(a001).distance(geography.getGeometry(a050)));
	}

	@Test
	public void queryTest() {
		int totalNumObject = 0;
		MGeoTorusDistanceQuery<Object> containsQuery = new MGeoTorusDistanceQuery<Object>(
				this.geography, 50, a100);
		for (@SuppressWarnings("unused") Object agent : containsQuery.query()) {
			totalNumObject++;
		}
		assertEquals(2, totalNumObject);
		
		totalNumObject = 0;
		containsQuery = new MGeoTorusDistanceQuery<Object>(
				this.geography, 20, a100);
		for (@SuppressWarnings("unused") Object agent : containsQuery.query()) {
			totalNumObject++;
		}
		assertEquals(1, totalNumObject);
	}
	
	@Test
	public void queryLowerBoundTest() {
		int totalNumObject = 0;

		PmParameterManager.setParameter(MBasicPa.TORUS_FIELD_LOWER_X, 50.0);
		TestAgent a030 = new TestAgent(30);
		geography.move(a030,
				geoFactory.createPoint(new MTorusCoordinate(30, 1)));

		
		MGeoTorusDistanceQuery<Object> containsQuery = new MGeoTorusDistanceQuery<Object>(
				this.geography, 50, a100);
		for (@SuppressWarnings("unused") Object agent : containsQuery.query()) {
			totalNumObject++;
		}
		assertEquals(3, totalNumObject);
		
		totalNumObject = 0;
		containsQuery = new MGeoTorusDistanceQuery<Object>(
				this.geography, 10, a100);
		for (@SuppressWarnings("unused") Object agent : containsQuery.query()) {
			totalNumObject++;
		}
		assertEquals(1, totalNumObject);

	}
}
