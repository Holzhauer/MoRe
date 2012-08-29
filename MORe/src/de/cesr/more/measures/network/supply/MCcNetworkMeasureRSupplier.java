/**
 * RS_SoNetA
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 12.04.2010
 */
package de.cesr.more.measures.network.supply;



import java.util.Map;

import org.apache.log4j.Logger;

import de.cesr.more.basic.MNetworkManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.network.MAbstractNetworkMeasure;
import de.cesr.more.measures.network.MNetworkMeasureCategory;
import de.cesr.more.measures.network.supply.algos.MClusteringCoefficientR;
import de.cesr.more.measures.node.MNodeMeasureCategory;
import de.cesr.more.measures.util.MAbstractAction;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.util.Log4jLogger;



/**
 * RS_SoNetA
 * 
 * @author Sascha Holzhauer
 * @date 12.04.2010
 * 
 */
public class MCcNetworkMeasureRSupplier extends MAbstractMeasureSupplier {

	/**
	 * MORe
	 * 
	 * @author Sascha Holzhauer
	 * @date 04.01.2011
	 * 
	 */
	public enum Short {
		R_N_CL_OVERALL("R_N-Cl-overall");

		String	name;

		private Short(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	static MCcNetworkMeasureRSupplier	instance;

	MMeasureDescription					description;

	/**
	 * Logger
	 */
	static private Logger				logger	= Log4jLogger.getLogger(MCcNetworkMeasureSupplier.class);

	private MCcNetworkMeasureRSupplier() {
		addMeasures();
		addCategories();
	}

	public static MCcNetworkMeasureRSupplier getInstance() {
		if (instance == null) {
			instance = new MCcNetworkMeasureRSupplier();
		}
		return instance;
	}

	private void addCategories() {
		categories.add(MNodeMeasureCategory.NODE_CENTRALITY);
	}

	private void addMeasures() {

		description = new MMeasureDescription(MNetworkMeasureCategory.NETWORK_CLUSTERING, Short.R_N_CL_OVERALL.name,
				"R: Network Clustering coefficient (overall ratio)");

		measures.put(description, new MAbstractNetworkMeasure(description, Double.class) {
			@Override
			public <T, E extends MoreEdge<? super T>> MoreAction getAction(final MoreNetwork<T, E> network,
					Map<String, Object> parameters) {
				return new MAbstractAction() {

					@Override
					public void execute() {
						logger
								.info("Calculate " + Short.R_N_CL_OVERALL.getName() + " for " + network.getName()
										+ "...");
						MNetworkManager.setNetworkMeasure(network, description, MClusteringCoefficientR
								.getClusteringCoefficientOverallR(network.getJungGraph()));
						logger.info("... finished.");
					}

					@Override
					public String toString() {
						return Short.R_N_CL_OVERALL.getName() + "(" + network.getName() + ")";
					}
				};
			}
		});
	}
}
