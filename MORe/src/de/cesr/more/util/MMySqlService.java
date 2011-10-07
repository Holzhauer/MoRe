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
	 * @return an instance of this class Created by Sascha Holzhauer on 12.07.2010
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
	 * @param sql the statement to query
	 * @return the ResultSet Created by Sascha Holzhauer on 13.07.2010
	 */
	public ResultSet connect(String sql) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Execute SQL: " + sql);
		}
		
		try {
			if (sql.startsWith("update:")) {
				getConnection().createStatement().executeUpdate(sql.substring(7));
				return null;
			} else {
				return getConnection().createStatement().executeQuery(sql);
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
		return null;
	}

	/**
	 * Disconnect the current mySQL connection Created by Sascha Holzhauer on 13.07.2010
	 */
	public static void disconnect() {

		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
