/**
 * Center for Environmental Systems Research, Kassel
 * Created by Holzhauer on 08.01.2009
 */
package de.cesr.more.util.param;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.cesr.more.util.Log4jLogger;



/**
 * 
 * Defines an interface for classes that provide parameter values. Also defines all parameters used throughout the model.
 * See ParametrFramework_Documentation.doc for further information!
 * 
 * @author Holzhauer
 * @date 08.01.2009
 * 
 */
public class MParameterManager extends AbstractParameterReader {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MParameterManager.class);
	
	static Map<ParameterDefinition, Object>	params		= new HashMap<ParameterDefinition, Object>();
	static ArrayList<ParameterDefinition>		definitions	= new ArrayList<ParameterDefinition>();
	static MParameterManager paraManager;

	/**
	 * Registers {@link ParameterDefinition}s to this manager.
	 * Usually, parameters are defined by an enumerations that extends {@link ParameterDefinition}
	 * 
	 * Created by Holzhauer on 08.01.2009
	 * @param definitions 
	 */
	public static void registerParametersDefinitions(Collection<? extends ParameterDefinition> definitions) {
		for (ParameterDefinition definition : definitions) {
			MParameterManager.definitions.add(definition);
		}
	}
	
	/**
	 * Registers {@link ParameterDefinition}s to this manager.
	 * Usually, parameters are defined by an enumerations that extends {@link ParameterDefinition}
	 * 
	 * @param definitions
	 * Created by Sascha Holzhauer on 30.07.2010
	 */
	public static void registerParametersDefinitions(ParameterDefinition[] definitions) {
		for (ParameterDefinition definition : definitions) {
			MParameterManager.definitions.add(definition);
		}
	}
	

	/**
	 * Get any registered parameter
	 * 
	 * @param parameter the {@link ParameterDefinition} whose value is requested
	 * @return the parameter's current value
	 * 
	 *         Created by Holzhauer on 08.01.2009
	 */
	public static Object getParameter(ParameterDefinition parameter) {
		if (params.containsKey(parameter)) {
			return params.get(parameter);
		} else {
			return parameter.getDefaultValue();
		}
	}
	
	/**
	 * @param definition
	 * @param value
	 * Created by Sascha Holzhauer on 29.06.2010
	 */
	public static void setParameter(ParameterDefinition definition, Object value) {
		
		if (! definition.getType().isInstance(value)) {
			logger.warn("The given value is not assignable to the type specified in the parameter definition!");
		}
		params.put(definition, value);
	}
	
	/**
	 * 
	 * Created by Sascha Holzhauer on 15.09.2010
	 */
	public static void init() {
		if (paraManager == null) {
			paraManager = new MParameterManager();
		}
		paraManager.initParameters();
	}
	
	public static void registerReader(ParameterReader reader) {
		if (paraManager == null) {
			paraManager = new MParameterManager();
		}
		paraManager.registerParameterReader(reader);
	}
	
	/**
	 * Set every field to null
	 * Created by Sascha Holzhauer on 30.06.2010
	 */
	public static void reset() {
		paraManager = null;
		params = null;
		definitions = null;
	}
}
