/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 10.02.2010
 */
package de.cesr.more.testing.standalone;

/**
 *
 * @author Sascha Holzhauer
 * @date 10.02.2010 
 *
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;



/**
 * run all adapter tests at once
 */
@RunWith(Suite.class)
@SuiteClasses({MScheduleTest.class})
public class AllStandaloneTests {

}
