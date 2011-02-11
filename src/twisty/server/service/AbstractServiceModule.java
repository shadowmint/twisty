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

import java.util.HashMap;

import com.google.inject.AbstractModule;

/** 
 * Service config; to be implemented by client code. 
 * <p>
 * The configure function should define implementations for the 
 * various service classes:
 * <ul>
 * <li> ServiceData
 * <li> ServiceCrypt
 * <li> ServiceNet
 * <li> ServiceSmtp
 * </ul>
 * <p>
 * Different service implementations will require that different configuration
 * parameters are present; these values should be set when the service module
 * is created.
 */
public abstract class AbstractServiceModule extends AbstractModule {
	
	/** The configuration paramaters for the module. */
	private HashMap<String,Object> params = new HashMap<String, Object>();
	
	/** Return a configuration parameter. */
	public Object getParam(String param) throws Exception {
		Object rtn = params.get(param);
		if (rtn == null) 
			throw new Exception("Missing config param in service module: " + param);
		return(rtn);
	}
	
	/** Registers a configuration parameter. */
	protected void setParam(String param, Object value) {
		params.put(param, value);
	}
}
