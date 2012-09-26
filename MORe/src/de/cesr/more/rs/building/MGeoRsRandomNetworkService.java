/**
 * 
 */
package de.cesr.more.rs.building;

import java.util.Collection;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.UndirectedJungNetwork;
import cern.jet.random.Uniform;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.util.MRandomNetworkGenerator;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MRandomPa;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.rs.network.MRsContextJungNetwork;
import de.cesr.more.rs.network.MoreRsNetwork;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.uranus.core.URandomService;

/**
 * @author holzhauer
 * TODO incorporate edgeModifier!
 */
public class MGeoRsRandomNetworkService<AgentType extends MoreMilieuAgent, EdgeType extends MRepastEdge<AgentType>>
		extends MGeoRsNetworkService<AgentType, EdgeType> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(MGeoRsRandomNetworkService.class);

	Context<AgentType> context;
	MoreEdgeFactory<AgentType, EdgeType> eFac;
	String									name;
	
	
	/**
	 * Uses "Network" as name.
	 * 
	 * @param eFac
	 */
	public MGeoRsRandomNetworkService(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		this(eFac, "Network");
	}
	
	/**
	 * @param eFac
	 */
	public MGeoRsRandomNetworkService(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		super(eFac);
		this.name = name;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public MoreRsNetwork<AgentType, EdgeType> buildNetwork(Collection<AgentType> agents) {

		checkAgentCollection(agents);

		MRandomNetworkGenerator<AgentType> generator = new MRandomNetworkGenerator<AgentType>(1.0 / (agents.size() - 1)
				*
				((Integer) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_RANDOM_AVG_DEGREE)).intValue(),
				false, (Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED));

		if (context == null) {
			// <- LOGGING
			logger.error("The context has not bee set!");
			// LOGGING ->
			throw new IllegalStateException("The context has not bee set!");
		}
		
		MRsContextJungNetwork<AgentType, EdgeType> network = new MRsContextJungNetwork<AgentType, EdgeType >(
				((Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) ?
						new DirectedJungNetwork<AgentType>(name) :
						new UndirectedJungNetwork<AgentType>(name), context, this.edgeModifier.getEdgeFactory());

		for (AgentType agent : context) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Add agent " + agent + " to network.");
			}
			// LOGGING ->

			network.addNode(agent);
		}
		
		return (MoreRsNetwork<AgentType, EdgeType>) generator.createNetwork(network);
	}

	/**
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#setContext(repast.simphony.context.Context)
	 */
	@Override
	public void setContext(Context<AgentType> context) {
		this.context = context;
	}

	/**
	 * TODO test!
	 * 
	 * @see de.cesr.more.manipulate.network.MoreNetworkModifier#addAndLinkNode(de.cesr.more.basic.network.MoreNetwork,
	 *      java.lang.Object)
	 */
	@Override
	public boolean addAndLinkNode(MoreNetwork<AgentType, EdgeType> network, AgentType node) {

		Uniform uniform = URandomService.getURandomService().getNewUniformDistribution(
				URandomService.getURandomService().getGenerator(
						((String) PmParameterManager.getParameter(MRandomPa.RND_STREAM_RANDOM_NETWORK_BUILDING))));
		double p = 1.0 / (network.numNodes() - 1) *
				((Integer) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_RANDOM_AVG_DEGREE)).intValue();
		network.addNode(node);
		for (AgentType partner : network.getNodes()) {
			if (partner != node) {
				if (uniform.nextDouble() < p) {
					network.connect(node, partner);
				}
				if (uniform.nextDouble() < p
						&& (Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED)) {
					network.connect(partner, node);
				}
			}
		}
		return true;
	}
}
