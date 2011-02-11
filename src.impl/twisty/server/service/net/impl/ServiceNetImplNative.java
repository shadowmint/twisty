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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public ServiceNetResponse makeRequest(Method method, String url, Map<String,String> headers, Map<String, String> params) throws Exception {
		
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
		
		if (headers.get("Content-Type") == null) 
		  headers.put("Content-Type", "application/x-www-form-urlencoded");
		ServiceNetResponse response = makeRequest(method, url, headers, encoded);
		return(response);
	}
	
	@Override
	public ServiceNetResponse makeRequest(Method method, String url, Map<String,String> headers, String content) throws Exception {
		
		ServiceNetResponse rtn = new ServiceNetResponse();
		
		// Handle various methods
		BufferedReader rd = null;
		try {
		  
  		switch(method) {
  			case POST:
  				rtn.connection = post("POST", url, content, headers);
  				break;
  			case GET: 
  				rtn.connection = get(url, content, headers);
  				break;
  			case PUT:
  				rtn.connection = post("PUT", url, content, headers);
  				break;
  			case DELETE:
  				rtn.connection = post("DELETE", url, content, headers);
  				break;
  		}
  		
  	  // Get the response
  	  rtn.responseCode = rtn.connection.getResponseCode();
  		rtn.content = "";
  	  rd = new BufferedReader(new InputStreamReader(rtn.connection.getInputStream()));
  	  String line;
  	  while ((line = rd.readLine()) != null) {
  	  	rtn.content += line;
  	  }
  	  
  	  // Get the reponse headers
  	  Map<String,List<String>> rtnHeaders = rtn.connection.getHeaderFields();
  	  rtn.headers = new HashMap<String,List<String>>();
  	  for (String header : rtnHeaders.keySet()) {
  	    rtn.headers.put(header, new ArrayList<String>(rtnHeaders.get(header)));
  	  }
  	  
  	  rtn.contentType = rtn.connection.getContentType();
		}
		catch(IOException e) {
		  rtn.content = e.toString();
		}
		
	  if (rd != null)
  	  rd.close();
		
	  return(rtn);
	}
	
	private HttpURLConnection post(String method, String url, String content, Map<String,String> headers) throws Exception {
	    URL urlObject = new URL(url);
	    HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
	    conn.setUseCaches(false);
	    OutputStreamWriter wr = null;
    	conn.setRequestMethod(method);
	    conn.setDoOutput(true);
	    for(String key : headers.keySet()) {
  	    conn.setRequestProperty(key, headers.get(key));
	    }
	    wr = new OutputStreamWriter(conn.getOutputStream());
	    if (content.length() > 0)
  	    wr.write(content);
	    wr.flush();
	    wr.close();
	    return(conn);
	}
	
	private HttpURLConnection get(String url, String encodedParams, Map<String,String> headers) throws Exception {
		if (url.indexOf('?') != -1)
			url = url.substring(0, url.indexOf('?'));
		if (encodedParams.length() > 0)
  		url += "?" + encodedParams;
		HttpURLConnection conn = post("GET", url, "", headers);
	  return(conn);
	}
}
