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
 * Created by Sascha Holzhauer on 05.06.2012
 */
package de.cesr.more.manipulate.agent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import de.cesr.more.basic.MManager;
import de.cesr.more.basic.MNetworkManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.manipulate.agent.analyse.MoreLinkManipulationAnalysableAgent;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetManipulatePa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.rs.building.MDefaultPartnerFinder;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.rs.building.MorePartnerFinder;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 05.06.2012 
 *
 */
public class MBlacklistThresholdLinkProcessor<A extends MoreLinkManipulatableAgent<A> & MoreMilieuAgent, 
		E extends MoreEdge<? super A>> extends MThresholdLinkProcessor<A, E> {
	/**
	 * Logger
	 */
	static protected Logger logger = Logger.getLogger(MBlacklistThresholdLinkProcessor.class);

	static protected final String RANDOM_GENERATOR_PROBABILISTIC_EDGE_CREATION = "RANDOM_GENERATOR_PROBABILISTIC_EDGE_CREATION";
	static protected final String RANDOM_DIST_PROBABILISTIC_EDGE_CREATION = "RANDOM_DIS_PROBABILISTIC_EDGE_CREATION";

	protected MoreNetworkEdgeModifier<A, E> edgeMan;

	protected Uniform rand;
	

	protected MoreNetwork<A, MoreEdge<A>> blacklistNetwork;
	
	protected MMilieuNetworkParameterMap netParams = (MMilieuNetworkParameterMap) PmParameterManager
			.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);

	protected MorePartnerFinder<A, E> partnerFinder = new MDefaultPartnerFinder<A, E>();

	
	public MBlacklistThresholdLinkProcessor(
			MoreNetworkEdgeModifier<A, E> edgeMan) {
		super(edgeMan);
		this.edgeMan = edgeMan;
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
	}
	
	@SuppressWarnings("unchecked")
	protected MoreNetwork<A, MoreEdge<A>> getBlacklistNetwork() {
		if (this.blacklistNetwork == null) {
			this.blacklistNetwork = (MoreNetwork<A, MoreEdge<A>>) 
					MNetworkManager.getNetwork(((String)PmParameterManager.getParameter(MNetManipulatePa.DYN_BLACKLIST_NAME)));
		}
		return this.blacklistNetwork;
	}

	@Override
	public void process(A agent,
			MoreNetwork<A, E> network) {

		int counter = 0;
		for (A neighbour : network.getPredecessors(agent)) {
			E edge = network.getEdge(neighbour, agent);
			if (edge.getWeight() <= 0.0) {
				edgeMan.removeEdge(network, neighbour, agent);
				if ((Boolean)PmParameterManager.getParameter(MNetManipulatePa.DYN_USE_BLACKLIST)) {
					getBlacklistNetwork().connect(neighbour, agent);
				}
				counter++;

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Edge (" + edge.getWeight() + ")removed: "
							+ edge);
				}
				// LOGGING ->

			}
		}
		if (counter > 0) {
			// <- LOGGING
			logger.info(agent + "> Number of removed links: " + counter);
			// LOGGING ->

			makeNewConnections(counter, agent, network);
		}		
	}

	protected class TieCounter {
		public int transitiveTies = 0;
		public int reciprocalTies = 0;
	}

	/**
	 * @param numNewConnections
	 */
	public void makeNewConnections(int numNewConnections, A agent,
			MoreNetwork<A, E> net) {
		Map<Double, Set<A>> potPartners = new TreeMap<Double, Set<A>>();

		int transitiveTiesPool = 0;
		int reciprocalTiesPool = 0;

		TieCounter tieCounter = new TieCounter();

		ArrayList<A> transitiveAgents = null;
		ArrayList<A> reciprocalAgents = null;

		if (logger.isInfoEnabled()) {
			transitiveAgents = new ArrayList<A>();
		}
		reciprocalAgents = new ArrayList<A>();

		// add reciprocal links:
		for (A successor : net.getSuccessors(agent)) {
			// check if a links does not already exist:
			if ((!net.isSuccessor(agent, successor)) && rand.nextDouble() <= netParams.getDynProbReciprocity(agent
					.getMilieuGroup())) {
				Double value = new Double(Math.abs(agent.getValueDifference(successor)));
				if (!potPartners.containsKey(value)) {
					potPartners.put(value, new HashSet<A>());
				}
				potPartners.get(value).add(successor);

				reciprocalTiesPool++;
				reciprocalAgents.add(successor);
			}
			potentiallyAddGlobalLink(agent, potPartners, net);
		}

		// find transitivity links
		for (A neighbour : net.getPredecessors(agent)) {
			for (A third : net.getPredecessors(neighbour)) {

				if (third != agent && !reciprocalAgents.contains(third)) {
					if (rand.nextDouble() <= netParams.getDynProbTransitivity(agent.getMilieuGroup())) {
						Double value = new Double(Math.abs(agent.getValueDifference(third)));
						if (!potPartners.containsKey(value)) {
							potPartners.put(value, new HashSet<A>());
						}
						potPartners.get(value).add(third);

						if (logger.isInfoEnabled()) {
							transitiveTiesPool++;
							transitiveAgents.add(third);
						}
					}
					potentiallyAddGlobalLink(agent, potPartners, net);
				}
			}
		}
	
		// create new links:
		int counter = createNewLinks(numNewConnections, agent, net,
				potPartners, tieCounter, transitiveAgents, reciprocalAgents);

		// fill required links with global ones:
		int numAgents = net.numNodes();
		while (counter < numNewConnections && numAgents > 0) {
			A global = partnerFinder.findPartner(
					net.getJungGraph(), agent, true);

			if (global != null && !getBlacklistNetwork().isSuccessor(agent, global)) {
				net.connect(global, agent);
				edgeMan.createEdge(net, global, agent);
				
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Edge created: " + net.getEdge(global, agent));
				}
				// LOGGING ->
				counter++;
			}
			numAgents--;
		}

		
		if (agent instanceof MoreLinkManipulationAnalysableAgent) {
			((MoreLinkManipulationAnalysableAgent)agent).setNumNewLinks(counter);
		}
		
		
		if (counter < numNewConnections) {
			logger.warn(agent
					+ "> Less connections established than requried: "
					+ (numNewConnections - counter));
		}

		// <- LOGGING
		if (logger.isInfoEnabled()) {
			logger.info(agent + "> Number of established links: " + counter
					+ "(transitive: " + tieCounter.transitiveTies
					+ " / reciprocal: " + tieCounter.reciprocalTies + ")"
					+ "(transitive Pool: " + transitiveTiesPool
					+ " / reciprocal Pool: " + reciprocalTiesPool + ")");
		}
		// LOGGING ->
	}

	/**
	 * @param numNewConnections
	 * @param agent
	 * @param net
	 * @param potPartners
	 * @param tieCounter
	 * @param transitiveAgents
	 * @param reciprocalAgents
	 * @return
	 */
	protected int createNewLinks(int numNewConnections, A agent,
			MoreNetwork<A, E> net,
			Map<Double, Set<A>> potPartners,
			TieCounter tieCounter, ArrayList<A> transitiveAgents,
			ArrayList<A> reciprocalAgents) {
		Iterator<Double> iterator = potPartners.keySet().iterator();
		int counter = 0;
		while (iterator.hasNext() && counter < numNewConnections) {
			Set<A> list = potPartners.get(iterator.next());
			for (A item : list) {
				if (!net.isSuccessor(agent, item) && item != agent
						&& !getBlacklistNetwork().isSuccessor(agent, item)) {
					net.connect(item, agent);
					edgeMan.createEdge(net, item, agent);
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Edge created: "
								+ net.getEdge(item, agent));
					}
					if (logger.isInfoEnabled()) {
						if (transitiveAgents.contains(item)) {
							tieCounter.transitiveTies++;
						}
						if (reciprocalAgents.contains(item)) {
							tieCounter.reciprocalTies++;
						}
					}
					counter++;
					// LOGGING ->
					if (counter == numNewConnections) {
						break;
					}
				}
			}
		}
		return counter;
	}

	protected void potentiallyAddGlobalLink(A agent,
			Map<Double, Set<A>> potPartners, MoreNetwork<A, E> net) {
		if (rand.nextDouble() <= netParams.getDynProbGlobal(agent.getMilieuGroup())) {
			A potPartner = partnerFinder.findPartner(net.getJungGraph(), agent);
			if (potPartner != null) {
				Double value = new Double(Math.abs(agent.getValueDifference(potPartner)));
				if (!potPartners.containsKey(value)) {
					potPartners.put(value, new HashSet<A>());
				}
				potPartners.get(value).add(potPartner);
			}
		}
	}
}