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
 * Created by Sascha Holzhauer on 20.06.2013
 */
package de.cesr.more.geo.building.network;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import repast.simphony.query.space.gis.WithinQuery;
import repast.simphony.space.gis.Geography;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.agent.MAbstractAnalyseNetworkAgent;
import de.cesr.more.basic.agent.MoreNetworkAgent;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.geo.MoreGeoEdge;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildHdffPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.param.reader.MMilieuNetDataReader;
import de.cesr.more.rs.building.MMilieuPartnerFinder;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.rs.geo.util.MGeoHexagon;
import de.cesr.more.rs.geo.util.MoreGeoHexagon;
import de.cesr.more.rs.geo.util.MoreGeoHexagonInitialiser;
import de.cesr.more.util.MRuntimeDbWriter;
import de.cesr.more.util.MRuntimeMemoryDbWriter;
import de.cesr.more.util.MoreRunIdProvider;
import de.cesr.more.util.MoreRuntimeAnalysable;
import de.cesr.more.util.distributions.MGeneralDistributionParameter;
import de.cesr.more.util.distributions.MIntegerDistribution;
import de.cesr.more.util.distributions.MRandomEngineGenerator;
import de.cesr.more.util.distributions.MRealDistribution;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 *
 * This network service considers baseline homophily [1] and distance distributions [2]. The network is build as
 * follows:
 *
 * <ol>
 * <li>Read hexagon shapefile, init {@link MGeoHexagon}s, and add them to the geography and the root context.</li>
 * <li>Assign agents to hexagons and determine distances between hexagons</li>
 * <li>Init degree distributions for milieu groups and degree target for each agent</li>
 * <li>Shuffle agents</li>
 * <li>Do as long as agents' degree targets are not fulfilled:
 * <ol>
 * <li>For every agents with degree less than degree target
 * <ol>
 * <li>Draw a distance from milieu-specific distance distribution, identify the according distant hexagon, and randomly
 * choose an agent within that hexagon according to milieu preferences (inbreeding homophily) using
 * {@link MMilieuPartnerFinder}</li>
 * <li>Link the agent to that ambassador, update degree target and explore its neighbours (breadth first traversing):
 * <ol>
 * <li>Add predecessor neighbours to a list</li>
 * <li>Add successor neighbours to the list</li>
 * <lI>Traverse the list in a random order until degree target fulfilled or list's end</li>
 * <li>Check a preceding neighbour as backward link considering distance, partner milieu, and backward probability</li>
 * <li>If check is positive, create an edge and add to exploration queue</li>
 * <li>Check a succeeding neighbour as forward link considering distance, partner milieu, and forward probability</li>
 * <li>If check is positive, create an edge and add to exploration queue</li>
 * </ol>
 * Repeat the steps under 2. with the queue's head and remove it until queue is empty or degree target fulfilled.</li>
 * </ol>
 * </li>
 * <li>Shuffle agents with respect to <code>MNetBuildHdffPa.AGENT_SHUFFLE_INTERVAL</code>.</li>
 * </ol>
 * </li>
 * </ol>
 * See <a href="../../../../../../../networkGeneration.html">MoRe Network Building</a> for a guide to create hexagon
 * shapefiles.
 *
 * The class is such constructed that it enables the building of several networks with same parameters one after
 * another.
 *
 * <table>
 * <th>Property</th>
 * <th>Value</th>
 * <tr>
 * <td>#Vertices</td>
 * <td>N (via collection of agents)</td>
 * </tr>
 * <tr>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>#Edges:</td>
 * <td>undefined</td>
 * </tr>
 * </table>
 *
 * <br>
 * <br>
 *
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa#BUILD_DIRECTED}</li>
 * <li>{@link MNetBuildHdffPa#HEXAGON_SHAPEFILE}</li>
 * <li>{@link MNetBuildHdffPa#HEXAGON_SHAPEFILE_2ND}</li>
 * <li>{@link MNetBuildHdffPa#HEXAGON_INITIALISER_CLASS}</li>
 *
 * <li>{@link MNetworkBuildingPa#P_MILIEUS} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#PROB_FORWARD} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#PROB_BACKWARD} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * 
 * <li>{@link MNetBuildHdffPa#K_DISTRIBUTION_CLASS} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#K_PARAM_A} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#K_PARAM_B} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 *
 * <li>{@link MNetBuildHdffPa#DIST_DISTRIBUTION_CLASS} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#DIST_PARAM_A} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#DIST_PARAM_B} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#DIST_PARAM_XMIN} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#DIST_PARAM_PLOCAL} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * 
 * <li>{@link MNetBuildHdffPa#DIM_WEIGHTS_GEO} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#DIM_WEIGHTS_MILIEU} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#MAX_SEARCH_RADIUS}</li>
 * </ul>
 *
 * NOTE: The hexagon shapefile must cover all agent positions and should not be much larger since it is used to
 * calculated the area's diameter which is used to initialised the distance distributions.
 *
 * NOTE: In general, it is possible to build several networks by means of a single network service instance of this
 * type. However, it must be understood that a hexagon-agent-relationship needs to be maintained. Consequently, when
 * agents are removed from the simulation {@link #removeNode(MoreNetwork, Object)} must be called which deletes the node
 * also from the hexagon infrastructure. Contrary, if a node shall only be removed from one of several networks based on
 * the same network service, the node may only be removed from the network and {@link #removeNode(MoreNetwork, Object)}
 * may not be called. Also, the node needs to remain in the geography in that case.
 *
 * NOTE: If several instances of this type shall exist for the same spatial extent, distinct geographies need to be used
 * for the instances. Otherwise, overlaying hexagons belonging to different instances are most likely to cause problems.
 *
 * <br>
 * <br>
 *
 * [1] McPherson, M.; Smith-Lovin, L. & Cook, J. Birds of a feather: Homophily in social networks Annual Review of
 * Sociology, Annual Reviews, 2001, 27, 415-444 <br>
 *
 * [2] Onnela, J.-P.; Arbesman, S.; Gonzalez, M. C.; Barabasi, A.-L. & Christakis, N. A. Geographic Constraints on
 * Social Network Groups, PLOS ONE, PUBLIC LIBRARY SCIENCE, 2011, 6
 *
 * <br>
 *
 * @author Sascha Holzhauer
 * @date 20.06.2013
 *
 */
public class MGeoHomophilyDistanceFfNetworkService<AgentType extends MoreMilieuAgent & MoreNetworkAgent<AgentType, EdgeType>, EdgeType extends MoreGeoEdge<AgentType> & MoreEdge<AgentType>>
		extends MGeoNetworkService<AgentType, EdgeType> implements MoreRuntimeAnalysable {

	/**
	 * Logger
	 */
	static private Logger								logger								= Logger.getLogger(MGeoHomophilyDistanceFfNetworkService.class);
	
	static final public int								DISTANCE_FACTOR_FOR_DISTRIBUTION	= 1000;

	static final public double							TOLERANCE_VALUE_DIM_WEIGHTS			= 0.01;

	protected String									name								= "Not Defined";

	protected MMilieuNetworkParameterMap				paraMap;

	protected MoreGeoHexagonInitialiser<AgentType>		hexagonInitialiser;

	protected MMilieuPartnerFinder<AgentType, EdgeType>	partnerFinder;

	protected Uniform									rand;

	protected Map<Integer, MIntegerDistribution>		degreeDistributions;

	protected Map<Integer, MRealDistribution>			distanceDistributions;

	protected Map<AgentType, MoreGeoHexagon<AgentType>>	agentHexagons						= new HashMap<AgentType, MoreGeoHexagon<AgentType>>();

	protected double									distanceStep;
	
	protected double									distanceFactorForDistribution;

	protected double									areaDiameter						= -1.0;

	protected MRuntimeDbWriter							runtimeWriter;

	// these Bools are used for efficiency reasons:
	protected boolean									considerBackwardLinks				= false;
	protected boolean									considerForwardLinks				= false;
	protected boolean									considerMilieus						= false;
	protected boolean									considerDistance					= false;

	protected boolean									initialised							= false;

	/**
	 * Takes the geography from {@link MBasicPa#ROOT_GEOGRAPHY}.
	 *
	 * @param edgeFac
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public MGeoHomophilyDistanceFfNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		this((Geography<Object>) PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac, name);
	}

	/**
	 * Uses main instance of {@link PmParameterManager}.
	 *
	 * @param areasGeography
	 */
	public MGeoHomophilyDistanceFfNetworkService(Geography<Object> geography,
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		this(geography, edgeFac, name, PmParameterManager.getInstance(null));
	}

	public MGeoHomophilyDistanceFfNetworkService(
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name, PmParameterManager pm) {
		this(null, edgeFac, name, pm);
	}

	/**
	 * @param areasGeography
	 */
	public MGeoHomophilyDistanceFfNetworkService(Geography<Object> geography,
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name, PmParameterManager pm) {
		super(geography, edgeFac, pm);

		this.name = name;
		this.pm = pm;

		assignMilieuParamMap();

		for (Integer milieu : this.paraMap.keySet()) {
			if (this.paraMap.getBackwardProb(milieu.intValue()) > 0.0) {
				this.considerBackwardLinks = true;
				break;
			}
		}

		for (Integer milieu : this.paraMap.keySet()) {
			if (this.paraMap.getForwardProb(milieu.intValue()) > 0.0) {
				this.considerForwardLinks = true;
				break;
			}
		}

		for (Integer milieu : this.paraMap.keySet()) {
			if ((Double) this.paraMap.getMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_MILIEU, milieu.intValue()) > 0.0) {
				this.considerMilieus = true;
				break;
			}
		}

		for (Integer milieu : this.paraMap.keySet()) {
			if ((Double) this.paraMap.getMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_GEO, milieu.intValue()) > 0.0) {
				this.considerDistance = true;
				break;
			}
		}
	}

	/**
	 * Initialises the {@link MRuntimeDbWriter}. Runtimes are only recorded when this method was called before {@link
	 * this#buildNetwork(Collection)}.
	 *
	 * @param provider
	 */
	@Override
	public void initRuntimeDbWriter(MoreRunIdProvider provider) {
		this.runtimeWriter = new MRuntimeMemoryDbWriter(provider);
	}

	/**
	 * The returned network is always directed!
	 *
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MoreNetwork<AgentType, EdgeType> buildNetwork(Collection<AgentType> agents) {

		MoreNetwork<AgentType, EdgeType> network = new MDirectedNetwork<AgentType, EdgeType>(
				this.edgeModifier.getEdgeFactory(), name);

		if (this.runtimeWriter != null) {
			this.runtimeWriter.start();
		}

		checkAgentCollection(agents);

		checkParameter();

		Map<AgentType, Integer> degreeTargets = null;

		this.partnerFinder = new MMilieuPartnerFinder<AgentType, EdgeType>(this.paraMap, this.pm);

		// an additional agent collection is required since all alternative collections need
		// to be persistent till the last link is established...
		ArrayList<AgentType> orderedAgents = null;

		if (this.runtimeWriter != null) {
			this.runtimeWriter.addMeasurement("Instanciations");
		}

		this.hexagonInitialiser.init(this.pm, this.geography);

		if (this.runtimeWriter != null) {
			this.runtimeWriter.addMeasurement("Init hexagons");
		}

		this.hexagonInitialiser.initDistanceMatrix(agentHexagons, this.geography);

		if (this.runtimeWriter != null) {
			this.runtimeWriter.addMeasurement("Init distance matrix");
		}

		initDegreeDistributions();

		initDistanceDistributions();

		this.initialised = true;

		degreeTargets = initDegreeTargets(agents);

		if (this.runtimeWriter != null) {
			this.runtimeWriter.addMeasurement("Init distributions");
		}

		orderedAgents = createRandomAgentList(agents, degreeTargets);

		for (AgentType agent : agents) {
			network.addNode(agent);
		}

		if (this.runtimeWriter != null) {
			this.runtimeWriter.addMeasurement("Add agents");
		}

		int turn = 0;
		LinkedHashSet<AgentType> agentsToGo = new LinkedHashSet<AgentType>(orderedAgents);
		while (agentsToGo.size() > 0) {
			turn++;

			// <- LOGGING
			logger.info("Enter turn " + turn + " (agents in orderedAgents: " + orderedAgents.size()
					+ " - some probably had degree target = 0)");
			// LOGGING ->

			int counter = 0;
			for (AgentType agent : agentsToGo) {
				// <- LOGGING
				counter++;
				if ((counter) % Math.ceil((agentsToGo.size() / 100.0)) == 0) {
					logger.info(this + "> Connect (" + Math.round((double) counter / agentsToGo.size() * 100.0)
							+ "%...");
				}
				// LOGGING ->
				
				// <- LOGGING
				logger.debug("Connect agent " + agent);
				// LOGGING ->

				if (agent instanceof MAbstractAnalyseNetworkAgent) {
					((MAbstractAnalyseNetworkAgent<AgentType, EdgeType>) agent).addAmbassador();
				}

				AgentType ambassador = null;
				// Select a distance range probabilistically according to distance function

				while (ambassador == null) {
					// Determine according hexagons and agent within
					Set<AgentType> potPartners = new LinkedHashSet<AgentType>();

					MoreGeoHexagon<AgentType> h = agentHexagons.get(agent);
					if (h == null) {
						logger.error("Agent " + agent + "(" + this.geography.getGeometry(agent) + ")" + " is not assigned to a hexagon.");
						
						if (this.geography.getGeometry(agent) == null) {
							logger.error("Check that agents are part of geography!");							
						} else {
							logger.error("Check that hexagon shapefile covers all agent positions!");
						}
						
						throw new IllegalStateException("Agent " + agent + "(" + this.geography.getGeometry(agent) + ")"+ " is not assigned to a hexagon. " +
										"Check that agents are part of geography and hexagon shapefile covers all agent positions!");
					}
		
					for (MoreGeoHexagon<AgentType> hexagon : h.getHexagonsOfDistance(agent, 
							this.distanceDistributions.get(new Integer(agent.getMilieuGroup())))) {
						potPartners.addAll(hexagon.getAgents());
					}

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Number of potential partners: " + potPartners.size());
					}
					// LOGGING ->
					
					if (potPartners.size() == agents.size()) {
						logger.warn("All agents returned as potential partners. It is likely there is an inconsistancy between CRS and distance distribution.");
					}

					// Select a random ambassador according to milieu preferences (inbreeding homophily) from these
					// hexagons
					ambassador = partnerFinder.findPartner(potPartners, network.getJungGraph(), agent, true);
				}
				linkPartner(agent, ambassador, network, degreeTargets);

				// Follow links of ambassador with respect to forward probability, distance term, milieu preference
				// (baseline homophily)
				if (degreeTargets.get(agent) > 0) {
					LinkedList<AgentType> toExplore = new LinkedList<AgentType>();
					toExplore.add(ambassador);
					explorePartner(agent, toExplore, network, degreeTargets);
				}
				if (degreeTargets.get(agent).intValue() <= 0) {
					assert degreeTargets.get(agent).intValue() == 0;
					orderedAgents.remove(agent);
				}
			}

			// shuffle agents if required:
			if (turn % ((Integer) pm.getParam(MNetBuildHdffPa.AGENT_SHUFFLE_INTERVAL)).intValue() == 0) {
				Collections.shuffle(orderedAgents, new Random(((Integer) pm.getParam(
						MRandomPa.RANDOM_SEED_NETWORK_BUILDING)).intValue()));

				logger.debug("Shuffle order: " + agents);
			}
			agentsToGo.clear();
			agentsToGo.addAll(orderedAgents);
		}

		if (this.runtimeWriter != null) {
			this.runtimeWriter.addMeasurement("Build up links");
			this.runtimeWriter.stopAndStore();
		}

		return network;
	}

	/**
	 * @see http://mathworld.wolfram.com/RandomNumber.html
	 */
	protected double getDistance(int milieu) {
		return this.distanceDistributions.get(new Integer(milieu)).sample() *
				this.distanceFactorForDistribution;
	}

	/**
	 * Calculates the area diameter from hexagon shapefiles.
	 *
	 * @return diameter
	 */
	protected double getAreaDiameter() {
		if (this.areaDiameter < 0.0) {
			Envelope envelope = new Envelope();
			
			for (Object o : this.geography.getLayer(this.hexagonInitialiser.getHexagonType()).getAgentSet()) {
				envelope.expandToInclude(this.geography.getGeometry(o).getEnvelopeInternal());
			}
			this.areaDiameter = Math.min(
					Math.sqrt(Math.pow(envelope.getWidth(), 2.0) + Math.pow(envelope.getHeight(), 2.0)),
					((Double) pm.getParam(MNetBuildHdffPa.MAX_SEARCH_RADIUS)).doubleValue());

			// <- LOGGING
			logger.info("Area diameter is " + this.areaDiameter);
			// LOGGING ->
		}

		return this.areaDiameter;
	}

	/**
	 * @param agents
	 * @return
	 */
	protected Map<AgentType, Integer> initDegreeTargets(Collection<AgentType> agents) {
		Map<AgentType, Integer> degreeTargets = new HashMap<AgentType, Integer>();
		for (AgentType agent : agents) {

			if (agent.getMilieuGroup() >= paraMap.size() + (Integer) pm.getParam(MBasicPa.MILIEU_START_ID)) {
				logger.error("There is no parameterisation for milieu " +
						agent.getMilieuGroup() + "!");
				throw new IllegalStateException("There is no parameterisation for milieu " +
						agent.getMilieuGroup() + "!");
			}
			degreeTargets.put(agent,
					Math.min(new Integer(Math.round(this.degreeDistributions.get(new Integer(agent.getMilieuGroup()))
							.sample())), agents.size() - 1));
		}
		return degreeTargets;
	}

	/**
	 *
	 * Assumes that the distance distribution's density is highest at supported lower bound (p_local / x_min).
	 *
	 * @param agent
	 *        agent/ambassador
	 * @param partner
	 * @param network
	 * @param orderedAgents
	 * @param degreeTargets
	 */
	protected void explorePartner(AgentType agent, LinkedList<AgentType> toExplore,
			MoreNetwork<AgentType, EdgeType> network, Map<AgentType, Integer> degreeTargets) {

		AgentType partner = null;
		if (!toExplore.isEmpty()) {
			partner = toExplore.remove();
		} else {
			return;
		}

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(agent + "> Explore partner " + partner + " (remaining degreeTarget: "
					+ degreeTargets.get(agent).intValue() + ")");
		}
		// LOGGING ->

		MRealDistribution agentDistanceDist = this.distanceDistributions.get(agent.getMilieuGroup());

		Uniform uniform = (Uniform) MManager.getURandomService().getDistribution(
				(String) pm.getParam(MRandomPa.RND_UNIFORM_DIST_NETWORK_BUILDING));

		int indegree = 0;
		ArrayList<AgentType> neighbours = new ArrayList<AgentType>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		int counter = 0;

		if (this.considerForwardLinks) {
			indegree = network.getInDegree(partner);
			for (AgentType p : network.getPredecessors(partner)) {
				neighbours.add(p);
				indices.add(new Integer(counter++));
			}
		}

		assert counter == indegree;

		if (this.considerBackwardLinks) {
			for (AgentType s : network.getSuccessors(partner)) {
				neighbours.add(s);
				indices.add(new Integer(counter++));
			}
		}

		Collections.shuffle(indices, new Random(((Integer) pm.getParam(
				MRandomPa.RANDOM_SEED_NETWORK_BUILDING)).intValue()));

		while (degreeTargets.get(agent) > 0 && indices.size() > 0) {
			int random = uniform.nextIntFromTo(0, indices.size() - 1);
			int index = indices.get(random);
			indices.remove(random);

			AgentType neighbour = neighbours.get(index);

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(agent + ">> Check neighbour (" + neighbour + ") of partner (" + partner + ")");
				logger.debug(agent + "> Is not successor? " + !network.isSuccessor(agent, neighbour));
			}
			// LOGGING ->

			if (agent != neighbour && (!network.isSuccessor(agent, neighbour))) {

				Double distanceProb = getDistanceProb(agent, neighbour) /
						agentDistanceDist.density(agentDistanceDist.getSupportLowerBound())
						* (Double) this.paraMap.getMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_GEO, agent.getMilieuGroup());

				Double milieuProb = this.paraMap.getP_Milieu(agent.getMilieuGroup(),
						neighbour.getMilieuGroup())
						* (Double) this.paraMap.getMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_MILIEU,
								agent.getMilieuGroup());

				Double probability = 0.0;
				if (index < indegree) {
					// its a forward link...
					probability = (distanceProb + milieuProb)
							* this.paraMap.getForwardProb(agent.getMilieuGroup());

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Probability for forward linking " + agent + " with " + neighbour + ": "
								+ probability
								+ "\n\t(distance: " + distanceProb + ")"
								+ "\n\t(milieu: " + milieuProb + ")");
					}
					// LOGGING ->

				} else {
					// it's a backward link...
					probability = (distanceProb + milieuProb)
							* this.paraMap.getBackwardProb(agent.getMilieuGroup());

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Probability for backward linking " + agent + " with " + neighbour + ": "
								+ probability
								+ "\n\t(distance: " + distanceProb + ")"
								+ "\n\t(milieu: " + milieuProb + ")");
					}
					// LOGGING ->
				}

				if (probability >= rand.nextDouble()) {
					toExplore.add(neighbour);
					linkPartner(agent, neighbour, network, degreeTargets);
				}
			}
		}
		if (degreeTargets.get(agent) > 0) {
			explorePartner(agent, toExplore, network, degreeTargets);
		}
	}

	/**
	 * @param agent
	 * @param partner
	 * @param network
	 * @param orderedAgents
	 * @param degreeTargets
	 */
	protected void linkPartner(AgentType agent, AgentType partner, MoreNetwork<AgentType, EdgeType> network,
			Map<AgentType, Integer> degreeTargets) {
		this.edgeModifier.createEdge(network, partner, agent);
		degreeTargets.put(agent, new Integer(degreeTargets.get(agent) - 1));

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(agent + ">Linked! Indegree: " + network.getInDegree(agent) + " | remaining target: " +
					degreeTargets.get(agent).intValue());
		}
		// LOGGING ->
	}

	/**
	 * @param ego
	 * @param partner
	 * @return
	 */
	protected double getDistanceProb(AgentType ego, AgentType partner) {
		return this.distanceDistributions.get(ego.getMilieuGroup()).
				density(this.geography.getGeometry(ego).distance(geography.getGeometry(partner)) /
						this.distanceFactorForDistribution);
	}

	/**
	 *
	 */
	private void initDegreeDistributions() {
		this.degreeDistributions = new HashMap<Integer, MIntegerDistribution>();
		for (int i = (Integer)pm.getParam(MBasicPa.MILIEU_START_ID); 
				i < paraMap.size() + (Integer)pm.getParam(MBasicPa.MILIEU_START_ID); i++) {

			MIntegerDistribution dist = null;

			try {
				dist = (MIntegerDistribution) Class.forName(paraMap.getKDistributionClass(i)).
						getConstructor(RandomGenerator.class).newInstance(
								new MRandomEngineGenerator(MManager.getURandomService().getGenerator(
										(String) pm.getParam(MRandomPa.RND_STREAM_NETWORK_BUILDING))));

				dist.setParameter(MGeneralDistributionParameter.PARAM_A, ((Double) paraMap.
						getMilieuParam(MNetBuildHdffPa.K_PARAM_A, i)).doubleValue());
				dist.setParameter(MGeneralDistributionParameter.PARAM_B, ((Double) paraMap.
						getMilieuParam(MNetBuildHdffPa.K_PARAM_B, i)).doubleValue());
				dist.init();

			} catch (NoSuchMethodException exception) {
				exception.printStackTrace();

				// <- LOGGING
				logger.warn("The distribution " + paraMap.getKDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->

			} catch (IllegalArgumentException exception) {
				exception.printStackTrace();

				// <- LOGGING
				logger.warn("The distribution " + paraMap.getKDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->

			} catch (SecurityException exception) {
				exception.printStackTrace();

				// <- LOGGING
				logger.warn("The distribution " + paraMap.getKDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->

			} catch (InstantiationException exception) {
				exception.printStackTrace();

				// <- LOGGING
				logger.warn("The distribution " + paraMap.getKDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->

			} catch (IllegalAccessException exception) {
				exception.printStackTrace();

				// <- LOGGING
				logger.warn("The distribution " + paraMap.getKDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->

			} catch (InvocationTargetException exception) {
				exception.printStackTrace();

				// <- LOGGING
				logger.warn("The distribution " + paraMap.getKDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->

			} catch (ClassNotFoundException exception) {
				exception.printStackTrace();

				// <- LOGGING
				logger.warn("The distribution " + paraMap.getKDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->

			}
			this.degreeDistributions.put(new Integer(i), dist);
		}
	}

	private void initDistanceDistributions() {
		this.distanceDistributions = new HashMap<Integer, MRealDistribution>();
		
		for (int i = (Integer) pm.getParam(MBasicPa.MILIEU_START_ID);
				i < paraMap.size() + (Integer)pm.getParam(MBasicPa.MILIEU_START_ID); i++) {
			MRealDistribution dist = null;

			try {
				dist = (MRealDistribution) Class.forName(paraMap.getDistDistributionClass(i)).
						getConstructor(RandomGenerator.class).newInstance(
								new MRandomEngineGenerator(MManager.getURandomService().getGenerator(
										(String) pm.getParam(MRandomPa.RND_STREAM_NETWORK_BUILDING))));
				dist.setParameter(MGeneralDistributionParameter.PARAM_A, paraMap.getDistParamA(i));
				dist.setParameter(MGeneralDistributionParameter.PARAM_B, paraMap.getDistParamB(i));
				dist.setParameter(MGeneralDistributionParameter.PARAM_C, paraMap.getDistParamXMin(i));
				dist.setParameter(MGeneralDistributionParameter.PARAM_D, this.getAreaDiameter()
						/ this.distanceFactorForDistribution);
				dist.setParameter(MGeneralDistributionParameter.PARAM_E, paraMap.getDistParamPLocal(i));
				dist.init();

				this.distanceDistributions.put(new Integer(i), dist);
			} catch (IllegalArgumentException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			} catch (SecurityException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			} catch (InstantiationException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			} catch (IllegalAccessException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			} catch (InvocationTargetException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			} catch (NoSuchMethodException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			} catch (ClassNotFoundException exception) {
				exception.printStackTrace();
				// <- LOGGING
				logger.warn("The distribution " + paraMap.getDistDistributionClass(i) + " for milieu " + i +
						" could not be initialised!");
				// LOGGING ->
			}
		}
	}

	/**
	 * TODO test
	 */
	protected void adjustProbabilityWeights() {
		if (!considerDistance) {
			if (considerMilieus) {
				for (Integer milieu : this.paraMap.keySet()) {
					if ((Double) this.paraMap.getMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_MILIEU, milieu) != 1.0) {
						this.paraMap.setMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_MILIEU, milieu, 1.0);
						logger.warn("DimWeightMilieu adjusted to 1.0 for milieu " + milieu);
					}
					if ((Double) this.paraMap.getMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_GEO, milieu) != 0.0) {
						this.paraMap.setMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_GEO, milieu, 0.0);
						logger.warn("DimWeightGeo adjusted to 0.0 for milieu " + milieu);
					}
				}
			}
		} else {
			if (!considerMilieus) {
				for (Integer milieu : this.paraMap.keySet()) {
					if ((Double) this.paraMap.getMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_GEO, milieu) != 1.0) {
						this.paraMap.setMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_GEO, milieu, 1.0);
						logger.warn("DimWeightGeo adjusted to 1.0 for milieu " + milieu);
					}
					if ((Double) this.paraMap.getMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_MILIEU, milieu) != 0.0) {
						this.paraMap.setMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_MILIEU, milieu, 0.0);
						logger.warn("DimWeightMilieu adjusted to 0.0 for milieu " + milieu);
					}
				}
			} else {
				// consider both
				for (Integer milieu : this.paraMap.keySet()) {
					if (Math.abs(((Double) this.paraMap.getMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_GEO, milieu) +
							(Double) this.paraMap.getMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_MILIEU, milieu)) - 1.0) > TOLERANCE_VALUE_DIM_WEIGHTS) {
						logger.error("DimWeightGeo and DimWeightMilieu do not sum up to 1.0 for milieu " +
								milieu);
					}
				}
			}
		}
	}

	/**
	 * Checks...
	 * <ul>
	 * <li>...context</li>
	 * <li>...random distribution (rand)</li>
	 * <li>...whether MNetworkBuildingPa.MILIEU_NETWORK_PARAMS has been initialised.</li>
	 * </ul>
	 */
	@SuppressWarnings("unchecked") // class
	protected void checkParameter() {
		AbstractDistribution abstractDis = MManager
				.getURandomService()
				.getDistribution(
						(String) pm.getParam(MRandomPa.RND_UNIFORM_DIST_NETWORK_BUILDING));

		if (abstractDis instanceof Uniform) {
			this.rand = (Uniform) abstractDis;
		} else {
			this.rand = MManager.getURandomService().getUniform();
			logger.warn("Use default uniform distribution");
		}

		try {
			this.hexagonInitialiser = (MoreGeoHexagonInitialiser<AgentType>) Class.forName((String) pm.getParam(
					MNetBuildHdffPa.HEXAGON_INITIALISER_CLASS)).newInstance();
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();
		} catch (InstantiationException exception) {
			exception.printStackTrace();
		} catch (IllegalAccessException exception) {
			exception.printStackTrace();
		}

		this.distanceFactorForDistribution = ((Double) pm.getParam(MNetBuildHdffPa.DISTANCE_FACTOR_FOR_DISTRIBUTION)).doubleValue();
		assignMilieuParamMap();
		adjustProbabilityWeights();

		if (this.geography == null) {
			logger.error("Geography is null. Call setGeography()!");
			throw new IllegalStateException("Geography is null. Call setGeography()!");
		}
	}

	/**
	 *
	 */
	protected void assignMilieuParamMap() {
		if (((MMilieuNetworkParameterMap) pm
				.getParam(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS)) == null) {
			new MMilieuNetDataReader().initParameters();

			// <- LOGGING
			logger.warn("Parameter MNetworkBuildingPa.MILIEU_NETWORK_PARAMS has not been set! (Re-)Initialise it.");
			// LOGGING ->
		}

		this.paraMap = (MMilieuNetworkParameterMap) pm
				.getParam(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);
	}

	/**
	 * Skips agents with non-positive degree target and those whose link preferences sum to 0.0
	 * 
	 * @param agents
	 */
	// checked before casting
	protected ArrayList<AgentType> createRandomAgentList(Collection<AgentType> agents,
			Map<AgentType, Integer> degreeTargets) {
		// <- LOGGING
		logger.info("Create random agent list from " + agents.size() + " agents ...");
		// LOGGING ->

		ArrayList<AgentType> orderedAgents = new ArrayList<AgentType>();

		for (AgentType a : agents) {
			if ((degreeTargets.get(a) > 0)
					&& !this.partnerFinder.getNoPartnerMilieus().contains(new Integer(a.getMilieuGroup()))) {
				orderedAgents.add(a);
			}
		}

		Collections.shuffle(orderedAgents, new Random(((Integer) pm.getParam(
				MRandomPa.RANDOM_SEED_NETWORK_BUILDING)).intValue()));

		logger.debug("Shuffle order: " + orderedAgents);

		return orderedAgents;
	}

	/**
	 * @see de.cesr.more.building.network.MNetworkService#removeNode(de.cesr.more.basic.network.MoreNetwork,
	 *      java.lang.Object)
	 */
	@Override
	public boolean removeNode(MoreNetwork<AgentType, EdgeType> network, AgentType node) {
		checkInitialisation();
		super.removeNode(network, node);
		this.agentHexagons.get(node).removeAgent(node);
		return this.agentHexagons.remove(node) != null;
	}

	/**
	 * Assumes that the agent to add is already within the geography!
	 *
	 * @see de.cesr.more.manipulate.network.MoreNetworkModifier#addAndLinkNode(de.cesr.more.basic.network.MoreNetwork,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network, AgentType node) {
		checkInitialisation();

		int degreetarget = Math.round(this.degreeDistributions.get(new Integer(node.getMilieuGroup())).sample());
		network.addNode(node);

		Geometry nodeGeom = this.geography.getGeometry(node);
		if (nodeGeom == null) {
			logger.error("Node " + node + " has not been added to geography " + this.geography);
			throw new IllegalStateException("Node " + node + " has not been added to geography " + this.geography);
		}

		// determine surrounding hexagon:
		MGeoHexagon<AgentType> hexagon = null;
		WithinQuery<Object> containsQuery = new WithinQuery<Object>(
				this.geography, nodeGeom);
		for (Object o : containsQuery.query()) {
			if (o instanceof MGeoHexagon) {
				hexagon = (MGeoHexagon<AgentType>) o;
			}
		}

		if (hexagon != null) {
			hexagon.addAgent(node);
			this.agentHexagons.put(node, hexagon);

			while (degreetarget > 0) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Connect agent " + node);
				}
				// LOGGING ->

				AgentType ambassador = null;
				// Select a distance range probabilistically according to distance function

				while (ambassador == null) {
					// Determine according hexagons and agent within
					Set<AgentType> potPartners = new LinkedHashSet<AgentType>();

					for (MoreGeoHexagon<AgentType> h : hexagon.getHexagonsOfDistance(node, 
							this.distanceDistributions.get(new Integer(node.getMilieuGroup())))) {
						potPartners.addAll(h.getAgents());
					}

					// Select a random ambassador according to milieu preferences (inbreeding homophily) from these
					// hexagons
					ambassador = partnerFinder.findPartner(potPartners, network.getJungGraph(), node, true);
				}

				// Follow links of ambassador with respect to forward probability, distance term, milieu preference
				// (baseline homophily)
				Map<AgentType, Integer> degreeTargets = new HashMap<AgentType, Integer>();
				degreeTargets.put(node, new Integer(degreetarget));

				linkPartner(node, ambassador, network, degreeTargets);

				LinkedList<AgentType> toExplore = new LinkedList<AgentType>();
				toExplore.add(ambassador);
				explorePartner(node, toExplore, network, degreeTargets);
				degreetarget = degreeTargets.get(node);
			}
		} else {
			logger.error("Agent " + node + " is not surrounded by a hexagon");
		}

		return true;
	}

	/**
	 * 
	 */
	protected void checkInitialisation() {
		if (!this.initialised) {

			checkParameter();

			this.partnerFinder = new MMilieuPartnerFinder<AgentType, EdgeType>(this.paraMap);

			this.hexagonInitialiser.init(this.pm, this.geography);

			this.hexagonInitialiser.initDistanceMatrix(agentHexagons, this.geography);

			initDegreeDistributions();

			initDistanceDistributions();
			this.initialised = true;
		}
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MGeoHomophily Distance ForestFire Network Service";
	}
}
