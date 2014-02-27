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
 * Created by Sascha Holzhauer on 16.12.2011
 */
package de.cesr.more.basic.agent;


import javax.units.SI;

import org.apache.log4j.Logger;

import repast.simphony.space.gis.DefaultGeography;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.UTMFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.cesr.more.basic.MNetworkManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.supply.MAvgNearestNeighbourDegree;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildWsPa;
import de.cesr.more.param.MNetManipulatePa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 * 
 * TODO simplify!!
 * 
 * @author Sascha Holzhauer
 * @date 16.12.2011
 * 
 */
public class MAgentAnalyseNetworkComp<A extends MoreNetworkAgent<A, E> & MoreMilieuAgent,
	E extends MoreEdge<? super A>> extends MAgentNetworkComp<A, E> implements MoreAgentAnalyseNetworkComp<A, E> {
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MAgentAnalyseNetworkComp.class);
	
	Geography<Object> geography;
	
	int						numAmbassadors	= 0;
	
	/**
	 * @param agent
	 */
	@SuppressWarnings("unchecked")
	public MAgentAnalyseNetworkComp(A agent) {
		this(agent, (Geography<Object>)PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY));
	}
	
	/**
	 * @param env
	 * @param name
	 */
	public MAgentAnalyseNetworkComp(A agent, Geography<Object> geography) {
		super(agent);
		if (geography.getCRS().getCoordinateSystem().getAxis(0).getUnit() == SI.METER) {
			this.geography = geography;
		} else {
			GeometryFactory geoFactory = new GeometryFactory(new PrecisionModel(),
					((Integer) PmParameterManager.getParameter(MBasicPa.SPATIALREFERENCEID)).intValue());
			this.geography = new DefaultGeography<Object>("utmGeography");
			for (Object o : geography.getAllObjects()) {
				// TODO check if getGeomFactory returns the correct factory (normally WGS 84)
				geography.move(o, geoFactory.createGeometry(geography.getGeometry(o)));
			}
			this.geography.setCRS(UTMFinder.getUTMFor(geoFactory.createPoint(new Coordinate()),
					geography.getCRS()));
		}
		this.geography = geography;
	}

	/**
	 * @return the numAmbassadors
	 */
	public int getNumAmbassadors() {
		return numAmbassadors;
	}

	/**
	 * @param numAmbassadors
	 *        the numAmbassadors to set
	 */
	public void setNumAmbassadors(int numAmbassadors) {
		this.numAmbassadors = numAmbassadors;
	}

	/**
	 * Returns the number of incoming links.
	 * 
	 * @return indegree
	 */
	@Override
	public int getInDegree() {
		return getMainNetwork().getInDegree(agent);
	}

	/**
	 * Adds 1 to prevent 0 values for sizing in GIS
	 * 
	 * @return indegree + 1
	 */
	@Override
	public int getXtInDegree() {
		return 1 + agent.getNetworkComp().getMainNetwork().getInDegree(agent);
	}

	/**
	 * Returns the number of outgoing links.
	 * 
	 * @return outdegree
	 */
	@Override
	public int getOutDegree() {
		return agent.getNetworkComp().getMainNetwork().getOutDegree(agent);
	}

	/**
	 * Calculates the average distance between this household and its neighbours.
	 * 
	 * @return average distance
	 */
	@Override
	public float getNbrDispers() {
		float sum = 0.0f;

		Geometry gthis = geography.getGeometry(agent);
		if (gthis == null) {
			logger.warn("Geometry is null for " + agent);
			return Float.NaN;
		} else {
			for (A n : getMainNetwork().getPredecessors(agent)) {
				Geometry gn = geography.getGeometry(n);
				if (gn != null) {
					sum += gn.distance(geography.getGeometry(agent));
				} else {
					logger.warn("Geometry is null for " + n);
				}
			}
			return sum / getMainNetwork().getInDegree(agent);
		}
	}

	/**
	 * Returns the average in-degree of nearest neighbours (see Boguna2004)
	 * 
	 * @return average degree of nearest neighbours
	 */
	@Override
	public float getNNAvgDeg() {
		Number value = agent.getNetworkMeasureObject(getMainNetwork(),
				new MMeasureDescription(MAvgNearestNeighbourDegree.Short.MC_AVG_NN_DEGREE_UNDIRECTED.getName()));
		return value != null ? value.floatValue() : Float.NaN;
	}

	/**
	 * @return
	 */
	@Override
	public double getNetPrefDev() {
		MMilieuNetworkParameterMap map = ((MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS));

		MoreNetwork<A, E> network = getMainNetwork();
		double deviation = 0.0;
		int[] neighbours = new int[map.size()];

		for (A n : network.getPredecessors(agent)) {
			neighbours[n.getMilieuGroup() - 1]++;
		}
		int sumNeighbours = network.getInDegree(agent);

		// calculate deviation for every milieu group
		for (int m = 1; m <= map.size(); m++) {
			deviation += Math.abs(map.getP_Milieu(agent.getMilieuGroup(), m)
					- (double) neighbours[m - 1] / (double) sumNeighbours);
		}
		return deviation;
	}


	/**
	 * @return
	 */
	@Override
	public int getNetKDev() {
		MMilieuNetworkParameterMap map = ((MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS));
		MoreNetwork<A, E> network = getMainNetwork();

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Actual indegree: " + network.getInDegree(agent) + " / Desired K: "
					+ ((Integer) map.getMilieuParam(MNetBuildWsPa.K, agent.getMilieuGroup())).intValue());
		}
		// LOGGING ->

		return network.getInDegree(agent)
				- ((Integer) map.getMilieuParam(MNetBuildWsPa.K, agent.getMilieuGroup())).intValue();
	}

	/**
	 * @see de.cesr.more.basic.agent.MoreAgentAnalyseNetworkComp#getNetworkDistanceWeight(double, double)
	 * @return meanDistance / distance
	 */
	@Override
	public double getNetworkDistanceWeight(double meanDistance, double distance) {
		return meanDistance / distance;
	}

	/**
	 * @see de.cesr.more.basic.agent.MoreAgentAnalyseNetworkComp#getBlacklistSize()
	 */
	@Override
	public int getBlacklistSize() {
		return ((MoreNetwork<A, E>) MNetworkManager.getNetwork((String) PmParameterManager.
				getParameter(MNetManipulatePa.DYN_BLACKLIST_NAME))).
				getInDegree((A) this);
	}
}
