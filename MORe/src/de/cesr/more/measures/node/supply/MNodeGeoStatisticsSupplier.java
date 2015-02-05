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
 * Created by Sascha Holzhauer on 03.01.2011
 */
package de.cesr.more.measures.node.supply;



import java.util.Map;

import org.apache.log4j.Logger;

import repast.simphony.space.gis.Geography;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.network.MRestoreNetworkService;
import de.cesr.more.building.network.MoreNetworkBuilder;
import de.cesr.more.geo.building.network.MoreGeoNetworkBuilder;
import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.network.supply.MNetworkStatisticsSupplier;
import de.cesr.more.measures.node.MAbstractNodeMeasure;
import de.cesr.more.measures.node.MNodeMeasureCategory;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.measures.util.MAbstractAction;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.util.Log4jLogger;
import de.cesr.more.util.MNetworkBuilderRegistry;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 03.01.2011
 * 
 */
public class MNodeGeoStatisticsSupplier extends MAbstractMeasureSupplier {

	public enum Short {
		NODE_DISTANCE_NEIGHBOUR_AVG("ND-Dist-NeighbourAvg");

		String	name;

		private Short(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	static MNodeGeoStatisticsSupplier	instance;

	MMeasureDescription					description;

	/**
	 * Logger
	 */
	static private Logger				logger	= Log4jLogger.getLogger(MNetworkStatisticsSupplier.class);

	private MNodeGeoStatisticsSupplier() {
		addMeasures();
		addCategories();
	}

	public static MNodeGeoStatisticsSupplier getInstance() {
		if (instance == null) {
			instance = new MNodeGeoStatisticsSupplier();
		}
		return instance;
	}

	private void addCategories() {
		categories.add(MNodeMeasureCategory.NODE_GEO);
	}

	private void addMeasures() {

		description = new MMeasureDescription(MNodeMeasureCategory.NODE_GEO, "ND-Dist-NeighbourAvg",
				"");

		measures.put(description, new MAbstractNodeMeasure(description, Double.class) {
			@Override
			public <V extends MoreNodeMeasureSupport, E extends MoreEdge<? super V>> MoreAction getAction(
					final MoreNetwork<V, E> network,
					Map<String, Object> parameters) {
				return new MAbstractAction() {
					@Override
					public void execute() {
						for (V node : network.getNodes()) {
							double distance =0.0;
							if (MNetworkBuilderRegistry.getNetworkBuilder(network) == null) {
								logger.error("The MNetworkBuilderRegistry does not contain an entry for network "
										+ network + ". Please register the network builder!");
								throw new IllegalStateException(
										"The MNetworkBuilderRegistry does not contain an entry for network "
												+ network + ". Please register the network builder!");
							}
							MoreNetworkBuilder<?, ?> builder = MNetworkBuilderRegistry.getNetworkBuilder(network);
							if (builder instanceof MRestoreNetworkService) {
								builder = ((MRestoreNetworkService<?, ?>) builder).getMaintainingNetworkService();
							}

							if (!(builder instanceof MoreGeoNetworkBuilder)) {
								logger.error("The netowrk builder registered at MNetworkBuilderRegistry (" + builder
										+ ") does not implement the interface MoreGeoNetworkBuilder!");
								throw new IllegalStateException(
										"The netowrk builder registered at MNetworkBuilderRegistry (" + builder
												+ ") does not implement the interface MoreGeoNetworkBuilder!");
							}

							Geography<Object> geography = ((MoreGeoNetworkBuilder<?, ?>) builder).getGeography();
							for (V predecessor : network.getPredecessors(node)){
								distance += geography.getGeometry(node).distance(geography.getGeometry(predecessor));
							}
							node.setNetworkMeasureObject(network, new MMeasureDescription(
									MNodeMeasureCategory.NODE_GEO, "ND-Dist-NeighbourAvg",
									"Average geographical distance to neighbours"),
									distance / network.getInDegree(node));
						}
					}
				};
			}
		});
	}
}
