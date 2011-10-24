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
package de.cesr.more.measures.node;

import java.util.Map;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.MoreMeasure;
import de.cesr.more.measures.util.MoreAction;



/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 08.11.2010 
 *
 */
public abstract class MAbstractNodeMeasure implements MoreMeasure{

	
	/**
	 * the measure's type
	 */
	protected Class<?> type;
	
	/**
	 * The measure description
	 */
	protected MMeasureDescription desc;
	
	/**
	 * Parameters used to calculate this measure
	 */
	protected Map<String, Object> parameters;

	/**
	 * The action to schedule the measure calculation
	 */
	protected MoreAction action;
	
	/**
	 * @param description The measure description
	 * @param measureType the measure's type
	 */
	public MAbstractNodeMeasure(MMeasureDescription description, Class<?> measureType) {
		this(description, measureType, null);
	}
	
	/**
	 * Initializes a <code>Measure</code> with its {@link MMeasureDescription}, its return type class
	 * and a map with defined keys and default values as parameters.
	 * 
	 * @param description The {@link MMeasureDescription}
	 * @param measureType The class type of the object that represents the measure and is set at the nodes
	 * @param parameters A map with predefined key-(default)values pairs as parameters
	 */
	public MAbstractNodeMeasure(MMeasureDescription description, Class<?> measureType, Map<String, Object> parameters) {
		this.desc = description;
		this.type = measureType;
		this.parameters = parameters;
	}
	
	/**
	 * @date 15.08.2008
	 *
	 * @return The class type of the object that represents the measure and is set at the nodes
	 */
	@Override
	public Class<?> getType() {
		return type;
	}
	
	/**
	 * @date 15.08.2008
	 *
	 * @return A map with key-values pairs as parameters for this measure
	 */
	@Override
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	/**
	 * @date 15.08.2008
	 *
	 * @return The {@link MMeasureDescription} for this measure
	 */
	@Override
	public MMeasureDescription getMeasureDescription() {
		return desc;
	}
	
	/**
	 * A new measure need to provide a {@link BasicAction} that calculates the measure values
	 * @date 15.08.2008
	 *
	 * @param <T> The node type
	 * @param network The network the measure is calculated for
	 * @param parameters The parameter map
	 * @return The <code>BasicAction</code> that is scheduled for computation
	 */
	public abstract <T extends MoreNodeMeasureSupport, E extends MoreEdge<? super T>> MoreAction getAction(MoreNetwork<T, E> network,
			Map<String, Object> parameters);
}
