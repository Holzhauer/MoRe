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
 * Created by holzhauer on 27.06.2011
 */
package de.cesr.more.util;

import org.apache.log4j.Logger;

import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

/**
 * MORe
 *
 * @author holzhauer
 * @date 27.06.2011 
 *
 */
public class MUniformDistributionController extends Uniform {

	/**
	 * Logger
	 */
	static private Logger	logger_st	= Log4jLogger.getLogger(MRandom.class.getName() + ".stacktrace");
	static private Logger	logger		= Log4jLogger.getLogger(MRandom.class);

	/**
	 * @param arg2
	 */
	public MUniformDistributionController(RandomEngine arg2) {
		super(arg2);
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * @see cern.jet.random.Uniform#nextIntFromTo(int, int)
	 */
	@Override
	public int nextIntFromTo(int from, int to) {
		int rand = super.nextIntFromTo(from, to);
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new MIdentifyCallerException());
		return rand;
	}

	@Override
	public boolean nextBoolean() {
		boolean rand = super.nextBoolean();
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new MIdentifyCallerException());
		return rand;
	}

	@Override
	public double nextDouble() {
		double rand = super.nextDouble();
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new MIdentifyCallerException());
		return rand;
	}

	/**
	 * Returns a uniformly distributed random number in the open interval <tt>(from,to)</tt> (excluding <tt>from</tt>
	 * and <tt>to</tt>). Pre conditions: <tt>from &lt;= to</tt>.
	 */
	@Override
	public double nextDoubleFromTo(double from, double to) {
		double rand = super.nextDoubleFromTo(from, to);
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new MIdentifyCallerException());
		return rand;
	}

	/**
	 * Returns a uniformly distributed random number in the open interval <tt>(from,to)</tt> (excluding <tt>from</tt>
	 * and <tt>to</tt>). Pre conditions: <tt>from &lt;= to</tt>.
	 */
	@Override
	public float nextFloatFromTo(float from, float to) {
		float rand = super.nextFloatFromTo(from, to);
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new MIdentifyCallerException());
		return rand;
	}

	/**
	 * Returns a uniformly distributed random number in the closed interval <tt>[min,max]</tt> (including <tt>min</tt>
	 * and <tt>max</tt>).
	 */
	@Override
	public int nextInt() {
		int rand = super.nextInt();
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new MIdentifyCallerException());
		return rand;
	}

	/**
	 * Returns a uniformly distributed random number in the closed interval <tt>[from,to]</tt> (including <tt>from</tt>
	 * and <tt>to</tt>). Pre conditions: <tt>from &lt;= to</tt>.
	 */
	@Override
	public long nextLongFromTo(long from, long to) {
		long rand = super.nextLongFromTo(from, to);
		logger.debug("Random number: " + rand);
		logger_st.error("Stack trace: ", new MIdentifyCallerException());
		return rand;
	}
}
