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
package de.cesr.more.measures.network.supply;

import java.util.Map;

import org.apache.log4j.Logger;

import de.cesr.more.basic.MNetworkManager;
import de.cesr.more.basic.MoreEdge;
import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.MNetworkMeasureCategory;
import de.cesr.more.measures.measures.MAbstractNetworkMeasure;
import de.cesr.more.measures.network.supply.algos.MClusteringCoefficient;
import de.cesr.more.measures.network.supply.algos.MNetworkStatisticsR;
import de.cesr.more.measures.node.MNodeMeasureCategory;
import de.cesr.more.measures.util.MAbstractAction;
import de.cesr.more.measures.util.MScheduleParameters;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.util.Log4jLogger;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 03.01.2011 
 *
 */
public class MNetworkStatisticsSupplier  extends MAbstractMeasureSupplier{

	public enum MCcShort {
		N_STAT_AVGPATH("N-Stat-AvgPath");
		
		String name;
		private MCcShort(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
	}

	static MNetworkStatisticsSupplier instance;
	
	MMeasureDescription		description;

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(MCcNetworkMeasureSupplier.class);

	private MNetworkStatisticsSupplier() {
		addMeasures();
		addCategories();
	}
	
	public static MNetworkStatisticsSupplier getInstance() {
		if (instance == null) {
			instance = new MNetworkStatisticsSupplier();
		}
		return instance;
	}

	private void addCategories() {
		categories.add(MNodeMeasureCategory.NODE_CENTRALITY);
	}

	private void addMeasures() {

		description = new MMeasureDescription(MNetworkMeasureCategory.NETWORK_CLUSTERING, MCcShort.N_STAT_AVGPATH.name,
				"Network Statistics: Average Path Length");

		measures.put(description, new MAbstractNetworkMeasure(description, Double.class) {
			@Override
			public <T, E extends MoreEdge> MoreAction getAction(final MoreNetwork<T, E> network, Map<String, Object> parameters) {
				return new MAbstractAction() {

					@Override
					public void execute() {
						MNetworkManager.setNetworkMeasure(network, description, 
								MNetworkStatisticsR.<T,E>getAveragepathLengthR(network.getJungGraph()));
					}
				};
			}
		});
	}
}
