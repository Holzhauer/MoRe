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
 * Created by Sascha Holzhauer on 03.01.2011
 */
package de.cesr.more.util;



import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.cesr.more.param.MSqlPa;
import de.cesr.parma.core.PmParameterManager;



/**
 * 
 * Write network data to database
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 03.01.2011
 * 
 */
public class MDbNetworkDataWriter {

	public static final boolean	WRITE_RUN_ID		= true;

	public static final boolean	WRITE_VERSION_ID	= true;

	protected String			externalVersion;

	protected String			network;

	protected int				paramId;

	protected MoreRunIdProvider	prov;

	/**
	 * Logger
	 */
	static private Logger		logger				= Log4jLogger.getLogger(MDbNetworkDataWriter.class);

	private final Map<String, String>	values;

	/**
	 * @param network network for which data is to be stored
	 * @param externalVersion simulation code version
	 * @param prov the provider of the current runID
	 */
	public MDbNetworkDataWriter(String network, String externalVersion, MoreRunIdProvider prov) {
		this.values = new HashMap<String, String>();
		this.network = network;
		this.externalVersion = externalVersion;
		this.prov = prov;
	}

	/**
	 * Store a network measure/data in order to write it to the database by
	 * {@link #writeData()}.
	 * @param column the column to store the data in
	 * @param value the data to store
	 */
	public void addValue(String column, String value) {
		values.put(column, value);
	}


	/**
	 * Finally writes the stored network measures to table ({@link MSqlPa#TBLNAME_NETWORK_MEASURES} the database
	 * ({@link MSqlPa#LOCATION}, {@link MSqlPa#DBNAME}, {@link MSqlPa#USER}, {@link MSqlPa#PASSWORD}).
	 */
	public void writeData() {
		this.writeData(true);
	}

	/**
	 * Finally writes the stored network measures to table ({@link MSqlPa#TBLNAME_NETWORK_MEASURES} the database (
	 * {@link MSqlPa#LOCATION}, {@link MSqlPa#DBNAME}, {@link MSqlPa#USER}, {@link MSqlPa#PASSWORD}).
	 */
	public void writeData(boolean closeConnection) {

		String t1 = (String) PmParameterManager.getParameter(MSqlPa.TBLNAME_NETWORK_MEASURES);

		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO `" + t1 + "` SET ");

		if (WRITE_RUN_ID) {
			sql.append("runID = " + prov.getRunId() + ", ");
		}

		if (WRITE_VERSION_ID) {
			sql.append("Version = 'MoRe: " + MVersionInfo.revisionNumber + "/" + MVersionInfo.timeStamp + " | "
					+ externalVersion + "', ");
		}

		sql.append("network = '" + network + "'");

		for (Entry<String, String> e : this.values.entrySet()) {
			sql.append(", `" + e.getKey() + "` = '" + (e.getValue().equals("NaN") ? "-1" : e.getValue()) + "'");
		}
		sql.append(";");
		logger.debug("SQL-statement to write data: " + sql);

		try {
			MMySqlService.connect(sql.toString());
		} catch (SQLException exception) {
			exception.printStackTrace();
		} catch (InstantiationException exception) {
			exception.printStackTrace();
		} catch (IllegalAccessException exception) {
			exception.printStackTrace();
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
}
