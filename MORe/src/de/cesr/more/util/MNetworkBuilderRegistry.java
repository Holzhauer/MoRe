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
 * Created by Sascha Holzhauer on 21 Jan 2015
 */
package de.cesr.more.util;

import java.util.HashMap;
import java.util.Map;

import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.network.MoreNetworkBuilder;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 21 Jan 2015 
 *
 */
public class MNetworkBuilderRegistry {

	static protected Map<MoreNetwork<?, ?>, MoreNetworkBuilder<?, ?>>	networkBuilders	=
																				new HashMap<MoreNetwork<?, ?>, MoreNetworkBuilder<?, ?>>();

	public static void registerNetworkBuiler(MoreNetwork<?, ?> network, MoreNetworkBuilder<?, ?> nbuilder) {
		networkBuilders.put(network, nbuilder);
	}

	public static MoreNetworkBuilder<?, ?> getNetworkBuilder(MoreNetwork<?, ?> network) {
		return networkBuilders.get(network);
	}

	public static void reset() {
		networkBuilders.clear();
	}
}
