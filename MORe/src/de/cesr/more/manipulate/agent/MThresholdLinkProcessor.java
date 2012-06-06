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
 * Created by Sascha Holzhauer on 29.03.2012
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


/**
 * MORe
 * 
 * When weights fall below zero, the according links are removed and another one is established.
 *
 * @author Sascha Holzhauer
 * @date 29.03.2012 
 *
 */
public class MThresholdLinkProcessor<A extends MoreLinkManipulatableAgent<A>, E extends MoreEdge<? super A>>
		implements MoreEgoNetworkProcessor<A, E> {

	/**
	 * Logger
	 */
	static private Logger					logger	= Logger.getLogger(MThresholdLinkProcessor.class);

	protected MoreNetworkEdgeModifier<A, E>	edgeMan;

	public MThresholdLinkProcessor(MoreNetworkEdgeModifier<A, E> edgeMan) {
		this.edgeMan = edgeMan;
	}

	/**
	 * @see de.cesr.more.manipulate.agent.MoreEgoNetworkProcessor#process(java.lang.Object,
	 *      de.cesr.more.basic.network.MoreNetwork)
	 */
	@Override
	public void process(A agent, MoreNetwork<A, E> net) {
		int counter = 0;
		for (A neighbour : net.getPredecessors(agent)) {
			E edge = net.getEdge(neighbour, agent);
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
		makeNewConnections(counter, agent, net);

	}

	/**
	 * @param numNewConnections
	 */
	public void makeNewConnections(int numNewConnections, A agent, MoreNetwork<A, E> net) {
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

}
