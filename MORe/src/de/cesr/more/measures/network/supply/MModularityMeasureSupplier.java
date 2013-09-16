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
 * Created by Sascha Holzhauer on 27.06.2013
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
import de.cesr.more.measures.network.supply.algos.MNetworkModularityR;
import de.cesr.more.measures.util.MAbstractAction;
import de.cesr.more.measures.util.MoreAction;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 27.06.2013 
 *
 */
public class MModularityMeasureSupplier extends MAbstractMeasureSupplier {

	/**
	 * Logger
	 */
	static private Logger						logger	= Logger.getLogger(MModularityMeasureSupplier.class);

	private static MModularityMeasureSupplier	instance;

	public enum Short {
		/**
		 * Network Measure: Modularity based on Edge-Betweenness
		 */
		NET_MOD_EDGEBETWEEN("Net-Mod-EdgeBetweenness");

		String	name;

		private Short(String name) {
			this.name = name;
		}

		/**
		 * @return the short name
		 */
		public String getName() {
			return name;
		}
	}

	MMeasureDescription	description;

	/**
	 * Adds categories and measures
	 */
	private MModularityMeasureSupplier() {
		addCategories();
		addMeasures();
	}

	/**
	 * @return
	 */
	public static MModularityMeasureSupplier getInstance() {
		if (instance == null) {
			instance = new MModularityMeasureSupplier();
		}
		return instance;
	}

	private void addCategories() {
		categories.add(MNetworkMeasureCategory.NETWORK_MODULARITY);
	}

	private void addMeasures() {
		description = new MMeasureDescription(MNetworkMeasureCategory.NETWORK_MODULARITY, Short.NET_MOD_EDGEBETWEEN
				.getName(), "Modularity based on EdgeBetweenness (not normalized)");

		measures.put(description, new MAbstractNetworkMeasure(description, Double.class) {

			@Override
			public <T, EdgeType extends MoreEdge<? super T>> MoreAction getAction(
					final MoreNetwork<T, EdgeType> network,
					Map<String, Object> parameters) {
				return new MAbstractAction() {

					@Override
					public void execute() {
						logger.info("Calculate " + Short.NET_MOD_EDGEBETWEEN.getName() + " for " + network.getName()
								+ "...");

						MNetworkManager.setNetworkMeasure(network,
								new MMeasureDescription(Short.NET_MOD_EDGEBETWEEN.getName()),
								MNetworkModularityR.getModularityR(network.getJungGraph()));
						logger.info("... finished.");
					}

					@Override
					public String toString() {
						return Short.NET_MOD_EDGEBETWEEN.getName() + "(" + network.getName() + ")";
					}
				};
			}
		});
	}
}
