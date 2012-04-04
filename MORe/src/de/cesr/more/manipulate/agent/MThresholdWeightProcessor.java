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


import org.apache.log4j.Logger;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.param.MNetManipulatePa;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 29.03.2012 
 *
 */
public class MThresholdWeightProcessor<A extends MoreLinkManipulatableAgent<A>, E extends MoreEdge<? super A>>
		implements MoreEgoNetworkProcessor<A, E> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MThresholdWeightProcessor.class);

	private final double	increaseThreshold;
	private final double	decreaseThreshold;

	private final double	increaseAmount;
	private final double	decreaseAmount;


	public MThresholdWeightProcessor() {
		increaseThreshold = (Double) PmParameterManager.getParameter(MNetManipulatePa.INCREASE_THRESHOLD);
		decreaseThreshold = (Double) PmParameterManager.getParameter(MNetManipulatePa.INCREASE_THRESHOLD);

		increaseAmount = (Double) PmParameterManager.getParameter(MNetManipulatePa.INCREASE_THRESHOLD);
		decreaseAmount = (Double) PmParameterManager.getParameter(MNetManipulatePa.INCREASE_THRESHOLD);
	}

	/**
	 * @see de.cesr.more.manipulate.agent.MoreEgoNetworkProcessor#process(java.lang.Object,
	 *      de.cesr.more.basic.network.MoreNetwork)
	 */
	@Override
	public void process(A agent, MoreNetwork<A, E> net) {
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
		}
	}

}
