/**
 * KUBUS_Proto01
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 29.06.2010
 */
package de.cesr.more.param;

import de.cesr.more.util.param.ParameterDefinition;


/**
 * KUBUS_Proto01
 *
 * @author Sascha Holzhauer
 * @date 29.06.2010 
 *
 */
public enum MoreBasicPa implements ParameterDefinition {
	
	
	/**
	 * ID of used parameter set
	 */
	PARAMS_ID(Integer.class, 1),
	
	/**
	 * Random seed used for all random streams throughout the model
	 */
	RANDOM_SEED(Integer.class, 0),
	
	/**
	 * Location of XML file that specifies database settings:
	 */
	DB_SETTINGS_FILE(String.class, "./DBSettings.xml"), 
	
	LOCATION(String.class, "mysql2"),
	DBNAME(String.class, "holzhauer"),
	USER(String.class, "holzhauer"),
	PASSWORD(String.class, "ymxncbv"),
	
	TBLNAME_NETWORK_MEASURES(String.class, "kubus_sna2011_more_net_measures");
	
	private Class<?> type;
	private Object defaultValue;
	
	MoreBasicPa(Class type) {
		this(type, null);
	}

	MoreBasicPa(Class type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public Class<?> getType() {
		return type;
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
}
