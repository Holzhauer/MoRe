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
	
	/**
	 * Inits the parameter values of this reader.
	 * Created by Sascha Holzhauer on 06.05.2011
	 */
	public void initParameters();
	
	/**
	 * Registers further {@link ParameterReader}s at this reader.
	 * @param reader to register.
	 * Created by Sascha Holzhauer on 06.05.2011
	 */
	public void registerParameterReader(ParameterReader reader);

}
