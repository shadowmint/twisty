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

package twisty.server.service;

import javax.servlet.ServletConfig;

import com.google.inject.AbstractModule;

/** 
 * Returns a guice module from the client code. 
 * <p>
 * The servlet config should define the context param "ServiceGuiceModule",
 * which is created and returned.
 * <p>
 * The irony of having a factory to create a guice module is not lost here.
 */
public class ServiceModuleProvider {
	
	/** Caches the service module for other classes to use. */
	private static AbstractServiceModule instance = null;
	
	/** Creates a service module from a servlet config file, and caches the result. */
	public static AbstractModule getModule(ServletConfig config) throws Exception {
		String module = config.getServletContext().getInitParameter("ServiceGuiceModule");
		if (module != null) {
			Class<?> template = Class.forName(module);
			if (template != null) 
				instance = (AbstractServiceModule) template.newInstance();
		}
		return(instance);
	}
	
	/** Returns the service module, or null if it has not been created yet. */
	public static AbstractServiceModule get() throws Exception {
		if (instance == null) 
			throw new Exception("No service module has been created.");
		return(instance);
	}
}
