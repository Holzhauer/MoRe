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
 * Created by Sascha Holzhauer on 21.02.2014
 */
package de.cesr.more.param.reader;


import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;

import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.parma.core.PmAbstractParameterReader;
import de.cesr.parma.core.PmParameterManager;


/**
 * CSV file may not contain headers! Cell entries give the probability that an agent of the milieu represented by a row
 * is linked to an agent represented by the column. I.e., row sums must be equal to 1.0.
 *
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 21.02.2014
 *
 */
public class MMilieuNetLinkDataCsvReader extends PmAbstractParameterReader {

	/**
	 * Logger
	 */
	static private Logger		logger	= Logger.getLogger(MMilieuNetLinkDataCsvReader.class);

	PmParameterManager	pm;
	MMilieuNetworkParameterMap	map;

	public MMilieuNetLinkDataCsvReader(Object pmIdentifier) {
		this.pm = PmParameterManager.getInstance(pmIdentifier);
	}

	public MMilieuNetLinkDataCsvReader(PmParameterManager pm) {
		this.pm = pm;
	}

	@Override
	public void initParameters() {

		this.map = (MMilieuNetworkParameterMap) pm.getParam(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS);

		if (this.map == null) {
			this.map = new MMilieuNetworkParameterMap(pm);
			pm.setParam(MNetworkBuildingPa.MILIEU_NETWORK_PARAMS, this.map);
		}

		CsvReader reader;
		try {
			reader = new CsvReader(((String) pm.getParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_MILIEULINKS)),
					((Character) pm.getParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_DELIMITER)).charValue());

			int initialMilieuId = ((Integer)pm.getParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_MILIEU_ID_START)).intValue();
			int milieuEgo = initialMilieuId;

			while( reader.readRecord() )
			{
				double sum = 0.0;
				for (int columnId = 0;
							columnId < reader.getColumnCount(); columnId++) {
					map.setP_Milieu(milieuEgo, columnId + initialMilieuId, Double.parseDouble(reader.get(columnId)));
					sum += Double.parseDouble(reader.get(columnId));
					logger.debug("Read link probbaility for Milieu " + (columnId + initialMilieuId) + " to Milieu "
							+ milieuEgo + " from CSV file. Value: " + reader.get(columnId));
				}

				if (Math.abs(sum - 1.0) > 0.001) {
					logger.warn("Milieu probabilities do not sum up to one (row sum)!");
				}

				milieuEgo++;
			}
		} catch (FileNotFoundException exception) {
			logger.error("File " + pm.getParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_MILIEULINKS) + " not found");
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
