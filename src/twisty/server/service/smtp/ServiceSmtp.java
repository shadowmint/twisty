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

package twisty.server.service.smtp;

import java.util.HashMap;
import java.util.Map;

import twisty.server.service.Service;


public interface ServiceSmtp extends Service {
	
	/** Proprety for the mail host. */
	public static String HOST = "host";
	
	/** Property for the from address. */
	public static String FROM = "from";
	
	/** Property for the to address. */
	public static String TO = "to";
	
	/** Property for the subject. */
	public static String SUBJECT = "subject";
	
	/** Property for the body. */
	public static String BODY = "body";
	
	/** Property for the auth username. */
	public static String AUTH_USERNAME = "auth_username";
	
	/** Property for the auth password. */
	public static String AUTH_PASSWORD = "auth_password";
	
	/** The MIME type for the sent message. */
	public static String MIME_TYPE = "mime_type";
	
	/** 
	 * Returns a template to use for sending messages.
	 * <p>
	 * The hash has all the fields required set to default
	 * values. 
     * @return An email template.
	 */
	public HashMap<String, String> template();
	
	/**
	 * Dispatches an email based on the properties of the hash passed in. 
	 * <p>
	 * The fields that are required in the hash passed in must match
	 * those defined in the public constants of this interface.
	 * <p>
	 * The follow properties are option and are ignored if not specified:<br/>
	 * HOST, AUTH_USERNAME, AUTH_PASSWORD
	 * <p>
	 * On failure an exception will be raised.
	 * @param message The set of properties of the email to send.
	 * @throws Exception
	 */
	public void send(Map<String, String> message) throws Exception;
}
