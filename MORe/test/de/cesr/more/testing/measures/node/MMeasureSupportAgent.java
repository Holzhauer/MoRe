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
 * Created by Sascha Holzhauer on 30.01.2012
 */
package de.cesr.more.testing.measures.node;

import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.MNodeMeasures;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.measures.node.supply.MCentralityNodeMSupplier;
import de.cesr.more.rs.edge.MRepastEdge;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 30.01.2012 
 *
 */
public class MMeasureSupportAgent implements MoreNodeMeasureSupport {

	protected MNodeMeasures															measures	= new MNodeMeasures();
	protected MoreNetwork<MMeasureSupportAgent, MRepastEdge<MMeasureSupportAgent>>	network;

	public MMeasureSupportAgent() {
		this.network = null; // substitute by the More Network this agent belongs to
	}

	/**********************************************************
	 *** Network Measure Support ***
	 **********************************************************/

	/**
	 * @see de.cesr.more.measures.node.MoreNodeMeasureSupport#getNetworkMeasureObject(de.cesr.more.basic.network.MoreNetwork,
	 *      de.cesr.more.measures.MMeasureDescription)
	 */
	@Override
	public Number getNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
			MMeasureDescription key) {

		if (measures.getNetworkMeasureObject(network, key) == null) {
		}
		return measures.getNetworkMeasureObject(network, key);
	}

	/**
	 * @see de.cesr.more.measures.node.MoreNodeMeasureSupport#setNetworkMeasureObject(de.cesr.more.basic.network.MoreNetwork,
	 *      de.cesr.more.measures.MMeasureDescription, java.lang.Number)
	 */
	@Override
	public void setNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
			MMeasureDescription key, Number value) {
		measures.setNetworkMeasureObject(network, key, value);
	}

	public int getInDegree() {
		return (Integer) measures.getNetworkMeasureObject(network, new MMeasureDescription(
				MCentralityNodeMSupplier.Short.NODE_CEN_INDEGREE_NN.getName()));
	}
}
