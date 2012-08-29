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
 * Created by holzhauer on 24.06.2011
 */
package de.cesr.more.building.edge;


import org.apache.log4j.Logger;

import de.cesr.more.basic.edge.MEdge;
import de.cesr.more.basic.edge.MoreEdge;

/**
 * MORe
 *
 * @author holzhauer
 * @date 24.06.2011 
 *
 */
public class MDefaultEdgeFactory<V> implements MoreEdgeFactory<V, MoreEdge<V>> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MDefaultEdgeFactory.class);

	/**
	 * @see de.cesr.more.building.edge.MoreEdgeFactory#createEdge(java.lang.Object, java.lang.Object, boolean)
	 */
	@Override
	public MoreEdge<V> createEdge(V source, V target, boolean directed) {
		if (source == null) {
			// <- LOGGING
			logger.error("Source node is null!");
			// LOGGING ->
			throw new IllegalStateException("Source node is null!");
		}

		if (target == null) {
			// <- LOGGING
			logger.error("Target node is null!");
			// LOGGING ->
			throw new IllegalStateException("Target node is null!");
		}
		return new MEdge<V>(source, target, directed);
	} 
}
