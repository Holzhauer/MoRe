/**
 * KUBUS_Proto01
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 29.06.2010
 */
package de.cesr.more.util.param;

/**
 * KUBUS_Proto01
 *
 * @author Sascha Holzhauer
 * @date 29.06.2010 
 *
 */
public interface ParameterReader {
	
	public void initParameters();
	
	public void registerParameterReader(ParameterReader reader);

}
