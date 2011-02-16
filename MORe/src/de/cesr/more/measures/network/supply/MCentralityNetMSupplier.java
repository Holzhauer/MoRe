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
 * Created by Sascha Holzhauer on 22.12.2010
 */
package de.cesr.more.measures.network.supply;



import java.util.Map;

import org.apache.log4j.Logger;

import de.cesr.more.basic.MNetworkManager;
import de.cesr.more.basic.MoreEdge;
import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.network.MNetworkMeasureCategory;
import de.cesr.more.measures.measures.MAbstractNetworkMeasure;
import de.cesr.more.measures.network.MNetworkMeasureManager;
import de.cesr.more.measures.network.supply.MCcNetworkMeasureSupplier.Short;
import de.cesr.more.measures.network.supply.algos.MInDegreeScorer;
import de.cesr.more.measures.network.supply.algos.MOutDegreeScorer;
import de.cesr.more.measures.node.MNodeMeasureCategory;
import de.cesr.more.measures.util.MAbstractAction;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.util.Log4jLogger;
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;



/**
 * 
 * TODO convert other measures!
 * 
 * @author Sascha Holzhauer
 * @date 17.08.2008
 * 
 */
public class MCentralityNetMSupplier extends MAbstractMeasureSupplier {

	/**
	 * MORe Short descriptions for measures defined in this class
	 * 
	 * @author Sascha Holzhauer
	 * @date 22.12.2010
	 * 
	 */
	public enum Short {
		/**
		 * Network Measure: Degree-based centrality
		 */
		NET_CEN_DEGREE("Net-Cen-degree"),
		
		/**
		 * Network Measure: In-Degree-based centrality
		 */
		NET_CEN_INDEGREE("Net-Cen-indegree"),
		
		/**
		 * Network Measure: Out-Degree-based centrality
		 */
		NET_CEN_OUTDEGREE("Net-Cen-outdegree");

		String	name;

		private Short(String name) {
			this.name = name;
		}

		/**
		 * @return the short name Created by Sascha Holzhauer on 22.12.2010
		 */
		public String getName() {
			return name;
		}

	}

