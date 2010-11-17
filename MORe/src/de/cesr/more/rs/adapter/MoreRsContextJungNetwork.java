/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 12.10.2010
 */
package de.cesr.more.rs.adapter;



import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.ContextJungNetwork;
import repast.simphony.space.graph.EdgeCreator;
import repast.simphony.space.graph.JungNetwork;
import repast.simphony.space.graph.RepastEdge;
import de.cesr.more.basic.MNetworkManager;
import de.cesr.more.exception.IllegalValueTypeException;
import de.cesr.more.measures.MMeasureDescription;
import de.cesr.more.measures.network.MNetworkMeasureManager;
import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.networks.MoreRsNetwork;
import de.cesr.more.util.Log4jLogger;
import edu.uci.ics.jung.graph.Graph;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @param <AgentT>
 * @param <EdgeT>
 * @date 12.10.2010
 * 
 */
public class MoreRsContextJungNetwork<AgentT, EdgeT extends RepastEdge<AgentT>> extends ContextJungNetwork<AgentT>
		implements MoreRsNetwork<AgentT, EdgeT> {

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(MoreRsContextJungNetwork.class);

	private EdgeCreator<? extends RepastEdge, AgentT> edgeCreator = null;
	
	private MMeasureDescription mesaureDescA;
	private MMeasureDescription mesaureDescB;
	
	/**
	 * @param net
	 * @param context
	 */
	public MoreRsContextJungNetwork(JungNetwork<AgentT> net, Context<AgentT> context) {
		super(net, context);
	}

	@Override
	public void addNode(AgentT node) {
		this.addVertex(node);
	}

	@Override
	public void connect(AgentT source, AgentT target) {
		if (edgeCreator != null) {
			this.addEdge(edgeCreator.createEdge(source, target, this.isDirected(), 0.0));
		}
		else {
			this.addEdge(source, target);
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

	public void setEdgeFactory(EdgeCreator<? extends RepastEdge, AgentT> edgeCreator) {
		this.edgeCreator = edgeCreator;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EdgeT getEdge(AgentT source, AgentT target) {
		return (EdgeT) super.getEdge(source, target);
	}

	/**
	 * @see de.cesr.more.networks.MoreNetwork#getInstanceWithNewGraph(edu.uci.ics.jung.graph.Graph)
	 */
	@Override
	public MoreNetwork<AgentT, EdgeT> getInstanceWithNewGraph(Graph<AgentT, EdgeT> graph) {
		// TODO Auto-generated method stub
		return null;
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

}
