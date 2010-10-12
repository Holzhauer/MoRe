package de.cesr.more.util;

import org.apache.log4j.Logger;

public class MorePseudoClass {
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MorePseudoClass.class);

	/**
	 * @param args
	 * Created by Sascha Holzhauer on 05.10.2010
	 */
	public static void main(String[] args) {
		// <- LOGGING
		logger.info("Logging in MORe Works...");
		// LOGGING ->
	}

}
