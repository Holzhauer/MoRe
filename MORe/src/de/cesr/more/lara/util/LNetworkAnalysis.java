/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara.util;

import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.more.basic.MoreEdge;
import de.cesr.more.lara.ComboundNetworkInfo;
import de.cesr.more.lara.LaraSimpleNetworkAgent;
import de.cesr.more.networks.MoreNetwork;

/** 
 * 
 * TODO move to MoreLara
 * @author Sascha Holzhauer
 * @date 19.01.2010
 */
public class LNetworkAnalysis {
	
	
	/**
	 * Calculated the compound network information as specified and writes the result as value
	 * of the {@link ComboundNetworkInfo} object. NOTE: the passed object is altered and returned!
	 * When the distance of node within reach to the centre is more than 1, the weights along the path
	 * are multiplied. For weights < 1 this means a discount for distant nodes. Normalized weights are
	 * considered.
	 * @param network 
	 * @param <A> agent type
	 * @param <E> edge type
	 * 
	 * @param agent
	 * @param netInfo 
	 * @return ComboundNetworkInfo
	 */
	public static <A extends LaraSimpleNetworkAgent<A, ?, E>, E  extends MoreEdge<? super A>> ComboundNetworkInfo getCompoundValue(MoreNetwork<A, E> network, 
			A agent, ComboundNetworkInfo netInfo) {
		netInfo.setValue(getAdjacentValues(network, agent, null, netInfo.getName(), netInfo.getReach(), 1, 1.0) / 
				getNumReachedNodes(network, agent, null, netInfo.getReach(), 1));
		return netInfo;
	}
	
	
	/**
	 * TODO discuss: how plausible is it to access the adjacent agent's memory?
	 * 
	 * Recursively sums up values of the given key of agents within the distance given by reach.
	 * The average is calculated whereas the weight decreases with increasing distance to the start
	 * agent. The value of the given agent is not considered.
	 * If a node is connected to the centred agent via different path, both paths count since additional
	 * paths increase the opportunity to get informed! 
	 * @param network 
	 * @param <A> 
	 * 
	 * @param agent
	 * @param precessor 
	 * @param key
	 * @param reach
	 * @param curReach
	 * @param weight 
	 * @return
	 * Created by Sascha Holzhauer on 15.01.2010
	 */
	protected static <A extends LaraAgent<A, ?>, E  extends MoreEdge<? super A>> double getAdjacentValues(MoreNetwork<A, E> network, A agent, 
			A precessor, String key, int reach, int curReach, double weight) {
		double value = 0.0;
		for (A a : network.getAdjacent(agent)) {
			if (a != precessor) {
				value += ((Float)a.getLaraComp().getGeneralMemory().recall(key, LModel.getModel().getCurrentStep()).getValue()).floatValue() * (
						network.getWeight(agent, a) * weight);
				if (reach > curReach) {
					value += getAdjacentValues(network, a, agent, key, reach, curReach + 1,
							weight * network.getWeight(agent, a));
				}
			}
		}
		return value;
	}
	
	/**
	 * Analogue to {@link LNetworkAnalysis#getAdjacentValues(LaraNetwork, LaraAgent, LaraAgent, String, int, int, double)} nodes
	 * may be considered more than once because of paths. However, a path by which an agent was reached is not gone
	 * back.
	 * @param network 
	 * @param <A> 
	 * @param <E> edge type
	 * @param center
	 * @param predecessor
	 * @param reach 
	 * @param curReach 
	 * @return
	 * Created by Sascha Holzhauer on 19.01.2010
	 */
	public static <A extends LaraAgent<A, ?>, E  extends MoreEdge<? super A>> int getNumReachedNodes(MoreNetwork<A, E> network, A center, A predecessor,
			int reach, int curReach) {
		int sum = 0;
		for (A a : network.getAdjacent(center)) {
			if (a != predecessor) {
				sum++;
				if (reach > curReach) {
					sum += getNumReachedNodes(network, a, center, reach, curReach + 1);
				}
			}
		}
		return sum;
	}
}