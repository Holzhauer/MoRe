/**
 * MORe - Managing Ongoing Relationships is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Center for Environmental Systems Research, Kassel
 * 
 * Created by Sascha Holzhauer on 22.07.2010
 */
package de.cesr.more.rs.building;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;
import repast.simphony.context.Context;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.util.collections.IndexedIterable;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.param.reader.MMilieuNetDataReader;
import de.cesr.more.rs.building.edge.MRsEdgeFactory;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.geo.util.MGeographyWrapper;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterManager;

import de.cesr.more.util.MDefaultMilieuKeysMap;

/**
 * <h3><code>BaslineDhhRadiusNetworkBuilder</code></h3>
	This network builder considers baseline
	homophily (McPherson2001). Agents are linked as follows:
	<ol>
		<li>Build a collection of all agents from the main context.</li>
		<li>For every agent in the context
			<ol>
				<li>Fetch all agents within a given radius (<code>SEARCH_RADIUS</code>) from the focal agent (do not consider area boundaries).</li>
				<li>For every potential partner that is not yet connected check according to milieu specific probability
					if it should be connected with focal agent. The approach to check the agents that are in the surroundings <i>as they come</i>
					considers the local milieu distribution and reflects <i>baseline homophily</i>. Applying milieu specific tie probabilities reflects
					<i>inbreeding homophily</i>.</li>
				<li>If the number of required neighbours is not satisfied but all fetched agents checked, request more agents from geography 
					within an extended radius (<code>X_SEARCH_RADIUS</code>) until maximum radius (<code>MAX_SEARCH_RADIUS</code>) is reached.</li>
		</ol>
		<li>Rewire: For each agent, check if every existing link should be rewired (with probability <code>p_rewire</code>) to a randomly 
		chosen agent from the whole region that passes the milieu check (applying milieu tie probabilities (<code>p_links</code> for every
		<code>partnerMilieu</code>). On purpose the new partner's milieu is not the same as that of the original link: The partners within direct
		surroundings are coined by local milieu distributions (baseline homophly) and therefore do not entirely reflect the focal agent's preferences.
		Determining the milieu during rewiring anew may correct to milieu distributions of partners towards inbreeeding homophily and is desired. </li>
	</ol>
 * 
 * @author Sascha Holzhauer
 * @param <AgentT>
 *            The type of nodes
 * @date 22.07.2010
 * 
 */
