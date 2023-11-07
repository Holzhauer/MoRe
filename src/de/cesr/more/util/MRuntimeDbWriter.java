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
 * 
 * Created by Sascha Holzhauer on 11.02.2014
 */
package de.cesr.more.util;


import java.sql.SQLException;
import java.util.Map.Entry;

import org.apache.commons.collections15.map.LinkedMap;
import org.apache.log4j.Logger;

import de.cesr.more.param.MSqlPa;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 * 
 * Stores runtime information and writes these to the DB defined in params in {@link MSqlPa} when requested. This helps
 * to avoid disturbances of runtime measurement during DB writing.
 * 
 * @author Sascha Holzhauer
 * @date 11.02.2014
 * 
 */
public class MRuntimeDbWriter {

	/**
	 * Logger
	 */
	static private Logger		logger				= Logger.getLogger(MRuntimeDbWriter.class);

	public static final boolean	WRITE_RUN_ID		= true;

	public static final boolean	WRITE_VERSION_ID	= true;
	
	public static final String START	= "Start";

	protected LinkedMap<String, Long>	measurements	= new LinkedMap<String, Long>();

	protected MoreRunIdProvider	prov;

	public MRuntimeDbWriter(MoreRunIdProvider prov) {
		this.prov = prov;
	}

	public void start() {
		this.measurements.put(START, System.currentTimeMillis());
	}

	public void addMeasurement(String action) {
		measurements.put(action, new Long(System.currentTimeMillis()));
	}

	public void stopAndStore() {
		String t1 = (String) PmParameterManager.getParameter(MSqlPa.TBLNAME_RUNTIME_INFO);

		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO `" + t1 + "` (");

		if (WRITE_RUN_ID) {
			sql.append("runID, ");
		}
		sql.append("action, time) ");

		sql.append(" VALUES ");
		long previous = Long.MAX_VALUE;
		for (Entry<String, Long> e : this.measurements.entrySet()) {
			if (e.getKey() != START) {
				sql.append("(");
				if (WRITE_RUN_ID) {
					sql.append(prov.getRunId() + ", ");
				}
				sql.append("'" + e.getKey() + "', " + (e.getValue() - previous) + "),");
			}
			previous = e.getValue();
		}
		String sqlString = sql.substring(0, sql.length() - 1);
		sqlString = sqlString + ";";

		logger.debug("SQL-statement to write data: " + sqlString);

		try {
			MMySqlService.connect(sqlString);
		} catch (SQLException exception) {
			exception.printStackTrace();
		} catch (InstantiationException exception) {
			exception.printStackTrace();
		} catch (IllegalAccessException exception) {
			exception.printStackTrace();
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();
		}
		MMySqlService.disconnect();
	}
}
