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
 * Created by Sascha Holzhauer on 01.08.2012
 */
package de.cesr.more.rs.building;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import repast.simphony.space.gis.Geography;
import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.geo.util.MGeographyWrapper;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 * 
 * TODO doc
 * 
 * Since the power function is invariant against scale/unit we do not need to adapt the meters scale.
 * 
 * @author Sascha Holzhauer
 * @date 01.08.2012
 * 
 */
public class MGeoRsHomophilyDistanceNetworkService<AgentType extends MoreMilieuAgent, 
EdgeType extends MRepastEdge<AgentType> & MoreEdge<AgentType>> extends MGeoRsBaselineRadiusNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MGeoRsHomophilyDistanceNetworkService.class);

	
	public MGeoRsHomophilyDistanceNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this(edgeFac, "Network");
	}
	
	/**
	 * @param edgeFac
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public MGeoRsHomophilyDistanceNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		this((Geography<Object>) PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac, name);
	}
	
	/**
	 * @param geography
	 * @param edgeFac
	 * @param name
	 */
	public MGeoRsHomophilyDistanceNetworkService(Geography<Object> geography, MoreEdgeFactory<AgentType, EdgeType> edgeFac,
			String name) {
		super(geography, edgeFac, name);
	}
	
	/**
	 * TODO integerate 
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


		Class<? extends AgentType> requestClass = getRequestClass(hh);
			
		int numNeighbors = paraMap.getK(hh.getMilieuGroup());
		double radiusMax = paraMap.getMaxSearchRadius(hh.getMilieuGroup());
		double alpha = paraMap.getDistanceProbExp(hh.getMilieuGroup());
		int numRings = (int) (1.0 / paraMap.getExtengingSearchFraction(hh.getMilieuGroup()));


		// Calculate Distance Probability Compensation Factor $c_{distance}$ = 1 / \sum_{r = 1}^R (d_r)^\alpha
		double cDistance = 0.0;
		for (int i = 0; i < numRings; i++) {
			cDistance += Math.pow((radiusMax) / numRings * (i + 0.5d), alpha);
		}
		cDistance = 1.0 / cDistance;
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("cDistance: " + MManager.getFloatPointFormat().format(cDistance));
		}
		// LOGGING ->

		double dRing, dRingMax, randomNumber;
		List<AgentType> neighbourslist;
		List<AgentType> checkedNeighbours = new ArrayList<AgentType>();
		List<AgentType> potentialPartners = new ArrayList<AgentType>();
		int numLinkedNeighbors = 0, numLinkedNeighborsSum = 0;
		
		for (int i = 0; i < numRings; i++) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Handle " + (i + 1) + "th ring");
			}
			// LOGGING ->

			potentialPartners.clear();
			numLinkedNeighbors = 0;
			dRing = radiusMax / numRings * (i + 0.5d);
			dRingMax = radiusMax / numRings * (i + 1) ;
			
			neighbourslist = geoWrapper
					.<AgentType>getSurroundingAgents(hh, dRingMax, requestClass);
			neighbourslist.removeAll(checkedNeighbours);
			checkedNeighbours.addAll(neighbourslist);
			
	
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Found " + neighbourslist.size() + " of class " + 
						hh.getClass().getSuperclass()+ " neighbours within " + dRing + " +/- " + radiusMax / numRings * 0.5 + " meters.");
			}
			// LOGGING ->
	
			// mixing neighbour collection
			shuffleCollection(neighbourslist);
			
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Shuffled: " + neighbourslist);
			}
			// LOGGING ->

	
			Iterator<AgentType> neighbourIter = neighbourslist.iterator();
			AgentType potPartner;
	
			while (potentialPartners.size() < numNeighbors && neighbourIter.hasNext()) {
					
				potPartner = neighbourIter.next();
				if (partnerFinder.checkPartner(network.getJungGraph(), paraMap, hh, potPartner, 0)) {
					potentialPartners.add(potPartner);
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh + " > Found potential(!) partner: " + potPartner);
					}
					// LOGGING ->
				}
			}
			
			// Apply distance probabilities...
			for (AgentType pPartner : potentialPartners) {
				randomNumber = rand.nextDouble();
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(MManager.getFloatPointFormat().format(Math.pow((dRing), alpha) * cDistance)
							+ " (Probability of linking " + pPartner + " with "
							+ hh + ") | random number: " + randomNumber);
				}
				// LOGGING ->

				if (randomNumber < Math.pow((dRing), alpha) * cDistance) {
					createEdge(network, pPartner, hh);
				
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh + " > Linked partner: " + pPartner);
					}
					// LOGGING ->
					
					numLinkedNeighbors++;
							
					// substitutes rewiring:		
					if (distantLinking(paraMap, network, hh, requestClass) != null) {
						numLinkedNeighbors++;
					}
				}
			}
			numLinkedNeighborsSum +=numLinkedNeighbors;
			numNotConnectedPartners += numNeighbors - numLinkedNeighbors;
		}
		
		// <- LOGGING
		logger.info(hh + " > " + numLinkedNeighborsSum
				+ " neighbours found (from " + numNeighbors + ")");
		// LOGGING ->
		return numNotConnectedPartners;
	}
}
