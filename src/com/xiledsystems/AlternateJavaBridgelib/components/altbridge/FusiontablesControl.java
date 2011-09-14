package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ClientLoginHelper;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.IClientLoginHelper;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.SdkLevel;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Appinventor fusiontables control
 */

public class FusiontablesControl extends AndroidNonvisibleComponent implements Component {
  private static final String LOG_TAG = "fusion";
  private static final String DIALOG_TEXT = "Choose an account to access FusionTables";
  private static final String FUSION_QUERY_URL = "http://www.google.com/fusiontables/api/query";
  private static final String FUSIONTABLES_SERVICE = "fusiontables";
  private static final int SERVER_TIMEOUT_MS = 30000;

  private final Activity activity;
  private final IClientLoginHelper requestHelper;
  private String query;

  public FusiontablesControl(ComponentContainer componentContainer) {
    super(componentContainer.$form());
    this.activity = componentContainer.$context();
    requestHelper = createClientLoginHelper(DIALOG_TEXT, FUSIONTABLES_SERVICE);
  }

  
  public void Query(String query) {
    this.query = query;
  }

  
  public String Query() {
    return query;
  }

  
  public void DoQuery() {
    if (requestHelper != null) {
      new QueryProcessor().execute(query);
    } else {
      form.dispatchErrorOccurredEvent(this, "DoQuery",
          ErrorMessages.ERROR_FUNCTIONALITY_NOT_SUPPORTED_FUSIONTABLES_CONTROL);
    }
  }

  
  public void GotResult(String result) {
    // Invoke the application's "GotValue" event handler
    EventDispatcher.dispatchEvent(this, "GotResult", result);
  }

  /* TODO(user): figure out why this isn't working
  @SimpleFunction
  public void ForgetLogin() {
    if (requestHelper != null) {
      requestHelper.forgetAccountName();
    }
  }
  */

  private IClientLoginHelper createClientLoginHelper(String accountPrompt, String service) {
    if (SdkLevel.getLevel() >= SdkLevel.LEVEL_ECLAIR) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpConnectionParams.setSoTimeout(httpClient.getParams(), SERVER_TIMEOUT_MS);
      HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), SERVER_TIMEOUT_MS);
      return new ClientLoginHelper(activity, service, accountPrompt, httpClient);
    }
    return null;
  }

  /**
   * Generate a FusionTables POST request
   */
  private HttpUriRequest genFusiontablesQuery(String query) throws IOException {
    HttpPost request = new HttpPost(FUSION_QUERY_URL);
    ArrayList<BasicNameValuePair> pair = new ArrayList<BasicNameValuePair>(1);
    pair.add(new BasicNameValuePair("sql", query));
    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pair, "UTF-8");
    entity.setContentType("application/x-www-form-urlencoded");
    request.setEntity(entity);
    return request;
  }

  /**
   * Send the fusiontables request to the server and get back the results.
   *
   */
  private class QueryProcessor extends AsyncTask<String, Void, String> {
    private ProgressDialog progress = null;

    @Override
    protected void onPreExecute() {
      progress = ProgressDialog.show(activity, "Fusiontables", "processing query...", true);
    }

    /**
     * Query the fusiontables server.
     * @return The resulant table, error page, or exception message.
     */
    @Override
    protected String doInBackground(String... params) {
      try {
        HttpUriRequest request = genFusiontablesQuery(params[0]);
        Log.d(LOG_TAG, "Fetching: " + params[0]);
        HttpResponse response = requestHelper.execute(request);
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        response.getEntity().writeTo(outstream);
        Log.d(LOG_TAG, "Response: " + response.getStatusLine().toString());
        return outstream.toString();
      } catch (IOException e) {
        e.printStackTrace();
        return e.getMessage();
      }
    }

    /**
     * Got the results.  We could parse the CSV and do something useful with it.
     */

    @Override
    protected void onPostExecute(String result) {
      progress.dismiss();
      GotResult(result);
      // (result.stqueryartsWith("<HTML>") ? Html.fromHtml(result) : result);
    }
  }
}
