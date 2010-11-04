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

package twisty.server.service.smpt.impl;

import twisty.server.service.ServiceImpl;
import twisty.server.service.smtp.ServiceSmtp;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/** 
 * Interface for sending mail. 
 * <p>
 * Implementation based on:<br/>
 * <li> http://java.sun.com/products/javamail/ 
 */
public class ServiceSmtpImplGAE extends ServiceImpl implements ServiceSmtp {
	
	public String getServiceName() {
		return("Smtp");
	}
	
	public String getServiceImpl() {
		return("Google App Engine (http://code.google.com/appengine/)");
	}
	
	/** Returns a default template to work with. */
	public HashMap<String, String> template() {
		HashMap<String, String> rtn = new HashMap<String, String>();
		rtn.put(ServiceSmtp.TO, "");
		rtn.put(ServiceSmtp.FROM, "");
		rtn.put(ServiceSmtp.BODY, "");
		rtn.put(ServiceSmtp.SUBJECT, "");
		rtn.put(ServiceSmtp.MIME_TYPE, "text/plain");
		rtn.put(ServiceSmtp.HOST, null);
		rtn.put(ServiceSmtp.AUTH_USERNAME, null);
		rtn.put(ServiceSmtp.AUTH_PASSWORD, null);
		return(rtn);
	}
	
	/** 
	 * Sends a message based on the properties of map.
	 * <p>
	 * Attempts to use auth and host if specified.
	 * @param message The set of properties.
	 * @throws Exception
	 */
	public void send(Map<String, String> message) throws Exception {
		
		// Setup mail server
		Properties props = System.getProperties();
		if (message.get(ServiceSmtp.HOST) != null)
			props.put("mail.smtp.host", message.get(ServiceSmtp.HOST));

		// Setup auth
		Authenticator auth = null;
		if ((message.get(ServiceSmtp.AUTH_PASSWORD) != null) && (message.get(ServiceSmtp.AUTH_USERNAME) != null)) {
			final String username = message.get(ServiceSmtp.AUTH_USERNAME);
			final String password = message.get(ServiceSmtp.AUTH_PASSWORD);
			auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
				    return new PasswordAuthentication(username, password);
			    }
			};
		}
		Session session = Session.getDefaultInstance(props, auth);

		// Define message
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(message.get(ServiceSmtp.FROM)));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(message.get(ServiceSmtp.TO)));
		msg.setSubject(message.get(ServiceSmtp.SUBJECT));
		msg.setContent(message.get(ServiceSmtp.BODY), message.get(ServiceSmtp.MIME_TYPE));

		// Send message
		Transport.send(msg);
	}
}
