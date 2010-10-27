/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.cesr.more.networks.MoreNetwork;

/** 
 * TODO check if networks of different type are required!
 * TODO make sure not more than one network with same name inserted
 * @author Sascha Holzhauer
 * @param <AgentType> the common type (of agents) that is contained as nodes in the networks
 * @date 19.01.2010
 */
public class LAgentNetworkComp<AgentType, EdgeType> implements LaraAgentNetworkComp<AgentType, EdgeType> {
	
	LaraSimpleNetworkAgent<AgentType, EdgeType> agent;
	

	/**
	 * @uml.property  name="networks"
	 */
	private Map<String, MoreNetwork<AgentType, EdgeType>> networks;
	
	/**
	 * @param agent
	 */
	public LAgentNetworkComp(LaraSimpleNetworkAgent<AgentType, EdgeType> agent) {
		super();
		this.agent = agent;
		networks = new HashMap<String, MoreNetwork<AgentType, EdgeType>>();
	}


	/**
	 * @see de.cesr.more.lara.LaraAgentNetworkComp#perceiveNetworks()
	 */
	@Override
	public void perceiveNetworks() {
		// TODO Auto-generated method stub
	}



	/**
	 * Getter of the property <tt>networks</tt>
	 * @return  Returns the networks.
	 * @uml.property  name="networks"
	 */
	public Collection<MoreNetwork<AgentType, EdgeType>> getNetworks() {
		return networks.values();
	}


	/**
	 * @see de.cesr.more.lara.LaraAgentNetworkComp#setNetwork(de.cesr.lara.components.LaraNetwork)
	 */
	public void setNetwork(MoreNetwork<AgentType, EdgeType> network) {
		this.networks.put(network.getName(), network);
	}
	

	/**
	 * @see de.cesr.more.lara.LaraAgentNetworkComp#getNetwork(java.lang.String)
	 */
	public MoreNetwork<AgentType, EdgeType> getNetwork(String name) {
		return this.networks.get(name);
	}

	/**
	 * @uml.property  name="cni"
	 */
	private Collection<ComboundNetworkInfo> cni;


	/**
	 * @see de.cesr.more.lara.LaraAgentNetworkComp#changeComboundNetworkInfo(de.cesr.more.lara.ComboundNetworkInfo)
	 */
	@Override
	public void changeComboundNetworkInfo(ComboundNetworkInfo cni) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see de.cesr.more.lara.LaraAgentNetworkComp#getComboundNetworkInfos()
	 */
	@Override
	public Collection<ComboundNetworkInfo> getComboundNetworkInfos() {
		return cni;
	}

}
