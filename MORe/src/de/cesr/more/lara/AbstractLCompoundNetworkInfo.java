/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 15.01.2010
 */
package de.cesr.more.lara;

import de.cesr.more.lara.ComboundNetworkInfo;



/**
 * @author Sascha Holzhauer
 * @date 15.01.2010
 */
public class AbstractLCompoundNetworkInfo implements ComboundNetworkInfo {

	
	/**
	 * @param name
	 * @param reach
	 */
	public AbstractLCompoundNetworkInfo(String name, int reach) {
		this.propertyName = name;
		this.reach = reach;
	}
	
	private int		reach;

	private String	propertyName;

	private double	value;

	/**
	 * Getter of the property <tt>reach</tt>
	 */
	public int getReach() {
		return reach;
	}

	/**
	 * Setter of the property <tt>reach</tt>
	 * @param reach 
	 */
	public void setReach(int reach) {
		this.reach = reach;
	}

	/**
	 * Getter of the property <tt>propertyName</tt>
	 * @return property name
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Setter of the property <tt>propertyName</tt>
	 * 
	 * @param propertyName
	 *            The propertyName to set.
	 * @uml.property name="propertyName"
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String getName() {
		return propertyName;
	}

}
