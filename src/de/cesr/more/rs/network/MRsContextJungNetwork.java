/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 12.10.2010
 */
package de.cesr.more.rs.network;



import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.JungNetwork;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.basic.MNetworkManager;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MNetworkBuilderNotSpecified;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.building.edge.MDefaultEdgeFactory;
import de.cesr.more.building.edge.MoreEdgeFactory;
import de.cesr.more.building.network.MoreNetworkBuilder;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.network.MNetworkMeasureManager;
import de.cesr.more.measures.network.supply.algos.MNetworkStatisticsR;
import de.cesr.more.rs.building.edge.MRsEdgeFactory;
import de.cesr.more.rs.edge.MRepastEdge;
import de.cesr.more.util.Log4jLogger;
import de.cesr.more.util.exception.MIllegalValueTypeException;
import edu.uci.ics.jung.graph.Graph;



/**
 * MORe
 * Network Adapter for Repast Simphony models.
 * Extends a ContextJungNetwork (required to be used within Repast Simphony).
 * 
 * @author Sascha Holzhauer
 * @param <AgentT>
 * @param <EdgeT>
 * @date 12.10.2010
 * 
 */
public class MRsContextJungNetwork<AgentT, EdgeT extends RepastEdge<AgentT> & MoreEdge<AgentT>> extends
		ContextJungNetwork<AgentT>
		implements MoreRsNetwork<AgentT, EdgeT> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(MRsContextJungNetwork.class);
	
	protected Context<AgentT> context;
	protected JungNetwork<AgentT> network;
	
	protected MoreEdgeFactory<AgentT, EdgeT> edgeFac;
	
	private MMeasureDescription mesaureDescA;
	private MMeasureDescription mesaureDescB;
	
	protected Class<? extends MoreNetworkBuilder<?, ?>>	networkBuilderClass	= MNetworkBuilderNotSpecified.class;

	/**
	 * @param net
	 * @param context
	 */
	public MRsContextJungNetwork(JungNetwork<AgentT> network, Context<AgentT> context) {
		this(network, context, null);
	}

	/**
	 * @param net
	 * @param context
	 */
	public MRsContextJungNetwork(JungNetwork<AgentT> network, Context<AgentT> context, MoreEdgeFactory<AgentT, EdgeT> edgeFac) {
		super(network, context);
		this.context = context;
		this.network = network;
		this.edgeFac = edgeFac;
	}
	
	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#addNode(java.lang.Object)
	 */
	@Override
	public void addNode(AgentT node) {
		this.addVertex(node);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#connect(java.lang.Object, java.lang.Object)
	 * Tries to use the {@link MDefaultGeoEdgeFactory} in case no {@link MoreEdgeFactory} has been
	 * set before. This fails if EdgeT is not {@link MoreEdge<AgentT}.
	 */
	@Override
	public EdgeT connect(AgentT source, AgentT target) {
		if (edgeFac == null) {
			this.edgeFac = new MRsEdgeFactory<AgentT, EdgeT>();
		}
		EdgeT edge = this.edgeFac.createEdge(source, target, isDirected());
		this.connect(edge);
		return edge;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#disconnect(java.lang.Object, java.lang.Object)
	 */
	@Override
	public EdgeT disconnect(AgentT source, AgentT target) {
		EdgeT edge = this.getEdge(source, target);
		this.removeEdge(edge);
		return edge;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getWeight(java.lang.Object, java.lang.Object)
	 */
	@Override
	public double getWeight(AgentT source, AgentT target) {
		return this.getEdge(source, target).getWeight();
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#normalizeWeights()
	 */
	@Override
	public void normalizeWeights() {
		double maxWeight = 0;
		double current = 0;
		for (RepastEdge<AgentT> edge : this.getEdges()) {
			current = Math.abs(edge.getWeight());
			if (maxWeight < current) {
				maxWeight = edge.getWeight();
			}
		}
		for (RepastEdge<AgentT> edge : this.getEdges()) {
			edge.setWeight(edge.getWeight() / maxWeight);
		}
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#numNodes()
	 */
	@Override
	public int numNodes() {
		return this.size();
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#setWeight(java.lang.Object, java.lang.Object, double)
	 */
	@Override
	public void setWeight(AgentT source, AgentT target, double weight) {
		EdgeT edge = this.getEdge(source, target);
		if (edge == null) {
			logger.error("There is no edge between " + source + " and " + target + "!");
		}
		edge.setWeight(weight);
	}

	/**
	 * @see repast.simphony.context.space.graph.ContextJungNetwork#getEdge(java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EdgeT getEdge(AgentT source, AgentT target) {
		return (EdgeT) super.getEdge(source, target);
	}


	
	/*************************************************
	 *  Accessing network measures by Repast Simphony
	 ************************************************/
	
	/**
	 * @see de.cesr.more.rs.network.MoreRsNetwork#getMeasureA()
	 */
	@Override
	public double getMeasureA() {
		Object value = MNetworkManager.getNetworkMeasure(this, mesaureDescA);
		if (value instanceof Double) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Requested measure A: " + ((Double) value).doubleValue());
			}
			// LOGGING ->
			
			return ((Double) value).doubleValue();
		}
		if (value instanceof Float) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Requested measure A: " + ((Float) value).doubleValue());
			}
			// LOGGING ->
			
			return ((Float) value).doubleValue();
		}
		assert false;
		return Double.NaN;
	}

	/**
	 * @see de.cesr.more.rs.network.MoreRsNetwork#getMeasureB()
	 */
	@Override
	public double getMeasureB() {
		Object value = MNetworkManager.getNetworkMeasure(this, mesaureDescB);
		if (value instanceof Double) {
			
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Requested measure B: " + ((Double) value).doubleValue());
			}
			// LOGGING ->

			return ((Double) value).doubleValue();
		}
		if (value instanceof Float) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Requested measure B: " + ((Float) value).doubleValue());
			}
			// LOGGING ->
			
			return ((Float) value).doubleValue();
		}
		assert false;
		return Double.NaN;
	}

	/**
	 * @see de.cesr.more.rs.network.MoreRsNetwork#setMeasureA(de.cesr.more.measures.MMeasureDescription)
	 */
	@Override
	public void setMeasureA(MMeasureDescription desc) throws MIllegalValueTypeException{
		if (! (MNetworkMeasureManager.getInstance().findMeasure(desc).getType() == Double.class || 
				MNetworkMeasureManager.getInstance().findMeasure(desc).getType() == Float.class)) {
			throw new MIllegalValueTypeException("Value type of " + desc + " is not of Double or Float!");
		}
		this.mesaureDescA = desc;
		logger.debug("Set measure A to " + desc);
	}

	/**
	 * @see de.cesr.more.rs.network.MoreRsNetwork#setMeasureB(de.cesr.more.measures.MMeasureDescription)
	 */
	@Override
	public void setMeasureB(MMeasureDescription desc)  throws MIllegalValueTypeException {
		if (! (MNetworkMeasureManager.getInstance().findMeasure(desc).getType() == Double.class || 
				MNetworkMeasureManager.getInstance().findMeasure(desc).getType() == Float.class)) {
			throw new MIllegalValueTypeException("Value type of " + desc + " is not of Double or Float!");
		}
		this.mesaureDescB = desc;	
		logger.debug("Set measure B to " + desc);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getGraphFilteredInstance(edu.uci.ics.jung.graph.Graph)
	 */
	@SuppressWarnings("unchecked")  // ContextJungNetworks do not support edge parameters
	@Override
	public MoreNetwork<AgentT, EdgeT> getGraphFilteredInstance(Graph<AgentT, EdgeT> graph, String newName) {
		JungNetwork<AgentT> jnetwork = 
				(this.isDirected() ? new DirectedJungNetwork<AgentT>(newName) : new UndirectedJungNetwork<AgentT>(
						newName));
		jnetwork.setGraph(((Graph<AgentT, RepastEdge<AgentT>>) graph));
		MoreNetwork<AgentT, EdgeT> out_net = new MRsContextJungNetwork<AgentT, EdgeT>(jnetwork, context);
		return out_net;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getJungGraph()
	 */
	@SuppressWarnings("unchecked")  // ContextJungNetworks do not support edge parameters
	@Override
	public Graph<AgentT, EdgeT> getJungGraph() {
		return (Graph<AgentT, EdgeT>) super.getGraph();
	}
	
	/**
	 * @see repast.simphony.context.space.graph.ContextJungNetwork#toString()
	 */
	@Override
	public String toString()  {
		return getName() + " (" + numNodes() + " nodes / " + numEdges() + " edges)";
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#reverseNetwork()
	 * Tries to use the {@link MDefaultGeoEdgeFactory} in case no {@link MoreEdgeFactory} has been
	 * set before. This fails if EdgeT is not {@link MoreEdge<AgentT}.
	 */
	@Override
	public void reverseNetwork() {
		if (this.isDirected()) {
			Collection<EdgeT> orgEdges = this.getEdgesCollection();
			for (EdgeT edge :orgEdges) {
				this.removeEdge(edge);
			}
			if (edgeFac == null) {
				this.edgeFac = new MRsEdgeFactory<AgentT, EdgeT>();
			}
			for (EdgeT edge :orgEdges) {
				this.addEdge(edgeFac.createEdge(edge.getTarget(),  edge.getSource(), this.isDirected()));
			}
		}
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getEdgesCollection()
	 */
	@SuppressWarnings("unchecked") // ContextJungNetworks do not support edge parameters
	@Override
	public Collection<EdgeT> getEdgesCollection() {
		Collection<MRepastEdge<AgentT>> edges = new HashSet<MRepastEdge<AgentT>>();
		for (RepastEdge<AgentT> edge : this.getEdges()) {
			edges.add((MRepastEdge<AgentT>) edge);
		}
		return (Collection<EdgeT>) edges;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#connect(java.lang.Object)
	 */
	@Override
	public EdgeT addEdge(AgentT source, AgentT target) {
		return this.connect(source, target);
	}
	
	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#connect(java.lang.Object)
	 */
	@Override
	public void connect(EdgeT edge) {
		super.addEdge(edge);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#removeNode(java.lang.Object)
	 */
	@Override
	public void removeNode(AgentT node) {
		network.removeVertex(node);
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#setEdgeFactory(de.cesr.more.building.edge.MoreEdgeFactory)
	 */
	@Override
	public void setEdgeFactory(MoreEdgeFactory<AgentT, EdgeT> edgeFac) {
		this.edgeFac = edgeFac;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#containsNode(java.lang.Object)
	 */
	@Override
	public boolean containsNode(AgentT node) {
		for (AgentT n : network.getNodes())  {
			if (n.equals(node)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getNetworkBuilderClass()
	 */
	@Override
	public Class<? extends MoreNetworkBuilder<?, ?>> getNetworkBuilderClass() {
		return this.networkBuilderClass;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#setNetworkBuilderClass(java.lang.Class)
	 */
	@Override
	public void setNetworkBuilderClass(Class<? extends MoreNetworkBuilder<?, ?>> builderClass) {
		this.networkBuilderClass = builderClass;
	}

	/**
	 * @see de.cesr.more.basic.network.MoreNetwork#getNetworkInfo()
	 */
	@Override
	public String getNetworkInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Network " + this.getName() + ":" + System.getProperty("line.separator"));
		buffer.append("Number of Nodes: " + this.numNodes() + System.getProperty("line.separator"));
		buffer.append("Number of Edges: " + this.numEdges() + System.getProperty("line.separator"));
		buffer.append("Directedness: " + (this.isDirected() ? " Directed" : "Undirected")
				+ System.getProperty("line.separator"));
		buffer.append("APL: " + MNetworkStatisticsR.getAveragepathLengthR(this.getJungGraph(), false));
		return buffer.toString();
	}
}