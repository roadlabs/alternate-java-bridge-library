package com.xiledsystems.AlternateJavaBridgelib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import android.app.Service;
import android.text.TextUtils;
import android.util.Log;

import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.android.util.AsynchUtil;
import com.google.devtools.simple.runtime.components.android.util.FileUtil;
import com.google.devtools.simple.runtime.components.util.ErrorMessages;
import com.google.devtools.simple.runtime.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.AndroidServiceComponent;
import com.xiledsystems.AlternateJavaBridgelib.JsonUtil2;
import com.xiledsystems.AlternateJavaBridgelib.MediaUtilsvc;
import com.xiledsystems.AlternateJavaBridgelib.SvcComponentContainer;

// THIS IS NOT WORKING YET. DO NOT USE THIS COMPONENT.

public class Websvc extends AndroidServiceComponent implements Component {
	
	private static final String LOG_TAG = "Websvc";

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

	  private final Service service;

	  private String urlString = "";
	  private boolean saveResponse;
	  private String responseFileName = "";

	  /**
	   * Creates a new Web component.
	   *
	   * @param container the Form that this component is contained in.
	   */
	  public Websvc(SvcComponentContainer container) {
	    super(container.$formService());
	    service = container.$context();
	  }

	  /**
	   * This constructor is for testing purposes only.
	   */
	  protected Websvc() {
	    super(null);
	    service = null;
	  }

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

