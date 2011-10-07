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
package de.cesr.more.rs.building.geo;

import javax.units.SI;

import org.apache.commons.collections15.Predicate;
import org.apache.log4j.Logger;
import org.geotools.referencing.operation.DefaultCoordinateOperationFactory;
import org.geotools.geometry.jts.JTS;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.opengis.spatialschema.geometry.MismatchedDimensionException;

import repast.simphony.query.space.gis.AbstractGeometryQuery;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.UTMFinder;
import repast.simphony.util.collections.FilteredIterator;


/**
 * MORe
 *
 * @author holzhauer
 * @date 06.10.2011 
 *
 */
public class MGeoDistanceQuery<AgentType> extends AbstractGeometryQuery<AgentType> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MGeoDistanceQuery.class);
	
	private static DefaultCoordinateOperationFactory cFactory = new DefaultCoordinateOperationFactory();
	
	protected double distance;
	
	protected boolean convert;
	
	/**
	 * @param geography
	 * @param sourceObject
	 */
	public MGeoDistanceQuery(Geography<AgentType> geography, double distance,
			AgentType sourceObject) {
		super(geography, sourceObject);
		init(geography, distance, geography.getGeometry(sourceObject));
	}
	
	  protected void init(Geography<AgentType> geography, double distance, Geometry location) {
		    Point p = location.getCentroid();
		   
		    // don't convert if we are already in a meter based crs
		    convert = !geography.getUnits(0).equals(SI.METER);
		    
		    this.distance = distance;
		    CoordinateReferenceSystem utm = null;
		    CoordinateReferenceSystem crs = geography.getCRS();

		   
		    try {
		      // convert p to UTM
		      if (convert) {
		    	

		        utm = UTMFinder.getUTMFor(p, crs);
		        p = (Point) JTS.transform(p, cFactory.createOperation(crs, utm).getMathTransform());
		        // <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(geography.getUnits(0));
					logger.debug("Convert from " + crs + " to " + utm);
				}
				// LOGGING ->
		      }
		      geom = p;
		    } catch (FactoryException e) {
		      logger.error("Error during crs transform", e);
		    } catch (MismatchedDimensionException e) {
		    	logger.error("Error during crs transform", e);
		    	e.printStackTrace();
			} catch (org.opengis.referencing.operation.TransformException e) {
				logger.error("Error during crs transform", e);
				e.printStackTrace();
			}
		  }

	/**
	 * Considers all objects in the geography as potential matches.
	 * @see repast.simphony.query.space.gis.AbstractGeometryQuery#query()
	 */
	@Override
	public Iterable<AgentType> query() {
			Iterable<AgentType> potential = geography.getAllObjects();
	    return new FilteredIterator<AgentType>(potential.iterator(), predicate);
		}

	  /**
	   * Creates a predicate that tests for intersection.
	   *
	   * @return a predicate that tests for intersection.
	   */
	  @Override
	protected Predicate<AgentType> createPredicate() {
	    if (sourceObject != null) return new IntersectsPredicate<AgentType>();
	    return new NoSourceIntersectsPredicate<AgentType>();
	  }

	  private class NoSourceIntersectsPredicate<AgentT> implements Predicate<AgentT> {

	    @Override
		public boolean evaluate(AgentT o) {
	      Geometry other = geography.getGeometry(o);
	      
	      return other != null && other.distance(geom) <= distance;
	    }
	  }

	  private class IntersectsPredicate<AgentT> implements Predicate<AgentT> {

	    /**
	     * TODO does not work with MTorusCoordinate when coordinates need be transformed
	     * (when CRS is not metric)! > possible solution -> exchange coordinates after transformation
	     * 
	     * @see org.apache.commons.collections15.Predicate#evaluate(java.lang.Object)
	     */
	    @Override
		public boolean evaluate(AgentT o) {
	    	 Geometry other = geography.getGeometry(o);
	      if (!o.equals(sourceObject) && other instanceof Point) {
	    	 
	    	  if (convert) {
		        CoordinateReferenceSystem utm = UTMFinder.getUTMFor(other, geography.getCRS());
		        try {
					other = (Point) JTS.transform(other, cFactory.createOperation(geography.getCRS(), utm).getMathTransform());
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	  }
	        return other != null && other.distance(geom) <= distance;
	      }
	      return false;
	    }
	  }
}
