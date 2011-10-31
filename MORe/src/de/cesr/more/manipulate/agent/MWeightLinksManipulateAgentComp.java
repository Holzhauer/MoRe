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
 * Created by Sascha Holzhauer on 21.04.2011
 */
package de.cesr.more.manipulate.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import de.cesr.more.util.Log4jLogger;

/**
 * Agent component that manages the agents links according to differences between partners regarding some node property.
 * The agents need to implement {@link MoreNetStructureManageable}. The component takes care of updating link weights and 
 * removing and establishing links if required.
 * In case the node properties undergo a certain threshold, weights are increased. As the opposite, if properties exceed a 
 * threshold, weights get decreased. When weights fall below zero, the according links is removed and another
 * one is established. 
 * For making new connections, transitivity links and common-out-neighbour links are considered and ordered according to
 * the property difference between the focal node and the potential partner node.
 * 
 * Thresholds for weight changes and amounts of amount of changed can be defined by setter methods (defaults are given).
 *  
 * @author Sascha Holzhauer
 * @param <A> 
 * @param <E> 
 * @date 21.04.2011 
 *
 */
public class MWeightLinksManipulateAgentComp<A extends MoreLinkManipulatableAgent<A, ?, E>,
		E extends MoreEdge<? super A>> {
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MWeightLinksManipulateAgentComp.class);

	public final double INCREASE_THRESHOLD = 0.1;
	public final double DECREASE_THRESHOLD = 0.5;
	
	public final double INCREASE_AMOUNT = 0.05;
	public final double DECREASE_AMOUNT = 0.05;
	
	private double increaseThreshold = INCREASE_THRESHOLD;
	private double decreaseThreshold = DECREASE_THRESHOLD;
	
	private double increaseAmount = INCREASE_AMOUNT;
	private double decreaseAmount = DECREASE_AMOUNT;

	protected MoreNetworkEdgeModifier<A, E> edgeMan;
	
	protected A agent;
	protected String network;
	protected MoreNetwork<A, E> net;
	
	/**
	 * @param agent 
	 * @param network
	 */
	public MWeightLinksManipulateAgentComp(A agent, String network, MoreNetworkEdgeModifier<A, E> edgeMan) {
		this.agent = agent;
		this.network = network;
		this.net = agent.getLNetworkComp().getNetwork(network);
		this.edgeMan = edgeMan;
	}
	
	/**
	 * @return number of dissolved links
	 */
	public int updateLinkWeight() {
		int counter = 0;
		for (A neighbour : net.getPredecessors(agent)) {
			E edge = net.getEdge(neighbour, agent);
			if (agent.getValueDifference(neighbour) < increaseThreshold) {
				edge.setWeight(Math.min(edge.getWeight() + 
						increaseAmount, 1.0));
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Edge weight (" + edge.getWeight() + ") increased: " + edge);
				}
				// LOGGING ->
			}
			if (agent.getValueDifference(neighbour) > decreaseThreshold) {
				edge.setWeight(Math.max(edge.getWeight() - 
						decreaseAmount, 0.0));
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Edge weight (" + edge.getWeight() + ") decreased: " + edge);
				}
				// LOGGING ->
			}
			if (net.getEdge(neighbour, agent).getWeight() == 0.0) {
				edgeMan.removeEdge(net, neighbour, agent);
				counter++;
				
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Edge (" + edge.getWeight() + ")removed: " + edge);
				}
				// LOGGING ->

			}
		}
		makeNewConnections(counter);
		return counter;
	}

	
	/**
	 * @param numNewConnections
	 */
	public void makeNewConnections(int numNewConnections) {
		Map<Double, List<A>> potPartners = new TreeMap<Double, List<A>>();
		// find transitivity links
		for (A neighbour : net.getPredecessors(agent)) {
			for (A third : net.getPredecessors(neighbour)) {
				Double value = new Double(Math.abs(agent.getValueDifference(third)));
				if (!potPartners.containsKey(value)) {
					potPartners.put(value, new ArrayList<A>());
				}
				potPartners.get(value).add(third);
			}
		}
		
		// find common-out-neighbour links:
		for (A neighbour : net.getSuccessors(agent)) {
			for (A third : net.getPredecessors(neighbour)) {
				Double value = new Double(Math.abs(agent.getValueDifference(third)));
				if (!potPartners.containsKey(value)) {
					potPartners.put(value, new ArrayList<A>());
				}
				potPartners.get(value).add(third);
			}
		}
		Iterator<Double> iter = potPartners.keySet().iterator();
		int counter = 0;
		while (iter.hasNext() && counter < numNewConnections) {
			List<A> list = potPartners.get(iter.next());
			for (A item : list) {
				if (!net.isSuccessor(item, agent)) {
					net.connect(item, agent);
					edgeMan.createEdge(net, item, agent);
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Edge created: " + net.getEdge(item, agent));
					}
					counter++;
					// LOGGING ->
					if (counter == numNewConnections) {
						break;
					}
				}
			}
		}
	}
	/**
	 * @return the increaseThreshold
	 */
	public double getIncreaseThreshold() {
		return increaseThreshold;
	}

	/**
	 * @param increaseThreshold the increaseThreshold to set
	 */
	public void setIncreaseThreshold(double increaseThreshold) {
		this.increaseThreshold = increaseThreshold;
	}

	/**
	 * @return the decreaseThreshold
	 */
	public double getDecreaseThreshold() {
		return decreaseThreshold;
	}

	/**
	 * @param decreaseThreshold the decreaseThreshold to set
	 */
	public void setDecreaseThreshold(double decreaseThreshold) {
		this.decreaseThreshold = decreaseThreshold;
	}

	/**
	 * @return the increaseAmount
	 */
	public double getIncreaseAmount() {
		return increaseAmount;
	}

	/**
	 * @param increaseAmount the increaseAmount to set
	 */
	public void setIncreaseAmount(double increaseAmount) {
		this.increaseAmount = increaseAmount;
	}

	/**
	 * @return the decreaseAmount
	 */
	public double getDecreaseAmount() {
		return decreaseAmount;
	}

	/**
	 * @param decreaseAmount the decreaseAmount to set
	 */
	public void setDecreaseAmount(double decreaseAmount) {
		this.decreaseAmount = decreaseAmount;
	}
}