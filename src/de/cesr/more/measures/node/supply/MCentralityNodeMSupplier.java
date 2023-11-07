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
 * @date 17.08.2008
 * 
 */
package de.cesr.more.measures.node.supply;



import java.util.Map;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.MAbstractNodeMeasure;
import de.cesr.more.measures.node.MNodeMeasureCategory;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.measures.util.MAbstractAction;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.BarycenterScorer;



/**
 * 
 * TODO convert other measures!
 * 
 * @author Sascha Holzhauer
 * @date 17.08.2008
 * 
 */
public class MCentralityNodeMSupplier extends MAbstractMeasureSupplier {

	static private MCentralityNodeMSupplier	instance	= null;

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
		NODE_CEN_INDEGREE_NN("IDCnn");

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
	
	MMeasureDescription	description;

	private MCentralityNodeMSupplier() {
		addMeasures();
		addCategories();
	}

	public static MCentralityNodeMSupplier getInstance() {
		if (instance == null) {
			instance = new MCentralityNodeMSupplier();
		}
		return instance;
	}

	private void addCategories() {
		categories.add(MNodeMeasureCategory.NODE_CENTRALITY);
	}

	private void addMeasures() {

		description = new MMeasureDescription(MNodeMeasureCategory.NODE_CENTRALITY, "IDCnn",
				"Indegree based centrality (not normalized)");

		measures.put(description, new MAbstractNodeMeasure(description, Double.class) {
			@Override
			public <V extends MoreNodeMeasureSupport, E extends MoreEdge<? super V>> MoreAction getAction(final MoreNetwork<V, E> network,
					Map<String, Object> parameters) {
				return new MAbstractAction() {
					@Override
					public void execute() {
						for (V node : network.getNodes()) {
							node.setNetworkMeasureObject(network, new MMeasureDescription(
									MNodeMeasureCategory.NODE_CENTRALITY, "IDCnn",
									"Indegree based centrality (not normalized)"), network.getInDegree(node));
						}
					}
				};
			}
		});

		description = new MMeasureDescription(MNodeMeasureCategory.NODE_CENTRALITY, "ODCnn",
				"Outdegree based centrality (not normalized)");

		measures.put(description, new MAbstractNodeMeasure(description, Double.class) {
			@Override
			public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> MoreAction getAction(
					final MoreNetwork<T, E> network,
					Map<String, Object> parameters) {
				return new MAbstractAction(MScheduleParameters.getUnboundedRandomMScheduleParameters(
						(Double) parameters.get("INTERVAL") == null ? 1.0 : ((Double) parameters.get("INTERVAL"))
								.doubleValue())) {
					@Override
					public void execute() {
						for (T node : network.getNodes()) {
							node.setNetworkMeasureObject(network, new MMeasureDescription(
									MNodeMeasureCategory.NODE_CENTRALITY, "ODCnn",
											"Outdegree based centrality (not normalized)"), network.getOutDegree(node));
						}
					}
				};
			}
		});

		description = new MMeasureDescription(MNodeMeasureCategory.NODE_CENTRALITY, "CLCnn",
				"Closeness based centrality (not normalized)");

		measures.put(description, new MAbstractNodeMeasure(description, Double.class) {
			@Override
			public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> MoreAction getAction(
					final MoreNetwork<T, E> network,
					Map<String, Object> parameters) {
				return new MAbstractAction(MScheduleParameters.getUnboundedRandomMScheduleParameters(
						(Double) parameters.get("INTERVAL") == null ? 1.0 : ((Double) parameters.get("INTERVAL"))
								.doubleValue())) {
					BarycenterScorer<T, E>	ranker	= new BarycenterScorer<T, E>(network.getJungGraph());

					@Override
					public void execute() {
						for (T node : network.getNodes()) {
							node.setNetworkMeasureObject(network, new MMeasureDescription(
									MNodeMeasureCategory.NODE_CENTRALITY, "CLCnn",
									"Closeness based centrality (not normalized)"),
									// Closeness-Centrality is inverse of Bary-Center:
									1.0 / ranker.getVertexScore(node));
						}
					}
				};
			}
		});

		description = new MMeasureDescription(MNodeMeasureCategory.NODE_CENTRALITY, "BwCnn",
				"Betweeness based centrality (not normalized)");

		measures.put(description, new MAbstractNodeMeasure(description, Double.class) {
			@Override
			public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> MoreAction getAction(
					final MoreNetwork<T, E> network,
					Map<String, Object> parameters) {
				return new MAbstractAction(MScheduleParameters.getUnboundedRandomMScheduleParameters(
						(Double) parameters.get("INTERVAL") == null ? 1.0 : ((Double) parameters.get("INTERVAL"))
								.doubleValue())) {
					BetweennessCentrality<T, E>	ranker	= new BetweennessCentrality<T, E>(network.getJungGraph(), true,
																					false);
					{
						ranker.setNormalizeRankings(false);
					}

					@Override
					public void execute() {
						ranker.step();
						for (T node : network.getNodes()) {
							node.setNetworkMeasureObject(network, new MMeasureDescription(
									MNodeMeasureCategory.NODE_CENTRALITY, "BwCnn",
									"Betweeness based centrality (not normalized)"), ranker
									.getVertexRankScore(node));
						}
					}
				};
			}
		});

		description = new MMeasureDescription(MNodeMeasureCategory.NODE_CENTRALITY, "BaCnn",
				"Bary centrality (not normalized)");

		measures.put(description, new MAbstractNodeMeasure(description, Double.class) {
			@Override
			public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> MoreAction getAction(
					final MoreNetwork<T, E> network,
					Map<String, Object> parameters) {
				return new MAbstractAction(MScheduleParameters.getUnboundedRandomMScheduleParameters(
						(Double) parameters.get("INTERVAL") == null ? 1.0 : ((Double) parameters.get("INTERVAL"))
								.doubleValue())) {
					BarycenterScorer<T, E>	ranker	= new BarycenterScorer<T, E>(network.getJungGraph());

					@Override
					public void execute() {
						for (T node : network.getNodes()) {
							node.setNetworkMeasureObject(network, new MMeasureDescription(
									MNodeMeasureCategory.NODE_CENTRALITY, "BaCnn", "Bary centrality (not normalized)"),
									ranker.getVertexScore(node));
						}
					}
				};
			}
		});

		// description = new MMeasureDescription(MNodeMeasureCategory.NODE_CENTRALITY, "EccCnn",
		// "Eccentricity centrality (not normalized)");
		//
		// measures.put(description, new MAbstractNodeMeasure(description, Double.class) {
		// {
		// parameters = new HashMap<String, Object>();
		// parameters.put(EccentricityCentrality.PARAM_UNCONNECTEDREP, new Integer(Integer.MAX_VALUE));
		// parameters.put(EccentricityCentrality.PARAM_USEDIAMETER, true);
		// }
		//
		// public <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> MSchedulableAction getAction(
		// final MoreNetwork<T, E> network,
		// final Map<String, Object> parameters) {
		// return new MAbstractAction(MScheduleParameters.getUnboundedRandomMScheduleParameters(
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
