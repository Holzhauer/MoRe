/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 10.02.2010
 */
package de.cesr.more.testing.adapter;

/**
 *
 * @author Sascha Holzhauer
 * @date 10.02.2010 
 *
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.cesr.more.testing.adapter.snrs.AllSnRsAdapterTests;


/**
 * run all adapter tests at once
 */
@RunWith(Suite.class)
@SuiteClasses({AllSnRsAdapterTests.class})
public class AlLAdapterTests {

}
