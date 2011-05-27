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
package de.cesr.more.building.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.cesr.more.basic.MoreEdge;
import de.cesr.more.building.MoreEdgeManager;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.util.Log4jLogger;

/**
 * MORe
 *
 * Agent component to alter the agent's links structure according to link weights.
 * 
 *  
 * @author Sascha Holzhauer
 * @param <A> 
 * @param <E> 
 * @date 21.04.2011 
 *
 */
public class MNetStructureManagerLinkWeight<A extends MoreNetStructureManageable<A, ?, E>,
		E extends MoreEdge<? super A>> {
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MNetStructureManagerLinkWeight.class);

	public final double INCREASE_TRESHOLD = 0.1;
	public final double DECREASE_TRESHOLD = 0.5;
	
	public final double INCREASE_AMOUNT = 0.05;
	public final double DECREASE_AMOUNT = 0.05;
	
	private double increaseTreshold = INCREASE_TRESHOLD;
	private double decreaseTreshold = DECREASE_TRESHOLD;
	
	private double increaseAmount = INCREASE_AMOUNT;
	private double decreaseAmount = DECREASE_AMOUNT;

	protected MoreEdgeManager<A, E> edgeMan;
	
	protected A agent;
	protected String network;
	protected MoreNetwork<A, E> net;
	
	/**
	 * @param agent 
	 * @param network
	 */
	public MNetStructureManagerLinkWeight(A agent, String network, MoreEdgeManager<A, E> edgeMan) {
		this.agent = agent;
		this.network = network;
		this.net = agent.getLaraNetworkComp().getNetwork(network);
		this.edgeMan = edgeMan;
	}
	
	/**
	 * @return number of dissolved links
	 * Created by Sascha Holzhauer on 27.04.2011
	 */
	public int updateLinkWeight() {
		int counter = 0;
		for (A neighbour : net.getPredecessors(agent)) {
			E edge = net.getEdge(neighbour, agent);
			if (agent.getValueDifference(neighbour) < increaseTreshold) {
				edge.setWeight(Math.min(edge.getWeight() + 
						increaseAmount, 1.0));
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Edge weight (" + edge.getWeight() + ") increased: " + edge);
				}
				// LOGGING ->
			}
			if (agent.getValueDifference(neighbour) > decreaseTreshold) {
				edge.setWeight(Math.max(edge.getWeight() - 
						decreaseAmount, 0.0));
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Edge weight (" + edge.getWeight() + ") decreased: " + edge);
				}
				// LOGGING ->
			}
			if (net.getEdge(neighbour, agent).getWeight() == 0.0) {
				edgeMan.removeEdge(neighbour, agent);
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
	 * Created by Sascha Holzhauer on 26.04.2011
	 */
	public void makeNewConnections(int numNewConnections) {
		Map<Double, List<A>> potPartners = new TreeMap<Double, List<A>>();
		// find transitivity links
		for (A neighbour : net.getPredecessors(agent)) {
			for (A third : net.getPredecessors(neighbour)) {
				Double value = new Double(Math.abs(agent.getValueDifference(third)));
				if (!potPartners.containsKey(value)) {
					potPartners.put(value, new ArrayList());
				}
				potPartners.get(value).add(third);
			}
		}
		
		// find common-out-neighbour links:
		for (A neighbour : net.getSuccessors(agent)) {
			for (A third : net.getPredecessors(neighbour)) {
				Double value = new Double(Math.abs(agent.getValueDifference(third)));
				if (!potPartners.containsKey(value)) {
					potPartners.put(value, new ArrayList());
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
					edgeMan.createEdge(item, agent, true);
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
	 * @return the increaseTreshold
	 */
	public double getIncreaseTreshold() {
		return increaseTreshold;
	}

	/**
	 * @param increaseTreshold the increaseTreshold to set
	 */
	public void setIncreaseTreshold(double increaseTreshold) {
		this.increaseTreshold = increaseTreshold;
	}

	/**
	 * @return the decreaseTreshold
	 */
	public double getDecreaseTreshold() {
		return decreaseTreshold;
	}

	/**
	 * @param decreaseTreshold the decreaseTreshold to set
	 */
	public void setDecreaseTreshold(double decreaseTreshold) {
		this.decreaseTreshold = decreaseTreshold;
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