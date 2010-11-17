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
 * Created by Sascha Holzhauer on 08.11.2010
 */
package de.cesr.more.measures.measures;

import java.util.Map;

import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.networks.MoreNetwork;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 08.11.2010 
 *
 */
public interface MoreNodeMeasure extends MoreMeasure {
	
	
	/**
	 * A new measure need to provide a {@link MoreAction} that calculates the measure values
	 * @date 15.08.2008
	 *
	 * @param <T> The node type
	 * @param <EdgeType> the network's edge type
	 * @param network The network the measure is calculated for
	 * @param parameters The parameter map
	 * @return The <code>BasicAction</code> that is scheduled for computation
	 */
	abstract public <T extends MoreNodeMeasureSupport, EdgeType> MoreAction getAction(
			MoreNetwork<T, EdgeType> network,
			Map<String, Object> parameters);

}
