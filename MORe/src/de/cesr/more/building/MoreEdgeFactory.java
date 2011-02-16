/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.10.2010
 */
package de.cesr.more.building;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 05.10.2010 
 *
 */
public interface MoreEdgeFactory<V,E> {
	
	public E createEdge(V source, V target, boolean directed);

}
