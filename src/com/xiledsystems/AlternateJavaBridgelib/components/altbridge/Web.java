package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;


import android.text.TextUtils;
import org.json.JSONException;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Maps;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.AsynchUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.FileUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.GingerbreadUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.SdkLevel;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.WebHelper;
import com.xiledsystems.AlternateJavaBridgelib.components.common.HtmlEntities;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;
import com.xiledsystems.AlternateJavaBridgelib.components.util.JsonUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The Web component provides functions for HTTP GET and POST requests.
 *
 */

public class Web extends AndroidNonvisibleComponent implements Component {

 
  /**
   * InvalidRequestHeadersException can be thrown from processRequestHeaders.
   * It is thrown if the list passed to processRequestHeaders contains an item that is not a list.
   * It is thrown if the list passed to processRequestHeaders contains an item that is a list whose
   * size is not 2.
   */
  private static class InvalidRequestHeadersException extends Exception {
    /*
     * errorNumber could be:
     * ErrorMessages.ERROR_WEB_REQUEST_HEADER_NOT_LIST
     * ErrorMessages.ERROR_WEB_REQUEST_HEADER_NOT_TWO_ELEMENTS
     */
    final int errorNumber;
    final int index;         // the index of the invalid header
    	InvalidRequestHeadersException(int errorNumber, int index) {
    		super();
    		this.errorNumber = errorNumber;
    		this.index = index;
    	}
  }
  
  /**
   * BuildPostDataException can be thrown from buildPostData.
   * It is thrown if the list passed to buildPostData contains an item that is not a list.
   * It is thrown if the list passed to buildPostData contains an item that is a list whose size is
   * not 2.
   */
  // VisibleForTesting
  static class BuildPostDataException extends Exception {
    /*
     * errorNumber could be:
     * ErrorMessages.ERROR_WEB_BUILD_POST_DATA_NOT_LIST
     * ErrorMessages.ERROR_WEB_BUILD_POST_DATA_NOT_TWO_ELEMENTS
     */
    final int errorNumber;
    final int index;         // the index of the invalid header

    BuildPostDataException(int errorNumber, int index) {
      super();
      this.errorNumber = errorNumber;
      this.index = index;
    }
  }

  /**
   * The CapturedProperties class captures the current property values from a Web component before
   * an asynchronous request is made. This avoids concurrency problems if the user changes a
   * property value after initiating an asynchronous request.
   */
  private static class CapturedProperties {
    final String urlString;
    final URL url;
    final boolean allowCookies;
    final boolean saveResponse;
    final String responseFileName;
    final Map<String, List<String>> requestHeaders;
    final Map<String, List<String>> cookies;

    CapturedProperties(Web web) throws MalformedURLException, InvalidRequestHeadersException {
      urlString = web.urlString;
      url = new URL(urlString);
      allowCookies = web.allowCookies;
      saveResponse = web.saveResponse;
      responseFileName = web.responseFileName;
      requestHeaders = processRequestHeaders(web.requestHeaders);

      Map<String, List<String>> cookiesTemp = null;
      if (allowCookies && web.cookieHandler != null) {
        try {
          cookiesTemp = web.cookieHandler.get(url.toURI(), requestHeaders);
        } catch (URISyntaxException e) {
          // Can't convert the URL to a URI; no cookies for you.
        } catch (IOException e) {
          // Sorry, no cookies for you.
        }
      }
      cookies = cookiesTemp;
    }
  }

  private static final String LOG_TAG = "Web";
  private boolean isaService = false;

  
  private static final Map<String, Character> htmlCharacterEntities;
  static {
    htmlCharacterEntities = new HashMap<String, Character>();
    htmlCharacterEntities.put("quot", '"');
    htmlCharacterEntities.put("amp", '&');
    htmlCharacterEntities.put("apos", '\'');
    htmlCharacterEntities.put("lt", '<');
    htmlCharacterEntities.put("gt", '>');
    // TODO(user) - consider adding more entities.
    // According to http://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
    // the HTML 4 DTDs define 252 named entities.
  }

