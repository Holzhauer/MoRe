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


import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.parma.definition.PmFrameworkPa;

/**
 * KUBUS_Proto01
 * 
 * @author Sascha Holzhauer
 * @date 29.06.2010
 * 
 */
public enum MSqlPa implements PmParameterDefinition {

	/**
	 * Location of XML file that specifies database settings:
	 */
	DB_SETTINGS_FILE(String.class, "./config/DBSettingsMore.xml"), 
	
	LOCATION(String.class, PmParameterManager.getParameter(PmFrameworkPa.LOCATION)),
	DBNAME(String.class, PmParameterManager.getParameter(PmFrameworkPa.DBNAME)),
	USER(String.class, PmParameterManager.getParameter(PmFrameworkPa.USER)),
	PASSWORD(String.class, PmParameterManager.getParameter(PmFrameworkPa.PASSWORD)),
	
	/**
	 * 
	 */
	TBLNAME_NETWORK_MEASURES(String.class, "more_net_measures"),

	
	/**
	 * 
	 */
	TBLNAME_MILIEU_GROUPS(String.class, "milieus_milieu_groups"),
	
	
	/**
	 * 
	 */
	TBLNAME_MILIEU_GROUPS_NAMES(String.class,
			"milieu_groups"),

	/**
	 * 
	 */
	TBLNAME_NET_PREFS(String.class, "more_netprefs"),
	
	
	/**
	 * 
	 */
	TBLNAME_NET_PREFS_LINKS(String.class, "more_netprefs_links");

	private Class < ? >	type;
	private Object		defaultValue;

	MSqlPa(Class < ? > type) {
		this(type, null);
	}

	MSqlPa(Class < ? > type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
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
