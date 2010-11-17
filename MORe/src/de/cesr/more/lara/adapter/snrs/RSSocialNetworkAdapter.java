/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 07.01.2010
 */
package de.cesr.more.lara.adapter.snrs;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.engine.environment.RunState;
import repast.simphony.space.graph.RepastEdge;
import de.cesr.lara.components.LaraSimpleAgent;
import de.cesr.more.networks.MoreNetwork;



/**
 * 
 * TODO testing
 * 
 * @author Sascha Holzhauer
 * @date 07.01.2010
 */
public class RSSocialNetworkAdapter {

	private Map<Context<? extends LaraSimpleAgent>, Map<String, MoreNetwork<? extends LaraSimpleAgent, ? extends 
			RepastEdge<? extends LaraSimpleAgent>>>>	networks;

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
	 * @return Created by Sascha Holzhauer on 04.02.2010
	 */
	public MoreNetwork<? extends LaraSimpleAgent, ? extends RepastEdge<? extends LaraSimpleAgent>> getNetwork(Context<? extends LaraSimpleAgent> context, String name) {
		return networks.get(context).get(name);
	}
}
