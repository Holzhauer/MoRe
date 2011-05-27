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
 * Created by Sascha Holzhauer on 06.05.2011
 */
package de.cesr.more.util.param;


import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.cesr.more.param.MoreBasicPa;
import de.cesr.more.util.Log4jLogger;

import static  de.cesr.more.util.param.MParameterManager.setParameter;

/**
 * MORe
 *
 * @author Sascha Holzhauer
 * @date 06.05.2011 
 *
 */
public class MDbXmlParameterReader extends AbstractParameterReader {
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MDbXmlParameterReader.class);
	
	/**
	 * @see de.cesr.more.util.param.AbstractParameterReader#initParameters()
	 */
	@Override
	public void initParameters() {
		try {
			File file = new File((String) MParameterManager.getParameter(MoreBasicPa.DB_SETTINGS_FILE));
			if (file.exists()) {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(file);
				doc.getDocumentElement().normalize();
	
				NodeList nodeLst = doc.getElementsByTagName("db");
				Node fstNode = nodeLst.item(0);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fstElmnt = (Element) fstNode;
					NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("SQL_LOCATION");
					Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
					NodeList fstNm = fstNmElmnt.getChildNodes();
					setParameter(MoreBasicPa.LOCATION, (fstNm.item(0)).getNodeValue());
	
	//				NodeList dbnameNmElmntLst = fstElmnt.getElementsByTagName("SQL_DBNAME");
	//				Element dbnameNmElmnt = (Element) dbnameNmElmntLst.item(0);
	//				NodeList dbnameNm = dbnameNmElmnt.getChildNodes();
	//				setParameter(SqlPa.DBNAME, (dbnameNm.item(0)).getNodeValue());
	
					NodeList userNmElmntLst = fstElmnt.getElementsByTagName("SQL_USER");
					Element userNmElmnt = (Element) userNmElmntLst.item(0);
					NodeList userNm = userNmElmnt.getChildNodes();
					setParameter(MoreBasicPa.USER, (userNm.item(0)).getNodeValue());
	
					NodeList pwNmElmntLst = fstElmnt.getElementsByTagName("SQL_PASSWORD");
					Element pwNmElmnt = (Element) pwNmElmntLst.item(0);
					NodeList pwNm = pwNmElmnt.getChildNodes();
					setParameter(MoreBasicPa.PASSWORD, (pwNm.item(0)).getNodeValue());
	
					NodeList tbNmElmntLst = fstElmnt.getElementsByTagName("SQL_TBLNAME");
					Element tbNmElmnt = (Element) tbNmElmntLst.item(0);
					if (tbNmElmnt != null) {
						NodeList tbNm = tbNmElmnt.getChildNodes();
						setParameter(MoreBasicPa.TBLNAME_NETWORK_MEASURES, (tbNm.item(0)).getNodeValue());
					} else {
						logger.warn("Parameter " + MoreBasicPa.TBLNAME_NETWORK_MEASURES + " not defined in " + 
								MoreBasicPa.DB_SETTINGS_FILE);
					}
				}
			} else {
				logger.info("Database settings XML file is not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
