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

package twisty.server.service.crypt.impl;

import twisty.server.service.crypt.ServiceCrypt;
import twisty.server.service.ServiceImpl;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.digest.StandardStringDigester;
import org.jasypt.util.password.StrongPasswordEncryptor;

/** 
 * Interface for encryption.
 * <p>
 * Implementation is based on:<br/>
 * <li> http://www.jasypt.org/
 * <li> http://commons.apache.org/codec/
 */
public class ServiceCryptImplJasypt extends ServiceImpl implements ServiceCrypt {
	
	/** The iteration counter for the sha-512 hash. */
	private int interations = 1;
	
	public String getServiceName() {
		return("Crypt");
	}
	
	public String getServiceImpl() {
		return("Service based on http://www.jasypt.org/ and http://commons.apache.org/codec/ implementation.");
	}
	
	/** Sets the interation counter. */
	public void setIterations(int iterations) {
		this.interations = iterations;
	}
	
	/** Creates apis. */
	@Override 
	public void init() throws Exception {
		super.init();
	}

	/** Invokes the jasypt password encryption function (sha-256, 10000 iterations). */
	@Override
	public String passwd(String raw) {
		StrongPasswordEncryptor passwdFactory = new StrongPasswordEncryptor();
		String rtn = passwdFactory.encryptPassword(raw);
		return(rtn);
	}
	
	/** Compares the passwd given to a hashed value; use this, don't rehash. */
	public boolean check(String passwd, String hash) {
		StrongPasswordEncryptor passwdFactory = new StrongPasswordEncryptor();
		boolean rtn = passwdFactory.checkPassword(passwd, hash);
		return(rtn);
	}

	/** Invokes the jasypt sha256 digest function and returns a hex coded string. */
	@Override
	public String sha512(String target) {
		StandardStringDigester digestFactory = new StandardStringDigester();
		digestFactory.setAlgorithm("SHA-512");
		digestFactory.setIterations(interations);
		digestFactory.setSaltSizeBytes(0);
		digestFactory.setStringOutputType("hexadecimal");
		String rtn = digestFactory.digest(target).toLowerCase();
		return(rtn);
	}
	
	/** Invokes the jasypt md5 digest function and returns a hex coded string. */
	@Override
	public String md5(String target) {
		StandardStringDigester digestFactory = new StandardStringDigester();
		digestFactory.setAlgorithm("MD5");
		digestFactory.setIterations(interations);
		digestFactory.setSaltSizeBytes(0);
		digestFactory.setStringOutputType("hexadecimal");
		String rtn = digestFactory.digest(target).toLowerCase();
		return(rtn);
	}

	@Override
	public String decodeBase64(String target) {
        String rtn = new String(Base64.decodeBase64(target.getBytes()));
		return(rtn);
	}

	@Override
	public String encodeBase64(String target) {
        String rtn = new String(Base64.encodeBase64(target.getBytes(), false));
		return(rtn);
	}
}
