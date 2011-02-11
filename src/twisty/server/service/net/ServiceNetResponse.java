package twisty.server.service.net;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.List;

public class ServiceNetResponse {
  
  /** The response content. */
  public String content;
  
  /** The response type. */
  public String contentType;
  
  /** The response code. */
  public int responseCode;
  
  /** The connection object. */
  public HttpURLConnection connection;

  /** The response headers. */
  public Map<String,List<String>> headers;
}
