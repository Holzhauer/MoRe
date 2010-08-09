/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 22.07.2010
 */
package de.cesr.more.building;

import java.util.Collection;

import de.cesr.more.networks.MoreNetwork;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @param <AgentType> 
 * @date 22.07.2010 
 *
 */
public interface MoreNetworkBuilder<AgentType> {
	
	/**
	 * @param agents
	 * @return
	 * Created by Sascha Holzhauer on 22.07.2010
	 */
	public MoreNetwork<AgentType> buildNetwork(Collection<AgentType> agents);

}
