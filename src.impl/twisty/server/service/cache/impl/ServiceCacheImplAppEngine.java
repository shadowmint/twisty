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

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * Interface for handling cache requests on app engine.
 * <p>
 * Implementation based on:<br/>
 * <li> com.google.appengine.api.memcache.MemcacheService
 */
public class ServiceCacheImplAppEngine extends ServiceImpl implements ServiceCache {

	/** The cache api. */
	private MemcacheService memcache = null;

	public String getServiceName() {
		return("Cache");
	}

	public String getServiceImpl() {
		return("AppEngine memcache based implementation.");
	}

	/** Create the service if required and returns it. */
	private MemcacheService api() {
		if (memcache == null)
			memcache = MemcacheServiceFactory.getMemcacheService();
		return(memcache);
	}

	@Override
	public void set(Object key, Object value) {
		api().put(key, value);
	}

	@Override
	public void set(Object key, Object value, int expires) {
		Expiration x = Expiration.byDeltaSeconds(expires);
		api().put(key, value, x);
	}

	@Override
	public Object get(Object key) {
		return(api().get(key));
	}

	@Override
	public boolean contains(Object key) {
		return(api().contains(key));
	}
}
