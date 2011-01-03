/**
 * KUBUS_Proto01
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 29.06.2010
 */
package de.cesr.more.util.param;

import java.util.ArrayList;
import java.util.Collection;


/**
 * KUBUS_Proto01
 *
 * @author Sascha Holzhauer
 * @date 29.06.2010 
 *
 */
public abstract class AbstractParameterReader implements ParameterReader {
	
	Collection<ParameterReader> readers = new ArrayList<ParameterReader>();

	/**
	 * @see param.framework.ParameterReader#initParameters()
	 */
	@Override
	public void initParameters() {
		
		
		for (ParameterReader reader : readers) {
			reader.initParameters();
		}
	}

	/**
	 * @see param.framework.ParameterReader#registerParameterReader(param.framework.ParameterReader)
	 */
	@Override
	public void registerParameterReader(ParameterReader reader) {
		readers.add(reader);
	}

}
