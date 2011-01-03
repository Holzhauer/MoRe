/**
 * Parameter Framework
 * 
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 28.06.2010
 */
package de.cesr.more.util.param;

/**
 *
 * @author Sascha Holzhauer
 * @date 28.06.2010 
 *
 */
public interface ParameterDefinition {

	/**
	 * Return the type of this parameter
	 * @return the type of this parameter
	 * Created by Sascha Holzhauer on 28.06.2010
	 */
	public Class<?> getType();

	/**
	 * Returns the default value that is assigned to this parameter at definition
	 * @return the parameter's default value
	 * Created by Sascha Holzhauer on 28.06.2010
	 */
	public Object getDefaultValue();

}