  private static final Map<String, String> mimeTypeToExtension;
  static {
    mimeTypeToExtension = new HashMap<String, String>();
    mimeTypeToExtension.put("application/pdf", "pdf");
    mimeTypeToExtension.put("application/zip", "zip");
    mimeTypeToExtension.put("audio/mpeg", "mpeg");
    mimeTypeToExtension.put("audio/mp3", "mp3");
    mimeTypeToExtension.put("audio/mp4", "mp4");
    mimeTypeToExtension.put("image/gif", "gif");
    mimeTypeToExtension.put("image/jpeg", "jpg");
    mimeTypeToExtension.put("image/png", "png");
    mimeTypeToExtension.put("image/tiff", "tiff");
    mimeTypeToExtension.put("text/plain", "txt");
    mimeTypeToExtension.put("text/html", "html");
    mimeTypeToExtension.put("text/xml", "xml");
    // TODO(user) - consider adding more mime types.
  }
  
  private String urlString = "";
  private boolean saveResponse;
  private boolean allowCookies;
  private WebHelper requestHeaders = new WebHelper();
  private String responseFileName = "";
  private final CookieHandler cookieHandler;

  /**
   * Creates a new Web component.
   *
   * @param container the Form that this component is contained in.
   */
  public Web(ComponentContainer container) {
    super(container);
    
    cookieHandler = (SdkLevel.getLevel() >= SdkLevel.LEVEL_GINGERBREAD) ? 
    		GingerbreadUtil.newCookieManager() : null;
    
  }
  
  public Web(SvcComponentContainer container) {
	  super(container);
	  
	  cookieHandler = (SdkLevel.getLevel() >= SdkLevel.LEVEL_GINGERBREAD) ? 
	    		GingerbreadUtil.newCookieManager() : null;
	   isaService = true;	 
  }

  /**
   * This constructor is for testing purposes only.
   * It is broken because of the implementation of
   * the service ability.
   */
  /*protected Web() {
    super(null);
    activity = null;
  }
*/
  /**
   * Returns the URL.
   */
  
  public String Url() {
    return urlString;
  }

  /**
   * Specifies the URL.
   */
  
  public void Url(String url) {
    urlString = url;
  }
  
  /**
   * Returns the request headers.
   */
  public WebHelper RequestHeaders() {
	    return requestHeaders;
  }
  
  /**
   *  Sets the request headers.
   *  
   * @param headers WebHelper instance containing the header names, and values
   */
  public void RequestHeaders(WebHelper headers) {
	// Call processRequestHeaders to validate the list parameter before setting the requestHeaders
    // field.
	  try {
		  processRequestHeaders(headers);
		  requestHeaders = headers;
	  } catch (InvalidRequestHeadersException e) {
		  if (isaService) {
			  sContainer.$formService().dispatchErrorOccurredEvent(this, "RequestHeaders", e.errorNumber, e.index);
		  } else {
			  container.$form().dispatchErrorOccurredEvent(this, "RequestHeaders", e.errorNumber, e.index);
		  }
	  }
  }
  
  /**
   * Returns whether cookies should be allowed
   */
  public boolean AllowCookies() {
	  return allowCookies;
  }
  
  /**
   * Specifies whether cookies should be allowed
   */
  public void AllowCookies(boolean allowCookies) {
	  this.allowCookies = allowCookies;
	  if (allowCookies && cookieHandler == null) {
		  if (isaService) {
			  sContainer.$formService().dispatchErrorOccurredEvent(this, "AllowCookies", ErrorMessages.ERROR_FUNCTIONALITY_NOT_SUPPORTED_WEB_COOKIES);
		  } else {
			  container.$form().dispatchErrorOccurredEvent(this, "AllowCookies", ErrorMessages.ERROR_FUNCTIONALITY_NOT_SUPPORTED_WEB_COOKIES);
		  }
	  }
  }


  /**
   * Returns whether the response should be saved in a file.
   */
  
  public boolean SaveResponse() {
    return saveResponse;
  }

  /**
   * Specifies whether the response should be saved in a file.
   */
  
