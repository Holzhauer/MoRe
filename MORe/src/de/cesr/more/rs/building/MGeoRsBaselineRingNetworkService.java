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
 * Created by Sascha Holzhauer on 27.07.2012
 */
package de.cesr.more.rs.building;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import repast.simphony.space.gis.Geography;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.analyse.MoreBaselineNetworkServiceAnalysableAgent;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.geo.util.MGeographyWrapper;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;


/**
 * TODO adapt
 * 
 * This network builder considers baseline homophily (McPherson2001). Agents are linked as follows:
 * <ol>
 * <li>For every agent in the context
 * <ol>
 * <li>Fetch all agents within a given radius (<code>SEARCH_RADIUS</code>) from the focal agent (do not consider area
 * boundaries).</li>
 * <li>For every potential partner that is not yet connected check according to milieu specific probability if it should
 * be connected with focal agent. The approach to check the agents that are in the surroundings <i>as they come</i>
 * considers the local milieu distribution and reflects <i>baseline homophily</i>. Applying milieu specific tie
 * probabilities reflects <i>inbreeding homophily</i>.</li>
 * <li>If the number of required neighbours is not satisfied but all fetched agents checked, request more agents from
 * geography within an extended radius (<code>X_SEARCH_RADIUS</code>) until maximum radius (
 * <code>MAX_SEARCH_RADIUS</code>) is reached.</li>
 * </ol>
 * <li>Rewire: For each agent, check if every existing link should be rewired (with probability <code>p_rewire</code>)
 * to a randomly chosen agent from the whole region that passes the milieu check (applying milieu tie probabilities (
 * <code>p_links</code> for every <code>partnerMilieu</code>). On purpose the new partner's milieu is not guaranteed to
 * be the same as that of the original link: The partners within direct surroundings are coined by local milieu 
 * distributions (baseline homophly) and therefore do not entirely reflect the focal agent's preferences.
 * Determining the milieu during rewiring anew may correct to milieu distributions of partners towards
 * inbreeeding homophily and is desired.</li>
 * </ol>
 * 
 * Uses {@link MGeographyWrapper#getSurroundingAgents(Object, double, Class)} to fetch agents (all agents within the
 * given radius of the given class).
 * 
 * Internal: The BaselineDhhRadiusNetworkBuilder is based on DHH_ColCalc_Computer_Lifestyle.
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
 * <li>...</li>
 * </ul>
 *
 * @author Sascha Holzhauer
 * @date 27.07.2012 
 *
 */
public class MGeoRsBaselineRingNetworkService<AgentType extends MoreMilieuAgent, 
EdgeType extends MRepastEdge<AgentType> & MoreEdge<AgentType>> extends MGeoRsBaselineRadiusNetworkService<AgentType, EdgeType> {

	
	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MGeoRsBaselineRingNetworkService.class);
	/**
	 * @param edgeFac
	 */
	
	public MGeoRsBaselineRingNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this(edgeFac, "Network");
	}
	
	/**
	 * @param edgeFac
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public MGeoRsBaselineRingNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		this((Geography<Object>) PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac, name);
	}
	
	/**
	 * @param geography
	 * @param edgeFac
	 * @param name
	 */
	public MGeoRsBaselineRingNetworkService(Geography<Object> geography, MoreEdgeFactory<AgentType, EdgeType> edgeFac,
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

		int numNeighbors = 0;
		
		int numRadiusExtensions = 0;

		Class<? extends AgentType> requestClass = getRequestClass(hh);
			

		double curRadius = paraMap.getSearchRadius(hh.getMilieuGroup());

		// fetch potential neighbours from proximity. NumNeighbors should be
		// large enough to find required number of
		// parters per milieu
		numNeighbors = paraMap.getK(hh.getMilieuGroup());
		
		List<AgentType> neighbourslist = geoWrapper
				.<AgentType>getSurroundingAgents(hh, curRadius, requestClass);
		
		List<AgentType> checkedNeighbours = new ArrayList<AgentType>(neighbourslist.size() * 
				CHECKED_NEIGHBOURS_CAPACITY_FACTOR);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Found " + neighbourslist.size() + " of class " + 
					hh.getClass().getSuperclass()+ " neighbours within " + curRadius + " meters.");
		}
		// LOGGING ->

		// mixing neighbour collection
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

				if (partnerFinder.checkPartner(network.getJungGraph(), paraMap, hh, potPartner, 0)) {
					createEdge(network, potPartner, hh);

					numLinkedNeighbors++;
					
					// substitutes rewiring:
					
					if (numLinkedNeighbors < numNeighbors &&
							distantLinking(paraMap, network, hh, requestClass) != null) {
						numLinkedNeighbors++;
					}

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh + " > Found partner: " + potPartner);
					}
					// LOGGING ->
				}
			} else {
				// in case no partner was found the source set should be
				// increased:
				if (curRadius < paraMap.getMaxSearchRadius(hh.getMilieuGroup())) {
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh
								+ " > No Partner found, but max search radius NOT reached!");
					}
					// LOGGING ->

					// extending list of potential neighbours:
					curRadius += paraMap.getXSearchRadius(hh.getMilieuGroup());
					numRadiusExtensions++;

					checkedNeighbours.addAll(neighbourslist);
					
					neighbourslist = geoWrapper
							.<AgentType> getSurroundingAgents(hh, curRadius, requestClass);
					
					neighbourslist.removeAll(checkedNeighbours);

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Found " + neighbourslist.size() + " new neighbours within " + curRadius
								+ " meters.");
					}
					// LOGGING ->

					shuffleCollection(neighbourslist);

					neighbourIter = neighbourslist.iterator();
				} else {
					anyPartnerAssignable = false;
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh + " > Not enough partners found in max search radius!");
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
			logger.debug(hh + " > " + numLinkedNeighbors
					+ " neighbours found (from " + numNeighbors + ")");
		}
		// LOGGING ->
		return numNotConnectedPartners;
	}

}
