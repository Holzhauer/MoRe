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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.cesr.more.util.Log4jLogger;


/**
 * Provides a basic implementation of {@link MoreMeasureSupplier}
 * 
 * @author Sascha Holzhauer
 * @date 10.07.2008
 */
public abstract class MAbstractMeasureSupplier implements MoreMeasureSupplier{

	/**
	 * A map that contains {@link MeasureDescription} and their {@link Measure}s
	 */
	protected Map<MMeasureDescription, MoreMeasure> measures;
	
	/**
	 * Set that hold all {@link MeasureCategory} this supplier supports
	 */
	protected Set<MoreMeasureCategory> categories;
	
	/**
	 * A Set that stores all children
	 */
	protected Set<MoreMeasureSupplier> suppliers;
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MAbstractMeasureSupplier.class);
	
	/**
	 * Basic constructor that initializes the map and sets
	 */
	public MAbstractMeasureSupplier() {
		measures = new HashMap<MMeasureDescription, MoreMeasure>();
		categories = new HashSet<MoreMeasureCategory>();
		suppliers = new HashSet<MoreMeasureSupplier>();
	}
	
	/**
	 * @see edu.MoreMeasureSupplier.sh.soneta.measures.supply.NetworkMeasureSupplier#addMeasureSupplier(edu.MoreMeasureSupplier.sh.soneta.measures.supply.NetworkMeasureSupplier)
	 */
	@Override
	public boolean addMeasureSupplier(MoreMeasureSupplier supplier) {
		return suppliers.add(supplier);
	}
	
	/**
	 * @see edu.MoreMeasureSupplier.sh.soneta.measures.supply.NetworkMeasureSupplier#removeMeasureSupplier(edu.MoreMeasureSupplier.sh.soneta.measures.supply.NetworkMeasureSupplier)
	 */
	@Override
	public boolean removeMeasureSupplier(MoreMeasureSupplier supplier) {
		return suppliers.remove(supplier);
	}
	
	/**
	 * @see edu.MoreMeasureSupplier.sh.soneta.measures.supply.NetworkMeasureSupplier#getMeasureDescriptions()
	 */
	@Override
	public Set<MMeasureDescription> getMeasureDescriptions() {
		Set<MMeasureDescription> descriptions = new HashSet<MMeasureDescription>();
		descriptions.addAll(measures.keySet());
		for (MoreMeasureSupplier supplier : suppliers) {
			descriptions.addAll(supplier.getMeasureDescriptions());
		}
		return descriptions;
	}
	
	/**
	 * Delegate Pattern / Chain of Responsibility:
	 * Searches for a {@link MoreMeasure} according to the given {@link MMeasureDescription} in the chain of {@link MoreMeasureSupplier}s.
	 * @see edu.MoreMeasureSupplier.sh.soneta.measures.supply.NetworkMeasureSupplier#findMeasure(edu.uos.sh.soneta.measures.NetworkMeasureUtilities.MeasureDescriptionTemp)
	 */
	@Override
	public MoreMeasure findMeasure(MMeasureDescription description){
		if (measures.containsKey(description)) {
			return measures.get(description);
		}
		else {
			for (MoreMeasureSupplier supplier : suppliers) {
				MoreMeasure measure = supplier.findMeasure(description);
				if (measure != null) {
					return measure;
				}
			}
		}
		return null;
	}
	
	/**
	 * @see edu.MoreMeasureSupplier.sh.soneta.measures.supply.NetworkMeasureSupplier#getCategories()
	 */
	@Override
	public Set<MoreMeasureCategory> getCategories() {
		HashSet<MoreMeasureCategory> categories = new HashSet<MoreMeasureCategory>();
		for (MoreMeasureSupplier supplier : suppliers) {
			categories.addAll(supplier.getCategories());
		}
		return categories;
	}
	
	/**
	 * Uses the Classes of the objects to compare and calls the classes' equals.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return this.getClass().equals(o.getClass());
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}
}