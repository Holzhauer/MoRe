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


import org.apache.log4j.Logger;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 05.06.2012 
 *
 */
public class MMilieuThresholdWeightProcessor<A extends MoreLinkManipulatableAgent<A> & MoreMilieuAgent, E extends MoreEdge<? super A>>
		extends MThresholdWeightProcessor<A, E> {
	protected static final double WEIGHT_MAX = 2.0;
	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MMilieuThresholdWeightProcessor.class);

	

	@Override
	public void process(A agent,
			MoreNetwork<A, E> network) {

		MMilieuNetworkParameterMap pmap = (MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);

		for (A neighbour : network.getPredecessors(agent)) {
			E edge = network.getEdge(neighbour, agent);


			if (Math.abs(agent.getValueDifference(neighbour)) > pmap.getDynDecreaseThreshold(agent.getMilieuGroup())) {
				// partner's opinion is outside ego's uncertainty range and
				// ego's opinion is outside other's uncertainty range
				edge.setWeight(edge.getWeight() - pmap.getDynDecreaseAmount(agent.getMilieuGroup()));

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Edge ("
							+ edge
							+ ") weight decreased by "
							+ MManager.getFloatPointFormat()
									.format(pmap.getDynDecreaseAmount(agent.getMilieuGroup())) + " ( new value: "
							+ edge.getWeight() + ")");
				}
				// LOGGING ->

			} else {
				// otherwise increase weight by
				// <kernel-function>(ego,other)+<kernel-function>(other,ego)
				if (edge.getWeight() < WEIGHT_MAX && 
						Math.abs(agent.getValueDifference(neighbour)) < pmap.getDynIncreaseThreshold(agent.getMilieuGroup())) {
	
					double weightChange = pmap.getDynIncreaseAmount(agent.getMilieuGroup());
					edge.setWeight(edge.getWeight()
							+ weightChange);
	
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						if (weightChange > 0.0) {
							logger.debug("Edge ("
									+ edge
									+ ") weight increased by "
									+ MManager
											.getFloatPointFormat()
											.format(weightChange)
									+ " ( new value: " + edge.getWeight() + ")");
						}
					}
					// LOGGING ->
				}
			}
		}
	}
}
