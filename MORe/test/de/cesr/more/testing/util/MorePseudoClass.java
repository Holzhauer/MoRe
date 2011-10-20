package de.cesr.more.testing.util;

import org.apache.log4j.Logger;

import de.cesr.more.util.Log4jLogger;

public class MorePseudoClass {
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(MorePseudoClass.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// <- LOGGING
		logger.info("Logging in MORe Works...");
		// LOGGING ->
	}

}
