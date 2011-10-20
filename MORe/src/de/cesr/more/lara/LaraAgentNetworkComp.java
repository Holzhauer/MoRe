/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 19.01.2010
 */
package de.cesr.more.lara;



import java.util.Collection;

import de.cesr.more.edges.MoreEdge;
import de.cesr.more.lara.ComboundNetworkInfo;
import de.cesr.more.networks.MoreNetwork;



/**
 * @author Sascha Holzhauer
 * @param <A> the common type (of agents) that is contained as nodes in the networks
 * @param <E> the edge type
 * @date 19.01.2010
 */
public interface LaraAgentNetworkComp<A, E extends MoreEdge<? super A>> {

	
	/**
	 * @param network
	 * @uml.property  name="network"
	 */
	public void setNetwork(MoreNetwork<A, E> network);
	
	/**
	 * @param name
	 * @return the network with the given name
	 */
	public MoreNetwork<A, E> getNetwork(String name);

	/**
		 */
	public abstract void perceiveNetworks();

	/**
	 * @param cni
	 */
	public abstract void changeComboundNetworkInfo(ComboundNetworkInfo cni);

	/**
	 * @return network info
	 */
	public abstract Collection<ComboundNetworkInfo> getComboundNetworkInfos();

}
