/**
 * Social Network Analysis and Visualization Library
 * for RepastJ Models (SoNetA)
 * 
 * [see license.txt in the root directory of this library
 *  for additional important notes]
 *
 * @author Sascha Holzhauer
 * @date 10.07.2008
 * 
 */
package de.cesr.more.measures.network.supply;

import de.cesr.more.measures.MAbstractMeasureSupplier;
import de.cesr.more.measures.MMeasureDescription;



/**
 * @author Sascha Holzhauer
 * @date 10.07.2008
 *
 */
public class MBasicNetworkMeasureSupplier extends MAbstractMeasureSupplier {

	MMeasureDescription description;
	
	public MBasicNetworkMeasureSupplier() {
		
		this.addMeasureSupplier(MCcNetworkMeasureSupplier.getInstance());
		// TODO adapt classes
		this.addMeasureSupplier(MCentralityNetMSupplier.getInstance());
//		this.addMeasureSupplier(new CentralityStandardizedNMSupplier());
//		
//		this.addMeasureSupplier(new PrestigeNetworkSupplier());
//		this.addMeasureSupplier(new AuthorityNMSupplier());
	}
}