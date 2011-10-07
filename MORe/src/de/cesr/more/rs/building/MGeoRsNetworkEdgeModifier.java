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
 * Created by holzhauer on 28.09.2011
 */
package de.cesr.more.rs.building;

import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.operation.DefaultCoordinateOperationFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.spatialschema.geometry.MismatchedDimensionException;

import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.UTMFinder;
import repast.simphony.space.graph.RepastEdge;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import de.cesr.more.basic.MoreEdge;
import de.cesr.more.basic.MoreGeoEdge;
import de.cesr.more.building.MoreEdgeFactory;
import de.cesr.more.building.MoreNetworkEdgeModifier;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author holzhauer
 * @date 28.09.2011 
 *
 */
public class MGeoRsNetworkEdgeModifier<AgentType, EdgeType extends RepastEdge<? super AgentType> & MoreEdge<AgentType>> implements
		MoreNetworkEdgeModifier<AgentType, EdgeType> {
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MGeoRsNetworkEdgeModifier.class);
	
	private static DefaultCoordinateOperationFactory cFactory = new DefaultCoordinateOperationFactory();
	
	private static final double		ARROW_ARC		= 20.0 / 180.0;
	/**
	 * in degree
	 */
	private static final double		ARROW_LENGTH	= 0.0005;
	
	/**
	 * Need to be of type {@link Object} since network objects and agents should be insertable
	 */
	protected Geography<Object>		geography;
	
	protected GeometryFactory		geoFactory		= null;
	
	protected MoreEdgeFactory<AgentType, EdgeType> edgeFac;
	
	public MGeoRsNetworkEdgeModifier(MoreEdgeFactory<AgentType, EdgeType> edgeFac, Geography<Object> geography,
			GeometryFactory geoFactory) {
		this.edgeFac = edgeFac;
		this.geography = geography;
		this.geoFactory = geoFactory;
	}

	/**
	 * @see de.cesr.more.building.MoreNetworkEdgeModifier#createEdge(java.lang.Object, java.lang.Object, boolean)
	 */
	@Override
	public EdgeType createEdge(MoreNetwork<AgentType, EdgeType> network, AgentType source, AgentType target) {
		EdgeType edge = edgeFac.createEdge(source, target, network.isDirected());
		
		network.connect(edge);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Edge added: " + edge);
		}
		// LOGGING ->

		addEdgeToGeography(source, target, edge);
		return edge;
	}

	/**
	 * @see de.cesr.more.building.MoreNetworkEdgeModifier#removeEdge(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean removeEdge(MoreNetwork<AgentType, EdgeType> network, AgentType source, AgentType target) {
		EdgeType edge = network.disconnect(source, target);
		if (edge != null) {
			geography.move(edge, null);
			return true;
		}
		return false;
	}

	/**
	 * @param target
	 * @param source
	 * @param edge
	 */
	protected void addEdgeToGeography(AgentType source, AgentType target, EdgeType edge) {
		if ((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.ADD_EDGES_TO_GEOGRAPHY)) {
			Coordinate[] coords1 = { geography.getGeometry(target).getCoordinate(),
					geography.getGeometry(source).getCoordinate() };

			// calculate arc location

			Geometry geoSource = geography.getGeometry(source);
			Geometry geoTarget = geography.getGeometry(target);
			Coordinate hhCoord = geoTarget.getCoordinate();
			Coordinate influencerCoord = geoSource.getCoordinate();
			double arcArrow = Math.atan((influencerCoord.y - hhCoord.y) / (influencerCoord.x - hhCoord.x));

			Coordinate[] coordsE = {
					hhCoord,
					new Coordinate(hhCoord.x - ARROW_LENGTH * Math.cos(arcArrow), hhCoord.y + ARROW_LENGTH
							* Math.sin(arcArrow)) };

			Coordinate[] coordsL = {
					hhCoord,
					new Coordinate(hhCoord.x - ARROW_LENGTH * Math.cos(1.0 / 2.0 - ARROW_ARC - arcArrow), hhCoord.y
							- ARROW_LENGTH * Math.sin(90.0 - ARROW_ARC - arcArrow)) };
			Coordinate[] coordsR = {
					hhCoord,
					new Coordinate(hhCoord.x - ARROW_LENGTH * Math.cos(1.0 / 2.0 + ARROW_ARC - arcArrow), hhCoord.y
							- ARROW_LENGTH * Math.sin(90.0 + ARROW_ARC - arcArrow)) };

			LineString[] lines = { this.geoFactory.createLineString(coords1),
			// TODO finish
			// ModelManager.getMan().getGeomFactory().createLineString(coordsE),
			// ModelManager.getMan().getGeomFactory().createLineString(coordsR)
			};

			geography.move(edge, this.geoFactory.createMultiLineString(lines));
			
			
			if (edge != null) {
				if (edge instanceof MoreGeoEdge) {
					
					CoordinateReferenceSystem crs = geography.getCRS();
					CoordinateReferenceSystem utm = UTMFinder.getUTMFor(geoSource, crs);
					Point pSource = null, pTarget = null;
					try {
						pSource = (Point) JTS.transform(geoSource, cFactory.createOperation(crs, utm).getMathTransform());
						pTarget = (Point) JTS.transform(geoTarget, cFactory.createOperation(crs, utm).getMathTransform());
					} catch (Exception e) {
						e.printStackTrace();
					}

					((MoreGeoEdge<?>) edge).setLength(pSource.distance(pTarget));
				}
			}

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Moved " + edge + " to " + lines + "(" + coordsL[0].x + "," + coordsL[0].y + ")" + " / "
						+ "(" + coordsL[1].x + "," + coordsL[1].y + ")" + "-" + "(" + coordsR[0].x + "," + coordsR[0].y
						+ ")" + " / " + "(" + coordsR[1].x + "," + coordsR[1].y + ")");
			}
			// LOGGING ->
		}
	}

	/**
	 * @return the geography
	 */
	public Geography<Object> getGeography() {
		return geography;
	}

	/**
	 * @param geography the geography to set
	 */
	public void setGeography(Geography<Object> geography) {
		this.geography = geography;
	}

	/**
	 * @return the geoFactory
	 */
	public GeometryFactory getGeoFactory() {
		return geoFactory;
	}

	/**
	 * @param geoFactory the geoFactory to set
	 */
	public void setGeoFactory(GeometryFactory geoFactory) {
		this.geoFactory = geoFactory;
	}
}
