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
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.more.util.distributions.MGeneralDistributionParameter;
import de.cesr.more.util.distributions.MIntegerDistribution;
import de.cesr.more.util.distributions.MRandomEngineGenerator;
import de.cesr.parma.core.PmParameterManager;

/**
 * MORe
 *
 * @author sholzhau
 * @date 7 Jun 2014
 *
 */
public class MOneTimeVaryingKWattsBetaSwBuilder<AgentType extends MoreMilieuAgent, EdgeType extends MoreEdge<AgentType>>
	extends MOneTimeWattsBetaSwBuilder<AgentType, EdgeType> {

	static private Logger logger = Logger.getLogger(MOneTimeVaryingKWattsBetaSwBuilder.class);


	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public MOneTimeVaryingKWattsBetaSwBuilder() {
		this((MoreEdgeFactory<AgentType, EdgeType>) new MDefaultEdgeFactory<AgentType>());
	}

	public MOneTimeVaryingKWattsBetaSwBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		this(eFac, "Network");
	}

	/**
	 * @param eFac
	 */
	public MOneTimeVaryingKWattsBetaSwBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		this(eFac, name, PmParameterManager.getInstance(null));
	}


	/**
	 * @param eFac
	 */
	public MOneTimeVaryingKWattsBetaSwBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name, PmParameterManager pm) {
		super(eFac);
		this.name = name;
		this.pm = pm;

		params =
				new MSmallWorldBetaModelNetworkGeneratorParams<AgentType, EdgeType>(this.pm);

		params.setkProvider(new MoreKValueProvider<AgentType>() {

			protected MMilieuNetworkParameterMap				paraMap;

			protected Map<Integer, MIntegerDistribution> degreeDistributions;

			{
				this.degreeDistributions = new HashMap<Integer, MIntegerDistribution>();
				for (int i = 1; i <= paraMap.size(); i++) {

					MIntegerDistribution dist = null;

					try {
						dist = (MIntegerDistribution) Class.forName((String) paraMap.getMilieuParam(MNetworkBuildingPa.MILIEU_K_DISTRIBUTION_CLASS, i)).
								getConstructor(RandomGenerator.class).newInstance(
										new MRandomEngineGenerator(MManager.getURandomService().getGenerator(
												(String) MOneTimeVaryingKWattsBetaSwBuilder.this.pm.getParam(MRandomPa.RND_STREAM_NETWORK_BUILDING))));

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
					this.degreeDistributions.put(new Integer(i), dist);
				}
			}

			@Override
			public int getKValue(AgentType node) {
				return this.degreeDistributions.get(new Integer(node.getMilieuGroup())).sample();
			}
		});
		params.setEdgeModifier(getEdgeModifier());
	}

}
