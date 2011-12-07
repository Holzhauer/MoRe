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
 * Created by Sascha Holzhauer on 02.12.2011
 */
package de.cesr.more.rs.building;


import java.util.Collection;

import repast.simphony.space.gis.Geography;
import repast.simphony.space.graph.DirectedJungNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.geo.util.MGeographyWrapper;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 * 
 * This network builder considers baseline homophily
 * (McPherson2001). Additional to {@link MGeoRsBaselineRadiusNetworkService} it also
 * completely links agent within a group context (in general, the agents parental context).
 * Agents are linked as follows:
 * <ol>
 * <li>Connect every agent with all other agents within the same group context. 
 * This may not entirely reflect the agents' preferences regarding partner milieus.
 * However, a group is as it is and may not be altered by the focal agent. 
 * Nevertheless an agent may choose the place he or she wants to live. These choices are 
 * represented in the market cell distributions the agent initialisation is based upon.</li>
 * <li>For every agent in the context
 * <ol>
 * <li>Fetch all agents within a given radius (<code>SEARCH_RADIUS</code>) from the focal agent (do not consider area
 * boundaries).</li>
 * <li>For every potential partner that is not yet connected check according to milieu specific probability if it should
 * be connected with focal agent. The approach to check the agents that are in the surroundings <i>as they come</i>
 * considers the local milieu distribution and reflects <i>baseline homophily</i>. Applying milieu specific tie
 * probabilities reflects <i>inbreeding homophily</i>.</li>
 * <li>If the number of required neighbours is not satisfied but all fetched agents checked, request more agents from
 * geography within an extended radius (<code>X_SEARCH_RADIUS</code>) until maximum radius (
 * <code>MAX_SEARCH_RADIUS</code>) is reached.</li>
 * </ol>
 * <li>Rewire: For each agent, check if every existing link should be rewired (with probability <code>p_rewire</code>)
 * to a randomly chosen agent from the whole region that passes the milieu check (applying milieu tie probabilities (
 * <code>p_links</code> for every <code>partnerMilieu</code>). On purpose the new partner's milieu is not guaranteed to
 * be the same as that of the original link: The partners within direct surroundings are coined by local milieu 
 * distributions (baseline homophly) and therefore do not entirely reflect the focal agent's preferences.
 * Determining the milieu during rewiring anew may correct to milieu distributions of partners towards
 * inbreeeding homophily and is desired.</li>
 * </ol>
 * 
 * Uses {@link MGeographyWrapper#getSurroundingAgents(Object, double, Class)} to fetch agents (all agents within the
 * given radius of the given class).
 * 
 * Internal: The BaselineDhhRadiusNetworkBuilder is based on DHH_ColCalc_Computer_Lifestyle.
 * 
 * @formatter:off
 * <table>
 * <th>Property</th><th>Value</th>
 * <tr><td>#Vertices</td><td>N (via collection of agents)</td></tr>
 * <tr><td></td><td></td></tr>
 * <tr><td>#Edges:</td><td>N*(N-1)</td></tr>
 * </table>
 *  
 * TODO test
 * 
 * @author Sascha Holzhauer
 * @date 02.12.2011
 * 
 */
public class MGroupBaselineRadiusNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType>>
		extends MGeoRsBaselineRadiusNetworkService<AgentType, EdgeType> {

	public MGroupBaselineRadiusNetworkService(Geography<Object> geography,
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		super(geography, edgeFac, name);
	}

	/**
	 * NOTE: Agents is not used since the collection of agents is taken from context!
	 * 
	 * For each agent: For each potential neighbour in surroundings: Connect according to network parameter map. Then,
	 * rewire and finally make sure the neighbourhood is completely connected.
	 * 
	 * @see de.cesr.more.building.MoreNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(Collection<AgentType> agents) {

		if (context == null) {
			throw new IllegalStateException("Context needs to be set before building the network!");
		}

		MMilieuNetworkParameterMap paraMap = (MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);

		MoreRsNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType>(
				new DirectedJungNetwork<AgentType>(name), context);

		addAgents(network, agents);

		connectGroupContext(agents, network);

		createRadiusNetwork(agents, paraMap, network);
		rewire(agents, paraMap, network);

		return network;
	}

	/**
	 * Connect each agent with every other agent within the same group context.
	 * 
	 * @param agents
	 * @param network
	 */
	protected void connectGroupContext(Collection<AgentType> agents,
			MoreRsNetwork<AgentType, EdgeType> network) {
		for (AgentType focalAgent : agents) {
			for (Object o : focalAgent.getParentContext()) {
				if (focalAgent.getClass().isInstance(o)) {
					@SuppressWarnings("unchecked")
					AgentType potInfluencer = (AgentType) o;
					if (!network.isSuccessor(potInfluencer, focalAgent)) {
						edgeModifier.createEdge(network, potInfluencer, focalAgent);
					}
				}
			}
		}
	}
}