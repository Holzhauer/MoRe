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
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;

import de.cesr.more.param.MMilieuNetworkParameterMap;
import de.cesr.more.param.MNetworkBuildingPa;
import de.cesr.parma.core.PmAbstractParameterReader;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;


/**
 * Reader of milieu-specific parameters from a CSV file. NOTE: Requires parameters to be specified in the first row
 * (header). The parameter {@link MNetworkBuildingPa#MILIEU_NETWORK_CSV_COLUMNPREFIX} defines a prefix for parameter
 * definitions that needs to be omitted from the fully qualified classname in the header.
 * 
 * @author Sascha Holzhauer
 * @date 21.02.2014
 * 
 */
public class MMilieuNetDataCsvReader extends PmAbstractParameterReader {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(MMilieuNetDataCsvReader.class);

	PmParameterManager	pm;
	MMilieuNetworkParameterMap	map;

	public MMilieuNetDataCsvReader(Object pmIdentifier) {
		this.pm = PmParameterManager.getInstance(pmIdentifier);
	}

	public MMilieuNetDataCsvReader(PmParameterManager pm) {
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
			reader = new CsvReader(((String)pm.getParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_MILIEUS)),
					((Character)pm.getParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_DELIMITER)).charValue());
			reader.readHeaders();
			
			List<String> columns = Arrays.asList( reader.getHeaders());

			Integer milieu = 0;

			while( reader.readRecord() )
 {
				if (columns.contains("milieu")) {
					milieu = Integer.parseInt(reader.get("milieu"));
				} else {
					milieu++;
					logger.warn("Column 'milieu' missing: Assuming " + milieu);
				}
				
				for (String column : columns) {
					if (!column.equals("milieu")) {
						this.processColumn(column, reader.get(column), milieu);
					}
				}
			}
		} catch (FileNotFoundException exception) {
			logger.error("File " + pm.getParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_MILIEUS) + " not found");
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void processColumn(String column, String value, int milieu) {
		column = ((String) pm.getParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_COLUMNPREFIX)) + column;
		if (column.contains(":")) {
			try {
				// !!! difficulty! >
				String param_class = column.split(":")[0];
				String param_name = column.split(":")[1];

				PmParameterDefinition definition;

				definition = (PmParameterDefinition) Enum
							.valueOf((Class<Enum>) Class
									.forName(param_class), param_name);

				if (definition.getType() == Class.class) {
					// handle Class.class parameter types:
					if (value.toString().contains(" ")) {
						logger.warn("The class for parameter "
								+ definition
								+ " contains withspaces. Maybe it is some hint.");
					} else if (value.toString().length() > 0) {
						map.setMilieuParam(definition, milieu, Class.forName(value));
					}
				} else {
					// handle all other parameter types:
					if (definition.getType().equals(Double.class)) {
						map.setMilieuParam(definition, milieu, Double.parseDouble(value));
					} else if (definition.getType().equals(String.class)) {
						map.setMilieuParam(definition, milieu, value);
					} else if (definition.getType().equals(Float.class)) {
						map.setMilieuParam(definition, milieu, Float.parseFloat(value));
					} else if (definition.getType().equals(Integer.class)) {
						map.setMilieuParam(definition, milieu, Integer.parseInt(value));
					} else if (definition.getType().equals(Long.class)) {
						map.setMilieuParam(definition, milieu, Long.parseLong(value));
					} else if (definition.getType().equals(Character.class)) {
						map.setMilieuParam(definition, milieu, new Character(value.charAt(0)));
					} else {
						logger.warn("The String " + value + " cannot be cast to the requested type ("
								+ definition.getType() + ")");
					}
				}

				logger.info("Parameter "
						+ Enum.valueOf((Class<Enum>) Class
								.forName(param_class), param_name)
						+ " read from" + " database. Value: "
						+ value);
			} catch (ClassNotFoundException exception) {
				exception.printStackTrace();
			}
		} else {
			logger.warn("The column '"
						+ column
						+ "' of file "
						+ pm.getParam(MNetworkBuildingPa.MILIEU_NETWORK_CSV_MILIEUS)
						+ " is not in proper parameter format (CLASS:PARAMETER_NAME)");
		}
	}
}
