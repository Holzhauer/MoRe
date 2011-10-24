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
 * Created by Sascha Holzhauer on 04.03.2011
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
import de.cesr.more.measures.util.MoreAction;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 04.03.2011
 * 
 */
public class MAvgNearestNeighbourDegree extends MAbstractMeasureSupplier {

	
	/**
	 * MORe
	 *
	 * @author Sascha Holzhauer
	 * @date 04.01.2011 
	 *
	 */
	public enum Short {
		/**
		 * 
		 */
		MC_AVG_NN_DEGREE_UNDIRECTED("Mc_AvgNNDgUd");
		
		String name;
		private Short(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
	}
	
	MMeasureDescription	description;

	/**
	 * 
	 */
	public MAvgNearestNeighbourDegree() {
		addMeasures();
		addCategories();
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 04.03.2011
	 */
	private void addCategories() {
		categories.add(MNodeMeasureCategory.NODE_MISC);
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 04.03.2011
	 */
	private void addMeasures() {
		description = new MMeasureDescription(MNodeMeasureCategory.NODE_MISC, Short.MC_AVG_NN_DEGREE_UNDIRECTED.getName(),
				"Average degree of neares neighbours (undirected, not normalized)");

		measures.put(description, new MAbstractNodeMeasure(description, Double.class) {
			@Override
			public <V extends MoreNodeMeasureSupport, E extends MoreEdge<? super V>> MoreAction getAction(final MoreNetwork<V, E> network,
					Map<String, Object> parameters) {
				return new MAbstractAction() {

					@Override
					public void execute() {
						for (V node : network.getNodes()) {
							int sum = 0;
							for (V neighbour : network.getAdjacent(node)) {
								sum += network.getDegree(neighbour);
							}
							node.setNetworkMeasureObject(network, new MMeasureDescription(
									MNodeMeasureCategory.NODE_MISC,  Short.MC_AVG_NN_DEGREE_UNDIRECTED.getName(),
									"Average degree of neares neighbours (undirected, not normalized)"),
									(double) sum / (double) network.getDegree(node));
						}

					}
				};
			}
		});
	}
}
