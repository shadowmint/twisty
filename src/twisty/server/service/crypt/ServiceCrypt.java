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

package twisty.server.service.crypt;

import twisty.server.service.Service;

public interface ServiceCrypt extends Service {
			
	/** Performs a SHA-512 digest on the given string. */
	public String sha512(String target);
	
	/** Performs an MD5 digest on the given string. */
	public String md5(String target);
	
	/** Performs a base64 encoding run on the given string. */
	public String encodeBase64(String target);
	
	/** Performs a base64 decoding run on the given string. */
	public String decodeBase64(String target);
	
	/** Performs a strong password encrypt on the given string, result is base64 encoded. */
	public String passwd(String raw);
	
	/** Set the iteration counter for crypt actions. */
	public void setIterations(int iterations);
	
	/** Returns true if the passwd matches the encrypted passwd in hash. */
	public boolean check(String passwd, String hash);
}
