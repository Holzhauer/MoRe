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
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.measures.node.supply;

import java.util.Map;

import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.MAbstractNodeMeasure;
import de.cesr.more.measures.node.MNodeMeasureCategory;
import de.cesr.more.measures.node.MoreComboundNetworkInfo;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.measures.node.MoreValueProvidingAgent;
import de.cesr.more.measures.util.MAbstractAction;
import de.cesr.more.measures.util.MoreAction;

/** 
 * @author Sascha Holzhauer
 * @date 19.01.2010
 */
public class MCompNetInfoSupplier extends MAbstractMeasureSupplier {
	
	MMeasureDescription	description;
	
	public MCompNetInfoSupplier() {
		addMeasures();
		addCategories();
	}
	
	private void addCategories() {
		categories.add(MNodeMeasureCategory.NODE_MISC);
	}
	
	
	private void addMeasures() {

		description = new MMeasureDescription(MNodeMeasureCategory.NODE_MISC, "NODE_MISC",
				"Compound network information (not normalized)");

		measures.put(description, new MAbstractNodeMeasure(description, Double.class) {
			@Override
			public <V extends MoreNodeMeasureSupport, E extends MoreEdge<? super V>> MoreAction getAction(final MoreNetwork<V, E> network,
					final Map<String, Object> parameters) {
				return new MAbstractAction() {

					@Override
					public void execute() {
						MoreComboundNetworkInfo netInfo = (MoreComboundNetworkInfo) parameters.get("networkInfo");
						for (V node : network.getNodes()) {
							if (node instanceof MoreValueProvidingAgent) {
								MoreValueProvidingAgent agent = (MoreValueProvidingAgent) node;
								node.setNetworkMeasureObject(network, new MMeasureDescription(
										MNodeMeasureCategory.NODE_MISC, "Comp",
										"Compound network information (not normalized)"),
										// TODO test!
										getCompoundValue((MoreNetwork<MoreValueProvidingAgent, MoreEdge<MoreValueProvidingAgent>>)network, 
												agent, netInfo).getValue());
							}
						}
					}
				};
			}
		});
	}
	/**
	 * Calculated the compound network information as specified and writes the result as value
	 * of the {@link MoreComboundNetworkInfo} object. NOTE: the passed object is altered and returned!
	 * When the distance of node within reach to the centre is more than 1, the weights along the path
	 * are multiplied. For weights < 1 this means a discount for distant nodes. Normalized weights are
	 * considered.
	 * @param network 
	 * @param <A> agent type
	 * @param <E> edge type
	 * 
	 * @param agent
	 * @param netInfo 
	 * @return MoreComboundNetworkInfo
	 */
	public static <AgentType extends MoreValueProvidingAgent, E  extends MoreEdge<? super AgentType>> MoreComboundNetworkInfo getCompoundValue(MoreNetwork<AgentType, E> network, 
			AgentType agent, MoreComboundNetworkInfo netInfo) {
		netInfo.setValue(getAdjacentValues(network, agent, null, netInfo.getPropertyName(), netInfo.getReach(), 1, 1.0) / 
				getNumReachedNodes(network, agent, null, netInfo.getReach(), 1));
		return netInfo;
	}
	
	
	/**
	 * Recursively sums up values of the given key of agents within the distance given by reach.
	 * The average is calculated whereas the weight decreases with increasing distance to the start
	 * agent. The value of the given agent is not considered.
	 * If a node is connected to the centered agent via different path, both paths count since additional
	 * paths increase the opportunity to get informed! 
	 * @param network 
	 * @param <AgentType> 
	 * 
	 * @param agent
	 * @param precessor 
	 * @param key
	 * @param reach
	 * @param curReach
	 * @param weight 
	 * @return
	 */
	protected static <AgentType extends MoreValueProvidingAgent, E  extends MoreEdge<? super AgentType>> double 
		getAdjacentValues(MoreNetwork<AgentType, E> network, AgentType agent, 
			AgentType precessor, String key, int reach, int curReach, double weight) {
		double value = 0.0;
		for (AgentType a : network.getAdjacent(agent)) {
			if (a != precessor) {
				value += a.getValue(key) * (
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
	 * Analogue to {@link MCompNetInfoSupplier#getAdjacentValues(LaraNetwork, LaraAgent, LaraAgent, String, int, int, double)} nodes
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
	 */
	public static <A extends MoreValueProvidingAgent, E  extends MoreEdge<? super A>> int getNumReachedNodes(MoreNetwork<A, E> network, A center, A predecessor,
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