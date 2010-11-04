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

package twisty.server.service.cache;

import twisty.server.service.Service;

public interface ServiceCache extends Service {

	/** Caches an object for as long as possible. */
	public void set(Object key, Object value);
	
	/** Caches an object with expiry in seconds. */
	public void set(Object key, Object value, int expires);

	/** Fetches a cached object. */
	public Object get(Object key);
	
	/** Returns true if a key exists. */
	public boolean contains(Object key);
}
