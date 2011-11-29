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
 * Created by Sascha Holzhauer on 24.11.2011
 */
package de.cesr.more.rs.building;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import repast.simphony.space.gis.DefaultGeography;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.UTMFinder;
import repast.simphony.space.graph.DirectedJungNetwork;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildSocialAttachment;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.geo.util.MGeographyWrapper;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.more.util.Log4jLogger;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.uranus.core.URandomService;

import javax.units.SI;

/**
 * MORe
 * 
 * Links agents based on their social distance [1].
 * Calculates the link probabilities based on distance considering milieu affiliations and
 * geographical distance. These values are weighted regarding
 * milieu-specific weights defined in the milieu network params
 * map.
 * 
 * @formatter:off
 * <table>
 * <th>Parameter</th><th>Value</th>
 * <tr><td>#Vertices</td><td>N (via collection of agents)</td></tr>
 * <tr><td>Milieu-specific distance weights</td>MILIEU_NETWORK_PARAMS<td></td></tr>
 * 
 * <th>Property</th><th>Value</th>
 * <tr><td>#Edges:</td><td>N*(N-1)</td></tr>
 * <tr><td></td><td></td></tr>
 * </table>
 * <br>
 * 
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * <li>{@link MNetworkBuildingPa.MILIEU_NETWORK_PARAMS}</li>
 * <li>{@link MRandomPa.RND_STREAM_NETWORK_BUILDING}</li>
 * <li>{@link MNetBuildSocialAttachment.DIM_WEIGHT_DEVIATION_TRESHOLD}</li>
 * </ul>
 * 
 * [1] Boguna, M.; Pastor-Satorras, R.; Diaz-Guilera, A. & Arenas, A. Models of social networks
 * based on social distance attachment PHYSICAL REVIEW E, AMERICAN PHYSICAL SOC, {2004}, {70}
 * 
 * @author Sascha Holzhauer
 * @date 24.11.2011
 * 
 */
