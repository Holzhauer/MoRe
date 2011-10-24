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
 * Created by Sascha Holzhauer on 28.10.2010
 */
package de.cesr.more.measures.node;



import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.measures.MMeasureDescription;



/**
 * MORe
 * 
 * 
 * @author Sascha Holzhauer
 * @date 28.10.2010
 * 
 */
public interface MoreNodeMeasureSupport {

	/**
	 * Sets the value-object of this vertex for the network measure identified by the network measure key with the
	 * network this measure is associated with.
	 * 
	 * @param network The Network the measure value to set is associated with
	 * @param key The Key that identifies the measure to set
	 * @param value Value-object of measure to set
	 */
	public void setNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
			MMeasureDescription key, Number value);

	/**
	 * Returns the value-object of this vertex for the network measure identified by the network measure key with the
	 * network this measure is associated with.
	 * 
	 * @param network The Network the measure value to set is associated with
	 * @param key The Key that identifies the measure to set
	 * 
	 * @return value The Value of this vertex for the given network measure key, <code>null</code> if the specified
	 *         measure key has not been set.
	 */
	public Number getNetworkMeasureObject(MoreNetwork<? extends MoreNodeMeasureSupport, ?> network,
			MMeasureDescription key);
}
