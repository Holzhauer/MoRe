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
package de.cesr.more.rs.building;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import repast.simphony.query.space.gis.ContainsQuery;
import repast.simphony.query.space.gis.WithinQuery;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.ShapefileLoader;
import repast.simphony.space.graph.UndirectedJungNetwork;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.agent.MoreNetworkAgent;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildHdffPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.param.reader.MMilieuNetDataReader;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.geo.util.MGeoHexagon;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.more.util.distributions.MGeneralDistributionParameter;
import de.cesr.more.util.distributions.MIntegerDistribution;
import de.cesr.more.util.distributions.MRandomEngineGenerator;
import de.cesr.more.util.distributions.MRealDistribution;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 * 
 * This network builder considers baseline homophily [1] and distance distributions [2]. The network is build as
 * follows:
 * 
 * <ol>
 * <li>Read hexagon shapefile, init {@link MGeoHexagon}s, and add them to the geography and the root context.</li>
 * <li>Assign agents to hexagons and determine distances between hexagons</li>
 * <li>Init degree distributions for milieu groups and degree target for each agent</li>
 * <li>Shuffle agents</li>
 * <li>Do as long as agent degree targets are not fulfilled:
 * <ol>
 * <li>For every agents with degree less than degree target
 * <ol>
 * <li>Draw a distance from milieu-specific distance distribution and randomly choose an agent within that distance
 * according to milieu preferences (inbreeding homophily) using {@link MMilieuPartnerFinder}</li>
 * <li>Link the agent to that ambassador, update degree target and explore its neighbours:
 * <ol>
 * <li>Check a neighbour as backward link considering distance, partner milieu, and backward probability</li>
 * <li>Create edge and explore links recursively if check is positive</li>
 * <li>Check a neighbour as forward link considering distance, partner milieu, and forward probability</li>
 * <li>Create edge and explore links recursively if check is positive</li>
 * </ol>
 * </li>
 * <li>
 * </li>
 * </ol>
 * </li>
 * <li>Shuffle agents with respect to <code>MNetBuildHdffPa.AGENT_SHUFFLE_INTERVAL</code>.</li>
 * </ol>
 * </li>
 * </ol>
 * See <a href="../networkGeneration.html">MoRe Network Building</a> for a guide to create hexagon shapefiles.
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
 * <li>{@link MNetBuildHdffPa#HEXAGON_SHAPEFILE}</li>
 * <li>{@link MNetworkBuildingPa#BUILD_DIRECTED}</li>
 * <li>{@link MNetBuildHdffPa#K_DISTRIBUTION_CLASS} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#K_PARAM_A} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#K_PARAM_B} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#DISTANCE_PROBABILITY_EXPONENT} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#MAX_SEARCH_RADIUS} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#P_MILIEUS} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#PROB_FORWARD} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#PROB_BACKWARD} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#DIM_WEIGHTS_GEO} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#DIM_WEIGHTS_MILIEU} (via {@link MNetworkBuildingPa#MILIEU_NETWORK_PARAMS})</li>
 * <li>{@link MNetBuildHdffPa#AGENT_SHUFFLE_INTERVAL}</li>
 * </ul>
 * 
 * <br>
 * <br>
 * 
 * [1] McPherson, M.; Smith-Lovin, L. & Cook, J. Birds of a feather: Homophily in social networks Annual Review of
 * Sociology, Annual Reviews, 2001, 27, 415-444
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
public class MGeoRsHomophilyDistanceFfNetworkService<AgentType extends MoreMilieuAgent & MoreNetworkAgent<AgentType, EdgeType>, EdgeType extends MRepastEdge<AgentType> & MoreEdge<AgentType>>
		extends MGeoRsNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger								logger	= Logger.getLogger(MGeoRsHomophilyDistanceFfNetworkService.class);

	static final public int								DISTANCE_FACTOR_FOR_DISTRIBUTION	= 1000;

	protected String									name	= "Not Defined";

	protected MMilieuNetworkParameterMap				paraMap;

	protected MMilieuPartnerFinder<AgentType, EdgeType>	partnerFinder;

	protected Uniform									rand;

	protected Map<Integer, MIntegerDistribution>		degreeDistributions;

	protected Map<Integer, MRealDistribution>			distanceDistributions;

	protected double									distanceStep;

	protected double									areaDiameter	= -1.0;

	// these Bools are used for efficiency reasons:
	protected boolean									considerForwardLinks	= false;
	protected boolean									considerMilieus			= false;
	protected boolean									considerDistance		= false;

	@SuppressWarnings("unchecked")
	// geography needs to be parameterised with Object
	public MGeoRsHomophilyDistanceFfNetworkService(MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		this((Geography<Object>) PmParameterManager.getParameter(MBasicPa.ROOT_GEOGRAPHY), edgeFac, name);
	}

	/**
	 * - builder constructor - edge modifier - builder set - parma
	 * 
	 * @param areasGeography
	 */
	public MGeoRsHomophilyDistanceFfNetworkService(Geography<Object> geography,
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		super(geography, edgeFac);

		this.name = name;

		this.paraMap = (MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);

		for (Integer milieu : this.paraMap.keySet()) {
			if (this.paraMap.getForwardProb(milieu.intValue()) > 0.0) {
				this.considerForwardLinks = true;
				break;
			}
		}

		for (Integer milieu : this.paraMap.keySet()) {
			if (this.paraMap.getDimWeightMilieu(milieu.intValue()) > 0.0) {
				this.considerMilieus = true;
				break;
			}
		}

		for (Integer milieu : this.paraMap.keySet()) {
			if (this.paraMap.getDimWeightGeo(milieu.intValue()) > 0.0) {
				this.considerDistance = true;
				break;
			}
		}
	}

	/**
	 * The returned network is always directed!
	 * 
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(Collection<AgentType> agents) {

		checkAgentCollection(agents);
		
		checkParameter();

		Map<AgentType, MGeoHexagon<AgentType>> agentHexagons = new HashMap<AgentType, MGeoHexagon<AgentType>>();

		MoreRsNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType>(
						new UndirectedJungNetwork<AgentType>(name), context, this.edgeModifier.getEdgeFactory());

		Map<AgentType, Integer> degreeTargets;

		this.partnerFinder = new MMilieuPartnerFinder<AgentType, EdgeType>(this.paraMap);
		
		// an additional agent collection is required since all alternative collections need
		// to be persistent till the last link is established...
		ArrayList<AgentType> orderedAgents = null;

		initHexagons();
		
		initDistanceMatrix(agents, agentHexagons);

		initDegreeDistributions();

		initDistanceDistributions();

		degreeTargets = initDegreeTargets(agents);

		orderedAgents = createRandomAgentList(agents);
		
		for (AgentType agent : orderedAgents) {
			network.addNode(agent);
		}

		int turn = 0;
		LinkedHashSet<AgentType> agentsToGo = new LinkedHashSet<AgentType>(orderedAgents);
		while (agentsToGo.size() > 0) {
			turn++;

			// <- LOGGING
			logger.info("Enter turn " + turn);
			// LOGGING ->

			for (AgentType agent : agentsToGo) {
				// <- LOGGING
				logger.info("Connect agent " + agent);
				// LOGGING ->

				AgentType ambassador = null;
				// Select a distance range probabilistically according to distance function

				while (ambassador == null) {
					double startDistance = getDistance(agent.getMilieuGroup());

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Start-Distance: " + startDistance);
					}
					// LOGGING ->

					// Determine according hexagons and agent within
					Set<AgentType> potPartners = new HashSet<AgentType>();

					MGeoHexagon<AgentType> h = agentHexagons.get(agent);
					if (h == null) {
						logger.error("Agent " + agent + " is not assigned to a hexagon. " +
								"Check that hexagon shapefile covers all agent positions!");
						throw new IllegalStateException("Agent " + agent + " is not assigned to a hexagon. " +
								"Check that hexagon shapefile covers all agent positions!");
					}
					for (MGeoHexagon<AgentType> hexagon : h.getHexagonsOfDistance(startDistance)) {
						potPartners.addAll(hexagon.getAgents());
					}

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Number of potential partners: " + potPartners.size());
					}
					// LOGGING ->

					// Select a random ambassador according to milieu preferences (inbreeding homophily) from these
					// hexagons
					ambassador = partnerFinder.findPartner(potPartners, network.getJungGraph(), agent, true);
				}

				// Follow links of ambassador with respect to forward probability, distance term, milieu preference
				// (baseline homophily)
				linkAndExplorePartner(agent, ambassador, network, orderedAgents, degreeTargets);
			}

			// shuffle agents if required:
			if (turn % ((Integer) PmParameterManager.getParameter(MNetBuildHdffPa.AGENT_SHUFFLE_INTERVAL)).intValue() == 0) {
				Collections.shuffle(orderedAgents, new Random(((Integer) PmParameterManager.getParameter(
						MRandomPa.RANDOM_SEED_NETWORK_BUILDING)).intValue()));

				logger.debug("Shuffle order: " + agents);
			}
			agentsToGo.clear();
			agentsToGo.addAll(orderedAgents);
		}
		return network;
	}

	/**
	 * @see http://mathworld.wolfram.com/RandomNumber.html
	 */
	protected double getDistance(int milieu) {
		return this.distanceDistributions.get(new Integer(milieu)).sample() *
				DISTANCE_FACTOR_FOR_DISTRIBUTION;
	}

	/**
	 * @return
	 */
	protected double getAreaDiameter() {
		if (this.areaDiameter < 0.0) {
			Envelope envelope = new Envelope();
			for (Object o : this.geography.getLayer(MGeoHexagon.class).getAgentSet()) {
				envelope.expandToInclude(this.geography.getGeometry(o).getEnvelopeInternal());
			}
			this.areaDiameter = Math.min(
					Math.sqrt(Math.pow(envelope.getWidth(), 2.0) + Math.pow(envelope.getHeight(), 2.0)),
					((Double) PmParameterManager.getParameter(MNetBuildHdffPa.MAX_SEARCH_RADIUS)).doubleValue());

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
			degreeTargets.put(agent,
					new Integer(Math.round(this.degreeDistributions.get(new Integer(agent.getMilieuGroup()))
							.sample())));
		}
		return degreeTargets;
	}

	/**
	 * @param agent
	 * @param ambassador
	 */
	protected void linkAndExplorePartner(AgentType agent, AgentType partner, MoreNetwork<AgentType, EdgeType> network,
			ArrayList<AgentType> orderedAgents, Map<AgentType, Integer>	degreeTargets) {
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(agent + "> Explore partner " + partner + " (degreeTarget: "
					+ degreeTargets.get(agent).intValue() + ")");
		}
		// LOGGING ->
		if (degreeTargets.get(agent).intValue() == 0) {
			orderedAgents.remove(agent);
		} else {
			this.edgeModifier.createEdge(network, partner, agent);
			degreeTargets.put(agent, new Integer(degreeTargets.get(agent) - 1));

			// explore incoming neighbours:
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug(agent + "> Indegree: " + network.getInDegree(agent) + " | target: " +
						degreeTargets.get(agent).intValue());
			}
			// LOGGING ->

			for (AgentType neighbour : network.getPredecessors(partner)) {
				if (neighbour != agent && degreeTargets.get(agent) > 0) {
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(agent + "> Check neighbour (" + neighbour + ") of partner (" + partner
								+ ") - distProb: "
								+ getDistanceProb(agent, neighbour));

						logger.debug("Is not successor? " + !network.isSuccessor(agent, neighbour));
						logger.debug("Consider Distance? " + considerDistance);
					}
					// LOGGING ->

					if ((!network.isSuccessor(agent, neighbour))
							&& (considerDistance ? getDistanceProb(agent, neighbour)
									* this.paraMap.getDimWeightGeo(agent.getMilieuGroup()) : 1.0)
									* (considerMilieus ? this.paraMap.getP_Milieu(agent.getMilieuGroup(),
											neighbour.getMilieuGroup())
											* this.paraMap.getDimWeightMilieu(agent.getMilieuGroup()) : 1.0)
									* this.paraMap.getBackwardProb(agent.getMilieuGroup()) < rand.nextDouble()) {
						this.edgeModifier.createEdge(network, neighbour, agent);
						// For each connected partner, decrease k_target
						degreeTargets.put(agent, new Integer(degreeTargets.get(agent) - 1));
						linkAndExplorePartner(agent, neighbour, network, orderedAgents, degreeTargets);
					}
				}
			}

			// explore out-going neighbours:
			if (this.considerForwardLinks) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(agent + "> Outdegree: " + network.getOutDegree(agent));
				}
				// LOGGING ->
				for (AgentType neighbour : network.getSuccessors(partner)) {
					if ((!network.isSuccessor(neighbour, agent) && neighbour != agent && degreeTargets.get(agent) > 0)
							&& (considerDistance ? getDistanceProb(agent, neighbour)
									* this.paraMap.getDimWeightGeo(agent.getMilieuGroup()) : 1.0)
									* (considerMilieus ? this.paraMap.getP_Milieu(agent.getMilieuGroup(),
											neighbour.getMilieuGroup())
											* this.paraMap.getDimWeightGeo(agent.getMilieuGroup()) : 1.0)
									* this.paraMap.getForwardProb(agent.getMilieuGroup()) < rand.nextDouble()) {
						this.edgeModifier.createEdge(network, neighbour, agent);
						// For each connected partner, decrease k_target
						degreeTargets.put(agent, new Integer(degreeTargets.get(agent) - 1));
						linkAndExplorePartner(agent, neighbour, network, orderedAgents, degreeTargets);
					}
				}
			}
		}
	}

	/**
	 * @param ego
	 * @param partner
	 * @return
	 */
	protected double getDistanceProb(AgentType ego, AgentType partner) {
		return this.distanceDistributions.get(ego.getMilieuGroup()).
				density(this.geography.getGeometry(ego).distance(geography.getGeometry(partner)) /
						DISTANCE_FACTOR_FOR_DISTRIBUTION);
	}

	/**
	 * 
	 */
	private void initDegreeDistributions() {
		this.degreeDistributions = new HashMap<Integer, MIntegerDistribution>();
		for (int i = 1; i <= paraMap.size(); i++) {

			MIntegerDistribution dist = null;

			try {
				dist = (MIntegerDistribution) Class.forName(paraMap.getKDistributionClass(i)).
						getConstructor(RandomGenerator.class).newInstance(
								new MRandomEngineGenerator(MManager.getURandomService().getGenerator(
										(String) PmParameterManager
										.getParameter(MRandomPa.RND_STREAM_NETWORK_BUILDING))));

				dist.setParameter(MGeneralDistributionParameter.PARAM_A, paraMap.getKparamA(i));
				dist.setParameter(MGeneralDistributionParameter.PARAM_B, paraMap.getKparamB(i));
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
		for (int i = 1; i <= paraMap.size(); i++) {
			MRealDistribution dist = null;

			try {
				dist = (MRealDistribution) Class.forName(paraMap.getDistDistributionClass(i)).
						getConstructor(RandomGenerator.class).newInstance(
								new MRandomEngineGenerator(MManager.getURandomService().getGenerator(
										(String) PmParameterManager
												.getParameter(MRandomPa.RND_STREAM_NETWORK_BUILDING))));
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

			dist.setParameter(MGeneralDistributionParameter.PARAM_A, paraMap.getDistParamA(i));
			dist.setParameter(MGeneralDistributionParameter.PARAM_B, paraMap.getDistParamB(i));
			dist.setParameter(MGeneralDistributionParameter.PARAM_C, paraMap.getDistParamXMin(i));
			dist.setParameter(MGeneralDistributionParameter.PARAM_D, this.getAreaDiameter()
					/ DISTANCE_FACTOR_FOR_DISTRIBUTION);
			dist.setParameter(MGeneralDistributionParameter.PARAM_E, paraMap.getDistParamPLocal(i));
			dist.init();

			this.distanceDistributions.put(new Integer(i), dist);
		}
	}

	/**
	 * Checks... ...context ...random distribution (rand) ...whether MNetworkBuildingPa.MILIEU_NETWORK_PARAMS has been
	 * initialised
	 */
	private void checkParameter() {
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

			if (this.paraMap == null) {
				// <- LOGGING
				logger.warn("Parameter MNetworkBuildingPa.MILIEU_NETWORK_PARAMS has not been set! (Re-)Initialise it.");
				// LOGGING ->
			}
		}
	}

	/**
	 * @param agents
	 */
	// checked before casting
	protected ArrayList<AgentType> createRandomAgentList(Collection<AgentType> agents) {
		// <- LOGGING
		logger.info("Create random agent list...");
		// LOGGING ->

		ArrayList<AgentType> orderedAgents = new ArrayList<AgentType>();
		
		if (!(agents instanceof ArrayList)) {
			orderedAgents.addAll(agents);
		} else {
			orderedAgents = (ArrayList<AgentType>) agents;
		}
		
		Collections.shuffle(orderedAgents, new Random(((Integer)PmParameterManager.getParameter(
				MRandomPa.RANDOM_SEED_NETWORK_BUILDING)).intValue()));
		
		logger.debug("Shuffle order: " + agents);

		return orderedAgents;
	}

	/**
	 * @param agents
	 */
	@SuppressWarnings("unchecked")
	private void initDistanceMatrix(Collection<AgentType> agents, Map<AgentType, MGeoHexagon<AgentType>> agentHexagons) {
		// <- LOGGING
		logger.info("Init distance matrix...");
		// LOGGING ->

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			for (AgentType agent : agents) {
				logger.debug(agent + "> cetroid: " + geography.getGeometry(agent).getCentroid());
			}
		}
		// LOGGING ->

		Set<MGeoHexagon<AgentType>> hexagons = new HashSet<MGeoHexagon<AgentType>>();
		for (Object o : this.geography.getLayer(MGeoHexagon.class).getAgentSet()) {
			hexagons.add((MGeoHexagon<AgentType>) o);
		}

		// height is required to determine distance ranges
		MGeoHexagon.setHexagonHeight(geography.getGeometry(hexagons.iterator().next()).getEnvelopeInternal()
				.getHeight());

		// Use the geography's agent set because elements get removed from hexagons in the loop:
		for (Object o : this.geography.getLayer(MGeoHexagon.class).getAgentSet()) {
			MGeoHexagon<AgentType> hexagon = (MGeoHexagon<AgentType>)o;
			Geometry hexagonGeo = this.geography.getGeometry(hexagon);
			Geometry hexagonCentroid = this.geography.getGeometry(hexagon).getCentroid();

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Hexagons centroid: " + hexagonCentroid);
			}
			// LOGGING ->

			// Assign Agents to hexagons
			ContainsQuery<Object> containsQuery = new ContainsQuery<Object>(
					this.geography, hexagonGeo);
			for (Object a : containsQuery.query()) {
				// Search agents within hexagon and init map agent > hexagon
				if (a instanceof MoreMilieuAgent) {
					agentHexagons.put((AgentType) a, hexagon);
					hexagon.addAgent((AgentType) a);

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Added agent " + a + " to hexagon " + hexagon);
					}
					// LOGGING ->
				}
			}
			// For each remaining hexagon h
			for (MGeoHexagon<AgentType> h : hexagons) {
				// Determine distance between hexagon and h
				double distance = hexagonCentroid.distance(geography.getGeometry(h).getCentroid());
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Distance between " + hexagon + " and " + h + ": " + distance);
				}
				// LOGGING ->

				hexagon.setDistance(h, distance);
				h.setDistance(hexagon, distance);
			}
			hexagons.remove(hexagon);
		}
	}

	/**
	 * @see de.cesr.more.manipulate.network.MoreNetworkModifier#addAndLinkNode(de.cesr.more.basic.network.MoreNetwork,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network, AgentType node) {
		int degreetarget = Math.round(this.degreeDistributions.get(new Integer(node.getMilieuGroup()))
				.sample());
		network.addNode(node);

		// determine surrounding hexagon:
		MGeoHexagon<AgentType> hexagon = null;
		WithinQuery<Object> containsQuery = new WithinQuery<Object>(
				this.geography, this.geography.getGeometry(node));
		for (Object o : containsQuery.query()) {
			if (o instanceof MGeoHexagon) {
				hexagon = (MGeoHexagon<AgentType>) o;
			}
		}

		if (node instanceof MoreMilieuAgent) {
			hexagon.addAgent(node);
		}

		while (degreetarget > 0) {
			// <- LOGGING
			logger.info("Connect agent " + node);
			// LOGGING ->

			AgentType ambassador = null;
			// Select a distance range probabilistically according to distance function

			while (ambassador == null) {
				double startDistance = getDistance(node.getMilieuGroup());
				// Determine according hexagons and agent within
				Set<AgentType> potPartners = new HashSet<AgentType>();

				for (MGeoHexagon<AgentType> h : hexagon.getHexagonsOfDistance(startDistance)) {
					potPartners.addAll(h.getAgents());
				}

				// Select a random ambassador according to milieu preferences (inbreeding homophily) from these
				// hexagons
				ambassador = partnerFinder.findPartner(potPartners, network.getJungGraph(), node, true);
			}

			// Follow links of ambassador with respect to forward probability, distance term, milieu preference
			// (baseline homophily)
			ArrayList<AgentType> orderedAgents = new ArrayList<AgentType>();
			Map<AgentType, Integer> degreeTargets = new HashMap<AgentType, Integer>();
			orderedAgents.add(node);
			degreeTargets.put(node, new Integer(degreetarget));
			linkAndExplorePartner(node, ambassador, network, orderedAgents, degreeTargets);
		}
		return true;
	}

	/**
	 * Init hexagons from shapefile into geography.
	 */
	@SuppressWarnings("rawtypes")
	protected void initHexagons() {

		File hexagonShapeFile = new File((String) PmParameterManager.getParameter(MNetBuildHdffPa.HEXAGON_SHAPEFILE));
		// check if shapefile exists:
		if (!hexagonShapeFile.exists()) {
			logger.error("The specified shape file (" + hexagonShapeFile + ") does not exist!");
			throw new IllegalArgumentException("The specified shape file (" + hexagonShapeFile + ") does not exist!");
		}

		// <- LOGGING
		logger.info("Init contexts from " + hexagonShapeFile);
		// LOGGING ->

		ShapefileLoader<MGeoHexagon> areasLoader = null;

		try {
			areasLoader = new ShapefileLoader<MGeoHexagon>(
					MGeoHexagon.class,
					hexagonShapeFile.toURI().toURL(),
					this.geography, MManager.getRootContext());
			while (areasLoader.hasNext()) {
				areasLoader.next(new MGeoHexagon<AgentType>());
			}
		} catch (java.net.MalformedURLException e) {
			logger.error("AreasCreator: malformed URL exception when reading areas shapefile.");
			e.printStackTrace();
		}
	}
	
}
