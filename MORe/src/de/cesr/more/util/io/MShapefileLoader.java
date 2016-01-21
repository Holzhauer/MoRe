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
 * Created by Sascha Holzhauer on 17 Jun 2014
 */
package de.cesr.more.util.io;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;

import repast.simphony.context.Context;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.ShapefileLoader;
import simphony.util.messages.MessageCenter;

import com.vividsolutions.jts.geom.Geometry;


/**
 * MoRe
 * 
 * Creates and sets agents properties from a features in shapefile.
 * 
 * @author Nick Collier
 * @author sholzhau
 * @date 17 Jun 2014
 * 
 */
public class MShapefileLoader<T> {

	private static final MessageCenter	msg					= MessageCenter.getMessageCenter(ShapefileLoader.class);
	private static Map<Class, Class>	primToObject		= new HashMap<Class, Class>();

	static {
		primToObject.put(int.class, Integer.class);
		primToObject.put(long.class, Long.class);
		primToObject.put(double.class, Double.class);
		primToObject.put(float.class, Float.class);
		primToObject.put(boolean.class, Boolean.class);
		primToObject.put(byte.class, Byte.class);
		primToObject.put(char.class, Character.class);
	}

	private MathTransform				transform;
	private Geography					geography;
	private Context						context;
	private Map<String, Method>			attributeMethodMap	= new HashMap<String, Method>();
	private Class						agentClass;
	private FileDataStore				store				= null;
	private SimpleFeatureIterator		iter;

	/**
	 * Creates a shapefile loader for agents of the specified class and whose data source is the specified shapefile.
	 * The agents will be placed into the specified Geography according to the geometry specified in the shapefile and
	 * transformed according to the geography's CRS. USing this constructor, a context is not maintained.
	 * 
	 * @param clazz
	 *        the agent class
	 * @param shapefile
	 *        the shapefile that serves as the datasource for the agent properties. Should set to read-only right after
	 *        initialisation!
	 * @param geography
	 *        the geography to hold spatial locations of the agents
	 */
	public MShapefileLoader(Class<T> clazz, File shapefile, Geography<?> geography) {
		this(clazz, shapefile, geography, null);
	}

