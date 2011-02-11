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

package twisty.server.service.cache.impl;

import twisty.server.service.ServiceImpl;
import twisty.server.service.cache.ServiceCache;

/**
 * Interface for not really handling cache requests.
 * <p>
 * Implementation based on:<br/>
 * <li> Dummy! No caching here.
 */
public class ServiceCacheImplDummy extends ServiceImpl implements ServiceCache {

	public String getServiceName() {
		return("Cache");
	}

	public String getServiceImpl() {
		return("Dummy implementation.");
	}

	@Override
	public void set(Object key, Object value) {}

	@Override
	public void set(Object key, Object value, int expires) {}

	@Override
	public Object get(Object key) {
	  return(null);
	}

	@Override
	public boolean contains(Object key) { 
	  return(false);
	}
}
