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

import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.engine.schedule.ISchedulableAction;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.measures.util.MoreAction;
import de.cesr.more.networks.MoreNetwork;


/**
 * MORe
 *
 * The basic measure class that holds the measure type, a map of parameters, the {@link MMeasureDescription}
 * and the {@link MoreAction} responsible for measure calculation.
 * 
 * The <code>parameters</code> map should be filled with valid <code>String</code> keys the user might
 * provide values for. The user than scans the map and assigns values to the keys.
 * 
 * @author Sascha Holzhauer
 * @date 08.11.2010 
 *
 */
public interface MoreMeasure {
	
	/**
	 * @return The class type of the object that represents the measure and is set at the nodes
	 * Created by Sascha Holzhauer on 08.11.2010
	 */
	public Class<?> getType();
	
	/**
	 * @date 15.08.2008
	 *
	 * @return A map with key-values pairs as parameters for this measure
	 */
	public Map<String, Object> getParameters();
	
	/**
	 * @date 15.08.2008
	 *
	 *
	 * @return The {@link MMeasureDescription} for this measure
	 */
	public MMeasureDescription getMeasureDescription();
}
