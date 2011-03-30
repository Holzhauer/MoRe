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



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.cesr.more.networks.MoreNetwork;
import de.cesr.more.param.MoreBasicPa;
import de.cesr.more.util.param.MParameterManager;



/**
 * MORe
 * 
 * @author Sascha Holzhauer
 * @date 03.01.2011
 * 
 */
public class MDbWriter {

	public static final boolean	WRITE_RUN_ID		= true;

	public static final boolean	WRITE_VERSION_ID	= true;

	protected String			externalVersion;

	protected MoreNetwork		network;

	protected int				paramId;

	protected MoreRunIdProvider	prov;

	/**
	 * Logger
	 */
	static private Logger		logger				= Log4jLogger.getLogger(MDbWriter.class);

	private Map<String, String>	values;

	private Connection			con;

	public MDbWriter(MoreNetwork network, String externalVersion, int paramId, MoreRunIdProvider prov) {
		this.values = new HashMap<String, String>();
		this.network = network;
		this.externalVersion = externalVersion;
		this.paramId = paramId;
		this.prov = prov;
	}

	public void addValue(String column, String value) {
		values.put(column, value);
	}

	/**
	 * @see param.framework.ParameterReader#initParameters()
	 */
	public void writeData() {

		String t1 = (String) MParameterManager.getParameter(MoreBasicPa.TBLNAME_NETWORK_MEASURES);

		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO `" + t1 + "` SET ");

		if (WRITE_RUN_ID) {
			sql.append("runID = " + prov.getRunId() + ", ");
		}

		if (WRITE_VERSION_ID) {
			sql.append("Version = 'MoRe: " + MVersionInfo.revisionNumber + "/" + MVersionInfo.timeStamp + " | "
					+ externalVersion + "', ");
		}

		sql.append("network = '" + network.getName() + "'");

		for (Entry<String, String> e : this.values.entrySet()) {
			sql.append(", `" + e.getKey() + "` = '" + (e.getValue().equals("NaN") ? "-1" : e.getValue()) + "'");
		}
		sql.append(";");
		logger.debug("SQL-statement to fetch params: " + sql);

		connect(sql.toString());
		disconnect();
	}

	/**
	 * @param sql
	 * @return Created by Sascha Holzhauer on 29.03.2010
	 */
	protected boolean connect(String sql) {
		try {
			if (con == null || con.isClosed()) {
				con = getConnection();
			}

			if (sql.startsWith("update:")) {
				con.createStatement().executeUpdate(sql.substring(7));
				return true;
			} else {
				return con.createStatement().execute(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Tries to establish a JDBC Connection to the MySQL database given the settings provided to the constructor.
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	protected Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			SQLException {

		Properties properties = new Properties();
		properties.put("user", MParameterManager.getParameter(MoreBasicPa.USER));
		properties.put("password", MParameterManager.getParameter(MoreBasicPa.PASSWORD));
		String connectTo = "jdbc:mysql://" + MParameterManager.getParameter(MoreBasicPa.LOCATION)
				+ (((String) MParameterManager.getParameter(MoreBasicPa.LOCATION)).endsWith("/") ? "" : "/")
				+ MParameterManager.getParameter(MoreBasicPa.DBNAME);

		// error handling:
		if (MParameterManager.getParameter(MoreBasicPa.LOCATION) == null) {
			throw new IllegalArgumentException("Invalid database settings: Invalid database location!");
		}
		if (MParameterManager.getParameter(MoreBasicPa.DBNAME) == null) {
			throw new IllegalArgumentException("Invalid database settings: Invalid database name!");
		}
		if (MParameterManager.getParameter(MoreBasicPa.USER) == null) {
			throw new IllegalArgumentException("Invalid database settings: Invalid user name!");
		}
		if (MParameterManager.getParameter(MoreBasicPa.PASSWORD) == null) {
			throw new IllegalArgumentException("Invalid database settings: Invalid password!");
		}

		Class.forName("com.mysql.jdbc.Driver").newInstance();
		logger.info("Connect to DB: " + connectTo);
		return DriverManager.getConnection(connectTo, properties);
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 29.03.2010
	 */
	protected void disconnect() {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Connection (" + con + ") closing...");
		}
		// LOGGING ->

		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
