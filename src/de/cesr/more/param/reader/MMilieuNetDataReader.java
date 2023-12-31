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
 * Created by Sascha Holzhauer on 30.07.2010
 */
package de.cesr.more.param.reader;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetBuildHdffPa;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MSqlPa;
import de.cesr.more.util.MMySqlService;
import de.cesr.parma.core.PmAbstractParameterReader;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.parma.definition.PmFrameworkPa;
import de.cesr.parma.reader.PmDbParameterReader;


/**
 * MoRe
 * 
 * Retrieves milieu-specific (i.e. agent type specific) parameters from DB tables defined in
 * {@link MSqlPa#TBLNAME_NET_PREFS} and {@link MSqlPa#TBLNAME_NET_PREFS_LINKS} for param ID
 * {@link MNetworkBuildingPa#MILIEU_NETPREFS_PARAMID} and sets them at ParMa.
 * 
 * @author Sascha Holzhauer
 * @date 30.07.2010
 * 
 */
public class MMilieuNetDataReader extends PmAbstractParameterReader {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MMilieuNetDataReader.class);

	protected PmParameterManager	pm;

	public MMilieuNetDataReader(PmParameterManager pm) {
		this.pm = pm;
	}

	public MMilieuNetDataReader() {
		this(PmParameterManager.getInstance(null));
	}

	/**
	 * @see param.framework.ParameterReader#initParameters()
	 */
	@Override
	public void initParameters() {
		MMilieuNetworkParameterMap map = new MMilieuNetworkParameterMap(pm);

		if (((String) PmParameterManager.getParameter(PmFrameworkPa.TBLNAME_PARAMS))
				.equals(PmDbParameterReader.NOT_DEFINED)) {
			// <- LOGGING
			logger.warn("Table is set to " + PmDbParameterReader.NOT_DEFINED
					+ ". Skipping reading parameters from database.");
			// LOGGING ->
		} else {
			String t1 = (String) PmParameterManager.getParameter(MSqlPa.TBLNAME_NET_PREFS);
			String t2 = (String) PmParameterManager.getParameter(MSqlPa.TBLNAME_NET_PREFS_LINKS);

			// fetch number of households along milieu groups (not milieus!):
			String sql = "SELECT * "
					+ "FROM " + t1 + " AS t1 " + "WHERE paramID ="
					+ PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETPREFS_PARAMID) + ";";

			if (logger.isDebugEnabled()) {
				logger.debug("MySQL-Satement in init(): " + sql);
			}

			try {
				ResultSet result = MMySqlService.connect(sql);
				
				boolean hasNext = result.next();
				boolean hasNextInner = false;

				List<String> colNames = new ArrayList<String>();
				java.sql.ResultSetMetaData metaData = result.getMetaData();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					colNames.add(metaData.getColumnName(i));
				}

				if (!hasNext) {
					logger.error("No milieu network parameter set in table for paramID "
							+ PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETPREFS_PARAMID));
					throw new IllegalStateException("No milieu network parameter set in table for paramID "
							+ PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETPREFS_PARAMID));
				}
				while (hasNext) {
					int milieu = result.getInt("milieu");

					if (colNames.contains("k")) {
						map.setK(milieu, result.getInt("k"));
					}
					if (colNames.contains("p_rewire")) {
						map.setP_Rewire(milieu, result.getDouble("p_rewire"));
					}

					if (colNames.contains("SEARCH_RADIUS")) {
						map.setSearchRadius(milieu, result.getDouble("SEARCH_RADIUS"));
					}

					if (colNames.contains("X_SEARCH_RADIUS")) {
						map.setXSearchRadius(milieu, result.getDouble("X_SEARCH_RADIUS"));
					}

					if (colNames.contains("MAX_SEARCH_RADIUS")) {
						map.setMaxSearchRadius(milieu, result.getDouble("MAX_SEARCH_RADIUS"));
					}

					if (colNames.contains("DIM_WEIGHT_GEO")) {
						map.setDimWeightGeo(milieu, result.getDouble("DIM_WEIGHT_GEO"));
						map.setMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_GEO, milieu, result.getDouble("DIM_WEIGHT_GEO"));
					}

					if (colNames.contains("DIM_WEIGHT_MILIEU")) {
						map.setDimWeightMilieu(milieu, result.getDouble("DIM_WEIGHT_MILIEU"));
						map.setMilieuParam(MNetBuildHdffPa.DIM_WEIGHTS_MILIEU, milieu,
								result.getDouble("DIM_WEIGHT_MILIEU"));
					}

					if (colNames.contains("DYN_DECREASE_AMOUNT")) {
						map.setDynDecreaseAmount(milieu, result.getDouble("DYN_DECREASE_AMOUNT"));
					}

					if (colNames.contains("DYN_DECREASE_THRESHOLD")) {
						map.setDynDecreaseThreshold(milieu, result.getDouble("DYN_DECREASE_THRESHOLD"));
					}

					if (colNames.contains("DYN_INCREASE_AMOUNT")) {
						map.setDynIncreaseAmount(milieu, result.getDouble("DYN_INCREASE_AMOUNT"));
					}

					if (colNames.contains("DYN_INCREASE_THRESHOLD")) {
						map.setDynIncreaseThreshold(milieu, result.getDouble("DYN_INCREASE_THRESHOLD"));
					}

					if (colNames.contains("DYN_INTERVAL_EDGE_UPDATING")) {
						map.setDynEdgeUpdatingInverval(milieu, result.getInt("DYN_INTERVAL_EDGE_UPDATING"));
					}

					if (colNames.contains("DYN_INTERVAL_LINK_MANAGEMENT")) {
						map.setDynLinkManagementInverval(milieu, result.getInt("DYN_INTERVAL_LINK_MANAGEMENT"));
					}

					if (colNames.contains("DYN_FADE_OUT_AMOUNT")) {
						map.setDynFadeOutAmount(milieu, result.getDouble("DYN_FADE_OUT_AMOUNT"));
					}

					if (colNames.contains("DYN_FADE_OUT_INTERVAL")) {
						map.setDynFadeOutInterval(milieu, result.getDouble("DYN_FADE_OUT_INTERVAL"));
					}

					if (colNames.contains("DYN_PROB_RECIPROCITY")) {
						map.setDynProbReciprocity(milieu, result.getDouble("DYN_PROB_RECIPROCITY"));
					}

					if (colNames.contains("DYN_PROB_TRANSITIVITY")) {
						map.setDynProbTransitivity(milieu, result.getDouble("DYN_PROB_TRANSITIVITY"));
					}

					if (colNames.contains("DYN_PROB_GLOBAL")) {
						map.setDynProbGlobal(milieu, result.getDouble("DYN_PROB_GLOBAL"));
					}

					if (colNames.contains("DYN_PROB_LOCAL")) {
						map.setDynProbDistance(milieu, result.getDouble("DYN_PROB_LOCAL"));
					}

					if (colNames.contains("DYN_PROB_GLOBAL")) {
						map.setDynLocalRadius(milieu, result.getDouble("DYN_PROB_GLOBAL"));
					}

					if (colNames.contains("DISTANCE_PROBABILITY_EXPONENT")) {
						map.setDistanceProbExp(milieu, result.getDouble("DISTANCE_PROBABILITY_EXPONENT"));
					}

					if (colNames.contains("EXTENDING_SEARCH_FRACTION")) {
						map.setExtendingSearchFraction(milieu, result.getDouble("EXTENDING_SEARCH_FRACTION"));
					}

					if (colNames.contains("PERCEIVE_SOCNET_INTERVAL")) {
						map.setNetUpdateInterval(milieu, result.getInt("PERCEIVE_SOCNET_INTERVAL"));
					}

					if (colNames.contains("PROB_BACKWARD")) {
						map.setBackwardProb(milieu, result.getDouble("PROB_BACKWARD"));
					}

					if (colNames.contains("PROB_FORWARD")) {
						map.setForwardProb(milieu, result.getDouble("PROB_FORWARD"));
					}

					if (colNames.contains("K_DISTRIBUTION_CLASS")) {
						map.setKDistributionClass(milieu, result.getString("K_DISTRIBUTION_CLASS"));
					}

					if (colNames.contains("K_PARAM_A")) {
						map.setKparamA(milieu, result.getDouble("K_PARAM_A"));
					}

					if (colNames.contains("K_PARAM_B")) {
						map.setKparamB(milieu, result.getDouble("K_PARAM_B"));
					}

					if (colNames.contains("DIST_DISTRIBUTION_CLASS")) {
						map.setDistDistributionClass(milieu, result.getString("DIST_DISTRIBUTION_CLASS"));
					}

					if (colNames.contains("DIST_PARAM_A")) {
						map.setDistParamA(milieu, result.getDouble("DIST_PARAM_A"));
					}

					if (colNames.contains("DIST_PARAM_B")) {
						map.setDistParamB(milieu, result.getDouble("DIST_PARAM_B"));
					}
					

					if (colNames.contains("DIST_PARAM_XMIN")) {
						map.setDistParamXMin(milieu, result.getDouble("DIST_PARAM_XMIN"));
					}

					if (colNames.contains("DIST_PARAM_PLOCAL")) {
						map.setDistParamPLocal(milieu, result.getDouble("DIST_PARAM_PLOCAL"));
					}
					
					String sql2 = "SELECT partnerMilieu, " + "p_links " + "FROM " + t2 + " AS t2 " + "WHERE paramID="
							+ PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETPREFS_PARAMID) + " AND "
							+ "milieu=" + milieu
							+ ";";

					if (logger.isDebugEnabled()) {
						logger.debug("MySQL-Satement in init(): " + sql2);
					}

					ResultSet resultInner = MMySqlService.connect(sql2);

					hasNextInner = resultInner.next();
					if (!hasNextInner) {
						logger.error("No milieu network parameter set in table for paramID "
								+ PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETPREFS_PARAMID));
						throw new IllegalStateException("No milieu network links parameter set in table for paramID "
								+ PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETPREFS_PARAMID));
					}

					while (hasNextInner) {
						map.setP_Milieu(milieu, resultInner.getInt("partnerMilieu"), resultInner.getDouble("p_links"));
						hasNextInner = resultInner.next();
					}

					resultInner.close();
					hasNext = result.next();
				}
				result.close();

			} catch (SQLException e) {
				logger.error("Error in fetching milieu net data (SQLException: " + e.getMessage() + ")");
				e.printStackTrace();
			} catch (InstantiationException e) {
				logger.error("Error in fetching milieu net data (InstantiationException)");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				logger.error("Error in fetching milieu net data (IllegalAccessException)");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				logger.error("Error in fetching milieu net data (ClassNotFoundException)");
				e.printStackTrace();
			} finally {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Disconnect...");
				}
				// LOGGING ->

				MMySqlService.disconnect();
			}
		}
		PmParameterManager.setParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS, map);
	}
}
