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

package twisty.server.service.net;

import java.util.HashMap;

import twisty.server.service.Service;

public interface ServiceNet extends Service {
			
	/** Allowed methods. */
	public enum Method {
		GET, PUT, POST, DELETE
	};
	
	/** Performs a fetch operation on a remote url. */
	public ServiceNetResponse makeRequest(Method method, String url, HashMap<String,String> params) throws Exception;
	
	/** Performs a fetch operation on a remote url. */
	public ServiceNetResponse makeRequest(Method method, String url, String payload, String contentType) throws Exception;
}
