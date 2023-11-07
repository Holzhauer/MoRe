/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.10.2010
 */
package de.cesr.more.building.edge;

/**
 * MORe
 * 
 * 
 *
 * @author Sascha Holzhauer
 * @date 05.10.2010 
 *
 */
public interface MoreEdgeFactory<V,E> {
	
	/**
	 * Creates a new edge from source node to target node.
	 * 
	 * @param source
	 * @param target
	 * @param directed
	 * @return a new edge
	 */
	public E createEdge(V source, V target, boolean directed);

}
