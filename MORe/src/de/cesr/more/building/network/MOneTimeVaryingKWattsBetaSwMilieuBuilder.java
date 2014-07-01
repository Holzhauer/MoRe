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
 * Created by sholzhau on 7 Jun 2014
 */
package de.cesr.more.building.network;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import de.cesr.more.basic.MManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.util.MoreKValueProvider;
import de.cesr.more.building.util.MSmallWorldBetaModelNetworkGenerator.MSmallWorldBetaModelNetworkGeneratorParams;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.param.reader.MMilieuNetDataReader;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.util.distributions.MGeneralDistributionParameter;
import de.cesr.more.util.distributions.MIntegerDistribution;
import de.cesr.more.util.distributions.MRandomEngineGenerator;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * Version of {@link MOneTimeWattsBetaSwMilieuBuilder} that enables random distribution based degrees.
 *
 * @formatter:off
 *
 * <table>
 * <th>Parameter</th><th>Value</th>
 * <tr><td>#Vertices</td><td>N (via collection of agents)</td></tr>
 * <tr><td>Degrees</td><td>randomly distributed via MILIEU_K_DISTRIBUTION_CLASS)</td></tr>
 * <th>Property</th><th>Value</th>
 * <tr><td>#Edges:</td><td>Directed: see degrees</td></tr>
 * <tr><td>Parameter provider</td><td>MSmallWorldBetaModelNetworkGeneratorParams</td></tr>
 * </table>
 * See {@link MSmallWorldBetaModelNetworkGeneratorParams} for further parameters!
 * <br>
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * <li>{@link MNetworkBuildingPa.MILIEU_K_DISTRIBUTION_CLASS} (milieu-specific)</li>
 * <li>{@link MNetworkBuildingPa.MILIEU_K_PARAM_A} (milieu-specific)</li>
 * <li>{@link MNetworkBuildingPa.MILIEU_K_PARAM_B} (milieu-specific)</li>
 * <li>{@link MNetworkBuildingPa.BUILD_WSSM_BETA} (milieu-specific)</li>
 * </ul>
 *
 * @author Sascha Holzhauer
 * @date 7 Jun 2014
 *
 */
public class MOneTimeVaryingKWattsBetaSwMilieuBuilder<AgentType extends MoreMilieuAgent, EdgeType extends MoreEdge<AgentType>>
	extends MOneTimeWattsBetaSwMilieuBuilder<AgentType, EdgeType> {

	static private Logger logger = Logger.getLogger(MOneTimeVaryingKWattsBetaSwBuilder.class);

	protected MMilieuNetworkParameterMap				paraMap;

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public MOneTimeVaryingKWattsBetaSwMilieuBuilder() {
		this((MoreEdgeFactory<AgentType, EdgeType>) new MDefaultEdgeFactory<AgentType>());
	}

	public MOneTimeVaryingKWattsBetaSwMilieuBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		this(eFac, "Network");
	}

	/**
	 * @param eFac
	 */
	public MOneTimeVaryingKWattsBetaSwMilieuBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		this(eFac, name, PmParameterManager.getInstance(null));
	}


	/**
	 * @param eFac
	 */
	public MOneTimeVaryingKWattsBetaSwMilieuBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name, final PmParameterManager pm) {
		super(eFac);
		this.name = name;
		this.pm = pm;

		params =
				new MSmallWorldBetaModelNetworkGeneratorParams<AgentType, EdgeType>(this.pm);

		assignMilieuParamMap();

		params.setkProvider(new MoreKValueProvider<AgentType>() {

			protected Map<Integer, MIntegerDistribution> degreeDistributions;

			{
				this.degreeDistributions = new HashMap<Integer, MIntegerDistribution>();

				int firstMilieuId = ((Integer) pm.getParam(MBasicPa.MILIEU_START_ID)).intValue();
				for (int i = firstMilieuId; i < paraMap.size() + firstMilieuId; i++) {

					logger.info("Init degree distribution for milieu " + i);

					MIntegerDistribution dist = null;

					if ((Double) paraMap.getMilieuParam(MNetworkBuildingPa.MILIEU_K_PARAM_A, i) < (Double) paraMap.getMilieuParam(MNetworkBuildingPa.MILIEU_K_PARAM_B, i)) {
						try {
							dist = (MIntegerDistribution) Class.forName((String) paraMap.getMilieuParam(MNetworkBuildingPa.MILIEU_K_DISTRIBUTION_CLASS, i)).
									getConstructor(RandomGenerator.class).newInstance(
											new MRandomEngineGenerator(MManager.getURandomService().getGenerator(
													(String) MOneTimeVaryingKWattsBetaSwMilieuBuilder.this.pm.getParam(MRandomPa.RND_STREAM_NETWORK_BUILDING))));

							dist.setParameter(MGeneralDistributionParameter.PARAM_A, (Double) paraMap.getMilieuParam(MNetworkBuildingPa.MILIEU_K_PARAM_A, i));
							dist.setParameter(MGeneralDistributionParameter.PARAM_B, (Double) paraMap.getMilieuParam(MNetworkBuildingPa.MILIEU_K_PARAM_B, i));
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
					}
						this.degreeDistributions.put(new Integer(i), dist);
				}
			}

			@Override
			public int getKValue(AgentType node) {
				if (this.degreeDistributions.get(new Integer(node.getMilieuGroup())) != null) {
					return this.degreeDistributions.get(new Integer(node.getMilieuGroup())).sample();
				} else {
					return ((Double) paraMap.getMilieuParam(MNetworkBuildingPa.MILIEU_K_PARAM_A, node.getMilieuGroup())).intValue();
				}
			}
		});
		params.setEdgeModifier(getEdgeModifier());
	}

	/**
	 *
	 */
	protected void assignMilieuParamMap() {
		logger.info("Assign Milieu Parameter Map...");

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
}