  public void SaveResponse(boolean saveResponse) {
    this.saveResponse = saveResponse;
  }

  /**
   * Returns the name of the file where the response should be saved.
   * If SaveResponse is true and ResponseFileName is empty, then a new file
   * name will be generated.
   */
  
  public String ResponseFileName() {
    return responseFileName;
  }

  /**
   * Specifies the name of the file where the response should be saved.
   * If SaveResponse is true and ResponseFileName is empty, then a new file
   * name will be generated.
   */
 
  public void ResponseFileName(String responseFileName) {
    this.responseFileName = responseFileName;
  }
  
  public void ClearCookies() {
	  if (cookieHandler != null) {
		  GingerbreadUtil.clearCookies(cookieHandler);
	  } else {
		  if (isaService) {
			  sContainer.$formService().dispatchErrorOccurredEvent(this, "ClearCookies", ErrorMessages.ERROR_FUNCTIONALITY_NOT_SUPPORTED_WEB_COOKIES);
		  } else {
			  container.$form().dispatchErrorOccurredEvent(this, "ClearCookies", ErrorMessages.ERROR_FUNCTIONALITY_NOT_SUPPORTED_WEB_COOKIES);
		  }
	  }
  }

  /**
   * Performs an HTTP GET request using the Url property and retrieves the
   * response.<br>
   * If the SaveResponse property is true, the response will be saved in a file
   * and the GotFile event will be triggered. The ResponseFileName property
   * can be used to specify the name of the file.<br>
   * If the SaveResponse property is false, the GotText event will be
   * triggered.
   */
  
