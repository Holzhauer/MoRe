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
 * Created by Sascha Holzhauer on 14.08.2012
 */
package de.cesr.more.rs.building;


import java.util.ArrayList;
import java.util.Arrays;
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
import de.cesr.more.rs.building.MGeoRsIdealNetworkService.MilieuSize;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.geo.util.MGeographyWrapper;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 14.08.2012 
 *
 */
public class MGeoRsIdealHomophilyDistanceNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType> & MoreEdge<AgentType>>
		extends
		MGeoRsHomophilyDistanceNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MGeoRsIdealHomophilyDistanceNetworkService.class);

	/**
	 * @param geography
	 * @param edgeFac
	 * @param name
	 */
	public MGeoRsIdealHomophilyDistanceNetworkService(Geography<Object> geography,
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		super(geography, edgeFac, name);
		// TODO Auto-generated constructor stub
	}

	public MGeoRsIdealHomophilyDistanceNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this(edgeFac, "Network");
	}

	/**
	 * @param edgeFac
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public MGeoRsIdealHomophilyDistanceNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		this((Geography<Object>) PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac, name);
	}

	/**
	 * TODO integerate
	 * 
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
		int numRings = (int) (1.0 / paraMap.getExtendingSearchFraction(hh.getMilieuGroup()));

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

		// <-- IDEAL specific
		int[] numMilieuPartners = calculatePartnerMilieus(paraMap, hh, numNeighbors);
		// IDEAL specific -->

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
			dRingMax = radiusMax / numRings * (i + 1);

			neighbourslist = geoWrapper
					.<AgentType> getSurroundingAgents(hh, dRingMax, requestClass);
			neighbourslist.removeAll(checkedNeighbours);
			checkedNeighbours.addAll(neighbourslist);

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Found " + neighbourslist.size() + " of class " +
						hh.getClass().getSuperclass() + " neighbours within " + dRing + " +/- " + radiusMax / numRings
						* 0.5 + " meters.");
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

				// <-- IDEAL specific
				if (checkPartner(network, numMilieuPartners, hh, potPartner)) {
					potentialPartners.add(potPartner);

					randomNumber = rand.nextDouble();

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(MManager.getFloatPointFormat().format(Math.pow((dRing), alpha) * cDistance)
								+ " (Probability of linking " + potPartner + " with "
								+ hh + ") | random number: " + randomNumber);
					}
					// LOGGING ->

					if (randomNumber < Math.pow((dRing), alpha) * cDistance) {
						createEdge(network, potPartner, hh);

						// <- LOGGING
						if (logger.isDebugEnabled()) {
							logger.debug(hh + " > Linked partner: " + potPartner);
						}
						// LOGGING ->

						// <-- IDEAL specific
						numMilieuPartners[potPartner.getMilieuGroup() - 1]--;

						numLinkedNeighbors++;

						// substitutes rewiring:
						if (globalLinking(paraMap, network, hh, requestClass) != null) {
							numLinkedNeighbors++;
						}
						// IDEAL specific -->
					}
				}
				// IDEAL specific -->
			}
			numLinkedNeighborsSum += numLinkedNeighbors;
			numNotConnectedPartners += numNeighbors - numLinkedNeighbors;
		}

		// <- LOGGING
		logger.info(hh + " > " + numLinkedNeighborsSum
				+ " neighbours found (from " + numNeighbors + ")");
		// LOGGING ->
		return numNotConnectedPartners;
	}

	/**
	 * @param paraMap
	 * @param hh
	 * @param numNeighbors
	 * @return
	 */
	protected int[] calculatePartnerMilieus(MMilieuNetworkParameterMap paraMap, AgentType hh, int numNeighbors) {
		// calculate number of required partners per milieu:
		int[] numMilieuPartners = new int[paraMap.size()];
		double[] remainder = new double[numMilieuPartners.length];
		int sum = 0;
		for (int i = 0; i < numMilieuPartners.length; i++) {
			numMilieuPartners[i] = (int) Math.floor(numNeighbors
					* paraMap.getP_Milieu(hh.getMilieuGroup(), i + 1));
			sum += numMilieuPartners[i];

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Partner milieus to connect " + (i + 1) + ": " + numMilieuPartners[i]);
			}
			// LOGGING ->

			remainder[i] = Math.ceil(numNeighbors * paraMap.getP_Milieu(hh.getMilieuGroup(), i + 1))
					- numNeighbors * paraMap.getP_Milieu(hh.getMilieuGroup(), i + 1);
		}

		// distribute remaining agents (to subtract) to those milieus with largest remainder:
		assert sum <= numNeighbors;

		if (sum < numNeighbors) {
			MilieuSize[] addArray = new MilieuSize[numMilieuPartners.length];

			for (int i = 0; i < numMilieuPartners.length; i++) {
				addArray[i] = new MilieuSize(remainder[i], i);
			}

			Arrays.sort(addArray);

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < addArray.length; i++) {
					buffer.append((i + 1) + ": " + addArray[i] + " >= ");
				}
				logger.debug("Sorted: " + buffer);
			}
			// LOGGING ->

			int checkSum = 0;
			for (int i = 0; i < numNeighbors - sum; i++) {

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Add agent to milieu " + (addArray[i].milieu + 1) + " (subtraction remainder: "
							+ addArray[i].size + ")");
				}
				// LOGGING ->

				numMilieuPartners[addArray[i].milieu]++;
				checkSum++;
			}
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("num of required Neighbours: " + numNeighbors + " / Checksum: " + (checkSum + sum)
						+ " (sum: " + sum + ")");
			}
			// LOGGING ->

			assert numNeighbors == checkSum + sum;
		}
		return numMilieuPartners;
	}

	/**
	 * @param paraMap
	 * @param partnerMilieu
	 * @return true if the given partners fit
	 */
	protected boolean checkPartner(MoreNetwork<AgentType, EdgeType> network, int[] numMilieuPartners,
			AgentType hh, AgentType potPartner) {
		if (network.isSuccessor(potPartner, hh)) {
			return false;
		}
		boolean pass = numMilieuPartners[potPartner.getMilieuGroup() - 1] > 0;

		if (logger.isDebugEnabled()) {
			logger.debug(pass ? hh + "> " + potPartner + "'s mileu (" + potPartner.getMilieuGroup() + ") accepted" : hh
					+ "> " + potPartner + "'s mileu (" + potPartner.getMilieuGroup() + ") rejected");
		}
		return pass;
	}
}
