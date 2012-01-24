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
 * Created by Sascha Holzhauer on 01.12.2011
 */
package de.cesr.more.rs.building;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import repast.simphony.space.gis.Geography;
import com.vividsolutions.jts.geom.Geometry;

import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildBhPa;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.geo.util.MGeographyWrapper;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 * 
 * TODO test
 * TODO description 
 * 
 * @author Sascha Holzhauer
 * @date 01.12.2011
 * 
 */
public class MGeoRsBaselineNumberNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType>>
		extends MGeoRsBaselineRadiusNetworkService<AgentType, EdgeType> {
	/**
	 * Logger
	 */
	static private Logger			logger							= Logger.getLogger(MGeoRsBaselineNumberNetworkService.class);

	Geography<Object>				areasGeography;
	
	protected double				numNeighboursFetchFactor;
	protected double				xNumNeighboursFetchFactor;
	protected double				searchRadius;
	

	/**
	 * @param areasGeography
	 */
	public MGeoRsBaselineNumberNetworkService(Geography<Object> geography,
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		super(geography, edgeFac, name);
		this.numNeighboursFetchFactor = ((Double) PmParameterManager.getParameter(
				MNetBuildBhPa.NUM_NEIGHBORS_FETCH_FACTOR));
		this.xNumNeighboursFetchFactor = ((Double) PmParameterManager.getParameter(
				MNetBuildBhPa.X_NUM_NEIGHBORS_FETCH_FACTOR));
		this.searchRadius = ((Double) PmParameterManager.getParameter(
				MNetBuildBhPa.SEARCH_RADIUS));
	}

	/**
	 * @param paraMap
	 * @param network
	 * @param numNotConnectedPartners
	 * @param geoWrapper
	 * @param hh
	 * @return the number of _not_ connected partners
	 */
	protected int connectAgent(MMilieuNetworkParameterMap paraMap,
		MoreNetwork<AgentType, EdgeType> network,
		int numNotConnectedPartners, MGeographyWrapper<Object> geoWrapper,
		AgentType hh) {
	
		logger.info(hh + " > Connect... (mileu: " + hh.getMilieuGroup() + ")");

		Geometry area = geography.getGeometry(
				geoWrapper.getContainingAreaContext(hh, (Class<?>)PmParameterManager.getParameter(MNetBuildBhPa.AREA_CONTEXT_CLASS)));

		// fetch potential neighbours from proximity. NumNeighbors should be large enough to find required number of
		
		// parters per milieu
		int numNeighbors = paraMap.getK(hh.getMilieuGroup());
		
		double fetchFactor = numNeighboursFetchFactor;

		@SuppressWarnings("unchecked")
		List<AgentType> neighbourslist = geoWrapper.<AgentType>getSurroundingNAgents(hh,
				(int) (numNeighbors * fetchFactor), area, searchRadius, (Class<AgentType>) hh.getClass());

		List<AgentType> checkedNeighbours = new ArrayList<AgentType>(neighbourslist.size() * 
				CHECKED_NEIGHBOURS_CAPACITY_FACTOR);
		
		shuffleCollection(neighbourslist);
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Shuffled: " + neighbourslist);
		}
		// LOGGING ->


		boolean anyPartnerAssignable = true;

		// to check if the required neighbours is satisfied
		int numLinkedNeighbors = 0;

		Iterator<AgentType> neighbourIter = neighbourslist.iterator();
		AgentType potPartner;

		while (numLinkedNeighbors < numNeighbors && anyPartnerAssignable) {
			if (neighbourIter.hasNext()) {
				potPartner = neighbourIter.next();

				// TODO check if potPartner has capacity (new feature)

				if (checkPartner(network, paraMap, hh, potPartner)) {
					createEdge(network, potPartner, hh);

					numLinkedNeighbors++;

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh + " > Found Influencer: " + potPartner);
					}
					// LOGGING ->
				}
			} else {
				// in case no partner was found the source set should be increased:
				// !
				if (neighbourslist.size() < geoWrapper.getMaxNumAgents(area, hh.getClass())) {
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh
								+ " > No Influencer found, but max number of surrounding agents NOT reached!");
					}
					// LOGGING ->

					// extending list of potential neighbours:
					// !
					fetchFactor = fetchFactor * xNumNeighboursFetchFactor;
					checkedNeighbours.addAll(neighbourslist);
					
					neighbourslist = geoWrapper.<AgentType>getSurroundingNAgents(hh,
							(int) (numNeighbors * fetchFactor), area, searchRadius, (Class<AgentType>) hh.getClass());
					
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Found " + neighbourslist.size() + " new neighbours");
					}
					// LOGGING ->
					
					shuffleCollection(neighbourslist);
					neighbourIter = neighbourslist.iterator();
				} else {
					anyPartnerAssignable = false;
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh + " > No Influencer found!");
					}
					// LOGGING ->
				}
			}
		}
		
		numNotConnectedPartners += numNeighbors - numLinkedNeighbors;
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(hh + " > " + numLinkedNeighbors + " neighbours found (from " + numNeighbors + ")");
		}
		// LOGGING ->
	
		return numNotConnectedPartners;
	}
}
