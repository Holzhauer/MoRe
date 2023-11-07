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


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.util.Log4jLogger;


/**
 * Agent component that manages the agents links according to differences between partners regarding some node property.
 * The agents need to implement {@link MoreNetStructureManageable}. The component takes care of updating link weights
 * and removing and establishing links if required.
 * 
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
public class MEgoNetworkManagerComp<A, E extends MoreEdge<? super A>> implements MoreEgoNetworkManagerComp<A, E> {
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MEgoNetworkManagerComp.class);

	protected Map<MoreEgoNetworkEvent, MoreEgoNetworkProcessor<A, E>>	processors;
	
	/**
	 * @param agent 
	 * @param network
	 */
	public MEgoNetworkManagerComp(Map<MoreEgoNetworkEvent, MoreEgoNetworkProcessor<A, E>> processorMap) {
		processors = processorMap;
	}

	public static <A, E extends MoreEdge<? super A>> Map<MoreEgoNetworkEvent, MoreEgoNetworkProcessor<A, E>> getEmptyProcessorMap() {
		return new HashMap<MoreEgoNetworkEvent, MoreEgoNetworkProcessor<A, E>>();
	}

	/**
	 * @see de.cesr.more.manipulate.agent.MoreEgoNetworkManagerComp#getEgoNetworkProcessor(de.cesr.more.manipulate.agent.MoreEgoNetworkEvent)
	 */
	@Override
	public MoreEgoNetworkProcessor<A, E> getEgoNetworkProcessor(MoreEgoNetworkEvent event) {
		return processors.get(event);
	}

	/**
	 * @see de.cesr.more.manipulate.agent.MoreEgoNetworkManagerComp#process(de.cesr.more.manipulate.agent.MoreEgoNetworkEvent)
	 */
	@Override
	public void process(MoreEgoNetworkEvent event, A agent, MoreNetwork<A, E> network) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Process event <" + event + "> for agent <" + agent + "> in network <" + network + ">");
		}
		// LOGGING ->

		processors.get(event).process(agent, network);
	}
}
