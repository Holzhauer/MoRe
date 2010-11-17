/**
 * MORe
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.10.2010
 */
package de.cesr.more.io;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 05.10.2010 
 *
 */
public interface MoreEdgeFactory<V,E> {
	
	public E createEdge(V soruce, V target, boolean directed);

}
