/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 15.01.2010
 */
package de.cesr.more.lara;

/** 
 * @author Sascha Holzhauer
 * @date 15.01.2010
 */
public interface ComboundNetworkInfo {
	
	/**
	 * @param value
	 */
	public void setValue(double value);
	
	/**
	 * @return value
	 */
	public double getValue();
	
	/**
	 * @return name
	 */
	public String getName();
	
	/**
	 * @return reach
	 */
	public int getReach();
}