	private static MCentralityNetMSupplier instance;
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MCentralityNetMSupplier.class);
	
	MMeasureDescription	description;

	/**
	 * Adds categories and measures
	 */
	private MCentralityNetMSupplier() {
		addMeasures();
		addCategories();
	}
	
	/**
	 * @return
	 * Created by Sascha Holzhauer on 23.12.2010
	 */
	public static MCentralityNetMSupplier getInstance() {
		if (instance == null) {
			instance = new MCentralityNetMSupplier();
		}
		return instance;
	}

	private void addCategories() {
		categories.add(MNetworkMeasureCategory.NETWORK_CENTRALITY);
	}

	private void addMeasures() {

		description = new MMeasureDescription(MNetworkMeasureCategory.NETWORK_CENTRALITY, Short.NET_CEN_DEGREE
				.getName(), "Degree based network centrality (not normalized)");

		measures.put(description, new MAbstractNetworkMeasure(description, Double.class) {

			@Override
			public <T, EdgeType extends MoreEdge> MoreAction getAction(final MoreNetwork<T, EdgeType> network,
					Map<String, Object> parameters) {
				return new MAbstractAction() {
					DegreeScorer<T>	scorer	= new DegreeScorer<T>(network.getJungGraph());

					@Override
					public void execute() {
						logger.info("Calculate " + Short.NET_CEN_DEGREE.getName() + " for " + network.getName() + "...");
						int sum = 0;
						for (T node : network.getNodes()) {
							sum += scorer.getVertexScore(node);
						}
						MNetworkManager.setNetworkMeasure(network, new MMeasureDescription(Short.NET_CEN_DEGREE.getName()),
								(double) sum / (double) network.numNodes());
						logger.info("... finished.");
					}
					
					public String toString() {
						return Short.NET_CEN_DEGREE.getName();
					}
				};
			}
		});

		description = new MMeasureDescription(MNetworkMeasureCategory.NETWORK_CENTRALITY, Short.NET_CEN_INDEGREE.name,
				"In-Degree based network centrality (not normalized)");

		measures.put(description, new MAbstractNetworkMeasure(description, Double.class) {

			@Override
			public <T, EdgeType extends MoreEdge> MoreAction getAction(final MoreNetwork<T, EdgeType> network,
					Map<String, Object> parameters) {
				return new MAbstractAction() {
					MInDegreeScorer<T>	scorer	= new MInDegreeScorer<T>(network.getJungGraph());

					@Override
					public void execute() {
						logger.info("Calculate " + Short.NET_CEN_INDEGREE.getName() + " for " + network.getName() + "...");
						int sum = 0;
						for (T node : network.getNodes()) {
							sum += scorer.getVertexScore(node);
						}
						MNetworkManager.setNetworkMeasure(network, new MMeasureDescription(Short.NET_CEN_INDEGREE.name),
								(double) sum / (double) network.numNodes());
						logger.info("... finished.");
					}
					
					public String toString() {
						return Short.NET_CEN_INDEGREE.getName();
					}
				};
			}
		});
		
		
		description = new MMeasureDescription(MNetworkMeasureCategory.NETWORK_CENTRALITY, Short.NET_CEN_OUTDEGREE.name
				, "Out-Degree based network centrality (not normalized)");

		measures.put(description, new MAbstractNetworkMeasure(description, Double.class) {

			@Override
			public <T, EdgeType extends MoreEdge> MoreAction getAction(final MoreNetwork<T, EdgeType> network,
					Map<String, Object> parameters) {
				return new MAbstractAction() {
					MOutDegreeScorer<T>	scorer	= new MOutDegreeScorer<T>(network.getJungGraph());

					@Override
					public void execute() {
						logger.info("Calculate " + Short.NET_CEN_OUTDEGREE.getName() + " for " + network.getName() + "...");
						int sum = 0;
						for (T node : network.getNodes()) {
							sum += scorer.getVertexScore(node);
						}
						MNetworkManager.setNetworkMeasure(network, new MMeasureDescription(Short.NET_CEN_OUTDEGREE.name),
								(double) sum / (double) network.numNodes());
						logger.info("... finished.");
					}
					
					public String toString() {
						return Short.NET_CEN_OUTDEGREE.getName();
					}
				};
			}
		});
		// description = new NetworkMeasureUtilities.MeasureDescription(NetworkMeasureUtilities.CENTRALITY, "ODCnn",
		// "Outdegree based centrality (not normalized)");
		//
		// measures.put(description, new NetworkMeasureUtilities.Measure(description, Double.class) {
		// public <T extends NetworkMeasureSupport> ISchedulableAction getAction(final ContextJungNetwork<T> network,
		// Map<String, Object> parameters) {
		// return new AbstractAction(ScheduleParameters.createRepeating(0,
		// (Double) parameters.get("INTERVAL") == null ? 1.0 : ((Double) parameters.get("INTERVAL"))
		// .doubleValue())) {
		// DegreeDistributionRanker<T, RepastEdge<T>> ranker = new DegreeDistributionRanker<T, RepastEdge<T>>(
		// network.getGraph(), false);
		// {
		// ranker.setNormalizeRankings(false);
		// }
		//
		// public void execute() {
		// ranker.step();
		// for (T node : network.getNodes()) {
		// node
		// .setNetworkMeasureObject(network, new NetworkMeasureUtilities.MeasureDescription(
		// NetworkMeasureUtilities.CENTRALITY, "ODCnn",
		// "Outdegree based centrality (not normalized)"), ranker
		// .getVertexRankScore((T) node));
		// }
		// }
		// };
		// }
		// });
		//
		// description = new NetworkMeasureUtilities.MeasureDescription(NetworkMeasureUtilities.CENTRALITY, "CLCnn",
		// "Closeness based centrality (not normalized)");
		//
		// measures.put(description, new NetworkMeasureUtilities.Measure(description, Double.class) {
		// public <T extends NetworkMeasureSupport> ISchedulableAction getAction(final ContextJungNetwork<T> network,
		// Map<String, Object> parameters) {
		// return new AbstractAction(ScheduleParameters.createRepeating(0,
		// (Double) parameters.get("INTERVAL") == null ? 1.0 : ((Double) parameters.get("INTERVAL"))
		// .doubleValue())) {
		// BarycenterScorer<T, RepastEdge<T>> ranker = new BarycenterScorer<T,
		// // rank vertices, don't rank edges:
		// RepastEdge<T>>(network.getGraph());
		//
		// public void execute() {
		// for (T node : network.getNodes()) {
		// node.setNetworkMeasureObject(network, new NetworkMeasureUtilities.MeasureDescription(
		// NetworkMeasureUtilities.CENTRALITY, "CLCnn",
		// "Closeness based centrality (not normalized)"),
		// // Closeness-Centrality is inverse of Bary-Center:
		// 1.0 / ranker.getVertexScore((T) node));
		// }
		// }
		// };
		// }
		// });
		//
		// description = new NetworkMeasureUtilities.MeasureDescription(NetworkMeasureUtilities.CENTRALITY, "BwCnn",
		// "Betweeness based centrality (not normalized)");
		//
		// measures.put(description, new NetworkMeasureUtilities.Measure(description, Double.class) {
		// public <T extends NetworkMeasureSupport> ISchedulableAction getAction(final ContextJungNetwork<T> network,
		// Map<String, Object> parameters) {
		// return new AbstractAction(ScheduleParameters.createRepeating(0,
		// (Double) parameters.get("INTERVAL") == null ? 1.0 : ((Double) parameters.get("INTERVAL"))
		// .doubleValue())) {
		// BetweennessCentrality<T, RepastEdge<T>> ranker = new BetweennessCentrality<T,
		// // rank vertices, don't rank edges:
		// RepastEdge<T>>(network.getGraph(), true, false);
		// {
		// ranker.setNormalizeRankings(false);
		// }
		//
		// public void execute() {
		// ranker.step();
		// for (T node : network.getNodes()) {
		// node.setNetworkMeasureObject(network, new NetworkMeasureUtilities.MeasureDescription(
		// NetworkMeasureUtilities.CENTRALITY, "BwCnn",
		// "Betweeness based centrality (not normalized)"), ranker
		// .getVertexRankScore((T) node));
		// }
		// }
		// };
		// }
		// });
		//
		// description = new NetworkMeasureUtilities.MeasureDescription(NetworkMeasureUtilities.CENTRALITY, "BaCnn",
		// "Bary centrality (not normalized)");
		//
		// measures.put(description, new NetworkMeasureUtilities.Measure(description, Double.class) {
		// public <T extends NetworkMeasureSupport> ISchedulableAction getAction(final ContextJungNetwork<T> network,
		// Map<String, Object> parameters) {
		// return new AbstractAction(ScheduleParameters.createRepeating(0,
		// (Double) parameters.get("INTERVAL") == null ? 1.0 : ((Double) parameters.get("INTERVAL"))
		// .doubleValue())) {
		// BaryCenter<T, RepastEdge<T>> ranker = new BaryCenter<T,
		// // rank vertices, don't rank edges:
		// RepastEdge<T>>(network.getGraph());
		// {
		// ranker.setNormalizeRankings(false);
		// }
		//
		// public void execute() {
		// ranker.step();
		// for (T node : network.getNodes()) {
		// node.setNetworkMeasureObject(network, new NetworkMeasureUtilities.MeasureDescription(
		// NetworkMeasureUtilities.CENTRALITY, "BaCnn", "Bary centrality (not normalized)"),
		// ranker.getVertexRankScore((T) node));
		// }
		// }
		// };
		// }
		// });
		//
		// description = new NetworkMeasureUtilities.MeasureDescription(NetworkMeasureUtilities.CENTRALITY, "EccCnn",
		// "Eccentricity centrality (not normalized)");
		//
		// measures.put(description, new NetworkMeasureUtilities.Measure(description, Double.class) {
		// {
		// parameters = new HashMap<String, Object>();
		// parameters.put(EccentricityCentrality.PARAM_UNCONNECTEDREP, new Integer(Integer.MAX_VALUE));
		// parameters.put(EccentricityCentrality.PARAM_USEDIAMETER, true);
		// }
		//
		// public <T extends NetworkMeasureSupport> ISchedulableAction getAction(final ContextJungNetwork<T> network,
		// final Map<String, Object> parameters) {
		// return new AbstractAction(ScheduleParameters.createRepeating(0,
		// (Double) parameters.get("INTERVAL") == null ? 1.0 : ((Double) parameters.get("INTERVAL"))
		// .doubleValue())) {
		// EccentricityCentrality<T, RepastEdge<T>> ranker = new EccentricityCentrality<T, RepastEdge<T>>(
		// network.getGraph());
		// {
		// ranker.setNormalizeRankings(false);
		// if (parameters != null && parameters.size() > 0) {
		// ranker.setUnconnectedRepresentative(((Integer) parameters
		// .get(EccentricityCentrality.PARAM_UNCONNECTEDREP)).intValue());
		// ranker.setUseDiameter(((Boolean) parameters.get(EccentricityCentrality.PARAM_USEDIAMETER))
		// .booleanValue());
		// }
		// }
		//
		// public void execute() {
		// ranker.step();
		// for (T node : network.getNodes()) {
		// node.setNetworkMeasureObject(network, new NetworkMeasureUtilities.MeasureDescription(
		// NetworkMeasureUtilities.CENTRALITY, "EccCnn",
		// "Eccentricity centrality (not normalized)"), ranker.getVertexRankScore((T) node));
		// }
		// }
		// };
		// }
		// });
	}
}