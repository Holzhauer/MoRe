/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 22.07.2010
 */
package de.cesr.more.rs.building;



import java.util.Collection;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.building.MoreNetworkBuilder;
import de.cesr.more.networks.MoreNetwork;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 22.07.2010
 * 
 */
public class MRsWattsBetaBuilder<AgentType> implements MoreNetworkBuilder<AgentType> {

	private Context	context;

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public MoreNetwork buildNetwork(Collection agents) {
		ContextJungNetwork<AgentType> network = new ContextJungNetwork<AgentType>(new UndirectedJungNetwork<AgentType>(
				"Network"), context);
		network = (ContextJungNetwork<AgentType>) new WattsBetaSmallWorldGenerator<AgentType>(0.1, 5, false)
				.createNetwork(network);
		return null;
	}

}
