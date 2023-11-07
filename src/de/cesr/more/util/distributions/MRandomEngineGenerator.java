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
 * Created by Sascha Holzhauer on 12.09.2013
 */
package de.cesr.more.util.distributions;

import org.apache.commons.math3.random.RandomGenerator;

import cern.jet.random.engine.RandomEngine;


/**
 * MORe
 * 
 * This wrapper is applied to use {@link RandomEngine}s as {@link RandomGenerator} within the apache commons math
 * library.
 * 
 * @author Sascha Holzhauer
 * @date 12.09.2013
 * 
 */
public class MRandomEngineGenerator implements RandomGenerator {

	RandomEngine	mersenneTwister	= null;

	/**
	 * @param twister
	 */
	public MRandomEngineGenerator(RandomEngine twister) {
		this.mersenneTwister = twister;
	}

	/**
	 * @see org.apache.commons.math3.random.RandomGenerator#nextBoolean()
	 */
	@Override
	public boolean nextBoolean() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.apache.commons.math3.random.RandomGenerator#nextBytes(byte[])
	 */
	@Override
	public void nextBytes(byte[] arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.apache.commons.math3.random.RandomGenerator#nextDouble()
	 */
	@Override
	public double nextDouble() {
		return this.mersenneTwister.nextDouble();
	}

	/**
	 * @see org.apache.commons.math3.random.RandomGenerator#nextFloat()
	 */
	@Override
	public float nextFloat() {
		return this.mersenneTwister.nextFloat();
	}

	/**
	 * @see org.apache.commons.math3.random.RandomGenerator#nextGaussian()
	 */
	@Override
	public double nextGaussian() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.apache.commons.math3.random.RandomGenerator#nextInt()
	 */
	@Override
	public int nextInt() {
		return this.mersenneTwister.nextInt();
	}

	/**
	 * @see org.apache.commons.math3.random.RandomGenerator#nextInt(int)
	 */
	@Override
	public int nextInt(int arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.apache.commons.math3.random.RandomGenerator#nextLong()
	 */
	@Override
	public long nextLong() {
		return this.mersenneTwister.nextLong();
	}

	/**
	 * @see org.apache.commons.math3.random.RandomGenerator#setSeed(int)
	 */
	@Override
	public void setSeed(int arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.apache.commons.math3.random.RandomGenerator#setSeed(int[])
	 */
	@Override
	public void setSeed(int[] arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.apache.commons.math3.random.RandomGenerator#setSeed(long)
	 */
	@Override
	public void setSeed(long arg0) {
		throw new UnsupportedOperationException();
	}
}
