/**
 * Social Network Analysis and Visualization Library
 * for RepastJ Models (SoNetA)
 * 
 * [see license.txt in the root directory of this library
 *  for additional important notes]
 *
 * @author Sascha Holzhauer
 * @date 23.06.2008
 * 
 */
package de.cesr.more.measures;



import java.util.Map;

import javax.swing.JLabel;

import de.cesr.more.basic.MoreEdge;
import de.cesr.more.measures.node.MoreNodeMeasureSupport;
import de.cesr.more.networks.MoreNetwork;

import repast.simphony.context.space.graph.ContextJungNetwork;



/**
 * MORe
 * 
 * The class comprises the network, the measure description and parameter map.
 * 
 * @author Sascha Holzhauer
 * @date 15.11.2010
 * 
 */
public class MMeasureBundle<T extends MoreNodeMeasureSupport, E  extends MoreEdge> {
	private MoreNetwork<T, E>										network;
	private MMeasureDescription										measure;
	private Map<String, Object>										params;

	private JLabel													jLabel;

	protected MMeasureBundle() {
		this(null, null);
	}

	public MMeasureBundle(MoreNetwork<T, E> net, MMeasureDescription measure) {
		this(net, measure, null);
	}

	protected MMeasureBundle(MoreNetwork<T, E> net, MMeasureDescription measure,
			Map<String, Object> params) {
		this.measure = measure;
		this.network = net;
		this.params = params;
	}

	public MoreNetwork<? extends MoreNodeMeasureSupport, ?> getNetwork() {
		return network;
	}

	void setJLabel(JLabel label) {
		jLabel = label;
	}

	JLabel getLabel() {
		return jLabel;
	}

	void setNetwork(MoreNetwork<T, E> network) {
		this.network = network;
	}

	public MMeasureDescription getMeasure() {
		return measure;
	}

	void setMeasure(MMeasureDescription measure) {
		this.measure = measure;
	}

	@Override
	public String toString() {
		return measure.toString();
	}

	@Override
	public boolean equals(Object bundle) {
		if (bundle instanceof MMeasureBundle) {
			return (this.network.equals(((MMeasureBundle) bundle).network) && this.measure
					.equals(((MMeasureBundle) bundle).measure));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return network.hashCode() + measure.getShort().hashCode();
	}

	/**
	 * @return the params
	 */
	public Map<String, Object> getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
}