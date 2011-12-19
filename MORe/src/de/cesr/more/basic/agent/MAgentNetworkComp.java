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
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.basic.agent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;


/**
 * TODO check if networks of different type are required! TODO make sure not more than one network with same name
 * inserted
 * 
 * @author Sascha Holzhauer
 * @param <A>
 *        the common type (of agents) that is contained as nodes in the networks
 * @date 19.01.2010
 */
public class MAgentNetworkComp<A, E extends MoreEdge<? super A>> implements MoreAgentNetworkComp<A, E> {

	protected A									agent;

	protected MoreNetwork<A, E>					mainNetwork;

	protected Map<String, MoreNetwork<A, E>>	networks;
	
	/**
	 * @param agent
	 */
	public MAgentNetworkComp(A agent) {
		super();
		this.agent = agent;
		networks = new HashMap<String, MoreNetwork<A, E>>();
	}


	/**
	 * @see de.cesr.more.basic.agent.MoreAgentNetworkComp#perceiveNetworks()
	 */
	@Override
	public void perceiveNetworks() {
		// nothing to do
	}


	/**
	 * Getter of the property <tt>networks</tt>
	 * @return  Returns the networks.
	 */
	@Override
	public Collection<MoreNetwork<A, E>> getNetworks() {
		return networks.values();
	}


	/**
	 * @see de.cesr.more.basic.agent.MoreAgentNetworkComp#setNetwork(de.cesr.lara.components.LaraNetwork)
	 */
	@Override
	public void setNetwork(MoreNetwork<A, E> network) {
		if (networks.size() == 0) {
			this.mainNetwork = network;
		}
		this.networks.put(network.getName(), network);
	}
	

	/**
	 * @see de.cesr.more.basic.agent.MoreAgentNetworkComp#getNetwork(java.lang.String)
	 */
	@Override
	public MoreNetwork<A, E> getNetwork(String name) {
		return this.networks.get(name);
	}


	/**
	 * @see de.cesr.more.basic.agent.MoreAgentNetworkComp#setMainNetwork(de.cesr.more.basic.network.MoreNetwork)
	 */
	@Override
	public void setMainNetwork(MoreNetwork<A, E> network) {
		this.mainNetwork = network;
	}


	/**
	 * @see de.cesr.more.basic.agent.MoreAgentNetworkComp#getMainNetwork()
	 */
	@Override
	public MoreNetwork<A, E> getMainNetwork() {
		return this.mainNetwork;
	}
}