public class MGeoRsSocialDistanceAttachNetworkBuilder<AgentType extends MoreMilieuAgent & MoreDistanceAttachableAgent, EdgeType extends MRepastEdge<AgentType>>
		extends MGeoRsNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger		logger	= Log4jLogger
												.getLogger(MGeoRsSocialDistanceAttachNetworkBuilder.class);

	MGeographyWrapper<Object>	geoWrapper;
	MMilieuNetworkParameterMap	paraMap;
	Uniform						rand;

	Collection<AgentType>		agents;

	String						name;

	/**
	 * @param areGeography
	 */
	public MGeoRsSocialDistanceAttachNetworkBuilder(
			Geography<Object> geography,
			MoreEdgeFactory<AgentType, EdgeType> edgeFac, String name) {
		super(geography, edgeFac);
		this.name = name;

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Area Geography: " + geography);
		}
		// LOGGING ->
	}

	/**
	 * @formatter:on
	 * @see socnet.KMoreNetworkBuilder#buildRsNetwork(java.util.Collection, java.lang.String)
	 */
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(
			Collection<AgentType> agents) {

		this.agents = agents;

		paraMap = (MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);

		AbstractDistribution abstractDis = URandomService
				.getURandomService()
				.getDistribution(
						((String) PmParameterManager
								.getParameter(MRandomPa.RND_STREAM_NETWORK_BUILDING)));
		if (abstractDis instanceof Uniform) {
			this.rand = (Uniform) abstractDis;
		} else {
			this.rand = LModel.getModel().getLRandom().getUniform();
			logger.warn("Use default uniform distribution");
		}

		MoreRsNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType>(
				new DirectedJungNetwork<AgentType>(name), context);

		for (AgentType ego : agents) {
			network.addNode(ego);
		}

		for (AgentType ego : agents) {
			addAndLinkNode(network, ego);
		}

		return network;
	}

	/**
	 * @param ego
	 * @param agents
	 */
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network,
			AgentType ego) {
		// check weights (should sum up to 1):
		if (Math.abs(paraMap.getDimWeightGeo(ego.getMilieuGroup())
				+ paraMap.getDimWeightMilieu(ego.getMilieuGroup()) - 1.0) > 
			((Double)PmParameterManager.getParameter(MNetBuildSocialAttachment.DIM_WEIGHT_DEVIATION_TRESHOLD)).doubleValue()) {
			logger.warn("Sum of dimension weights differs from 1.0");
		}

		// agents should implement hashCode() anyway, so its ok to use a
		// HashMap....
		Map<AgentType, Double> probMap = new HashMap<AgentType, Double>();
		Map<AgentType, Double> distMap = new HashMap<AgentType, Double>();
		Map<AgentType, Double> milieuDistMap = new HashMap<AgentType, Double>();

		// determine/limit potential partners

		// compute total weighted distance to potential partners
		double totalDistance = computeTotalWeightedDistance(ego, agents,
				distMap);
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Total distance: " + totalDistance);
		}
		// LOGGING ->

		// compute total milieu distance to potential partners
		double totalMilieuDistance = computeTotalMilieuDistance(ego, agents,
				milieuDistMap);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Total milieu distance: " + totalMilieuDistance);
		}
		// LOGGING ->

		for (AgentType potPartner : agents) {
			if (potPartner != ego) {
				double normalizedProbability;

				// normalise probability:
				normalizedProbability = (distMap.get(potPartner)
						/ totalDistance
						* paraMap.getDimWeightGeo(ego.getMilieuGroup()) + milieuDistMap
						.get(potPartner)
						/ totalMilieuDistance
						* paraMap.getDimWeightGeo(ego.getMilieuGroup()))
						* paraMap.getK(ego.getMilieuGroup());
				probMap.put(potPartner, normalizedProbability);

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Geo probability for "
							+ ego
							+ "(Milieu "
							+ ego.getMilieuGroup()
							+ ") to connect with "
							+ potPartner
							+ "(Milieu "
							+ potPartner.getMilieuGroup()
							+ "): "
							+ LModel.getModel()
									.getFloatPointFormat()
									.format(distMap.get(potPartner)
											/ totalDistance
											* paraMap.getDimWeightGeo(ego
													.getMilieuGroup())));
					logger.debug("Probability for "
							+ ego
							+ "(Milieu "
							+ ego.getMilieuGroup()
							+ ") to connect with "
							+ potPartner
							+ "(Milieu "
							+ potPartner.getMilieuGroup()
							+ "): "
							+ LModel.getModel().getFloatPointFormat()
									.format(normalizedProbability));
				}
				// LOGGING ->

				// check connection:
				if (normalizedProbability >= rand.nextDoubleFromTo(0.0, 1.0)) {
					createEdge(network, ego, potPartner);
				}
			}
		}

		// Add ego to agents in case it was newly inserted in the network.
		agents.add(ego);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			double normProbSum = 0.0;
			for (Double d : probMap.values()) {
				normProbSum += d;
			}
			logger.debug("Probability sum devided by k preference: "
					+ LModel.getModel().getFloatPointFormat()
							.format(normProbSum));
		}
		// LOGGING ->
		return true;
	}

	/**
	 * If the geography is not of type UTM the methods converts the 
	 * geography to an UTM geography every time it is called (this is
	 * because otherwise changes in the original geography might not be
	 * considered). Therefore, think about using a UTM geography if possible.
	 * 
	 * @param ego
	 * @param agents
	 */
	private double computeTotalWeightedDistance(AgentType ego,
			Collection<AgentType> agents, Map<AgentType, Double> distMap) {

		double totalDistance = 0.0;
		// geography needs to be requested here in order to shuffle agents
		// before the loop!
		Geography<Object> utmGeography;
		if (!geography.getCRS().getCoordinateSystem().getAxis(0).getUnit()
				.equals(SI.METER)) {
			utmGeography = new DefaultGeography<Object>("utmGeography");
			for (Object o : geography.getAllObjects()) {
				utmGeography.move(o,
						geoFactory.createGeometry(geography.getGeometry(o)));
			}
			utmGeography.setCRS(UTMFinder.getUTMFor(geography.getGeometry(ego),
					geography.getCRS()));

		} else {
			utmGeography = geography;
		}

		for (AgentType h : agents) {

			if (h != ego) {

				double distance = utmGeography.getGeometry(ego).distance(
						utmGeography.getGeometry(h));
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Distance between " + ego + " and " + h + ": "
							+ distance);
				}
				// LOGGING ->
				distance = ego.getNetworkDistanceWeight(distance);

				if (logger.isDebugEnabled()) {
					logger.debug("Weighted distance between " + ego + " and "
							+ h + ": " + distance);
				}

				distMap.put(h, new Double(distance));

				totalDistance += distance;
			}
		}
		return totalDistance;
	}

	/**
	 * @param ego
	 * @param agents
	 */
	private double computeTotalMilieuDistance(AgentType ego,
			Collection<AgentType> agents, Map<AgentType, Double> distMap) {

		double totalDistance = 0.0;

		for (AgentType h : agents) {

			if (h != ego) {

				double distance = paraMap.getP_Milieu(ego.getMilieuGroup(),
						h.getMilieuGroup());
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Milieu distance between " + ego + " and " + h
							+ ": " + distance);
				}
				// LOGGING ->

				distMap.put(h, new Double(distance));

				totalDistance += distance;
			}
		}
		return totalDistance;
	}
}
