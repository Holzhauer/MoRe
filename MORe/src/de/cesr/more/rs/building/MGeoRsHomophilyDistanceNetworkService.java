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
import de.cesr.more.param.MNetBuildBhPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.geo.util.MGeographyWrapper;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;


//@formatter:off
/**
 * The homophily and distance dependence (HDD) considering network generation consists of two interlinked parts, the
 * establishment of local to medium distance links and the process of global tie generation. The distance dependence is
 * accomplished by defining probabilities for concentric rings around each agent using a ring’s mean radius. This
 * network builder considers baseline homophily [1]. Agents are linked as follows:
 * 
 * <ol>
 * <li>For every agent <code>i</code> in the context
 * 	<ol>
 * 		<li>Calculate a distance probability compensation factor c_d to normalise probabilities across rings</li>
 * 		<li>For each ring r
 * 			<ol>
 * 				<li>Request agents in current ring</li>
 * 				<li>Shuffle agents</li>
 * 				<li>For each requested agent <code>j</code>
 * 					<ol>
 * 						<li>Check: <code>i</code> not yet connected with <code>j</code>?</li>
 * 						<li>Accept milieu of <code>j</code> with probability of <code>i</code> preference towards milieu of <code>j</code></li>
 * 						<li>Accept <code>j</code> as partner with  <code>i</code>'s <code>p_distance</code></li>
 * 						<li>Connect!</li>
 * 						<li>Establish distant link with <code>p_distant</code></li>
 * 						<li>Determine desired milieu of <code>i</code></li>
 * 						<li>Select random agent from entire population</li>
 * 						<li>Check: Not yet connected?</li>
 * 						<li>Check: Matches desired milieu?</li>
 * 					</ol>
 * 				</li>
 * 			</ol>
 * 		</li>
 * </ol>
 * </ol>
 * 
 * For details see [2].
 * 
 * <br>
 * <br>
 * 
 * Uses {@link MGeographyWrapper#getSurroundingAgents(Object, double, Class)} to fetch agents (all agents within the
 * given radius of the given class).
 * 
 * <br>
 * <br>
 * 
 * <table>
 * <th>Property</th><th>Value</th>
 * <tr><td>#Vertices</td><td>N (via collection of agents)</td></tr>
 * <tr><td></td><td></td></tr>
 * <tr><td>#Edges:</td><td>N*K (milieu-specific)</td></tr>
 * </table> 
 * 
 * <br>
 * <br>
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa#BUILD_DIRECTED}</li>
 * <li>{@link MNetBuildBhPa#K} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildBhPa#DISTANCE_PROBABILITY_EXPONENT} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildBhPa#EXTENDING_SEARCH_FRACTION} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildBhPa#MAX_SEARCH_RADIUS} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildBhPa#P_MILIEUS} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildBhPa#DISTANT_FORCE_MILIEU} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildBhPa#P_REWIRE} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * </ul>
 * 
 * <br>
 * 
 * NOTE: Since the power function is invariant against scale/unit we do not need to adapt the meters scale.
 * 
 * <br>
 * <br>
 * 
 * [1] McPherson, M.; Smith-Lovin, L. & Cook, J. Birds of a feather: Homophily in social networks Annual Review of
 * Sociology, Annual Reviews, 2001, 27, 415-444
 * 
 * <br>
 * 
 * [2] Holzhauer, S.; Krebs, F., Ernst, A. Considering baseline homophily when generating spatial social networks for
 * agent-based modelling. Comput Math Organ Theory, 2012, SI: SNAMAS
 * 
 * <br>
 * 
 * @version 0.9
 * @author Sascha Holzhauer
 * @date 01.08.2012
 * 
 * @param <AgentType>
 *        The type of nodes
 * @param <EdgeType>
 *        The type of edges
 * 
 */
public class MGeoRsHomophilyDistanceNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType> & MoreEdge<AgentType>>
		extends MGeoRsBaselineRadiusNetworkService<AgentType, EdgeType> {
	
	//@formatter:on
	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MGeoRsHomophilyDistanceNetworkService.class);

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
	public MGeoRsHomophilyDistanceNetworkService(Geography<Object> geography,
			MoreEdgeFactory<AgentType, EdgeType> edgeFac,
			String name) {
		super(geography, edgeFac, name);
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
					if (globalLinking(paraMap, network, hh, requestClass) != null) {
						numLinkedNeighbors++;
					}
				}
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
}
