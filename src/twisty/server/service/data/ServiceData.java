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

package twisty.server.service.data;

import java.util.HashMap;
import java.util.Map;

import twisty.server.service.Service;

public interface ServiceData extends Service {
			
	/** Url to connect to the database with. eg. localhost/database */
	public static String URL = "connection_url";
	
	/** Username to auth on the database with. */
	public static String USERNAME = "username";
	
	/** Password to auth  on the database with. */
	public static String PASSWORD = "password";
	
	/** Set of classes to register as persistable, in Class;Class;Class format. */
	public static String CLASSES = "classes";
	
	/** The name of the database profile to load. */
	public static String PROFILE = "profile";
			
	/** Connects to the database and registers persistable classes. */
	public void connect(Map<String, String> properties) throws Exception;
	
	/** 
	 * Registers a persistable class. 
	 * <p>
	 * This does not actual make classes persistable; persistable classes
	 * should be passed into connect() with the properties object using the
	 * CLASSES property.
	 * @see #getClassList
	 */
	public void registerClass(Class<?> target);
	
	/** 
	 * Returns a class definition list.
	 * <p> 
	 * The returned list is suitable for use as the CLASSES property 
	 * to the connect() function. 
	 */
	public String getClassList();
	
	/** 
	 * Returns a connection template. 
	 * <p>
	 * The correct field is pre-populated if the registerClass()
	 * call has been made before this is invoked.
	 */
	public HashMap<String, String> getConnectionTemplate();
	
	/** Get the JPA Entity manage to work with. */
	public Object getEntityManager() throws Exception;
}