	    AsynchUtil.runAsynchronously(new Runnable() {
	      @Override
	      public void run() {
	        try {
	          performRequest(urlString, null, null, saveResponse, responseFileName);
	        } catch (FileUtil.FileException e) {
	          formService.dispatchErrorOccurredEvent(Websvc.this, "Get",
	              e.getErrorMessageNumber());
	        } catch (Exception e) {
	          formService.dispatchErrorOccurredEvent(Websvc.this, "Get",
	              ErrorMessages.ERROR_WEB_UNABLE_TO_GET, urlString);
	        }
	      }
	    });
	  }

	  /**
	   * Performs an HTTP POST request using the Url property, and the specified
	   * text, and retrieves the response.<br>
	   * If the SaveResponse property is true, the response will be saved in a file
	   * and the GotFile event will be triggered. The responseFileName property
	   * can be used to specify the name of the file.<br>
	   * If the SaveResponse property is false, the GotText event will be
	   * triggered.
	   *
	   * @param text the text data for the POST request
	   * @param encoding the character encoding to use when sending the text. If
	   *                 encoding is empty or null, UTF-8 encoding will be used.
	   */
	 
	  public void PostText(final String text, final String encoding) {
	    // Capture property values before running asynchronously.
	    final String urlString = this.urlString;
	    final boolean saveResponse = this.saveResponse;
	    final String responseFileName = this.responseFileName;

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
	          formService.dispatchErrorOccurredEvent(Websvc.this, "PostText",
	              ErrorMessages.ERROR_WEB_UNSUPPORTED_ENCODING, encoding);
	          return;
	        }

	        try {
	          performRequest(urlString, postData, null, saveResponse, responseFileName);
	        } catch (FileUtil.FileException e) {
	          formService.dispatchErrorOccurredEvent(Websvc.this, "PostText",
	              e.getErrorMessageNumber());
	        } catch (Exception e) {
	          formService.dispatchErrorOccurredEvent(Websvc.this, "PostText",
	              ErrorMessages.ERROR_WEB_UNABLE_TO_POST, text, urlString);
	        }
	      }
	    });
	  }

	  /**
	   * Performs an HTTP POST request using the Url property, and data from the
	   * specified file, and retrieves the response.<br>
	   * If the SaveResponse property is true, the response will be saved in a file
	   * and the GotFile event will be triggered. The ResponseFileName property
	   * can be used to specify the name of the file.<br>
	   * If the SaveResponse property is false, the GotText event will be
	   * triggered.
	   *
	   * @param path the path of the file for the POST request
	   */
	  
	  public void PostFile(final String path) {
	    // Capture property values before running asynchronously.
	    final String urlString = this.urlString;
	    final boolean saveResponse = this.saveResponse;
	    final String responseFileName = this.responseFileName;

	    AsynchUtil.runAsynchronously(new Runnable() {
	      @Override
	      public void run() {
	        try {
	          performRequest(urlString, null, path, saveResponse, responseFileName);
	        } catch (FileUtil.FileException e) {
	          formService.dispatchErrorOccurredEvent(Websvc.this, "PostFile",
	              e.getErrorMessageNumber());
	        } catch (Exception e) {
	          formService.dispatchErrorOccurredEvent(Websvc.this, "PostFile",
	              ErrorMessages.ERROR_WEB_UNABLE_TO_POST_FILE, path, urlString);
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
	   * Decodes the given JSON text value. If the given JSON text is surrounded by
	   * quotes, the quotes will be removed.
	   *
	   * @param jsonText the JSON text to decode
	   * @return the decoded text
	   */
	  
	  public String JsonTextDecode(String jsonText) {
	    try {
	      return decodeJsonText(jsonText);
	    } catch (IllegalArgumentException e) {
	      formService.dispatchErrorOccurredEvent(this, "JsonTextDecode",
	          ErrorMessages.ERROR_WEB_JSON_TEXT_DECODE_FAILED, jsonText);
	      return "";
	    }
	  }

	  /**
	   * Decodes the given JSON text value. If the given JSON text is surrounded by
	   * quotes, the quotes will be removed.
	   *
	   * @param jsonText the JSON text to decode
	   * @return the decoded text
	   * @throws IllegalArgumentException if the JSON text can't be decoded
	   */
	  // VisibleForTesting
	  String decodeJsonText(String jsonText) throws IllegalArgumentException {
	    Object o;
	    try {
	      o = JsonUtil2.getObjectFromJson(jsonText);
	    } catch (JSONException e) {
	      throw new IllegalArgumentException("jsonText is not a legal JSON value");
	    }

	    if (o instanceof String) {
	      return (String) o;
	    }

	    throw new IllegalArgumentException("jsonText is not a legal JSON text value");
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
	      return decodeHtmlText(htmlText);
	    } catch (IllegalArgumentException e) {
	      formService.dispatchErrorOccurredEvent(this, "HtmlTextDecode",
	          ErrorMessages.ERROR_WEB_HTML_TEXT_DECODE_FAILED, htmlText);
	      return "";
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
	   * @throws IllegalArgumentException if the HTML text can't be decoded
	   */
	  // VisibleForTesting
	  String decodeHtmlText(String htmlText) throws IllegalArgumentException {
	    StringBuilder sb = new StringBuilder();
	    int htmlLength = htmlText.length();
	    for (int i = 0; i < htmlLength; i++) {
	      char c = htmlText.charAt(i);
	      if (c == '&') {
	        int indexOfSemi = htmlText.indexOf(';', i + 1);
	        if (indexOfSemi == -1) {
	          // There's no ; after the &.
	          throw new IllegalArgumentException("htmlText contains a & without a following ;");
	        }

	        // There is a semicolon at some point after i + 1.

	        // It is safe to call charAt(i + 1) because we know there is a semicolon at some point
	        // after i + 1.
	        if (htmlText.charAt(i + 1) == '#') {
	          // It's a numeric character reference.
	          int n;
	          // It is safe to call charAt(i + 2) because we know there is a semicolon at some point
	          // after i + 1 and charAt(i + 1) wasn't the semicolon.
	          if (htmlText.charAt(i + 2) == 'x') {
	            // The form is &#xhhhh, where hhhh is hex.
	            String hhhh  = htmlText.substring(i + 3, indexOfSemi);
	            try {
	              n = Integer.parseInt(hhhh, 16);
	            } catch (NumberFormatException e) {
	              throw new IllegalArgumentException("htmlText contains an illegal hex value: " + hhhh);
	            }
	          } else {
	            // The form is &#nnnn, where nnnn is decimal.
	            String nnnn  = htmlText.substring(i + 2, indexOfSemi);
	            try {
	              n = Integer.parseInt(nnnn);
	            } catch (NumberFormatException e) {
	              throw new IllegalArgumentException("htmlText contains an illegal decimal value: " +
	                  nnnn);
	            }
	          }
	          sb.append((char) n);
	          i = indexOfSemi;

	        } else {
	          String entity = htmlText.substring(i + 1, indexOfSemi);
	          Character decoded = htmlCharacterEntities.get(entity);
	          if (decoded != null) {
	            sb.append(decoded);
	            i = indexOfSemi;

	          } else {
	            // The entity isn't in the htmlCharacterEntities map.
	            throw new IllegalArgumentException("htmlText contains an unknown entity: &" +
	                entity + ";");
	          }
	        }

	      } else {
	        // c is a normal character.
	        sb.append(c);
	      }
	    }
	    return sb.toString();
	  }

	  /*
	   * Perform a HTTP get or post request.
	   * This method is always run on a different thread than the event thread. It does not use any
	   * property value fields because the properties may be changed while it is running. Instead, it
	   * uses the parameters.
	   * If either postData or postFile is non-null, then a post request is performed.
	   * If postData and postFile are both null, then a get request is performed.
	   * If saveResponse is true, the response will be saved in a file and the GotFile event will be
	   * triggered. responseFileName specifies the name of the  file.
	   * If saveResponse is false, the GotText event will be triggered.
	   *
	   * This method can throw an IOException. The caller is responsible for catching it and
	   * triggering the appropriate error event.
	   *
	   * @param urlString the url for the request
	   * @param postData the data for the post request, can be null
	   * @param postFile the data for the post request, can be null
	   * @param saveResponse whether to save the response in a file
	   * @param responseFileName the name of the file
	   *
	   * @throws IOException
	   */
	  private void performRequest(final String urlString, byte[] postData, String postFile,
	      boolean saveResponse, String responseFileName) throws IOException {

	    // Open the connection.
	    HttpURLConnection connection = openConnection(urlString);
	    Log.i(LOG_TAG, "performRequest - connection is " + connection);
	    if (connection != null) {
	      try {
	        if (postData != null) {
	          writePostData(connection, postData);
	        } else if (postFile != null) {
	          writePostFile(connection, postFile);
	        }

	        // Get the response.
	        final int responseCode = connection.getResponseCode();
	        Log.i(LOG_TAG, "performRequest - responseCode is " + responseCode);
	        final String responseType = (responseCode == HttpURLConnection.HTTP_OK) ?
	            getResponseType(connection) : "";
	        Log.i(LOG_TAG, "performRequest - responseType is " + responseType);

	        if (saveResponse) {
	          final String path = (responseCode == HttpURLConnection.HTTP_OK) ?
	              saveResponseContent(connection, responseFileName, responseType) : "";

	          // Dispatch the event.
	          formService.runOnSvcThread(new Runnable() {
	            @Override
	            public void run() {
	              GotFile(urlString, responseCode, responseType, path);
	            }
	          });
	        } else {
	          final String responseContent = (responseCode == HttpURLConnection.HTTP_OK) ?
	              getResponseContent(connection) : "";

	          // Dispatch the event.
	          formService.runOnSvcThread(new Runnable() {
	            @Override
	            public void run() {
	              GotText(urlString, responseCode, responseType, responseContent);
	            }
	          });
	        }

	      } finally {
	        connection.disconnect();
	      }
	    }
	  }

	  private static HttpURLConnection openConnection(String urlString)
	      throws MalformedURLException, IOException, ClassCastException {
	    return (HttpURLConnection) new URL(urlString).openConnection();
	  }

	  private static void writePostData(HttpURLConnection connection, byte[] postData)
	      throws IOException {
	    // According to the documentation at
	    // http://developer.android.com/reference/java/net/HttpURLConnection.html
	    // HttpURLConnection uses the GET method by default. It will use POST if setDoOutput(true) has
	    // been called.
	    connection.setDoOutput(true); // This makes it an HTTP POST.
	    // Write the data.
	    connection.setChunkedStreamingMode(0);
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
	    BufferedInputStream in = new BufferedInputStream(MediaUtilsvc.openMedia(formService, path));
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

	  private static String getResponseContent(HttpURLConnection connection) throws IOException {
	    // Use the content encoding to convert bytes to characters.
	    String encoding = connection.getContentEncoding();
	    if (encoding == null) {
	      encoding = "UTF-8";
	    }
	    InputStreamReader reader = new InputStreamReader(connection.getInputStream(), encoding);
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

	    BufferedInputStream in = new BufferedInputStream(connection.getInputStream(), 0x1000);
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

	  private static File createFile(String fileName, String responseType)
	      throws IOException, FileUtil.FileException {
	    // If a fileName was specified, use it.
	    if (!TextUtils.isEmpty(fileName)) {
	      return FileUtil.getExternalFile(fileName);
	    }

	    // Otherwise, try to determine an appropriate file extension from the responseType.
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
}