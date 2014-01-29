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
 * Created by Sascha Holzhauer on 02.04.2012
 */
package de.cesr.more.rs.building;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import repast.simphony.parameter.IllegalParameterException;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildBhPa;
import de.cesr.parma.core.PmParameterManager;
import edu.uci.ics.jung.graph.Graph;


/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 02.04.2012 
 *
 */
public class MMilieuPartnerFinder<AgentType extends MoreMilieuAgent, EdgeType extends MoreEdge<? super AgentType>>
		extends MDefaultPartnerFinder<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger		logger	= Logger.getLogger(MMilieuPartnerFinder.class);

	MMilieuNetworkParameterMap	networkParams;

	static final int			AGENT_LIST_SIZE_THRESHOLD	= 200;

	public MMilieuPartnerFinder(MMilieuNetworkParameterMap networkParams) {
		this.networkParams = networkParams;
	}

	/**
	 * @see de.cesr.more.rs.building.MorePartnerFinder#findPartner(edu.uci.ics.jung.graph.Graph, java.lang.Object,
	 *      boolean)
	 */
	@Override
	public AgentType findPartner(Graph<AgentType, EdgeType> graph, AgentType focal, boolean incoming) {
		return findPartner(graph.getVertices(), graph, focal, incoming);
	}

	/**
	 * @see de.cesr.more.rs.building.MDefaultPartnerFinder#findPartner(java.util.Collection,
	 *      edu.uci.ics.jung.graph.Graph, java.lang.Object, boolean)
	 */
	@Override
	public AgentType findPartner(Collection<AgentType> agents, Graph<AgentType, EdgeType> graph, AgentType focal,
			boolean incoming) {
		int desiredMilieu = 0;
		if ((Boolean) PmParameterManager.getParameter(MNetBuildBhPa.DISTANT_FORCE_MILIEU)) {
			// choose milieu to connect with
			desiredMilieu = getProbabilisticMilieu(networkParams, focal);
		}

		return agents.size() > AGENT_LIST_SIZE_THRESHOLD ?
				findPartnerLargeAgentList(agents, graph, focal, incoming, desiredMilieu) :
					findPartnerSmallAgentList(agents, graph, focal, incoming, desiredMilieu);
	}

	/**
	 * @param agents
	 * @param graph
	 * @param focal
	 * @param incoming
	 * @param desiredMilieu
	 * @return
	 */
	protected AgentType findPartnerLargeAgentList(Collection<AgentType> agents, Graph<AgentType, EdgeType> graph,
			AgentType focal, boolean incoming, int desiredMilieu) {
		AgentType random = null;
		List<AgentType> list;
		if (agents instanceof List) {
			list = (List<AgentType>) agents;
		} else {
			list = new ArrayList<AgentType>(agents);
		}
		
		Collection<AgentType> blacklist = new HashSet<AgentType>();
		if (list.contains(focal)) {
			blacklist.add(focal);
		}

		for (AgentType predecessor : (incoming ? graph.getPredecessors(focal) : graph.getSuccessors(focal))) {
			if (list.contains(predecessor)) {
				blacklist.add(predecessor);
			}
		}
		
		assert list.size() >= blacklist.size();

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			if (list.size() == blacklist.size()) {
				logger.debug("All potential partners are blacklisted!");
			}
		}
		// LOGGING ->

		boolean rewired = false;
		// fetch random partner:
		while (!rewired && list.size() > blacklist.size()) {
			random = list.get(getRandomDist().nextIntFromTo(0, list.size() - 1));

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(focal + "> Random object from context: "
						+ random);
			}
			// LOGGING ->

			if (!blacklist.contains(random)) {
				blacklist.add(random);

				if (checkPartnerMilieu(networkParams, focal, random, desiredMilieu)) {
					rewired = true;

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(focal + "> Selected " + random);
					}
					// LOGGING ->
				}
			}
		}

		return rewired ? random : null;
	}

	/**
	 * @param agents
	 * @param graph
	 * @param focal
	 * @param incoming
	 * @param desiredMilieu
	 * @return
	 */
	protected AgentType findPartnerSmallAgentList(Collection<AgentType> agents, Graph<AgentType, EdgeType> graph,
			AgentType focal, boolean incoming, int desiredMilieu) {

		AgentType random = null;
		List<AgentType> list = new ArrayList<AgentType>(agents);

		for (AgentType potPartner : agents) {
			if ((incoming ? graph.isPredecessor(potPartner, focal) : graph.isSuccessor(potPartner, focal))
					&& potPartner != focal) {
				list.add(potPartner);
			}
		}

		boolean rewired = false;

		// fetch random partner:
		while (!rewired && list.size() > 0) {
			random = list.get(getRandomDist().nextIntFromTo(0, list.size() - 1));
			list.remove(random);

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(focal + "> Random object from context: "
						+ random);
			}
			// LOGGING ->

			if (checkPartnerMilieu(networkParams, focal, random, desiredMilieu)) {
				rewired = true;

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(focal + "> Selected " + random);
				}
				// LOGGING ->
			}
		}
		return rewired ? random : null;
	}

	protected int getProbabilisticMilieu(MMilieuNetworkParameterMap networkParams, AgentType focus) {
		Map<Integer, Double> roulette_wheel = new LinkedHashMap<Integer, Double>();

		for (int i = 1; i <= networkParams.size(); i++) {
			roulette_wheel.put(new Integer(i), networkParams.getP_Milieu(focus.getMilieuGroup(), i));
		}

		double randFloat = getRandomDist().nextDouble();
		if (randFloat < 0.0 || randFloat > 1.0) {
			throw new IllegalStateException(rand
					+ "> Make sure min = 0.0 and max = 1.0");
		}

		float pointer = 0.0f;
		for (Entry<Integer, Double> entry : roulette_wheel.entrySet()) {
			pointer += entry.getValue().doubleValue();
			if (pointer >= randFloat) {
				return entry.getKey().intValue();
			}
		}
		if (pointer < 1.0) {
			// <- LOGGING
			logger.error("Partner link probabilities do not sum up to 1.0 for milieu " + focus.getMilieuGroup() + "!");
			// LOGGING ->
			throw new IllegalParameterException(
					"Partner link probabilities do not sum up to 1.0 for milieu " + focus.getMilieuGroup() + "!");

		}
		throw new IllegalStateException("This code should never be reached!");
	}

	/**
	 * Returns false if source is already a successor of target. Otherwise, the milieu is checked based on paraMap.
	 * 
	 * @param network
	 * @param paraMap
	 * @param ego
	 * @param potPartner
	 * @param desiredMilieu
	 *        desired milieu - 0 to select milieu probabilistically
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
		return checkPartnerMilieu(paraMap, ego, potPartner, desiredMilieu);
	}

	/**
	 * Potential partner's milieu is checked based on probabilities in paraMap. Does not check if the potential partner
	 * is already connected to ego!
	 * 
	 * @param paraMap
	 * @param ego
	 * @param potPartner
	 * @param desiredMilieu
	 *        desired milieu - 0 to select milieu probabilistically
	 * @return
	 */
	public boolean checkPartnerMilieu(MMilieuNetworkParameterMap paraMap, AgentType ego, AgentType potPartner,
			int desiredMilieu) {
		// find agent that belongs to the milieu
		if ((Boolean) PmParameterManager.getParameter(MNetBuildBhPa.DISTANT_FORCE_MILIEU) && desiredMilieu != 0) {
			if ((potPartner).getMilieuGroup() == desiredMilieu) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(ego + "> Link with partner");
				}
				// LOGGING ->

				return true;
			} else {
				return false;
			}
		} else {
			// determine if potenialpartner's milieu is probable to link with:
			double rand_float = getRandomDist().nextDoubleFromTo(0.0, 1.0);
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
