package twisty.client.utils;

import com.google.gwt.http.client.*;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Useful for making ajax requests that might have to be cancelled. 
 * <p>
 * TODO: Ajax: Make this easier to user and less shit.
 * <p>
 * Add <inherits name='com.google.gwt.http.HTTP'/> to your config XML to use this.
 */
public abstract class Ajax implements RequestCallback {
	
	/** If the request was successful */
	public boolean success = false;
	
	/** If the request has been completed. */
	public boolean completed = false;
	
	/** What the response was (or error message on failure) */
	public String response = ""; 
	
	/** The request object that is making the request. */
	private Request request = null;
	
	/** Context specific information */
	public Object context = null;
	
	/** Url request was made to. Don't set this; it won't do anything. */
	public String url = null; 
	
	/** The arguments for this request. */
	public HashMap<String, String> args = new HashMap<String, String>();

	/** Requests a specific URL be fetched. */
	public void fetch(String url) {
		fetch(url, "POST");
	}
	
	/** Builds a url encoded request string. */
	private String params() {
		String rtn = "";
		for(Entry<String, String> item : args.entrySet()) {
			String key = item.getKey();
			String value = item.getValue();
			if (rtn.length() == 0)
				rtn = URL.encode(key) + "=" + URL.encode(value);
			else
				rtn += "&" + URL.encode(key) + "=" + URL.encode(value);
		}
		return(rtn);
	}

	/** Fetchs a URL either via POST (default) or GET */
	public void fetch(String url, String method) {
		abort();
		RequestBuilder builder;
		String postData = null;
		if (method == "GET") {
			if (args.size() > 0) {
				url += "?" + params();
			}
			builder = new RequestBuilder(RequestBuilder.GET, url);
		}
		else {
			builder = new RequestBuilder(RequestBuilder.POST, url);
			builder.setHeader("Content-type", "application/x-www-form-urlencoded");
			postData = params();
		}
		
		try {
			this.url = url; 
			success = false;
			completed = false;
			response = "";
			request = builder.sendRequest(postData, this);
		} 
		catch (Exception e) {
			success = false;
			completed = true;
			response = e.toString();
			callback();        
		}
	}
	
	/** Invoked on errors. */
	public void onError(Request request, Throwable exception) {
		success = false;
		completed = true;
		response = exception.toString();
		request = null;
		callback();
	}

	public void onResponseReceived(Request request, Response response) {
		if (200 == response.getStatusCode()) {
			success = true;
			completed = true;
			this.response = response.getText();
			request = null;
			callback();
		} 
		else {
			success = false;
			completed = true;
			this.response = response.getStatusText();
			request = null;
			callback();
		}
	}
	
	/** Stops a pending request. */
	public void abort() {
		if (request != null) {
			request.cancel();
			request = null;
			success = false;
			completed = false;
			response = "";
		}
	}
		
	/** To be extended by any ajax listeners. */
	public abstract void callback();
}
