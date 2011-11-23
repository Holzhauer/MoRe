/**
 * 
 */
package de.cesr.more.building.network;

import java.util.Collection;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MDirectedNetwork;
import de.cesr.more.basic.network.MUndirectedNetwork;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.util.MLattice2DGenerator;
import de.cesr.more.manipulate.edge.MDefaultNetworkEdgeModifier;
import de.cesr.more.manipulate.edge.MoreNetworkEdgeModifier;
import de.cesr.more.param.MNetBuildLattice2DPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * 
 * <table>
 * <th>Parameter</th><th>Value</th>
 * <tr><td>#Vertices</td><td>N (via collection of agents; must be quadratic)</td></tr>
 * <th>Property</th><th>Value</th>
 * <tr><td>#Edges:</td><td>Toroidal & Directed: 4N</td></tr>
 * <tr><td></td><td></td></tr>
 * </table>
 * <br>
 * Considered {@link PmParameterDefinition}s:
 * <ul>
 * <li>{@link MNetworkBuildingPa.BUILD_DIRECTED}</li>
 * <li>{@link MNetBuildLattice2DPa.TOROIDAL}</li>
 * </ul>
 * 
 * @author Sascha Holzhauer
 *
 */
public class MLattice2DNetworkBuilder<AgentType, EdgeType extends MoreEdge<AgentType>>
		 implements MoreNetworkBuilder<AgentType, EdgeType> {
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(MLattice2DNetworkBuilder.class);
	
	protected MLattice2DGenerator<AgentType, EdgeType> latticeGenerator;
	
	protected MoreNetworkEdgeModifier<AgentType, EdgeType> edgeModifier;
	
	protected String name;
	
	/**
	 * Uses "Network" as name.
	 * @param eFac edge factory 
	 */
	public MLattice2DNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac) {
		this(eFac, "Network");
	}
	
	/**
	 * @param eFac
	 */
	public MLattice2DNetworkBuilder(MoreEdgeFactory<AgentType, EdgeType> eFac, String name) {
		this.name = name;
		this.edgeModifier = new MDefaultNetworkEdgeModifier<AgentType, EdgeType>(eFac);
		this.latticeGenerator = new MLattice2DGenerator<AgentType, EdgeType>(
				(Boolean)PmParameterManager.getParameter(MNetBuildLattice2DPa.TOROIDAL));
	}
	

	Context<AgentType> context;
	
	/**
	 * The order of elements in agents influences the lattice's characteristic.
	 * So, make sure to shuffle the collection unless you require a special grid!
	 * 
	 * Furthermore in this regard, make sure the network underlying graph is an ordered one
	 * (however, one that does not use HashMaps to store vertices), like
	 * UndirectedOrderedSparseMultigraph (RS's UndirectedJungNetworks and DirectedJungNetwork
	 * use such ordered graphs)!
	 * 
	 * @see de.cesr.more.rs.building.MoreRsNetworkBuilder#buildNetwork(java.util.Collection)
	 */
	@Override
	public MoreNetwork<AgentType, EdgeType> buildNetwork(
			Collection<AgentType> agents) {
		
		MoreNetwork<AgentType, EdgeType> network = (Boolean) PmParameterManager.getParameter(MNetworkBuildingPa.BUILD_DIRECTED) ?
				new MDirectedNetwork<AgentType, EdgeType>(edgeModifier.getEdgeFactory(), name) :
				new MUndirectedNetwork<AgentType, EdgeType>(edgeModifier.getEdgeFactory(), name);
		for (AgentType agent : agents) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Add agent " + agent + " to network.");
			}
			// LOGGING ->

			network.addNode(agent);
		}
		network = latticeGenerator.createNetwork(network, edgeModifier);
		return  network;
	}
}
