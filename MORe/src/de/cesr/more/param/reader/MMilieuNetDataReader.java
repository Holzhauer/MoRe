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

import org.apache.log4j.Logger;

import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.more.param.MSqlPa;
import de.cesr.more.util.MMySqlService;
import de.cesr.parma.core.PmAbstractParameterReader;
import de.cesr.parma.core.PmParameterManager;


/**
 * MoRe
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

	/**
	 * @see param.framework.ParameterReader#initParameters()
	 */
	@Override
	public void initParameters() {
		MMilieuNetworkParameterMap map = new MMilieuNetworkParameterMap();
		String t1 = (String) PmParameterManager.getParameter(MSqlPa.TBLNAME_NET_PREFS);
		String t2 = (String) PmParameterManager.getParameter(MSqlPa.TBLNAME_NET_PREFS_LINKS);

		// fetch number of households along milieu groups (not milieus!):
		String sql = "SELECT milieu, " + "k, " + "p_rewire, " + "SEARCH_RADIUS, " + "X_SEARCH_RADIUS, "
				+ "MAX_SEARCH_RADIUS, " + "DIM_WEIGHT_GEO, " + "DIM_WEIGHT_MILIEU, " + "DYN_DECREASE_AMOUNT, " +
				"DYN_DECREASE_THRESHOLD, " + "DYN_INCREASE_AMOUNT, " + "DYN_INCREASE_THRESHOLD, " +
				"DYN_INTERVAL_LINK_MANAGEMENT, " + "DYN_INTERVAL_EDGE_UPDATING, " + "DYN_FADE_OUT_AMOUNT, " + "DYN_FADE_OUT_INTERVAL, " +
				"DYN_PROB_RECIPROCITY, " + "DYN_PROB_TRANSITIVITY, " + "DYN_PROB_GLOBAL, " + "DYN_PROB_LOCAL, "
				+ "DYN_LOCAL_RADIUS, "
				+ "DYN_EDGE_MANAGE_OPTIMUM, "
				+ "DISTANCTE_PROBABILITY_EXPONENT, "
				+ "EXTENDING_SEARCH_FRACTION "
				+ "FROM " + t1 + " AS t1 " + "WHERE paramID ="
				+ PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETPREFS_PARAMID) + ";";

		if (logger.isDebugEnabled()) {
			logger.debug("MySQL-Satement in init(): " + sql);
		}

		ResultSet result = MMySqlService.getInstance().connect(sql);

		try {
			boolean hasNext = result.next();
			boolean hasNextInner = false;

			if (!hasNext) {
				logger.error("No milieu network parameter set in table for paramID "
						+ PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETPREFS_PARAMID));
				throw new IllegalStateException("No milieu network parameter set in table for paramID "
						+ PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETPREFS_PARAMID));
			}
			while (hasNext) {
				int milieu = result.getInt("milieu");
				map.setK(milieu, result.getInt("k"));
				map.setP_Rewire(milieu, result.getDouble("p_rewire"));
				map.setSearchRadius(milieu, result.getDouble("SEARCH_RADIUS"));
				map.setXSearchRadius(milieu, result.getDouble("X_SEARCH_RADIUS"));
				map.setMaxSearchRadius(milieu, result.getDouble("MAX_SEARCH_RADIUS"));
				map.setDimWeightGeo(milieu, result.getDouble("DIM_WEIGHT_GEO"));
				map.setDimWeightMilieu(milieu, result.getDouble("DIM_WEIGHT_MILIEU"));
				map.setDynDecreaseAmount(milieu, result.getDouble("DYN_DECREASE_AMOUNT"));
				map.setDynDecreaseThreshold(milieu, result.getDouble("DYN_DECREASE_THRESHOLD"));
				map.setDynIncreaseAmount(milieu, result.getDouble("DYN_INCREASE_AMOUNT"));
				map.setDynIncreaseThreshold(milieu, result.getDouble("DYN_INCREASE_THRESHOLD"));
				
				map.setDynEdgeUpdatingInverval(milieu, result.getInt("DYN_INTERVAL_EDGE_UPDATING"));
				map.setDynLinkManagementInverval(milieu, result.getInt("DYN_INTERVAL_LINK_MANAGEMENT"));
				
				map.setDynFadeOutAmount(milieu, result.getDouble("DYN_FADE_OUT_AMOUNT"));
				map.setDynFadeOutInterval(milieu, result.getDouble("DYN_FADE_OUT_INTERVAL"));
				
				map.setDynProbReciprocity(milieu, result.getDouble("DYN_PROB_RECIPROCITY"));
				map.setDynProbTransitivity(milieu, result.getDouble("DYN_PROB_TRANSITIVITY"));
				
				map.setDynEdgeManageOptimum(milieu, result.getDouble("DYN_EDGE_MANAGE_OPTIMUM"));				
				map.setDynProbGlobal(milieu, result.getDouble("DYN_PROB_GLOBAL"));
				map.setDynProbLocal(milieu, result.getDouble("DYN_PROB_LOCAL"));
				map.setDynLocalRadius(milieu, result.getDouble("DYN_PROB_GLOBAL"));
				
				map.setDistanceProbExp(milieu, result.getDouble("DISTANCTE_PROBABILITY_EXPONENT"));
				map.setExtengingSearchFraction(milieu, result.getDouble("EXTENDING_SEARCH_FRACTION"));

				String sql2 = "SELECT partnerMilieu, " + "p_links " + "FROM " + t2 + " AS t2 " + "WHERE paramID="
						+ PmParameterManager.getParameter(MNetworkBuildingPa.MILIEU_NETPREFS_PARAMID) + " AND " + "milieu=" + milieu
						+ ";";

				if (logger.isDebugEnabled()) {
					logger.debug("MySQL-Satement in init(): " + sql2);
				}

				ResultSet resultInner = MMySqlService.getInstance().connect(sql2);

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
			logger.error("Error in fetching milieu net data");
			e.printStackTrace();
		}
		PmParameterManager.setParameter(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS, map);
	}
}
