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
import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.MNetworkMeasureCategory;
import de.cesr.more.measures.measures.MAbstractNetworkMeasure;
import de.cesr.more.measures.network.supply.algos.MClusteringCoefficient;
import de.cesr.more.measures.node.MNodeMeasureCategory;
import de.cesr.more.measures.util.MAbstractAction;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.util.Log4jLogger;



/**
 * RS_SoNetA
 * 
 * @author Sascha Holzhauer
 * @date 12.04.2010
 * 
 */
public class MCcNetworkMeasureSupplier extends MAbstractMeasureSupplier {
	
	public enum MCcShort {
		N_CL_OVERALL("N-Cl-overall");
		
		String name;
		private MCcShort(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
	}

	static MCcNetworkMeasureSupplier instance;
	
	MMeasureDescription		description;

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(MCcNetworkMeasureSupplier.class);

	private MCcNetworkMeasureSupplier() {
		addMeasures();
		addCategories();
	}
	
	public static MCcNetworkMeasureSupplier getInstance() {
		if (instance == null) {
			instance = new MCcNetworkMeasureSupplier();
		}
		return instance;
	}

	private void addCategories() {
		categories.add(MNodeMeasureCategory.NODE_CENTRALITY);
	}

	private void addMeasures() {

		description = new MMeasureDescription(MNetworkMeasureCategory.NETWORK_CLUSTERING, MCcShort.N_CL_OVERALL.name,
				"Network Clustering coefficient (overall ration)");

		measures.put(description, new MAbstractNetworkMeasure(description, Double.class) {
			@Override
			public <T, E> MoreAction getAction(final MoreNetwork<T, E> network, Map<String, Object> parameters) {
				return new MAbstractAction() {

					@Override
					public void execute() {
						MNetworkManager.setNetworkMeasure(network, description, 
								MClusteringCoefficient.getClusteringCoefficientOverallRatio(network.getJungGraph()));
					}
				};
			}
		});
	}
}
