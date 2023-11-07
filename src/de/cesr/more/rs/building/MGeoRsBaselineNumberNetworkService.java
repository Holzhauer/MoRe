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
import edu.uci.ics.jung.graph.Graph;


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
	@Override
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

				if (checkPartner(network.getJungGraph(), paraMap, hh, potPartner, 0)) {
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

	/**
	 * Returns false if source is already a successor of target. Otherwise, the milieu is checked based on paraMap.
	 * 
	 * @param paraMap
	 * @param partnerMilieu
	 * @return true if the check was positive
	 */
	public boolean checkPartner(Graph<AgentType, EdgeType> network,
			MMilieuNetworkParameterMap paraMap, AgentType ego,
			AgentType potPartner, int desiredMilieu) {
		if (network.isSuccessor(ego, potPartner)) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(ego + "> " + potPartner + " is already predecessor of " + ego +
						" (" + ego + (network.isSuccessor(potPartner, ego) ? " is" : " is not") +
						" a predecessor of " + potPartner + ")");
			}
			// LOGGING ->

			return false;
		}
		// find agent that belongs to the milieu
		if ((Boolean) PmParameterManager.getParameter(MNetBuildBhPa.DISTANT_FORCE_MILIEU) && desiredMilieu != 0) {
			if ((potPartner).getMilieuGroup() == desiredMilieu) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(ego + "> Link with distant partner");
				}
				// LOGGING ->

				return true;
			} else {
				return false;
			}
		} else {
			// determine if potenialpartner's milieu is probable to link with:
			double rand_float = rand.nextDoubleFromTo(0.0, 1.0);
			boolean pass = paraMap.getP_Milieu(ego.getMilieuGroup(),
					potPartner.getMilieuGroup()) > rand_float;

			if (logger.isDebugEnabled()) {
				logger.debug((pass ? ego + "> " + potPartner + "'s mileu ("
						+ potPartner.getMilieuGroup() + ") accepted" : ego + "> "
						+ potPartner + "'s mileu (" + potPartner.getMilieuGroup()
						+ ") rejected")
						+ " (probability: "
						+ paraMap.getP_Milieu(ego.getMilieuGroup(),
								potPartner.getMilieuGroup())
						+ " / random: "
						+ rand_float);
			}
			return pass;
		}
	}
}
