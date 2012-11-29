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
 * Created by Sascha Holzhauer on 16.03.2012
 */
package de.cesr.more.rs.building;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import de.cesr.more.basic.MManager;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.util.MSmallWorldBetaModelNetworkGenerator;
import de.cesr.more.building.util.MoreKValueProvider;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildBhPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.param.reader.MMilieuNetDataReader;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterManager;
import edu.uci.ics.jung.graph.Graph;


/**
 * MORe
 * 
 * 
 * - uses MSmallWorldBetaModelNetworkGeneratorMilieuParams from MGeoRsWattsBetaSwBuilder
 * 
 * @author Sascha Holzhauer
 * @date 16.03.2012
 * 
 */
public class MGeoRsWattsBetaSwPartnerCheckingBuilder<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType>>
		extends MGeoRsWattsBetaSwBuilder<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MGeoRsWattsBetaSwPartnerCheckingBuilder.class);

	protected Uniform		rand;

	protected MMilieuNetworkParameterMap	paraMap;

	/**
	 * @param eFac
	 */
	public MGeoRsWattsBetaSwPartnerCheckingBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		super(eFac);
	}

	/**
	 * @param eFac
	 */
	public MGeoRsWattsBetaSwPartnerCheckingBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		super(eFac, name);
	}

	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(
			Collection<AgentType> agents) {

		if (context == null) {
			logger.error("Context not set!");
			throw new IllegalStateException("Context not set!");
		}


		if (((MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS)) == null) {
			new MMilieuNetDataReader().initParameters();

			if (this.paraMap == null) {
				// <- LOGGING
				logger.warn("Parameter MNetworkBuildingPa.MILIEU_NETWORK_PARAMS has not been set! (Re-)Initialise it.");
				// LOGGING ->
			}
		}
		this.paraMap = (MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);

		final MoreRsNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType>(
				((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ? new DirectedJungNetwork<AgentType>(
						this.name)
						: new UndirectedJungNetwork<AgentType>(
								this.name), context, this.edgeModifier.getEdgeFactory());

		params =
				new MSmallWorldBetaModelNetworkGeneratorMilieuParams<AgentType, EdgeType>();

		params.setNetwork(network);
		params.setEdgeModifier(edgeModifier);
		params.setRandomDist(randomDist);

		// TODO Check if required
		params.setkProvider(new MoreKValueProvider<AgentType>() {
			@Override
			public int getKValue(AgentType node) {
				return paraMap.getK(node.getMilieuGroup());
			}
		});

		AbstractDistribution abstractDis = MManager
				.getURandomService()
				.getDistribution(
						(String) PmParameterManager
								.getParameter(MRandomPa.RND_UNIFORM_DIST_NETWORK_BUILDING));

		if (abstractDis instanceof Uniform) {
			this.rand = (Uniform) abstractDis;
		} else {
			this.rand = MManager.getURandomService().getUniform();
			logger.warn("Use default uniform distribution");
		}

		params.setRewireManager(new MDefaultPartnerFinder<AgentType, EdgeType>() {

			@Override
			public AgentType findPartner(Graph<AgentType, EdgeType> graph, AgentType focus) {
				Class<? extends AgentType> requestClass = getRequestClass(focus);

				return findDistantTarget(paraMap, network, focus, requestClass);
			}
		});

		MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType> gen = new MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType>(
				params);

		return (MoreRsNetwork<AgentType, EdgeType>) gen.buildNetwork(agents);
	}

	/**
	 * @param hh
	 * @param requestClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	// handled by try/catch
	protected Class<? extends AgentType> getRequestClass(AgentType hh) {
		Class<? extends AgentType> requestClass;
		if (geoRequestClass == null) {
			try {
				requestClass = (Class<AgentType>) hh.getClass().getSuperclass();
			} catch (ClassCastException e) {
				logger.error("Agent's super class is not of type AgentType. Please use setGeoRequestClass!");
				throw new ClassCastException(
						"Agent's super class is not of type AgentType. Please use setGeoRequestClass!");
			}
		} else {
			requestClass = geoRequestClass;
		}
		return requestClass;
	}

	/**
	 * @param networkParams
	 * @param network
	 * @param focus
	 * @param requestClass
	 * @param oldInfluencer
	 */
	@SuppressWarnings("unchecked")
	protected AgentType findDistantTarget(MMilieuNetworkParameterMap networkParams,
			MoreNetwork<AgentType, EdgeType> network,
			AgentType focus, Class<? extends AgentType> requestClass) {
		boolean rewired;

		rewired = false;
		Object random = null;

		int desiredMilieu = 0;
		if ((Boolean) PmParameterManager.getParameter(MNetBuildBhPa.DISTANT_FORCE_MILIEU)) {
			// choose milieu to connect with
			desiredMilieu = getProbabilisticMilieu(networkParams, focus);
		}

		// fetch random partner:
		ArrayList<AgentType> notCheckedPartners = new ArrayList<AgentType>();
		for (AgentType potPartner : context.getObjects(requestClass)) {
			notCheckedPartners.add(potPartner);
		}
		do {
			random = notCheckedPartners.get(rand.nextIntFromTo(0, notCheckedPartners.size() - 1));
			notCheckedPartners.remove(random);

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(focus + "> Random object from context: "
							+ random);
			}
			// LOGGING ->

			if (checkPartner(network, networkParams, focus, (AgentType) random, desiredMilieu)) {
				rewired = true;

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(focus + "> Link with " + random);
				}
				// LOGGING ->
			}
		} while (!rewired && notCheckedPartners.size() > 0);

		if (notCheckedPartners.size() == 0 && !rewired) {
			// <- LOGGING
			logger.warn("All partners were checked and no appropriae partner could be found for " + focus
					+ "! Returning null.");
			// LOGGING ->
			return null;
		}

		
		return (AgentType) random;
	}

	protected int getProbabilisticMilieu(MMilieuNetworkParameterMap networkParams, AgentType focus) {
		Map<Integer, Double> roulette_wheel = new LinkedHashMap<Integer, Double>();

		for (int i = 1; i <= networkParams.size(); i++) {
			roulette_wheel.put(new Integer(i), networkParams.getP_Milieu(focus.getMilieuGroup(), i));
		}

		double randFloat = rand.nextDouble();
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
		throw new IllegalStateException("This code should never be reached!");
	}


	/**
	 * Returns false if source is already a successor of target. Otherwise, the milieu is checked based on paraMap.
	 * 
	 * @param paraMap
	 * @param partnerMilieu
	 * @return true if the check was positive
	 */
	protected boolean checkPartner(MoreNetwork<AgentType, EdgeType> network,
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
			if (potPartner.getMilieuGroup() == desiredMilieu) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(ego + "> Link with distant partner");
				}
				// LOGGING ->

				return true;
			} else {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(ego + "> Wrong milieu (" + potPartner.getMilieuGroup() + ")");
				}
				// LOGGING ->
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