public class MBaselineDhhRadiusNetworkBuilder<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType> & MoreEdge<AgentType>>
		extends MGeoRsNetworkService<AgentType, EdgeType> implements MoreRsNetworkBuilder<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MBaselineDhhRadiusNetworkBuilder.class);

	private Uniform rand;


	/**
	 * @param areasGeography
	 */
	public MBaselineDhhRadiusNetworkBuilder(MRsEdgeFactory<AgentType, EdgeType> edgeFac) {
		super(edgeFac);
		AbstractDistribution abstractDis = MManager
				.getMRandomService()
				.getDistribution(
						(String) PmParameterManager
								.getParameter(MRandomPa.RND_STREAM_NETWORK_BUILDING));
		if (abstractDis instanceof Uniform) {
			this.rand = (Uniform) abstractDis;
		} else {
			this.rand = MManager.getMRandomService().getUniform();
			logger.warn("Use default uniform distribution");
		}
	}

	/**
	 * NOTE: Agents is not used since the collection of agents is taken from
	 * context! Make sure that the order of agents in agents is defined and
	 * consistent for equal random seeds!
	 * 
	 * For each agent: For each potential neighbour in surroundings:
	 * 
	 * @see de.cesr.more.building.network.MoreNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildRsNetwork(
			Collection<AgentType> agents, String name) {
		if (context == null) {
			throw new IllegalStateException(
					"Context needs to be set before building the network!");
		}
		
		if (((MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS)).isEmpty()) {
			new MMilieuNetDataReader().initParameters();
		}
		
		MMilieuNetworkParameterMap paraMap = (MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);

		MoreRsNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType>(
				new DirectedJungNetwork<AgentType>(name), context);

		addAgents(network, agents);

		createRadiusNetwork(agents, paraMap, network);

		// <- LOGGING
		logEdges(network, "");
		// LOGGING ->

		rewire(agents, paraMap, network);

		// <- LOGGING
		logEdges(network, "AfterRewire: ");
		// LOGGING ->


		return network;
	}

	/**
	 * @param agents
	 * @param numNotConnectedPartners
	 * @param paraMap
	 * @param network
	 */
	protected void createRadiusNetwork(Collection<AgentType> agents,
			MMilieuNetworkParameterMap paraMap,
			MoreRsNetwork<AgentType, EdgeType> network) {

		int numNotConnectedPartners = 0;

		MGeographyWrapper<Object> geoWrapper = new MGeographyWrapper<Object>(
				super.geography);

		// map milieu ids to range from 0 to (# milieus -1):
		int[] milieus = new int[((Integer) PmParameterManager
				.getParameter(MNetworkBuildingPa.NUM_MILIEU_GROUPS)).intValue()];
		int j = 0;
		for (Integer i : ((MDefaultMilieuKeysMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEUS)).values()) {
			// milieu indices start at 1:
			milieus[j++] = i.intValue();
		}

		for (AgentType hh : agents) {
			numNotConnectedPartners = connectAgent(paraMap, network,
					numNotConnectedPartners, geoWrapper, hh);

		}
		// <- LOGGING
		logger.info("Number of not connected partners: "
				+ numNotConnectedPartners);
		// LOGGING ->
	}

	/**
	 * @param paraMap
	 * @param network
	 * @param numNotConnectedPartners
	 * @param geoWrapper
	 * @param hh
	 * @return the number of _not_ connected partners
	 */
	private int connectAgent(MMilieuNetworkParameterMap paraMap,
			MoreNetwork<AgentType, EdgeType> network,
			int numNotConnectedPartners, MGeographyWrapper<Object> geoWrapper,
			AgentType hh) {
		
		logger.info(hh + " > Connect... (mileu: " + hh.getMilieuGroup() + ")");

		int numNeighbors = 0;

		double curRadius = paraMap.getSearchRadius(hh.getMilieuGroup());

		// fetch potential neighbours from proximity. NumNeighbors should be
		// large enough to find required number of
		// parters per milieu
		// TODO generalise the super class approach to include all agent classes
		numNeighbors = paraMap.getK(hh.getMilieuGroup());
		List<AgentType> neighbourslist = (List<AgentType>) geoWrapper
				.getSurroundingAgents(hh, curRadius, hh.getClass().getSuperclass());
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Found " + neighbourslist.size() + " neighbours within " + curRadius + " meters.");
		}
		// LOGGING ->


		// mixing neighbour collection
		Collections.<AgentType> sort(neighbourslist,
				new Comparator<AgentType>() {
					@Override
					public int compare(AgentType o1, AgentType o2) {
						return o1.getAgentId().compareTo(o2.getAgentId());
					}
				});
		Collections.shuffle(neighbourslist, new Random(
				((Integer)PmParameterManager.getParameter(MRandomPa.RANDOM_SEED_NETWORK_BUILDING)).intValue()));

		boolean anyPartnerAssignable = true;

		Collection<AgentType> linkedNeighbors = new ArrayList<AgentType>(
				neighbourslist.size());

		Iterator<AgentType> neighbourIter = neighbourslist.iterator();
		AgentType potPartner;

		while (linkedNeighbors.size() < numNeighbors && anyPartnerAssignable) {
			if (neighbourIter.hasNext()) {
				potPartner = neighbourIter.next();

				// TODO check if potPartner has capacity (new feature)

				if (checkPartner(network, paraMap, hh, potPartner)) {
					createEdge(network, hh, potPartner);

					linkedNeighbors.add(potPartner);

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh + " > Found partner: " + potPartner);
					}
					// LOGGING ->
				}
			} else {
				// in case no partner was found the source set should be
				// increased:
				if (curRadius < paraMap.getMaxSearchRadius(hh.getMilieuGroup())) {
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh
								+ " > No Partner found, but max number of surrounding agents NOT reached!");
					}
					// LOGGING ->

					// extending list of potential neighbours:
					curRadius += paraMap.getXSearchRadius(hh.getMilieuGroup());

					List<AgentType> xNeighbourslist = geoWrapper
							.<AgentType>getSurroundingAgents(hh, curRadius, (Class<AgentType>) hh.getClass().getSuperclass());
					xNeighbourslist.removeAll(neighbourslist);
					neighbourslist = xNeighbourslist;

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Found " + neighbourslist.size() + " new neighbours within " + curRadius + " meters.");
					}
					// LOGGING ->
					
					// mixing neighbour collection
					Collections.<AgentType> sort(neighbourslist,
							new Comparator<AgentType>() {
								@Override
								public int compare(AgentType o1, AgentType o2) {
									return o1.getAgentId().compareTo(
											o2.getAgentId());
								}
							});
					Collections.shuffle(neighbourslist, new Random(
							((Integer)PmParameterManager.getParameter(MRandomPa.RANDOM_SEED_NETWORK_BUILDING)).intValue()));

					neighbourIter = neighbourslist.iterator();
				} else {
					anyPartnerAssignable = false;
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh + " > No Partner found!");
					}
					// LOGGING ->
				}
			}
		}
		numNotConnectedPartners += numNeighbors - linkedNeighbors.size();

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(hh + " > " + linkedNeighbors.size()
					+ " neighbours found (from " + numNeighbors + ")");
		}
		// LOGGING ->
		return numNotConnectedPartners;
	}

	/**
	 * Returns false if source is already a successor of target. Otherwise, the
	 * milieu is checked based on paraMap.
	 * 
	 * @param paraMap
	 * @param partnerMilieu
	 * @return true if the check was positive
	 */
	protected boolean checkPartner(MoreNetwork<AgentType, EdgeType> network,
			MMilieuNetworkParameterMap paraMap, AgentType target,
			AgentType source) {
		if (network.isSuccessor(source, target)) {
			return false;
		}
		// determine if potenialpartner's milieu is probable to link with:
		double rand_float = rand.nextDoubleFromTo(0.0, 1.0);
		boolean pass = paraMap.getP_Milieu(target.getMilieuGroup(),
				source.getMilieuGroup()) > rand_float;

		if (logger.isDebugEnabled()) {
			logger.debug((pass ? target + "> " + source + "'s mileu ("
					+ source.getMilieuGroup() + ") accepted" : target + "> "
					+ source + "'s mileu (" + source.getMilieuGroup()
					+ ") rejected")
					+ " (probability: "
					+ paraMap.getP_Milieu(target.getMilieuGroup(),
							source.getMilieuGroup())
					+ " / random: "
					+ rand_float);
		}
		return pass;
	}

	/**
	 * NOTE: Make sure that the order of agents in agents is defined and
	 * consistent for equal random seeds!
	 * 
	 * @param network
	 * @param agents
	 * @param networkParams
	 */
	public void rewire(Collection<AgentType> agents,
			MMilieuNetworkParameterMap networkParams,
			MoreRsNetwork<AgentType, EdgeType> network) {
		for (AgentType agent : agents) {
			rewireNode(networkParams, network, agent);
		}
	}

	/**
	 * @param networkParams
	 * @param network
	 * @param focus
	 */
	private void rewireNode(MMilieuNetworkParameterMap networkParams,
			MoreNetwork<AgentType, EdgeType> network, AgentType focus) {
		boolean rewired;
		for (AgentType oldInfluencer : network.getPredecessors(focus)) {
			// TODO check for side effects because new edges are added during
			// scanning!
			// check rewiring probability for each (old) partner:
			if (networkParams.getP_Rewire(focus.getMilieuGroup()) > this.rand
					.nextDouble()) {
				rewired = false;
				// fetch random partner:
				do {
					Object random = getRandomFromContext(context, (Class<AgentType>) focus.getClass());

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(focus + "> Random object from context: "
								+ random);
					}
					// LOGGING ->

					if (checkPartner(network, networkParams, focus,
							(AgentType) random)) {
						createEdge(network, focus, (AgentType) random);
						rewired = true;
					}
				} while (!rewired);
				context.remove(network.getEdge(oldInfluencer, focus));
				network.disconnect(oldInfluencer, focus);
			}
		}
	}

	/**
	 * @param context
	 * @return
	 */
	protected AgentType getRandomFromContext(Context<AgentType> context, Class<AgentType> clazz) {
		IndexedIterable<AgentType> iter = context.getObjects(clazz);
		return iter.get(rand.nextIntFromTo(0, iter.size() - 1));
	}

	@Override
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network,
			AgentType node) {

		MMilieuNetworkParameterMap networkParams = (MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);
		MGeographyWrapper<Object> geoWrapper = new MGeographyWrapper<Object>(
				super.geography);

		network.addNode(node);

		int numNotConnectedPartners = connectAgent(networkParams, network, 0,
				geoWrapper, node);
		// <- LOGGING
		logger.info("Number of not connected partners: "
				+ numNotConnectedPartners);
		// LOGGING ->

		rewireNode(networkParams, network, node);
		return true;
	}
}
