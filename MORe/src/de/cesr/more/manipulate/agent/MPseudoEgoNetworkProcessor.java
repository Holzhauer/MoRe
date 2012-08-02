package de.cesr.more.manipulate.agent;

import repast.simphony.space.gis.Geography;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import de.cesr.more.rs.building.MoreMilieuAgent;

/**
 * MORe
 *
 * Does nothing.
 * 
 * @author Sascha Holzhauer
 * @date 19.07.2012 
 *
 * @param <A>
 * @param <E>
 */
public class MPseudoEgoNetworkProcessor<A extends MoreLinkManipulatableAgent<A> & MoreMilieuAgent, 
E extends MoreEdge<? super A>> implements MoreEgoNetworkProcessor<A, E> {

	public MPseudoEgoNetworkProcessor() {
	}
	
	public MPseudoEgoNetworkProcessor(MoreNetworkEdgeModifier<A, E> edgeMan) {
	}
	
	public MPseudoEgoNetworkProcessor(MoreNetworkEdgeModifier<A, E> edgeMan, Geography<Object> geography) {
	}
	
	/**
	 * @see de.cesr.more.manipulate.agent.MoreEgoNetworkProcessor#process(java.lang.Object, de.cesr.more.basic.network.MoreNetwork)
	 */
	@Override
	public void process(A agent, MoreNetwork<A, E> network) {
		// no nothing
	}
}
