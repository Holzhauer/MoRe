/**
 * 
 */
package de.cesr.more.rs.building;

import java.util.Collection;

import org.apache.log4j.Logger;

import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MCompleteNetworkBuilder;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * 
 * Generates a complete network (all possible edges are created).
 * 
 * See {@link MCompleteNetworkBuilder} for information about properties and
 * considered {@link PmParameterDefinition}s.
 * 
 * TODO tests 
 * TODO document params
 * 
 *  * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * </ul>
 * 
 * @author Sascha Holzhauer
 *
 */
public class MGeoRsCompleteNetworkBuilder<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType>> 
	extends MGeoRsNetworkService<AgentType, EdgeType> {
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MGeoRsCompleteNetworkBuilder.class);

	MoreEdgeFactory<AgentType, EdgeType> eFac;
	
	String name;
	
	/**
	 * Uses "Network" as name.
	 * @param eFac
	 */
	public MGeoRsCompleteNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		this(eFac, "Network");
	}
	
	/**
	 * @param eFac
	 */
	public MGeoRsCompleteNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		super(eFac);
		this.name = name;
	}
	
	/**
	 * @see de.cesr.more.building.network.MoreNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(
			Collection<AgentType> agents) {
		if (context == null) {
			// <- LOGGING
			logger.error("The context has not been set!");
			// LOGGING ->
			throw new IllegalStateException("The context has not bee set!");
		}
		
		MRsContextJungNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType >(
				((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ?
						new DirectedJungNetwork<AgentType>(name) :
						new UndirectedJungNetwork<AgentType>(name), context, this.edgeModifier.getEdgeFactory());

		for (AgentType agent : agents) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Add agent " + agent + " to network.");
			}
			// LOGGING ->

			network.addNode(agent);
			
			// connect this agent with every already added other (undirected):
			for (AgentType other : network.getNodes()) {
				if (other != agent) {
					createEdge(network, agent, other);

					if ((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) {
						createEdge(network, other, agent);
					}

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug(agent + "> connect to (and from if directed): " + other);
					}
					// LOGGING ->
				}
			}
		}
		return network;
	}


	/**
	 * @see de.cesr.more.manipulate.network.MoreNetworkModifier#addAndLinkNode(de.cesr.more.basic.network.MoreNetwork, java.lang.Object)
	 */
	@Override
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network,
			AgentType node) {
		network.addNode(node);
		
		// connect this agent with every already added other (undirected):
		for (AgentType other : network.getNodes()) {
			if (other != node) {
				createEdge(network, node, other);

				if ((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) {
					createEdge(network, other, node);
				}

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug(node + "> connect to (and from if directed): " + other);
				}
				// LOGGING ->
			}
		}
		return true;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MGeoRsCompleteNetworkBuilder";
	}
}
