package de.cesr.more.testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.cesr.more.testing.util.MoreUtilitiesTest;


/**
 * run all tests at once
 */
@RunWith(Suite.class)
@SuiteClasses(MoreUtilitiesTest.class )
public class AllMoreTests {
}
