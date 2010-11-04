/** 
 * Copyright 2010 Douglas Linder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package twisty.server.service.data.impl;

import twisty.server.service.data.ServiceData;
import twisty.server.service.ServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** 
 * Interface for data persistence.
 * <p>
 * Implementation is based on Google App Engine.
 * <p>
 * Configuration details like this are required in the folder:<br/>
 * /WEB-INF/classes/META-INF/persistence.xml
 * <p>
 * <pre>
 *		<?xml version="1.0" encoding="UTF-8" ?>
 *		<persistence xmlns="http://java.sun.com/xml/ns/persistence"
 *		    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *		    xsi:schemaLocation="http://java.sun.com/xml/ns/persistence
 *		        http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd" version="1.0">
 *		
 *		    <persistence-unit name="appengine">
 *		        <provider>org.datanucleus.store.appengine.jpa.DatastorePersistenceProvider</provider>
 *		        <properties>
 *		            <property name="datanucleus.NontransactionalRead" value="true"/>
 *		            <property name="datanucleus.NontransactionalWrite" value="true"/>
 *		            <property name="datanucleus.ConnectionURL" value="appengine"/>
 *		        </properties>
 *		    </persistence-unit>
 *		</persistence>
 * </pre>
 * <p>
 * This implementation ignores the URL, USERNAME and PASSWORD fields.
 * Additionally, it is not necessary to pre-register classes before
 * persisting them using register.
 * <p>
 * However, only the app engine data store is supported for persistence.
 */
public class ServiceDataImplGAE extends ServiceImpl implements ServiceData {
	
	/** Persistence manager. */
	EntityManager entityManager = null;
	
	/** Set of classes registered as persistable. */
	ArrayList<Class<?>> classRegister = new ArrayList<Class<?>>();
	
	public String getServiceName() {
		return("Data");
	}
	
	public String getServiceImpl() {
		return("Google App Engine (http://code.google.com/appengine/)");
	}
	
	@Override 
	public void init() throws Exception {
		super.init();
	}
	
	/** Connects to the database and registers persistable classes. */
	public void connect(Map<String, String> properties) throws Exception {
		if (entityManager == null) {
			HashMap<String, String> props = new HashMap<String, String>();
	        props.put("datanucleus.NontransactionalRead", "true");
	        props.put("datanucleus.NontransactionalWrite", "true");
	        props.put("datanucleus.ConnectionURL", "appengine");
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(properties.get(ServiceData.PROFILE), props);
			entityManager = emf.createEntityManager();
		}
		else
			throw new Exception("Connection has already been established to the database.");
	}
	
	/** Registers a persistable class. */
	public void registerClass(Class<?> target) {
		classRegister.add(target);
	}
	
	/** Returns a class definition list. */
	public String getClassList() {
		String rtn = "";
        StringBuffer classNames = new StringBuffer(); 
        for (Class<?> c : classRegister) { 
	        if (classNames.length() > 0) 
	            classNames.append(";"); 
            classNames.append(c.getName()); 
        } 
        rtn = classNames.toString();
        return(rtn);
	}
	
	/** 
	 * Returns a connection template. 
	 * <p>
	 * The correct field is pre-populated if the registerClass()
	 * call has been made before this is invoked.
	 */
	public HashMap<String, String> getConnectionTemplate() {
		HashMap<String, String> rtn = new HashMap<String, String>();
		rtn.put(ServiceData.USERNAME, "");
		rtn.put(ServiceData.PASSWORD, "");
		rtn.put(ServiceData.PROFILE, "appengine");
		rtn.put(ServiceData.URL, "");
		rtn.put(ServiceData.CLASSES, getClassList());
		return(rtn);
	}
	
	/** Get the JPA Entity manage to work with. */
	public EntityManager getEntityManager() throws Exception {
		if (entityManager == null)
			throw new Exception("Database connection has not been established.");
		return(entityManager);
	}
}
