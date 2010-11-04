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

package twisty.server.service.net.impl;

import twisty.server.service.ServiceImpl;
import twisty.server.service.net.ServiceNet;
import twisty.server.service.net.ServiceNetResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/** 
 * Interface for handling remote urls.
 * <p>
 * Implementation based on:<br/>
 * <li> Native java.net.URL.
 */
public class ServiceNetImplNative extends ServiceImpl implements ServiceNet {
	
	public String getServiceName() {
		return("Net");
	}
	
	public String getServiceImpl() {
		return("Native java.net.URL based implementation.");
	}

	@Override
	public ServiceNetResponse makeRequest(Method method, String url, HashMap<String, String> params) throws Exception {
		
		// Prepare data
		String encoded = "";
		for (String key : params.keySet()) {
			String param = params.get(key);
			String value = "";
			if ((param != null) && (param.length() > 0))
				value = URLEncoder.encode(params.get(key), "UTF-8");
			if (encoded.length() != 0)
				encoded += "&";
			encoded += key + "=" + value;
		}
		
		ServiceNetResponse response = makeRequest(method, url, encoded, "application/x-www-form-urlencoded");
		return(response);
	}
	
	@Override
	public ServiceNetResponse makeRequest(Method method, String url, String content, String contentType) throws Exception {
		
		ServiceNetResponse rtn = new ServiceNetResponse();
		
		// Handle various methods
		switch(method) {
			case POST:
				rtn.connection = post("POST", url, content, contentType);
				break;
			case GET: 
				rtn.connection = get(url, content);
				break;
			case PUT:
				rtn.connection = post("PUT", url, content, contentType);
				break;
			case DELETE:
				rtn.connection = post("DELETE", url, content, contentType);
				break;
		}
		
	  // Get the response
		rtn.content = "";
	  BufferedReader rd = new BufferedReader(new InputStreamReader(rtn.connection.getInputStream()));
	  String line;
	  while ((line = rd.readLine()) != null) {
	  	rtn.content += line;
	  }
	  rtn.contentType = rtn.connection.getContentType();
	  rtn.responseCode = rtn.connection.getResponseCode();
	    
	  if (rtn.connection.getOutputStream() != null)
		  rtn.connection.getOutputStream().close();
	  rd.close();
	    
	  return(rtn);
	}
	
	private HttpURLConnection post(String method, String url, String content, String contentType) throws Exception {
	    URL urlObject = new URL(url);
	    HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
	    conn.setUseCaches(false);
	    OutputStreamWriter wr = null;
	    if (content.length() != 0) {
	    	conn.setRequestMethod(method);
		    conn.setDoOutput(true);
		    conn.setRequestProperty("Content-Type", contentType);
		    wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(content);
		    wr.flush();
	    }
	    return(conn);
	}
	
	private HttpURLConnection get(String url, String encodedParams) throws Exception {
		if (url.indexOf('?') != -1)
			url = url.substring(0, url.indexOf('?'));
		url += "?" + encodedParams;
	    URL urlObject = new URL(url);
	    HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
	    conn.setDoOutput(true);
	    conn.setUseCaches(false);
	    return(conn);
	}
}
