/**
 * Social Network Analysis and Visualisation Library
 * for RepastJ Models (SoNetA)
 * 
 * [see license.txt in the root directory of this library
 *  for additional important notes]
 *
 * @author Sascha Holzhauer
 * @date 10.07.2008
 * 
 */
package de.cesr.more.measures.node.supply;

import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.measures.MMeasureDescription;



/**
 * @author Sascha Holzhauer
 * @date 10.07.2008
 *
 */
public class MBasicNodeMeasureSupplier extends MAbstractMeasureSupplier {

	MMeasureDescription description;
	
	/**
	 * Adds other suppliers.
	 */
	public MBasicNodeMeasureSupplier() {
		
		this.addMeasureSupplier(new MCentralityNodeMSupplier());
		this.addMeasureSupplier(new MAvgNearestNeighbourDegree());
		
		// TODO adapt classes
//		this.addMeasureSupplier(new CentralityNormalizedNMSupplier());
//		this.addMeasureSupplier(new CentralityStandardizedNMSupplier());
//		
//		this.addMeasureSupplier(new PrestigeNetworkSupplier());
//		this.addMeasureSupplier(new AuthorityNMSupplier());
	}
}