	/**
	 * Creates a shapefile loader for agents of the specified class and whose data source is the specified shapefile.
	 * The agents will be placed into the specified Geography according to the geometry specified in the shapefile and
	 * transformed according to the geography's CRS.
	 * 
	 * @param clazz
	 *        the agent class
	 * @param shapefile
	 *        the shapefile that serves as the datasource for the agent properties. Should set to read-only right after
	 *        initialisation!
	 * @param geography
	 *        the geography to hold spatial locations of the agents
	 * @param context
	 *        the context to add the agents to
	 */
	public MShapefileLoader(Class<T> clazz, File shapefile, Geography geography, Context context) {
		this.geography = geography;
		this.agentClass = clazz;
		this.context = context;
		try {
			BeanInfo info = Introspector.getBeanInfo(clazz, Object.class);
			Map<String, Method> methodMap = new HashMap<String, Method>();
			PropertyDescriptor[] pds = info.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				if (pd.getWriteMethod() != null) {
					methodMap.put(pd.getName().toLowerCase(), pd.getWriteMethod());
				}
			}

			shapefile.setReadOnly();
			store = FileDataStoreFinder.getDataStore(shapefile);
	        
			// ShapefileDataStore store = new ShapefileDataStore(shapefile);
			SimpleFeatureType schema = store.getSchema(store.getTypeNames()[0]);

			// First attribute at index 0 is always the Geometry
			AttributeType type = schema.getType(0);
			String name = type.getName().getLocalPart();
			initTransform(geography, type);

			// Loop over remaining type attributes
			for (int i = 1, n = schema.getAttributeCount(); i < n; i++) {
				type = schema.getType(i);
				name = type.getName().getLocalPart();

				Method method = methodMap.get(name.toLowerCase());
				if (method == null)
					method = methodMap.get(name.replace("_", "").toLowerCase());
				if (method != null && isCompatible(method.getParameterTypes()[0], (type.getBinding()))) {
					attributeMethodMap.put(name, method);
				}
			}
			iter = store.getFeatureSource().getFeatures().features();
		} catch (IntrospectionException ex) {
			msg.error("Error while introspecting class", ex);
		} catch (IOException e) {
			msg.error(String.format("Error opening shapefile '%S'", shapefile), e);
		} catch (FactoryException e) {
			msg.error(String.format("Error creating transform between shapefile CRS and Geography CRS"), e);
		}
	}

	private boolean isCompatible(Class methodParam, Class attributeType) {
		if (methodParam.equals(attributeType))
			return true;
		Class clazz = primToObject.get(methodParam);
		if (clazz != null)
			return clazz.equals(attributeType);
		return false;
	}

	private void initTransform(Geography geography, AttributeType type) throws FactoryException {
		GeometryType gType = (GeometryType) type;
		if (geography != null) {
			try {
				transform = ReferencingFactoryFinder.getCoordinateOperationFactory(null).createOperation(
						gType.getCoordinateReferenceSystem(), geography.getCRS()).getMathTransform();

			} catch (OperationNotFoundException ex) {
				// bursa wolf params may be missing so try lenient.
				transform = CRS.findMathTransform(gType.getCoordinateReferenceSystem(),
						geography.getCRS(), true);
			}
		}
	}

	/**
	 * Creates all the agents for the shapefile features, setting each agent's properties to the value of a feature's
	 * relevant attributes.
	 */
	public void load() {
		while (hasNext())
			next();
	}

	/**
	 * Creates the next agent from the next feature in the shapefile, setting that agent's properties to the value of
	 * that feature's relevant attributes.
	 * 
	 * @return the created agent
	 */
	public T next() {
		T obj = null;
		try {
			obj = (T) agentClass.newInstance();
			obj = processNext(obj);
		} catch (InstantiationException e) {
			msg.error("Error creating agent instance from class", e);
		} catch (IllegalAccessException e) {
			msg.error("Error creating agent instance from class", e);
		}

		return obj;
	}

	private T processNext(T obj) {
		try {
			SimpleFeature feature = iter.next();
			obj = fillAgent(feature, obj);
			if (context != null && !context.contains(obj))
				context.add(obj);
			if (geography != null)
				geography.move(obj,
						JTS.transform(((Geometry) feature.getDefaultGeometry()), transform));
			return obj;
		} catch (IllegalAccessException e) {
			msg.error("Error setting agent property from feature attribute", e);
		} catch (InvocationTargetException e) {
			msg.error("Error setting agent property from feature attribute", e);
		} catch (TransformException e) {
			msg.error("Error transforming feature geometry to geography's CRS", e);
		}

		return null;
	}

	/**
	 * Creates the next agent from the next feature in the shapefile, setting that agent's properties to the value of
	 * that feature's relevant attributes. Note that the constructor matching is somewhat naive and looks for exact
	 * matches.
	 * 
	 * @param constructorArgs
	 *        parameters to pass to the constructor when creating the agent.
	 * @return the created agent
	 * @throws IllegalArgumentException
	 *         if the constructor args don't match a constructor.
	 */
	public T nextWithArgs(Object... constructorArgs) throws IllegalArgumentException {
		Constructor constructor = findConstructor(constructorArgs);
		if (constructor == null)
			throw new IllegalArgumentException("Unable to find matching constructor for arguments");
		try {
			T obj = (T) constructor.newInstance(constructorArgs);
			return processNext(obj);
		} catch (InstantiationException e) {
			msg.error("Error creating agent instance from class", e);
		} catch (IllegalAccessException e) {
			msg.error("Error creating agent instance from class", e);
		} catch (InvocationTargetException e) {
			msg.error("Error creating agent instance from class", e);
		}

		return null;
	}

	/**
	 * Sets the specified object's properties to the relevant attributes values of the next feature.
	 * 
	 * @param obj
	 *        the object whose properties we want to set.
	 * @return the object with is properties now set.
	 */
	public T next(T obj) {
		return processNext(obj);
	}

	private Constructor findConstructor(Object[] constructorArgs) {
		Class[] args = new Class[constructorArgs.length];
		int i = 0;
		for (Object obj : constructorArgs) {
			args[i++] = obj.getClass();
		}

		for (Constructor constructor : agentClass.getConstructors()) {
			Class[] params = constructor.getParameterTypes();
			if (params.length == args.length) {
				boolean pass = true;
				for (i = 0; i < args.length; i++) {
					if (!isCompatible(params[i], args[i])) {
						pass = false;
						break;
					}
				}
				if (pass)
					return constructor;
			}
		}

		return null;
	}

	/**
	 * Returns true if there are more features left to process, otherwise false.
	 * 
	 * @return true if there are more features left to process, otherwise false.
	 */
	public boolean hasNext() {
		boolean hasnext = iter.hasNext();
		if (!hasnext) {
			iter.close();
			store.dispose();
		}
		return hasnext;
	}

	private T fillAgent(SimpleFeature feature, T agent) throws IllegalAccessException, InvocationTargetException {
		for (String attribName : attributeMethodMap.keySet()) {
			Object val = feature.getAttribute(attribName);
			Method write = attributeMethodMap.get(attribName);
			write.invoke(agent, val);
		}
		return agent;
	}
}
