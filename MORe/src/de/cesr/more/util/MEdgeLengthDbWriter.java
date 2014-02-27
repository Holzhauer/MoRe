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


import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import repast.simphony.space.gis.Geography;
import de.cesr.more.basic.edge.MoreEdge;
import de.cesr.more.basic.network.MoreNetwork;
import de.cesr.more.param.MSqlPa;
import de.cesr.more.rs.building.MoreMilieuAgent;
import de.cesr.parma.core.PmParameterManager;


/**
 * MORe
 * 
 * Writes edge distance with runID and milieu ID to the DB defined in params in {@link MSqlPa} when requested. Table
 * name is taken from {@link MSqlPa#TBLNAME_EDGE_LENGTH}.
 * 
 * @author Sascha Holzhauer
 * @date 25.02.2014
 * 
 */
public class MEdgeLengthDbWriter {

	/**
	 * Logger
	 */
	static private Logger		logger			= Logger.getLogger(MEdgeLengthDbWriter.class);

	public static final boolean	WRITE_RUN_ID	= true;

	public static final boolean	WRITE_MILIEU_ID	= true;
	
	protected MoreRunIdProvider	prov;

	public MEdgeLengthDbWriter(MoreRunIdProvider prov) {
		this.prov = prov;
	}

	public <AgentType extends MoreMilieuAgent, EdgeType extends MoreEdge<? super AgentType>> void writeEdgeLength(
			MoreNetwork<AgentType, EdgeType> network, Geography<Object> geography) {
		PreparedStatement prepStat = getPreparedStatement();

		// <- LOGGING
		logger.info("Writing edge distances to DB...");
		// LOGGING ->

		int flushInterval = (Integer) PmParameterManager.getParameter(MSqlPa.FLUSH_INTERVAL);

		try {
			int counter = 0;
			MMySqlService.getConnection().setAutoCommit(false);

			for (EdgeType e : network.getEdgesCollection()) {
				prepStat.setInt(1, ((MoreMilieuAgent) e.getEnd()).getMilieuGroup());
				prepStat.setFloat(2,
						(float) geography.getGeometry(e.getStart()).distance(geography.getGeometry(e.getEnd())));
				prepStat.addBatch();
				counter++;
				if (counter == flushInterval) {
					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("Executing...");
					}
					// LOGGING ->

					prepStat.executeBatch();
					MMySqlService.getConnection().commit();

					// <- LOGGING
					if (logger.isDebugEnabled()) {
						logger.debug("...ready!");
					}
					// LOGGING ->

					counter = 0;
				}
			}
			prepStat.executeBatch();
			MMySqlService.getConnection().commit();
			prepStat.close();

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

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Completed!");
		}
		// LOGGING ->
	}

	/**
	 * @return
	 */
	protected PreparedStatement getPreparedStatement() {
		String t1 = (String) PmParameterManager.getParameter(MSqlPa.TBLNAME_EDGE_LENGTH);

		StringBuffer sql = new StringBuffer();
		StringBuffer sqlValues = new StringBuffer();
		sql.append("INSERT INTO `" + t1 + "` (");
		sqlValues.append("VALUES(");

		if (WRITE_RUN_ID) {
			sql.append("runID, ");
			sqlValues.append(this.prov.getRunId() + ",");
		}
		if (WRITE_MILIEU_ID) {
			sql.append("milieu, ");
			sqlValues.append("?,");
		}
		sql.append("distance) ");
		sqlValues.append("?)");

		sql.append(sqlValues);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Prepare Statement: " + sql.toString());
		}
		// LOGGING ->

		PreparedStatement prepStat = null;
		try {
			prepStat = MMySqlService.getConnection().prepareStatement(sql.toString());
		} catch (SQLException e) {
			logger.warn("Creation of SQL prepare statement failed.", e);
			e.printStackTrace();
		} catch (InstantiationException exception) {
			exception.printStackTrace();
		} catch (IllegalAccessException exception) {
			exception.printStackTrace();
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();
		}

		return prepStat;
	}
}
