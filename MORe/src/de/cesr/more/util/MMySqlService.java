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
 * Created by Sascha Holzhauer on 14.08.2012
 */
package de.cesr.more.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.cesr.more.param.MSqlPa;
import de.cesr.parma.core.PmParameterManager;


public class MMySqlService {
	
	/**
	 * Logger
	 */
	static private Logger			logger	= Logger.getLogger(MMySqlService.class);

	private static Connection		con;
	
	/**
	 * The instance of this class
	 */
	protected static MMySqlService	instance;

	/**
	 * Retrieve parameter manager
	 */
	private MMySqlService() {
	}

	/**
	 * @return an instance of this class
	 */
	public static MMySqlService getInstance() {
		if (instance == null) {
			instance = new MMySqlService();
		}
		return instance;
	}

	/**
	 * Returns the current connection. If not existing, it tries to establish a JDBC Connection to the MySQL database
	 * given the settings provided to the constructor.
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	static protected Connection getConnection() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {

		if (con == null || con.isClosed()) {
			Properties properties = new Properties();
			properties.put("user", PmParameterManager.getParameter(MSqlPa.USER));
			properties.put("password", PmParameterManager.getParameter(MSqlPa.PASSWORD));
			String connectTo = "jdbc:mysql://" + PmParameterManager.getParameter(MSqlPa.LOCATION)
					+ (((String) PmParameterManager.getParameter(MSqlPa.LOCATION)).endsWith("/") ? "" : "/")
					+ PmParameterManager.getParameter(MSqlPa.DBNAME);

			// error handling:
			if (PmParameterManager.getParameter(MSqlPa.LOCATION) == null) {
				throw new IllegalArgumentException("Invalid database settings: Invalid database location!");
			}
			if (PmParameterManager.getParameter(MSqlPa.DBNAME) == null) {
				throw new IllegalArgumentException("Invalid database settings: Invalid database name!");
			}
			if (PmParameterManager.getParameter(MSqlPa.USER) == null) {
				throw new IllegalArgumentException("Invalid database settings: Invalid user name!");
			}
			if (PmParameterManager.getParameter(MSqlPa.PASSWORD) == null) {
				throw new IllegalArgumentException("Invalid database settings: Invalid password!");
			}

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(connectTo, properties);
		}
		return con;
	}

	/**
	 * Execute the given SQL statement and return the according ResultSet
	 * 
	 * @param sql
	 *        the statement to query
	 * @return the ResultSet
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SQLException
	 */
	public ResultSet connect(String sql) throws SQLException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Execute SQL: " + sql);
		}

		if (sql.startsWith("update:")) {
			getConnection().createStatement().executeUpdate(sql.substring(7));
			return null;
		} else if (sql.startsWith("INSERT") || sql.startsWith("UPDATE")) {
			getConnection().createStatement().executeUpdate(sql);
			return null;
		} else {
			return getConnection().createStatement().executeQuery(sql);
		}
	}

	/**
	 * Disconnect the current mySQL connection
	 */
	public static void disconnect() {

		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug(con + "> disconnected.");
		}
		// LOGGING ->
	}

}
