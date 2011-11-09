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
 * Created by Sascha Holzhauer on 04.11.2011
 */
package de.cesr.more.basic.edge;

import de.cesr.more.util.io.MGraphMlWriter;

/**
 * The traceable edge enables the modeler to mark edges that are activated during
 * the particular tick. I.e., when these edges are output (e.g. via {@link MGraphMlWriter})
 * every tick the edges that were "used" during the specific tick can be visualized/analysed accordingly.
 * 
 * The modeler calls {@link #activate()} to mark the edge and set the active property, and MoRe
 * cares about deactivating the edge for the subsequent tick again.
 *
 * NOTE: Implementation of this interface need to take care to schedule the de-activating
 * at the very beginning of the next tick (since output is often done not before the end of a tick
 * the edge may not be de-activated to early).
 * 
 * @author Sascha Holzhauer
 * @date 04.11.2011 
 *
 */
public interface MoreTraceableEdge<AgentType> extends MoreEdge<AgentType> {
	
	/**
	 * Mark the edge active for the current tick.
	 */
	public void activate();
	
	/**
	 * True if the edge was/is active during the current tick.
	 * @return
	 */
	public boolean isActive();

}
