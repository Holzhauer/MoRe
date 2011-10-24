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
 * Created by Sascha Holzhauer on 07.01.2010
 */
package de.cesr.more.rs.network;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.engine.environment.RunState;
import repast.simphony.space.graph.RepastEdge;
import de.cesr.more.basic.network.MoreNetwork;



/**
 * 
 * TODO testing
 * 
 * @author Sascha Holzhauer
 * @date 07.01.2010
 */
public class MRsSocialNetworkFinder {

	private Map<Context<?>, Map<String, MoreNetwork<?, ? extends 
			RepastEdge<?>>>>	networks;

	/**
	 * This is a helper method to query all networks a given agent is in.
	 * 
	 * @param <T>
	 * @param agent
	 * @return networks as String[]
	 */

	public static <T extends Object> Collection<ContextJungNetwork<T>> getNetworks(T agent) {
		Collection<ContextJungNetwork<T>> networks = new ArrayList<ContextJungNetwork<T>>();
		for (Object proj : RunState.getInstance().getMasterContext().getProjections()) {
			if (proj instanceof ContextJungNetwork<?>) {
				@SuppressWarnings("unchecked")
				ContextJungNetwork<T> net = (ContextJungNetwork<T>) proj;
				if (net.getGraph().containsVertex(agent)) {
					networks.add(net);
				}
			}
		}
		// for (Context)
		return networks;
	}

	/**
	 * @param context
	 * @param name
	 * @return
	 */
	public MoreNetwork<?, ? extends RepastEdge<?>> getNetwork(Context<?> context, String name) {
		return networks.get(context).get(name);
	}
}
