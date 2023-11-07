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
 * Created by Sascha Holzhauer on 16.03.2012
 */
package de.cesr.more.rs.building;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import de.cesr.more.basic.MManager;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.util.MSmallWorldBetaModelNetworkGenerator;
import de.cesr.more.building.util.MSmallWorldBetaModelNetworkGenerator.MSmallWorldBetaModelNetworkGeneratorParams;
import de.cesr.more.building.util.MoreBetaProvider;
import de.cesr.more.building.util.MoreKValueProvider;
import de.cesr.more.param.MBasicPa;
import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildBhPa;
import de.cesr.more.param.MNetBuildHdffPa;
import de.cesr.more.param.MNetBuildWbSwPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.param.reader.MMilieuNetDataReader;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.more.util.MNetworkBuilderRegistry;
import de.cesr.more.util.distributions.MGeneralDistributionParameter;
import de.cesr.more.util.distributions.MIntegerDistribution;
import de.cesr.more.util.distributions.MRandomEngineGenerator;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import edu.uci.ics.jung.graph.Graph;


/**
 * MORe
 * 
 * Extension of {@link MGeoRsWattsBetaSwNetworkService}.
 * <table>
 * <th>Parameter</th>
 * <th>Value</th>
 * <tr>
 * <td>#Vertices</td>
 * <td>N (via collection of agents)</td>
 * </tr>
 * <tr>
 * <td>#Edges:</td>
 * <td>Directed: kN</td>
 * </tr>
 * <tr>
 * <td>Parameter provider</td>
 * <td>MSmallWorldBetaModelNetworkGeneratorParams</td>
 * </tr>
 * </table>
 * See {@link MSmallWorldBetaModelNetworkGeneratorParams} for further parameters! <br>
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * <li>{@link MNetworkBuildingPa.CONSIDER_SOURCES}</li>
 * <li>{@link MNetBuildWbSwPa.BETA}(used as default {@link MoreBetaProvider} in parameter provider)</li>
 * <li>{@link MNetBuildWbSwPa.K} (used as default {@link MoreKValueProvider} in parameter provider)</li>
 * </ul>
 * 
 * - uses MSmallWorldBetaModelNetworkGeneratorMilieuParams from MGeoRsWattsBetaSwBuilder - respects milieu preferences
 * for global links (rewired ones)
 * 
 * @author Sascha Holzhauer
 * @date 16.03.2012
 * 
 */
public class MGeoRsWattsBetaSwPartnerCheckKDistNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType>>
		extends MGeoRsWattsBetaSwPartnerCheckNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MGeoRsWattsBetaSwPartnerCheckNetworkService.class);

	protected Map<Integer, MIntegerDistribution>		degreeDistributions;
	
	/**
	 * @param eFac
	 */
	public MGeoRsWattsBetaSwPartnerCheckKDistNetworkService(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		super(eFac);
	}

	/**
	 * @param eFac
	 */
	public MGeoRsWattsBetaSwPartnerCheckKDistNetworkService(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		super(eFac, name);
	}
	
	/**
	 * @param eFac
	 */
	public MGeoRsWattsBetaSwPartnerCheckKDistNetworkService(MoreEdgeFactory<AgentType, EdgeType> eFac, String name,
			PmParameterManager pm) {
		super(eFac, name, pm);
	}

	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(final 
			Collection<AgentType> agents) {

		if (context == null) {
			logger.error("Context not set!");
			throw new IllegalStateException("Context not set!");
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
		this.paraMap = (MMilieuNetworkParameterMap) PmParameterManager
				.getParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);

		final MoreRsNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType>(
				((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ? new DirectedJungNetwork<AgentType>(
						this.name)
						: new UndirectedJungNetwork<AgentType>(
								this.name), context, this.edgeModifier.getEdgeFactory());

		params =
				new MSmallWorldBetaModelNetworkGeneratorMilieuParams<AgentType, EdgeType>();

		params.setNetwork(network);
		params.setEdgeModifier(edgeModifier);
		params.setRandomDist(randomDist);
		
		MGeoRsWattsBetaSwPartnerCheckKDistNetworkService.this.initDegreeDistributions();
		params.setkProvider(new MoreKValueProvider<AgentType>() {	
			@Override
			public int getKValue(AgentType node) {
				return Math.min(new Integer(Math.round(MGeoRsWattsBetaSwPartnerCheckKDistNetworkService.this.degreeDistributions.get(
						new Integer(node.getMilieuGroup()))
						.sample())), agents.size() - 1);
			}
		});
		
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

		params.setRewireManager(new MDefaultPartnerFinder<AgentType, EdgeType>() {

			@Override
			public AgentType findPartner(Graph<AgentType, EdgeType> graph, AgentType focus) {

				return findDistantTarget(paraMap, network, focus);
			}
		});

		MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType> gen = new MSmallWorldBetaModelNetworkGenerator<AgentType, EdgeType>(
				params);

		MoreRsNetwork<AgentType, EdgeType> realisedNetwork = (MoreRsNetwork<AgentType, EdgeType>) gen
				.buildNetwork(agents);
		MNetworkBuilderRegistry.registerNetworkBuiler(realisedNetwork, this);

		return realisedNetwork;
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
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MGeoRsWattsBetaSw PartnerChecking KDistribed NetworkService";
	}
}
