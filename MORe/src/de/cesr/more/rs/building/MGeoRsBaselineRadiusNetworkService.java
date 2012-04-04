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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import repast.simphony.util.collections.IndexedIterable;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.param.reader.MMilieuNetDataReader;
import de.cesr.more.rs.building.analyse.MoreBaselineNetworkServiceAnalysableAgent;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.geo.util.MGeographyWrapper;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;


/**
 * This network builder considers baseline homophily (McPherson2001). Agents are linked as follows:
 * <ol>
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
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * <li>{@link MNetworkBuildingPa.MILIEU_NETWORK_PARAMS}</li>
 * <li>...</li>
 * </ul>
 * 
 * 
 * @author Sascha Holzhauer
 * @param <AgentT>
 *        The type of nodes
 * @date 22.07.2010
 * 
 */
public class MGeoRsBaselineRadiusNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType> & MoreEdge<AgentType>>
		extends MGeoRsNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger
											.getLogger(MGeoRsBaselineRadiusNetworkService.class);
	
	/**
	 * The multiplicative of the first retrieved neighbours' list's size
	 * that is used to initialise the checked neighbours array list. 
	 */
	public static final int CHECKED_NEIGHBOURS_CAPACITY_FACTOR = 3;

	protected Uniform		rand;

	protected String		name;
	
	protected MMilieuNetworkParameterMap paraMap;
	
	protected MMilieuPartnerFinder<AgentType, EdgeType> partnerFinder;

	public MGeoRsBaselineRadiusNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac) {
		this(edgeFac, "Network");
	}

	@SuppressWarnings("unchecked") // geography needs to be parameterised with Object
	public MGeoRsBaselineRadiusNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		this((Geography<Object>) PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac, name);
	}

	/**
	 * - builder constructor - edge modifier - builder set - parma
	 * 
	 * @param areasGeography
	 */
	public MGeoRsBaselineRadiusNetworkService(Geography<Object> geography,
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		super(geography, edgeFac);
		this.name = name;
		this.paraMap = (MMilieuNetworkParameterMap) PmParameterManager
			.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);
		this.partnerFinder = new MMilieuPartnerFinder<AgentType, EdgeType>(this.paraMap);
	}

	/**
	 * NOTE: Agents is not used since the collection of agents is taken from context! Make sure that the order of agents
	 * in agents is defined and consistent for equal random seeds!
	 * 
	 * For each agent: For each potential neighbour in surroundings:
	 * 
	 * @see de.cesr.more.building.network.MoreNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(
			Collection<AgentType> agents) {

		checkParameter();
		
		PmParameterManager.logParameterValues(MNetworkBuildingPa.values());
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Milieu Network Parameter: " + paraMap);
		}
		// LOGGING ->


		
		MoreRsNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType >(
				((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ?
						new DirectedJungNetwork<AgentType>(name) :
						new UndirectedJungNetwork<AgentType>(name), context, this.edgeModifier.getEdgeFactory());

		addAgents(network, agents);

		createRadiusNetwork(agents, this.paraMap, network);

		// <- LOGGING
		logEdges(logger, network, "");
		// LOGGING ->

		// <- LOGGING
		logEdges(logger, network, "AfterRewire: ");
		// LOGGING ->

		return network;
	}

	/**
	 * 
	 */
	protected void checkParameter() {
		if (context == null) {
			throw new IllegalStateException(
					"Context needs to be set before building the network!");
		}

		AbstractDistribution abstractDis = MManager
				.getURandomService()
				.getDistribution(
						(String) PmParameterManager
								.getParameter(MRandomPa.RND_UNIFORM_DIST_NETWORK_BUILDING));

		if (abstractDis instanceof Uniform) {
			this.rand = (Uniform) abstractDis;
		} else {
			this.rand = MManager.getURandomService().getUniform();
			logger.warn("Use default uniform distribution");
		}

		if (((MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS)) == null) {
			new MMilieuNetDataReader().initParameters();
		}
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
	protected int connectAgent(MMilieuNetworkParameterMap paraMap,
			MoreNetwork<AgentType, EdgeType> network,
			int numNotConnectedPartners, MGeographyWrapper<Object> geoWrapper,
			AgentType hh) {

		logger.info(hh + " > Connect... (mileu: " + hh.getMilieuGroup() + ")");

		int numNeighbors = 0;
		
		int numRadiusExtensions = 0;

		Class<? extends AgentType> requestClass = getRequestClass(hh);
			

		double curRadius = paraMap.getSearchRadius(hh.getMilieuGroup());

		// fetch potential neighbours from proximity. NumNeighbors should be
		// large enough to find required number of
		// parters per milieu
		numNeighbors = paraMap.getK(hh.getMilieuGroup());
		List<AgentType> neighbourslist = geoWrapper
				.<AgentType>getSurroundingAgents(hh, curRadius, requestClass);
		
		List<AgentType> checkedNeighbours = new ArrayList<AgentType>(neighbourslist.size() * 
				CHECKED_NEIGHBOURS_CAPACITY_FACTOR);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Found " + neighbourslist.size() + " of class " + 
					hh.getClass().getSuperclass()+ " neighbours within " + curRadius + " meters.");
		}
		// LOGGING ->

		// mixing neighbour collection
		shuffleCollection(neighbourslist);
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Shuffled: " + neighbourslist);
		}
		// LOGGING ->


		boolean anyPartnerAssignable = true;

		// to check if the required neighbours is satisfied
		int numLinkedNeighbors = 0;

		Iterator<AgentType> neighbourIter = neighbourslist.iterator();
		AgentType potPartner;

		while (numLinkedNeighbors < numNeighbors && anyPartnerAssignable) {
			if (neighbourIter.hasNext()) {
				potPartner = neighbourIter.next();

				// TODO check if potPartner has capacity (new feature)

				if (partnerFinder.checkPartner(network.getJungGraph(), paraMap, hh, potPartner, 0)) {
					createEdge(network, potPartner, hh);

					numLinkedNeighbors++;
					
					// substitutes rewiring:
					
					if (numLinkedNeighbors < numNeighbors &&
							distantLinking(paraMap, network, hh, requestClass) != null) {
						numLinkedNeighbors++;
					}

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
								+ " > No Partner found, but max search radius NOT reached!");
					}
					// LOGGING ->

					// extending list of potential neighbours:
					curRadius += paraMap.getXSearchRadius(hh.getMilieuGroup());
					numRadiusExtensions++;

					checkedNeighbours.addAll(neighbourslist);
					
					neighbourslist = geoWrapper
							.<AgentType> getSurroundingAgents(hh, curRadius, requestClass);
					
					neighbourslist.removeAll(checkedNeighbours);

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Found " + neighbourslist.size() + " new neighbours within " + curRadius
								+ " meters.");
					}
					// LOGGING ->

					shuffleCollection(neighbourslist);

					neighbourIter = neighbourslist.iterator();
				} else {
					anyPartnerAssignable = false;
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(hh + " > Not enough partners found in max search radius!");
					}
					// LOGGING ->
				}
			}
		}
		
		if (hh instanceof MoreBaselineNetworkServiceAnalysableAgent) {
			MoreBaselineNetworkServiceAnalysableAgent agent = (MoreBaselineNetworkServiceAnalysableAgent) hh;
			agent.setFinalRadius(curRadius);
			agent.setNumRadiusExtensions(numRadiusExtensions);
		}
		
		numNotConnectedPartners += numNeighbors - numLinkedNeighbors;

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(hh + " > " + numLinkedNeighbors
					+ " neighbours found (from " + numNeighbors + ")");
		}
		// LOGGING ->
		return numNotConnectedPartners;
	}

	/**
	 * @param hh
	 * @param requestClass
	 * @return
	 */
	@SuppressWarnings("unchecked") // handled by try/catch
	protected Class<? extends AgentType> getRequestClass(AgentType hh) {
		Class<? extends AgentType> requestClass;
		if (geoRequestClass == null) {
			try {
				requestClass = (Class<AgentType>) hh.getClass().getSuperclass();
			} catch (ClassCastException e) {
				logger.error("Agent's super class is not of type AgentType. Please use setGeoRequestClass!");
				throw new ClassCastException("Agent's super class is not of type AgentType. Please use setGeoRequestClass!");
			}
		} else {
			requestClass = geoRequestClass;
		}
		return requestClass;
	}




	/**
	 * @param networkParams
	 * @param network
	 * @param focus
	 * @param requestClass
	 * @param oldInfluencer
	 */
	@SuppressWarnings("unchecked")
	protected AgentType distantLinking(MMilieuNetworkParameterMap networkParams, MoreNetwork<AgentType, EdgeType> network,
			AgentType focus, Class<? extends AgentType> requestClass) {
		boolean rewired;
		
		if (networkParams.getP_Rewire(focus.getMilieuGroup()) > this.rand
				.nextDouble()) {
	ArrayList<AgentType> agents = new ArrayList<AgentType>();
			for (AgentType agent : context.getObjects(requestClass)) {
				agents.add(agent);
			}
			return partnerFinder.findPartner(agents, network.getJungGraph(), focus, true);
		} else {
			return null;
		}
	}

	/**
	 * @param context
	 * @return
	 */
	protected AgentType getRandomFromContext(Context<AgentType> context, Class<? extends AgentType> clazz) {
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
		
		return true;
	}

	protected int getProbabilisticMilieu(MMilieuNetworkParameterMap networkParams, AgentType focus) {
		Map<Integer, Double> roulette_wheel = new LinkedHashMap<Integer, Double>();

		for (int i = 1; i <= networkParams.size(); i++) {
			roulette_wheel.put(new Integer(i), networkParams.getP_Milieu(focus.getMilieuGroup(), i));
		}

		double randFloat = rand.nextDouble();
		if (randFloat < 0.0 || randFloat > 1.0) {
			throw new IllegalStateException(rand
					+ "> Make sure min = 0.0 and max = 1.0");
		}

		float pointer = 0.0f;
		for (Entry<Integer, Double> entry : roulette_wheel.entrySet()) {
			pointer += entry.getValue().doubleValue();
			if (pointer >= randFloat) {
				return entry.getKey().intValue();
			}
		}
		throw new IllegalStateException("This code should never be reached!");
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MGeoRsBaselineRadiusNetworkService";
	}
}
