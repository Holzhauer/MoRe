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
 */
package de.cesr.more.testing.rs.geo.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.operation.DefaultCoordinateOperationFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import repast.simphony.space.gis.UTMFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import de.cesr.more.rs.geo.util.MGeographyWrapper;


/**
 * 
 * TODO test getSurroundingNAgents
 * 
 * @author Sascha Holzhauer
 * @date 26.07.2010
 * 
 */
public class MGeographyWrapperTest {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MGeographyWrapperTest.class);

	private static class TestAgent {
		private String	name	= "Name";

		public TestAgent(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	Geography<Object>		geo;
	Geometry				area;
	Point					center;
	TestAgent				centerAgent;
	ArrayList<TestAgent>	agents	= new ArrayList<TestAgent>();

	/**
	 * @throws Exception
	 *         Created by Sascha Holzhauer on 26.07.2010
	 */
	@Before
	public void setUp() throws Exception {
		// set up geography
		Context<Object> context = new DefaultContext<Object>();
		GeographyParameters<Object> geoParams = new GeographyParameters<Object>();
		geoParams.setCrs("EPSG:4326");
		geo = GeographyFactoryFinder.createGeographyFactory(null).createGeography("AreaGeography", context, geoParams);
		// geo.setCRS(DefaultGeographicCRS.WGS84); works...

		// ad an area
		GeometryFactory geoFac = new GeometryFactory();
		area = geoFac.toGeometry(new Envelope(49.95, 50.05, 49.95, 50.05));
		center = geoFac.createPoint(new Coordinate(50.0, 50.0));

		DefaultCoordinateOperationFactory cFactory = new DefaultCoordinateOperationFactory();
		CoordinateReferenceSystem crs = geo.getCRS();
		CoordinateReferenceSystem utmCenter = UTMFinder.getUTMFor(center, crs);
		Point centerUTM = (Point) JTS.transform(center, cFactory.createOperation(crs, utmCenter).getMathTransform());

		centerAgent = new TestAgent("Center");
		geo.move(centerAgent, center);
		geo.move(area, area);

		/*
		 * add 10 agents within the area, 10 without with increasing distance to a fix point every 2 agents should have
		 * equal distance to fix point
		 */
		for (int i = 1; i <= 10; i++) {
			TestAgent agent1 = new TestAgent("1-" + i);
			Coordinate p1 = new Coordinate(50 + (0.01 * i), 50 + (0.01 * i));
			agents.add(agent1);
			geo.move(agent1, geoFac.createPoint(p1));
			if (logger.isDebugEnabled()) {
				CoordinateReferenceSystem utmP1 = UTMFinder.getUTMFor(geoFac.createPoint(p1), crs);
				Point p1UTM = (Point) JTS.transform(geoFac.createPoint(p1), cFactory.createOperation(crs, utmP1)
						.getMathTransform());
				logger.debug("Distance between center and agent1-" + i + ": " + centerUTM.distance(p1UTM));
			}

			TestAgent agent2 = new TestAgent("2-" + i);
			Coordinate p2 = new Coordinate(50 + (0.01 * -i), 50 + (0.01 * -i));
			agents.add(agent2);
			geo.move(agent2, geoFac.createPoint(p2));
			if (logger.isDebugEnabled()) {
				CoordinateReferenceSystem utmP2 = UTMFinder.getUTMFor(geoFac.createPoint(p2), crs);
				Point p2UTM = (Point) JTS.transform(geoFac.createPoint(p2), cFactory.createOperation(crs, utmP2)
						.getMathTransform());
				logger.debug("Distance between center and agent2-" + i + ": " + centerUTM.distance(p2UTM));
			}
		}

	}

	/**
	 * @throws Exception
	 *         Created by Sascha Holzhauer on 26.07.2010
	 */
	@After
	public void tearDown() throws Exception {
		agents = null;
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 26.07.2010
	 */
	@Test
	public final void testGetSourroundingAgents() {
		// 0.01° ~ 1.1132 km at equator (?)
		MGeographyWrapper<Object> geoWrap = new MGeographyWrapper<Object>(geo);
		Collection<TestAgent> agentResult;

		// check if 4 agents are found that are contained in the area and are nearest
		agentResult = (geoWrap.getSurroundingNAgents(centerAgent, 4, area, 3.0, TestAgent.class));
		assertEquals("ResultSet should contain four agents", 4, agentResult.size());
		for (int i = 0; i < 4; i++) {
			assertTrue("Agent " + (i + 1) + " should be contained", agentResult.contains(agents.get(i)));
		}
		// check if 10 agents are found within the area when requested 12
		agentResult = (geoWrap.getSurroundingNAgents(centerAgent, 12, area, 16.0, TestAgent.class));
		assertEquals("ResultSet should contain four agents", 8, agentResult.size());
		for (int i = 0; i < 8; i++) {
			assertTrue("Agent " + (i + 1) + " should be contained", agentResult.contains(agents.get(i)));
		}

		// check if 6 agents are found within the area when radius is chosen too small
		agentResult = (geoWrap.getSurroundingNAgents(centerAgent, 6, area, 1.0, TestAgent.class));
		assertEquals("ResultSet should contain four agents", 6, agentResult.size());
		for (int i = 0; i < 6; i++) {
			assertTrue("Agent " + (i + 1) + " should be contained", agentResult.contains(agents.get(i)));
		}

		// check if 3 agents are found within the area that are nearest
		agentResult = (geoWrap.getSurroundingNAgents(centerAgent, 3, area, 15.0, TestAgent.class));
		assertEquals("ResultSet should contain four agents", 3, agentResult.size());
		for (int i = 0; i < 3; i++) {
			assertTrue("Agent " + (i + 1) + " should be contained", agentResult.contains(agents.get(i)));
		}
	}
}