  public void Get() {
    // Capture property values in local variables before running asynchronously.
    final String urlString = this.urlString;
    final boolean saveResponse = this.saveResponse;
    final String responseFileName = this.responseFileName;
    
    final CapturedProperties webProps = capturePropertyValues("Get");
    if (webProps == null) {
    	return;
    }

    AsynchUtil.runAsynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          performRequest(webProps, null, null);
        } catch (FileUtil.FileException e) {
        	if (isaService) {
        		sContainer.$formService().dispatchErrorOccurredEvent(Web.this, "Get", e.getErrorMessageNumber());
        	} else {
        		container.$form().dispatchErrorOccurredEvent(Web.this, "Get",
        				e.getErrorMessageNumber());
        	}
        } catch (Exception e) {
        	if (isaService) {
        		sContainer.$formService().dispatchErrorOccurredEvent(Web.this, "Get", ErrorMessages.ERROR_WEB_UNABLE_TO_GET, webProps.urlString);
        	} else {
        		container.$form().dispatchErrorOccurredEvent(Web.this, "Get",
        				ErrorMessages.ERROR_WEB_UNABLE_TO_GET, webProps.urlString);
        	}          
        }
      }
    });
  }

  /**
   * Performs an HTTP POST request using the Url property and the specified text.
   *
   * @param text the text data for the POST request
   */
  public void PostText(final String text) {
	  postTextImpl(text, "UTF-8", "PostText");
  }
  
  /**
   * Performs an HTTP POST request using the Url property and the specified text.
   *
   * @param text the text data for the POST request
   * @param encoding the character encoding to use when sending the text. If
   *                 encoding is empty or null, UTF-8 encoding will be used.
   */
  public void PostTextWithEncoding(final String text, final String encoding) {
	  postTextImpl(text, encoding, "PostTextWithEncoding");
  }
  
  /**
   * Performs an HTTP POST request using the Url property and the specified
   * text, and retrieves the response asynchronously.<br>
   * The characters of the text are encoded using the given encoding.<br>
   * If the SaveResponse property is true, the response will be saved in a file
   * and the GotFile event will be triggered. The ResponseFileName property
   * can be used to specify the name of the file.<br>
   * If the SaveResponse property is false, the GotText event will be
   * triggered.
   *
   * @param text the text data for the POST request
   * @param encoding the character encoding to use when sending the text. If
   *                 encoding is empty or null, UTF-8 encoding will be used.
   * @param functionName the name of the function, used when dispatching errors
   */
  private void postTextImpl(final String text, final String encoding, final String functionName) {
	// Capture property values before running asynchronously.
	    final CapturedProperties webProps = capturePropertyValues(functionName);
	    if (webProps == null) {
	      // capturePropertyValues has already called form.dispatchErrorOccurredEvent
	      return;
	    }

	    AsynchUtil.runAsynchronously(new Runnable() {
	      @Override
	      public void run() {
	        // Convert text to bytes using the encoding.
	        byte[] postData;
	        try {
	          if (encoding == null || encoding.length() == 0) {
	            postData = text.getBytes("UTF-8");
	          } else {
	            postData = text.getBytes(encoding);
	          }
	        } catch (UnsupportedEncodingException e) {
	        	if (isaService) {
	        		sContainer.$formService().dispatchErrorOccurredEvent(Web.this, functionName,
	        				ErrorMessages.ERROR_WEB_UNSUPPORTED_ENCODING, encoding);
	        	} else {
	        		container.$form().dispatchErrorOccurredEvent(Web.this, functionName,
	        				ErrorMessages.ERROR_WEB_UNSUPPORTED_ENCODING, encoding);
	        	}
	          return;
	        }

	        try {
	          performRequest(webProps, postData, null);
	        } catch (FileUtil.FileException e) {
	        	if (isaService) {
	        		sContainer.$formService().dispatchErrorOccurredEvent(Web.this, functionName,
	        				e.getErrorMessageNumber());
	        	} else {
	        		container.$form().dispatchErrorOccurredEvent(Web.this, functionName,
	        				e.getErrorMessageNumber());
	        	}
	        } catch (Exception e) {
	        	if (isaService) {
	        		sContainer.$formService().dispatchErrorOccurredEvent(Web.this, functionName,
	        				ErrorMessages.ERROR_WEB_UNABLE_TO_POST, text, webProps.urlString);
	        	} else {
	        		container.$form().dispatchErrorOccurredEvent(Web.this, functionName,
	        				ErrorMessages.ERROR_WEB_UNABLE_TO_POST, text, webProps.urlString);
	        	}
	        }
	      }
	    });
  }

  /**
   * Performs an HTTP POST request using the Url property and data from the
   * specified file, and retrieves the response.
   *
   * @param path the path of the file for the POST request
   */  
  public void PostFile(final String path) {
	// Capture property values before running asynchronously.
	    final CapturedProperties webProps = capturePropertyValues("PostFile");
	    if (webProps == null) {
	      // capturePropertyValues has already called form.dispatchErrorOccurredEvent
	      return;
	    }

	    AsynchUtil.runAsynchronously(new Runnable() {
	      @Override
	      public void run() {
	        try {
	          performRequest(webProps, null, path);
	        } catch (FileUtil.FileException e) {
	        	if (isaService) {
	        		sContainer.$formService().dispatchErrorOccurredEvent(Web.this, "PostFile",
	        				e.getErrorMessageNumber());
	        	} else {
	        		container.$form().dispatchErrorOccurredEvent(Web.this, "PostFile",
	        				e.getErrorMessageNumber());
	        	}
	        } catch (Exception e) {
	        	if (isaService) {
	        		sContainer.$formService().dispatchErrorOccurredEvent(Web.this, "PostFile",
	        				ErrorMessages.ERROR_WEB_UNABLE_TO_POST_FILE, path, webProps.urlString);
	        	} else {
	        		container.$form().dispatchErrorOccurredEvent(Web.this, "PostFile",
	        				ErrorMessages.ERROR_WEB_UNABLE_TO_POST_FILE, path, webProps.urlString);
	        	}
	        }
	      }
	    });

  }

  /**
   * Event indicating that a request has finished.<br>
   * If responseCode is 200, then the request succeeded and responseContent
   * contains the response.
   *
   * @param url the URL used for the request
   * @param responseCode the response code from the server
   * @param responseType the mime type of the response
   * @param responseContent the response content from the server
   */
  
  public void GotText(String url, int responseCode, String responseType, String responseContent) {
    // invoke the application's "GotText" event handler.
    EventDispatcher.dispatchEvent(this, "GotText", url, responseCode, responseType,
        responseContent);
  }

  /**
   * Event indicating that a request has finished.<br>
   * If responseCode is 200, then the request succeeded and the response has
   * been saved in a file.
   *
   * @param url the URL used for the request
   * @param responseCode the response code from the server
   * @param responseType the mime type of the response
   * @param fileName the full path name of the saved file
   */
  
  public void GotFile(String url, int responseCode, String responseType, String fileName) {
    // invoke the application's "GotFile" event handler.
    EventDispatcher.dispatchEvent(this, "GotFile", url, responseCode, responseType, fileName);
  }

  /**
   * Converts a list of two-element sublists, representing name and value pairs, to a
   * string formatted as application/x-www-form-urlencoded media type, suitable to pass to
   * PostText.
   *
   * @param list a list of two-element sublists representing name and value pairs
   */
  public String BuildPostData(WebHelper helper) {
	    try {
	      return buildPostData(helper);
	    } catch (BuildPostDataException e) {
	    	if (isaService) {
	    		sContainer.$formService().dispatchErrorOccurredEvent(this, "BuildPostData", e.errorNumber, e.index);
	    	} else {
	    		container.$form().dispatchErrorOccurredEvent(this, "BuildPostData", e.errorNumber, e.index);
	    	}
	      return "";
	    }
	  }

  /*
   * Converts a list of two-element sublists, representing name and value pairs, to a
   * string formatted as application/x-www-form-urlencoded media type, suitable to pass to
   * PostText.
   *
   * @param list a list of two-element sublists representing name and value pairs
   * @throws BuildPostDataException if the list is not valid
   */
  // VisibleForTesting
  private String buildPostData(WebHelper helper) throws BuildPostDataException {
    StringBuilder sb = new StringBuilder();
    String delimiter = "";
    
    int postsize = helper.postCount();
    String[] postName = new String[postsize];
    //postName = (String[]) helper.getPostDataKeySet().toArray();
    int x=0;
    for (String str : helper.getPostDataKeySet()) {
    	postName[x] = str;
    	x++;
    }
    
    for (int i = 0; i < postsize; i++) {
      Object item = helper.getPostData().get(postName[i]);
      // Each item must be a two-element sublist.
      if (item instanceof String) {
        //String sublist = item.toString();
        //if (sublist.size() == 2) {
          // The first element is the name.
          String name = postName[i];          
          // The second element is the value.
          String value = item.toString();
          sb.append(delimiter).append(UriEncode(name)).append('=').append(UriEncode(value));
        } else {
          throw new BuildPostDataException(
              ErrorMessages.ERROR_WEB_BUILD_POST_DATA_NOT_TWO_STRINGS, i + 1);        
      }
      delimiter = "&";
    }
    return sb.toString();
  }

  /**
   * Encodes the given text value so that it can be used in a URL.
   *
   * @param text the text to encode
   * @return the encoded text
   */
  
  public String UriEncode(String text) {
    try {
      return URLEncoder.encode(text, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // If UTF-8 is not supported, we're in big trouble!
      return "";
    }
  }

  /**
   * Decodes the given JSON encoded value to produce a corresponding AppInventor value.
   * A JSON list [x, y, z] decodes to a list (x y z),  A JSON object with name A and value B,
   * (denoted as A:B enclosed in curly braces) decodes to a list
   * ((A B)), that is, a list containing the two-element list (A B).
   *
   * @param jsonText the JSON text to decode
   * @return the decoded text
   */  
//This returns an object, which in general will be a Java ArrayList, String, Boolean, Integer,
 // or Double.
 // The object will be sanitized to produce the corresponding Yail data by call-component-method.
 // That mechanism would need to be extended if we ever change JSON decoding to produce
 // dictionaries rather than lists
 public Object JsonTextDecode(String jsonText) {
   try {
     return decodeJsonText(jsonText);
   } catch (IllegalArgumentException e) {
	   if (isaService) {
		   sContainer.$formService().dispatchErrorOccurredEvent(this, "JsonTextDecode",
				   ErrorMessages.ERROR_WEB_JSON_TEXT_DECODE_FAILED, jsonText);
	   } else {
		   container.$form().dispatchErrorOccurredEvent(this, "JsonTextDecode",
				   ErrorMessages.ERROR_WEB_JSON_TEXT_DECODE_FAILED, jsonText);
	   }
     return "";
   }
 }


 /**
  * Decodes the given JSON encoded value.
  *
  * @param jsonText the JSON text to decode
  * @return the decoded object
  * @throws IllegalArgumentException if the JSON text can't be decoded
  */
 // VisibleForTesting
 static Object decodeJsonText(String jsonText) throws IllegalArgumentException {
   try {
     return JsonUtil.getObjectFromJson(jsonText);
   } catch (JSONException e) {
     throw new IllegalArgumentException("jsonText is not a legal JSON value");
   }
 }


 /**
  * Decodes the given HTML text value.
  *
  * <pre>
  * HTML Character Entities such as &amp;, &lt;, &gt;, &apos;, and &quot; are
  * changed to &, <, >, ', and ".
  * Entities such as &#xhhhh, and &#nnnn are changed to the appropriate characters.
  * </pre>
  *
  * @param htmlText the HTML text to decode
  * @return the decoded text
  */

 public String HtmlTextDecode(String htmlText) {
   try {
     return HtmlEntities.decodeHtmlText(htmlText);
   } catch (IllegalArgumentException e) {
	   if (isaService) {
		   sContainer.$formService().dispatchErrorOccurredEvent(this, "HtmlTextDecode",
				   ErrorMessages.ERROR_WEB_HTML_TEXT_DECODE_FAILED, htmlText);
	   } else {
		   container.$form().dispatchErrorOccurredEvent(this, "HtmlTextDecode",
				   ErrorMessages.ERROR_WEB_HTML_TEXT_DECODE_FAILED, htmlText);
	   }
     return "";
   }
 }

  

 /*
  * Perform a HTTP GET or POST request.
  * This method is always run on a different thread than the event thread. It does not use any
  * property value fields because the properties may be changed while it is running. Instead, it
  * uses the parameters.
  * If either postData or postFile is non-null, then a post request is performed.
  * If both postData and postFile are non-null, postData takes precedence over postFile.
  * If postData and postFile are both null, then a get request is performed.
  * If saveResponse is true, the response will be saved in a file and the GotFile event will be
  * triggered. responseFileName specifies the name of the  file.
  * If saveResponse is false, the GotText event will be triggered.
  *
  * This method can throw an IOException. The caller is responsible for catching it and
  * triggering the appropriate error event.
  *
  * @param webProps the captured property values needed for the request
  * @param postData the data for the post request if it is not coming from a file, can be null
  * @param postFile the path of the file containing data for the post request if it is coming from
  *                 a file, can be null
  *
  * @throws IOException
  */
 private void performRequest(final CapturedProperties webProps, byte[] postData, String postFile)
     throws IOException {

   // Open the connection.
   HttpURLConnection connection = openConnection(webProps);
   if (connection != null) {
     try {
       if (postData != null) {
         writePostData(connection, postData);
       } else if (postFile != null) {
         writePostFile(connection, postFile);
       }

       // Get the response.
       final int responseCode = connection.getResponseCode();
       final String responseType = getResponseType(connection);
       processResponseCookies(connection);

       if (saveResponse) {
         final String path = saveResponseContent(connection, webProps.responseFileName,
             responseType);

         // Dispatch the event.
         if (isaService) {
        	 sContainer.$formService().runOnSvcThread(new Runnable() {				
				@Override
				public void run() {
					GotFile(webProps.urlString, responseCode, responseType, path);					
				}
			});
         } else {
        	 container.$form().runOnUiThread(new Runnable() {
        		 @Override
        		 public void run() {
        			 GotFile(webProps.urlString, responseCode, responseType, path);
        		 }
        	 });
         }
       } else {
         final String responseContent = getResponseContent(connection);

         // Dispatch the event.
         if (isaService) {
        	 sContainer.$formService().runOnSvcThread(new Runnable() {				
				@Override
				public void run() {
					GotText(webProps.urlString, responseCode, responseType, responseContent);
				}
			});
         } else {
        	 container.$form().runOnUiThread(new Runnable() {
        		 @Override
        		 public void run() {
        			 GotText(webProps.urlString, responseCode, responseType, responseContent);
        		 }
        	 });
         }
       }

     } finally {
       connection.disconnect();
     }
   }
 }

 private static HttpURLConnection openConnection(CapturedProperties webProps)
	      throws IOException, ClassCastException {

	    HttpURLConnection connection = (HttpURLConnection) webProps.url.openConnection();

	    // Request Headers
	    for (Map.Entry<String, List<String>> header : webProps.requestHeaders.entrySet()) {
	      String name = header.getKey();
	      for (String value : header.getValue()) {
	        connection.addRequestProperty(name, value);
	      }
	    }

	    // Cookies
	    if (webProps.cookies != null) {
	      for (Map.Entry<String, List<String>> cookie : webProps.cookies.entrySet()) {
	        String name = cookie.getKey();
	        for (String value : cookie.getValue()) {
	          connection.addRequestProperty(name, value);
	        }
	      }
	    }

    return connection;
  }


 private static void writePostData(HttpURLConnection connection, byte[] postData)
	      throws IOException {
	    // According to the documentation at
	    // http://developer.android.com/reference/java/net/HttpURLConnection.html
	    // HttpURLConnection uses the GET method by default. It will use POST if setDoOutput(true) has
	    // been called.
	    connection.setDoOutput(true); // This makes it an HTTP POST.
	    // Write the data.
	    connection.setFixedLengthStreamingMode(postData.length);
	    BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
	    try {
	      out.write(postData, 0, postData.length);
	      out.flush();
	    } finally {
	      out.close();
	    }
	  }

 private void writePostFile(HttpURLConnection connection, String path)
	      throws IOException {
	    // Use MediaUtil.openMedia to open the file. This means that path could be file on the SD card,
	    // an asset, a contact picture, etc.
	    BufferedInputStream in;
	    if (isaService) {
	    	in = new BufferedInputStream(MediaUtil.openMedia(sContainer.$formService(), path));
	    } else {
	    	in = new BufferedInputStream(MediaUtil.openMedia(container.$form(), path));
	    }
	    
	    try {
	      // Write the file's data.
	      // According to the documentation at
	      // http://developer.android.com/reference/java/net/HttpURLConnection.html
	      // HttpURLConnection uses the GET method by default. It will use POST if setDoOutput(true) has
	      // been called.
	      connection.setDoOutput(true); // This makes it an HTTP POST.
	      connection.setChunkedStreamingMode(0);
	      BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
	      try {
	        while (true) {
	          int b = in.read();
	          if (b == -1) {
	            break;
	          }
	          out.write(b);
	        }
	        out.flush();
	      } finally {
	        out.close();
	      }
	    } finally {
	      in.close();
	    }
	  }


  private static String getResponseType(HttpURLConnection connection) {
    String responseType = connection.getContentType();
    return (responseType != null) ? responseType : "";
  }
  
  private void processResponseCookies(HttpURLConnection connection) {
	    if (allowCookies && cookieHandler != null) {
	      try {
	        Map<String, List<String>> headerFields = connection.getHeaderFields();
	        cookieHandler.put(connection.getURL().toURI(), headerFields);
	      } catch (URISyntaxException e) {
	        // Can't convert the URL to a URI; no cookies for you.
	      } catch (IOException e) {
	        // Sorry, no cookies for you.
	      }
    }
  }


  private static String getResponseContent(HttpURLConnection connection) throws IOException {
	    // Use the content encoding to convert bytes to characters.
	    String encoding = connection.getContentEncoding();
	    if (encoding == null) {
	      encoding = "UTF-8";
	    }
	    InputStreamReader reader = new InputStreamReader(getConnectionStream(connection), encoding);
	    try {
	      int contentLength = connection.getContentLength();
	      StringBuilder sb = (contentLength != -1)
	          ? new StringBuilder(contentLength)
	          : new StringBuilder();
	      char[] buf = new char[1024];
	      int read;
	      while ((read = reader.read(buf)) != -1) {
	        sb.append(buf, 0, read);
	      }
	      return sb.toString();
	    } finally {
	      reader.close();
	    }
	  }


  private static String saveResponseContent(HttpURLConnection connection,
	      String responseFileName, String responseType) throws IOException {
	    File file = createFile(responseFileName, responseType);

	    BufferedInputStream in = new BufferedInputStream(getConnectionStream(connection), 0x1000);
	    try {
	      BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), 0x1000);
	      try {
	        // Copy the contents from the input stream to the output stream.
	        while (true) {
	          int b = in.read();
	          if (b == -1) {
	            break;
	          }
	          out.write(b);
	        }
	        out.flush();
	      } finally {
	        out.close();
	      }
	    } finally {
	      in.close();
	    }

	    return file.getAbsolutePath();
	  }

  private static InputStream getConnectionStream(HttpURLConnection connection) {
	    // According to the Android reference documentation for HttpURLConnection: If the HTTP response
	    // indicates that an error occurred, getInputStream() will throw an IOException. Use
	    // getErrorStream() to read the error response.
	    try {
	      return connection.getInputStream();
	    } catch (IOException e1) {
	      // Use the error response.
	      return connection.getErrorStream();
	    }
	  }


  private static File createFile(String fileName, String responseType)
      throws IOException, FileUtil.FileException {
    // If a fileName was specified, use it.
    if (!TextUtils.isEmpty(fileName)) {
      return FileUtil.getExternalFile(fileName);
    }
 // Otherwise, try to determine an appropriate file extension from the responseType.
    // The response type could contain extra information that we don't need. For example, it might
    // be "text/html; charset=ISO-8859-1". We just want to look at the part before the semicolon.
    int indexOfSemicolon = responseType.indexOf(';');
    if (indexOfSemicolon != -1) {
      responseType = responseType.substring(0, indexOfSemicolon);
    }
    String extension = mimeTypeToExtension.get(responseType);
    if (extension == null) {
      extension = "tmp";
    }
    return FileUtil.getDownloadFile(extension);
  }
  
  /*
   * Converts request headers (a WebHelper) into the structure that can be used with the Java API
   * (a Map<String, List<String>>). If the request headers contains an invalid element, an
   * InvalidRequestHeadersException will be thrown.
   */
  private static Map<String, List<String>> processRequestHeaders(WebHelper helper)
      throws InvalidRequestHeadersException {
    Map<String, List<String>> requestHeadersMap = Maps.newHashMap();
    
    requestHeadersMap = helper.getRequestHeaders();
    
    return requestHeadersMap;
  }

  /*
   * Captures the current property values that are needed for an HTTP request. If an error occurs
   * while validating the Url or RequestHeaders property values, this method calls
   * form.dispatchErrorOccurredEvent and returns null.
   *
   * @param functionName the name of the function, used when dispatching errors
   */
  private CapturedProperties capturePropertyValues(String functionName) {
    try {
      return new CapturedProperties(this);
    } catch (MalformedURLException e) {
    	if (isaService) {
    		sContainer.$formService().dispatchErrorOccurredEvent(this, functionName,
    				ErrorMessages.ERROR_WEB_MALFORMED_URL, urlString);
    	} else {
    		container.$form().dispatchErrorOccurredEvent(this, functionName,
    				ErrorMessages.ERROR_WEB_MALFORMED_URL, urlString);
    	}
    } catch (InvalidRequestHeadersException e) {
    	if (isaService) {
    		sContainer.$formService().dispatchErrorOccurredEvent(this, functionName, e.errorNumber, e.index);
    	} else {
    		container.$form().dispatchErrorOccurredEvent(this, functionName, e.errorNumber, e.index);
    	}
    }
    return null;
  }

  
}
