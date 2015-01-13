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
 * Created by Sascha Holzhauer on 16 Oct 2014
 */
package de.cesr.more.manipulate.agent;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;

import repast.simphony.space.gis.Geography;
import de.cesr.more.basic.MManager;
import de.cesr.more.basic.agent.MoreNetworkAgent;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildHdffPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.param.reader.MMilieuNetDataReader;
import de.cesr.more.rs.building.MGeoRsHomophilyDistanceFfNetworkService;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.geo.util.MoreGeoHexagon;
import de.cesr.more.util.distributions.MGeneralDistributionParameter;
import de.cesr.more.util.distributions.MRandomEngineGenerator;
import de.cesr.more.util.distributions.MRealDistribution;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 * 
 * Utilises a blacklist network to prevent creating links that have been discarded before. Considers defined
 * probabilities for transitivity, reciprocal, and distance distribution as defined for the HDFF network service.
 * 
 * NOTE: This class may not be instatiated before the HDFF network service is initialised (network build)!
 * 
 * @author Sascha Holzhauer
 * @param <A>
 * @date 16 Oct 2014
 * 
 */
public class MBlacklistThresholdDdLinkProcessor<A extends MoreLinkManipulatableAgent<A> & MoreMilieuAgent & MoreNetworkAgent<A, E>, 
	E extends MRepastEdge<A> & MoreEdge<A>> extends MBlacklistThresholdLinkProcessor<A, E> {

	protected MGeoRsHomophilyDistanceFfNetworkService<A, E>	hdffService;
	protected Map<Integer, MRealDistribution>			distanceDistributions;
	protected MMilieuNetworkParameterMap				paraMap;

	/**
	 * @param edgeMan
	 */
	public MBlacklistThresholdDdLinkProcessor(
			MoreNetworkEdgeModifier<A, E> edgeMan, Geography<Object> geography, 
			MGeoRsHomophilyDistanceFfNetworkService<A, E> hdffService, PmParameterManager pm) {
		super(edgeMan, geography, pm);
		this.hdffService = hdffService;
		
		if (this.hdffService.getAreaDiameter() < 0.0) {
			logger.error("This class may not be instatiated before the HDFF network service is "
					+ "initialised (network build)!");
			throw new IllegalStateException("This class may not be instatiated before the HDFF network service is "
					+ "initialised (network build)!");
		}
		assignMilieuParamMap();
		initDistanceDistributions();
	}

	/**
	 * @param edgeMan
	 * @param geography
	 */
	public MBlacklistThresholdDdLinkProcessor(MoreNetworkEdgeModifier<A, E> edgeMan, Geography<Object> geography,
			MGeoRsHomophilyDistanceFfNetworkService<A, E> hdffService) {
		this(edgeMan, geography, hdffService, PmParameterManager.getInstance(null));
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
	 * TODO
	 * 
	 * @param agent
	 * @param net
	 * @param potPartners
	 * @param localTiesPool
	 * @param localAgents
	 * @return
	 */
	protected int findDistanceDependentPartners(A agent, MoreNetwork<A, E> net, Map<Double, Set<A>> potPartners,
			int localTiesPool, ArrayList<A> localAgents) {
		
		ArrayList<A> agentsOfDistance = new ArrayList<A>();
		for (MoreGeoHexagon<A> hexagon : this.hdffService.getSurroundingHexagon(agent).getHexagonsOfDistance(agent, 
				this.distanceDistributions.get(new Integer(agent.getMilieuGroup())))) {
			agentsOfDistance.addAll(hexagon.getAgents());	
		}
		if (!locals.containsKey(agent)) {
			locals.put(agent, agentsOfDistance);
		}

		for (A local : locals.get(agent)) {
			if (local != null && (rand.nextDouble() <= netParams.getDynProbDistance(agent.getMilieuGroup()))) {
				Double value = new Double(Math.abs(agent.getValueDifference(local)));
				if (!potPartners.containsKey(value)) {
					potPartners.put(value, new HashSet<A>());
				}
				potPartners.get(value).add(local);

				if (linkLogger.isInfoEnabled()) {
					localTiesPool++;
					localAgents.add(local);
				}

				potentiallyAddGlobalLink(agent, potPartners, net);
			}
		}
		return localTiesPool;
	}

	private void initDistanceDistributions() {
		this.distanceDistributions = new HashMap<Integer, MRealDistribution>();

		for (int i = (Integer) pm.getParam(MBasicPa.MILIEU_START_ID); i < paraMap.size()
				+ (Integer) pm.getParam(MBasicPa.MILIEU_START_ID); i++) {
			MRealDistribution dist = null;

			
			try {
				dist = (MRealDistribution) Class.forName(paraMap.getDistDistributionClass(i)).
						getConstructor(RandomGenerator.class).newInstance(
								new MRandomEngineGenerator(MManager.getURandomService().getGenerator(
										(String) pm.getParam(MRandomPa.RND_STREAM_NETWORK_BUILDING))));
				dist.setParameter(MGeneralDistributionParameter.PARAM_A, paraMap.getDistParamA(i));
				dist.setParameter(MGeneralDistributionParameter.PARAM_B, paraMap.getDistParamB(i));
				dist.setParameter(MGeneralDistributionParameter.PARAM_C, paraMap.getDistParamXMin(i));
				dist.setParameter(MGeneralDistributionParameter.PARAM_D, this.hdffService.getAreaDiameter()
						/ ((Double) pm.getParam(MNetBuildHdffPa.DISTANCE_FACTOR_FOR_DISTRIBUTION)).doubleValue());
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
}
