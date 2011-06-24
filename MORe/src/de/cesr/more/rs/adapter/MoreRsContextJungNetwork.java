/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 12.10.2010
 */
package de.cesr.more.rs.adapter;



import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.space.graph.DirectedJungNetwork;
import repast.simphony.space.graph.EdgeCreator;
import repast.simphony.space.graph.JungNetwork;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.graph.UndirectedJungNetwork;
import de.cesr.more.basic.MNetworkManager;
import de.cesr.more.basic.MoreEdge;
import de.cesr.more.exception.IllegalValueTypeException;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.network.MNetworkMeasureManager;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.networks.MoreRsNetwork;
import de.cesr.more.util.Log4jLogger;
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
public class MoreRsContextJungNetwork<AgentT, EdgeT extends RepastEdge<AgentT> & MoreEdge<AgentT>> extends ContextJungNetwork<AgentT>
		implements MoreRsNetwork<AgentT, EdgeT> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(MoreRsContextJungNetwork.class);
	
	protected Context context;
	protected JungNetwork network;

	private EdgeCreator<? extends EdgeT, AgentT> edgeCreator = null;
	
	private MMeasureDescription mesaureDescA;
	private MMeasureDescription mesaureDescB;
	
	/**
	 * @param net
	 * @param context
	 */
	public MoreRsContextJungNetwork(JungNetwork<AgentT> network, Context<AgentT> context) {
		super(network, context);
		this.context = context;
		this.network = network;
	}

	@Override
	public void addNode(AgentT node) {
		this.addVertex(node);
	}

	@Override
	public EdgeT connect(AgentT source, AgentT target) {
		if (edgeCreator != null) {
			EdgeT edge = edgeCreator.createEdge(source, target, this.isDirected(), 0.0);
			this.addEdge(edge);
			return edge;
		}
		else {
			EdgeT edge = (EdgeT) new MRepastEdge<AgentT>(source, target, this.isDirected());
			this.addEdge(edge);
			return edge;
		}
	}

	@Override
	public void disconnect(AgentT source, AgentT target) {
		this.removeEdge(this.getEdge(source, target));
	}

	@Override
	public double getWeight(AgentT source, AgentT target) {
		return this.getEdge(source, target).getWeight();
	}

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

	@Override
	public int numNodes() {
		return this.size();
	}

	@Override
	public void setWeight(AgentT source, AgentT target, double weight) {
		EdgeT edge = this.getEdge(source, target);
		if (edge == null) {
			logger.error("There is no edge between " + source + " and " + target + "!");
		}
		edge.setWeight(weight);
	}

	/**
	 * @see de.cesr.more.networks.MoreRsNetwork#setEdgeFactory(repast.simphony.space.graph.EdgeCreator)
	 */
	public void setEdgeFactory(EdgeCreator<? extends EdgeT, AgentT> edgeCreator) {
		this.edgeCreator = edgeCreator;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EdgeT getEdge(AgentT source, AgentT target) {
		return (EdgeT) super.getEdge(source, target);
	}


	
	
	/*************************************************
	 *  Accessing network measures by Repast Simphony
	 ************************************************/
	
	
	/**
	 * @see de.cesr.more.networks.MoreRsNetwork#getMeasureA()
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
	 * @see de.cesr.more.networks.MoreRsNetwork#getMeasureB()
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
	 * @see de.cesr.more.networks.MoreRsNetwork#setMeasureA(de.cesr.more.measures.MMeasureDescription)
	 */
	@Override
	public void setMeasureA(MMeasureDescription desc) throws IllegalValueTypeException{
		if (! (MNetworkMeasureManager.getInstance().findMeasure(desc).getType() == Double.class || 
				MNetworkMeasureManager.getInstance().findMeasure(desc).getType() == Float.class)) {
			throw new IllegalValueTypeException("Value type of " + desc + " is not of Double or Float!");
		}
		this.mesaureDescA = desc;
		logger.debug("Set measure A to " + desc);
	}

	/**
	 * @see de.cesr.more.networks.MoreRsNetwork#setMeasureB(de.cesr.more.measures.MMeasureDescription)
	 */
	@Override
	public void setMeasureB(MMeasureDescription desc)  throws IllegalValueTypeException {
		if (! (MNetworkMeasureManager.getInstance().findMeasure(desc).getType() == Double.class || 
				MNetworkMeasureManager.getInstance().findMeasure(desc).getType() == Float.class)) {
			throw new IllegalValueTypeException("Value type of " + desc + " is not of Double or Float!");
		}
		this.mesaureDescB = desc;	
		logger.debug("Set measure B to " + desc);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getGraphFilteredInstance(edu.uci.ics.jung.graph.Graph)
	 */
	@Override
	public MoreNetwork<AgentT, EdgeT> getGraphFilteredInstance(Graph<AgentT, EdgeT> graph, String newName) {
		JungNetwork<AgentT> jnetwork = 
				(this.isDirected() ? new DirectedJungNetwork<AgentT>(newName) : new UndirectedJungNetwork<AgentT>(
						newName));
		jnetwork.setGraph(((Graph<AgentT, RepastEdge<AgentT>>) graph));
		MoreNetwork<AgentT, EdgeT> out_net = new MoreRsContextJungNetwork<AgentT, EdgeT>(jnetwork, context);
		return out_net;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getJungGraph()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Graph<AgentT, EdgeT> getJungGraph() {
		return (Graph<AgentT, EdgeT>) super.getGraph();
	}
	
	/**
	 * @see repast.simphony.context.space.graph.ContextJungNetwork#toString()
	 */
	public String toString()  {
		return getName() + " (" + numNodes() + " nodes / " + numEdges() + " edges)";
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#reverseNetwork()
	 */
	@Override
	public void reverseNetwork() {
		if (this.isDirected()) {
			Collection<EdgeT> orgEdges = this.getEdgesCollection();
			for (EdgeT edge :orgEdges) {
				this.removeEdge(edge);
			}
			for (EdgeT edge :orgEdges) {
				
				if (edgeCreator != null) {
					this.addEdge(edgeCreator.createEdge(edge.getTarget(),  edge.getSource(), this.isDirected(), 0.0));
				}
				else {
					this.addEdge(new MRepastEdge<AgentT>(edge.getTarget(),  edge.getSource(), this.isDirected()));
				}
			}
		}
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getEdgesCollection()
	 */
	@Override
	public Collection<EdgeT> getEdgesCollection() {
		Collection<RepastEdge<AgentT>> edges = new HashSet<RepastEdge<AgentT>>();
		for (RepastEdge<AgentT> edge : this.getEdges())
			edges.add(edge);
		return (Collection<EdgeT>) edges;
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#addEdge(java.lang.Object)
	 */
	@Override
	public EdgeT addEdge(AgentT source, AgentT target) {
		return this.connect(source, target);
	}
	
	/**
	 * @see de.cesr.more.networks.MoreNetwork#addEdge(java.lang.Object)
	 */
	@Override
	public void addEdge(EdgeT edge) {
		super.addEdge(edge);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#removeNode(java.lang.Object)
	 */
	@Override
	public void removeNode(AgentT node) {
		network.removeVertex(node);
	}

}
