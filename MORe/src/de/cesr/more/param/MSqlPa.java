/**
 * This file is part of
 * 
 * MORe - Managing Ongoing Relationships
 *
 * Copyright (C) 2010 Center for Environmental Systems Research, Kassel, Germany
 * 
 * MORe - Managing Ongoing Relationships is free software: You can redistribute 
 * it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *  
 * MORe - Managing Ongoing Relationships is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 29.06.2010
 */
package de.cesr.more.param;


import de.cesr.more.util.MDbNetworkDataWriter;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.parma.definition.PmFrameworkPa;

/**
 * Definition of database related parameters for MoRe
 * 
 * @author Sascha Holzhauer
 * @date 29.06.2010
 * 
 */
public enum MSqlPa implements PmParameterDefinition {
	
	/**
	 * Database location used to write data to. Default is {@link PmFrameworkPa#LOCATION}
	 */
	LOCATION(String.class, PmFrameworkPa.LOCATION),
	
	/**
	 * Database name used to write data to. Default is {@link PmFrameworkPa#DBNAME}
	 */
	DBNAME(String.class, PmFrameworkPa.DBNAME),
	
	/**
	 * Database user name used to write data. Default is {@link PmFrameworkPa#USER}
	 */
	USER(String.class, PmFrameworkPa.USER),
	
	/**
	 * (unencrypted) password used to write data. Default is {@link PmFrameworkPa#PASSWORD}
	 */
	PASSWORD(String.class, PmFrameworkPa.PASSWORD),
	
	/**
	 * Table where MoRe stores <i>network</i> measures. See {@link MDbNetworkDataWriter}.
	 * Default: <code>more_net_measures</code>
	 */
	TBLNAME_NETWORK_MEASURES(String.class, "more_net_measures"),

	/**
	 * Table from which agent network preferences are retrieved
	 * by {@link MMilieuNetDataReader}. Default: <code>more_netprefs</code>
	 */
	TBLNAME_NET_PREFS(String.class, "more_netprefs"),
	
	
	/**
	 * Table from which agent network link preferences are retrieved
	 * by {@link MMilieuNetDataReader}.
	 * Default: <code>more_netprefs_links</code>
	 */
	TBLNAME_NET_PREFS_LINKS(String.class, "more_netprefs_links"),
	
	/**
	 * Location of example XML file that specifies database settings for writing data.
	 * The reading of these file needs to be issued by the user using ParMa.
	 * Default: <code>./config/DBSettingsMore.xml"</code>
	 */
	DB_SETTINGS_EXAMPLE_FILE(String.class, "./config/DBSettingsMore.xml");
	

	private Class < ? >	type;
	private Object		defaultValue;

	MSqlPa(Class < ? > type) {
		this(type, null);
	}

	MSqlPa(Class < ? > type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	MSqlPa(Class<?> type, PmParameterDefinition defaultDefinition) {
		this.type = type;
		if (defaultDefinition != null) {
			this.defaultValue = defaultDefinition.getDefaultValue();
			PmParameterManager.setDefaultParameterDef(this, defaultDefinition);
		} else {
			this.defaultValue = null;
		}
	}

	@Override
	public Class < ? > getType() {
		return type;
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}
}
