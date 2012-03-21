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
 * Created by Sascha Holzhauer on 02.12.2011
 */
package de.cesr.more.rs.building;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import repast.simphony.space.gis.Geography;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildSocialAttachment;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.rs.building.analyse.MoreBaselineNetworkServiceAnalysableAgent;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.geo.util.MGeographyWrapper;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 * 
 * Based on {@link MGeoRsBaselineRadiusNetworkService}
 *
 *
 * @formatter:off
 * <table>
 * <th>Property</th><th>Value</th>
 * <tr><td>#Vertices</td><td>N (via collection of agents)</td></tr>
 * <tr><td></td><td></td></tr>
 * <tr><td>#Edges:</td><td>N*(N-1)</td></tr>
 * </table> 
 *
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * <li>{@link MNetworkBuildingPa.MILIEU_NETWORK_PARAMS}</li>
 * <li>{@link MRandomPa.RND_STREAM_NETWORK_BUILDING}</li>
 * <li>{@link MNetBuildSocialAttachment.NUM_MILIEU_GROUPS}</li>
 * </ul>
 * @author Sascha Holzhauer
 * @date 02.12.2011 
 *
 */
public class MGeoRsIdealNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType>>
		extends MGeoRsBaselineRadiusNetworkService<AgentType, EdgeType> {
	
	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MGeoRsIdealNetworkService.class);

	static class MilieuSize implements Comparable<MilieuSize> {
		double	size	= 0.0f;
		int		milieu;

		protected MilieuSize(double size, int milieu) {
			this.size = size;
			this.milieu = milieu;
		}

		@Override
		public int compareTo(MilieuSize o) {
			// reverse ordering because Array.sort sorts ascending but descending order required
			return (-1) * Double.compare(this.size, o.size);
		}

		@Override
		public String toString() {
			return "Milieu " + milieu + ": " + size;
		}
	}
	

	public MGeoRsIdealNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this(edgeFac, "Network");
	}

	public MGeoRsIdealNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		this((Geography) PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac, name);
	}
	
	/**
	 * - builder constructor - edge modifier - builder set - parma
	 * 
	 * @param areasGeography
	 */
	public MGeoRsIdealNetworkService(Geography<Object> geography,
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		super(geography, edgeFac, name);
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
	@SuppressWarnings("unchecked") // handled by try/catch
	protected int connectAgent(MMilieuNetworkParameterMap paraMap,
			MoreNetwork<AgentType, EdgeType> network,
			int numNotConnectedPartners, MGeographyWrapper<Object> geoWrapper,
			AgentType hh) {

		logger.info(hh + " > Connect... (mileu: " + hh.getMilieuGroup() + ")");

		double curRadius = paraMap.getSearchRadius(hh.getMilieuGroup());

		// fetch potential neighbours from proximity. NumNeighbors should be large enough to find required number of
		// parters per milieu
		int numNeighbors = paraMap.getK(hh.getMilieuGroup());
		
		Class<? extends AgentType> requestClass;
		if (geoRequestClass == null) {
			try {
				requestClass = (Class<AgentType>) hh.getClass().getSuperclass();
			} catch (ClassCastException e) {
				logger.error("Agent's super class is not of type AgentType. Please use setGeoRequestClass!");
				throw new ClassCastException("Agent's super class is not of type AgentType. Please use setGeoRequestClass!");
			}
		} else {
			requestClass = geoRequestClass;
		}
		
		
		List<AgentType> neighbourslist = geoWrapper.getSurroundingAgents(hh, curRadius,
				requestClass);

		// mixing neighbour collection
		shuffleCollection(neighbourslist);

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
					buffer.append(i + ": " + addArray[i] + " >= ");
				}
				logger.debug("Sorted: " + buffer);
			}
			// LOGGING ->

			int checkSum = 0;
			for (int i = 0; i < numNeighbors - sum; i++) {

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Add agent to milieu " + addArray[i].milieu + " (subtraction remainder: "
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

		boolean anyPartnerAssignable = true;
		
		List<AgentType> checkedNeighbours = new ArrayList<AgentType>(neighbourslist.size() * 
				CHECKED_NEIGHBOURS_CAPACITY_FACTOR);
		// to check if the required neighbours is satisfied
		int numLinkedNeighbors = 0;

		Iterator<AgentType> neighbourIter = neighbourslist.iterator();
		AgentType potPartner;

		int numRadiusExtensions = 0;
		
		while (numLinkedNeighbors < numNeighbors && anyPartnerAssignable) {
			if (neighbourIter.hasNext()) {
				potPartner = neighbourIter.next();

				// TODO check if potPartner has capacity (new feature)

				if (checkPartner(network, numMilieuPartners, hh, potPartner)) {
					edgeModifier.createEdge(network, potPartner, hh);
					
					numMilieuPartners[potPartner.getMilieuGroup() - 1]--;

					numLinkedNeighbors++;
					
					// substitutes rewiring:
					AgentType target = distantLinking(paraMap, network, hh, requestClass);
					if (target != null && 
							numLinkedNeighbors < numNeighbors) {
						numMilieuPartners[target.getMilieuGroup() - 1]--;
						numLinkedNeighbors++;
					}

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh + " > Found partner: " + potPartner);
					}
					// LOGGING ->
				}
			} else {
				// in case no partner was found the source set should be increased:
				if (curRadius < paraMap.getMaxSearchRadius(hh.getMilieuGroup())) {
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh + " > No Partner found, but max number of surrounding agents NOT reached!");
					}
					// LOGGING ->

					// extending list of potential neighbours:
					curRadius += paraMap.getXSearchRadius(hh.getMilieuGroup());
					numRadiusExtensions++;
					
					checkedNeighbours.addAll(neighbourslist);
					
					neighbourslist = geoWrapper
							.<AgentType> getSurroundingAgents(hh, curRadius, requestClass);
					
					neighbourslist.removeAll(checkedNeighbours);

					// mixing neighbour collection
					shuffleCollection(neighbourslist);

					neighbourIter = neighbourslist.iterator();
				} else {
					anyPartnerAssignable = false;
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh + " > No Partner found!");
					}
					// LOGGING ->
				}
			}
		}
		
		if (hh instanceof MoreBaselineNetworkServiceAnalysableAgent) {
			MoreBaselineNetworkServiceAnalysableAgent agent = (MoreBaselineNetworkServiceAnalysableAgent) hh;
			agent.setFinalRadius(curRadius);
			agent.setNumRadiusExtensions(numRadiusExtensions);
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
