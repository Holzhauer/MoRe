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
package de.cesr.more.measures;

import java.util.Set;

import de.cesr.more.measures.measures.MoreMeasure;


/**
 * Interface for classes that provide {@link Measure}s
 * 
 * @author Sascha Holzhauer
 * @date 10.07.2008
 *
 */
public interface MoreMeasureSupplier {
	
	/**
	 * Registers another <code>NetworkMeasureSupplier</code> at the implementing supplier
	 * @date 14.08.2008
	 *
	 * @param supplier The <code>NetworkMeasureSupplier</code> to register
	 * @return true is adding was successful
	 */
	public boolean addMeasureSupplier(MoreMeasureSupplier supplier);
	
	/**
	 * Removes another <code>NetworkMeasureSupplier</code> at the implementing supplier
	 * @date 14.08.2008
	 *
	 * @param supplier The <code>NetworkMeasureSupplier</code> to register
	 * @return true if removing was successful 
	 */
	public boolean removeMeasureSupplier(MoreMeasureSupplier supplier);
	
	/**
	 * Returns {@link MeasureDescription} of the {@link Measure}s this supplier provides
	 * @date 14.08.2008
	 *
	 * @return A set of <code>MeasureDescriptions</code>
	 */
	public Set<MMeasureDescription> getMeasureDescriptions();
	
	/**
	 * Return the according {@link Measure} when this supplier or any children supports
	 * the given {@link MeasureDescription}, <code>null</code> otherwise.
	 * @date 14.08.2008
	 *
	 * @param describtion the {@link MeasureDescription} whose calculation providing {@link Measure} is demanded
	 * @return the {@link Measure} that belongs to the given {@link MeasureDescription}
	 */
	public MoreMeasure findMeasure(MMeasureDescription describtion);
	
	/**
	 * Return all {@link MeasureCategory}s this supplier provides {@link Measure}s for
	 * @date 14.08.2008
	 *
	 * @return {@link MeasureCategory}s this supplier provides {@link Measure}s for
	 */
	public Set<MoreMeasureCategory> getCategories();

}
