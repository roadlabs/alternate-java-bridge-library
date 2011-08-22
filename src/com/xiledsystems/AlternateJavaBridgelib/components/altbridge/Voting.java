package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The Voting component communicates with a Web service to retrieve a ballot
 * and send back users' votes.
 *
 * <p>The application should call the method <code>RequestBallot</code>, usually
 * in the <code>Initialize</code> event handler, in order to get the ballot
 * question and options from the Web service (specified by the
 * <code>ServiceURL</code> property).  Depending on the response from the
 * Web service, the system will raise one of the following three events:
 * <ol>
 * <li> <code>GotBallot</code>, indicating that the ballot question and options
 *      were retrieved and the properties <code>BallotQuestion</code> and
 *      <code>BallotOptions</code> have been set.</li>
 * <li> <code>NoOpenPoll</code>, indicating that no ballot question is
 *      available.</li>
 * <li> <code>WebServiceError</code>, indicating that the service did not
 *      provide a legal response and providing an error messages.</li>
 * </ol></p>
 *
 * <p>After getting the ballot, the application should allow the user to make
 * a choice from among <code>BallotOptions</code> and set the property
 * <code>UserChoice</code> to that choice.  The application should also set
 * <code>UserId</code> to specify which user is voting.  Often, this will be set
 * to the property <code>UserEmailAddress</code>, which specifies the email
 * address associated with the phone.</p>
 *
 * <p>Once the application has set <code>UserChoice</code> and
 * <code>UserId</code>, the application can call <code>SendBallot</code> to
 * send this information to the Web service.  If the service successfully
 * receives the vote, the event <code>GotBallotConfirmation</code> will be
 * raised.  Otherwise, the event <code>WebServiceError</code> will be raised
 * with the appropriate error message.</p>
 *
 */
// TODO(user): Figure out the right way to handle licensing and signing the Google CLA



public class Voting extends AndroidNonvisibleComponent implements Component {
  private static final String LOG_TAG = "Voting";
  private static final String REQUESTBALLOT_COMMAND = "requestballot";
  private static final String SENDBALLOT_COMMAND = "sendballot";
  private static final String IS_POLLING_PARAMETER = "isPolling";
  private static final String ID_REQUESTED_PARAMETER = "idRequested";
  private static final String BALLOT_QUESTION_PARAMETER = "question";
  private static final String BALLOT_OPTIONS_PARAMETER = "options";
  private static final String USER_CHOICE_PARAMETER = "userchoice";
  private static final String USER_ID_PARAMETER = "userid";

  // private HttpClient httpClient;
  private Handler androidUIHandler;
  private ComponentContainer theContainer;
  private Activity activityContext;

  private String userId;
  private String userEmailAddress;
  private String serviceURL;
  private String ballotQuestion;
  private String ballotOptionsString;

  // The choices that a vote selects among
  private ArrayList<String> ballotOptions;

  // TODO(user): idRequested isn't used in this version, but we'll keep it for the future
  private Boolean idRequested;
  private String userChoice;
  private Boolean isPolling;

  public Voting(ComponentContainer container){
    super(container.$form());
    serviceURL = "http://androvote.appspot.com";
    userId = "";
    userEmailAddress = "";
    isPolling = false;
    idRequested = false;
    ballotQuestion = "";
    ballotOptions = new ArrayList<String>();
    userChoice = "";

    androidUIHandler = new Handler();
    theContainer = container;
    activityContext = container.$context();

    // We set the initial value of serviceURL to be the
    // demo Web service
    serviceURL = "http://androvote.appspot.com";
  }

  /**
   * The URL of the Voting Service
   */
  
  public String ServiceURL() {
    return serviceURL;
  }

  /**
   * Set the URL of the Voting Service
   *
   * @param serviceURL the URL (includes initial http:, but no trailing slash)
   */
  
  public void ServiceURL(String serviceURL) {
    this.serviceURL = serviceURL;
  }

  /**
   * The question to be voted on.
   */
  
  public String BallotQuestion() {
    return ballotQuestion;
  }

// TODO(user): Remove this after we get some use of the component
// This should not be settable by the user.
//   @SimpleProperty
//   public void BallotQuestion(String ballotQuestion){
//     this.ballotQuestion = ballotQuestion;
//   }

  /**
   * The list of choices to vote.
   */
 
  public List<String> BallotOptions(){
    return ballotOptions;
  }


  // This should not be settable by the user
  // @SimpleProperty
  // public void BallotOptions(String ballotOptions){
  //   this.ballotOptions = ballotOptions;
  // }

  /**
   * An Id that is sent to the Web server along with the vote.
   */
  
  public String UserId() {
    return userId;
  }

  /**
   * Set an Id to be sent to the Web server along with the vote.
   *
   * @param userId the string to use as the Id
   */
  
  public void UserId(String userId){
    this.userId = userId;
  }

  /**
   * The choice to select when sending the vote.
   */
 
  public String UserChoice() {
    return userChoice;
  }

  /**
   * Set the choice to select when sending the vote.
   *
   * @param userChoice the choice to select.  Must be one of the BallotOptions
   */
  
  public void UserChoice(String userChoice){
    this.userChoice = userChoice;
  }

  public void Initialize() {
    // TODO(user, billmag): Handle the email address with a helper class to
    // be used by general multiparty applications.
    initEmailAddressAsynch();
  }

  private void initEmailAddressAsynch() {
    AsynchUtil.runAsynchronously(new Runnable() {
      public void run() {
        ensureEmailAddress();
      }
    });
  }

  private void ensureEmailAddress() {
    if (userEmailAddress == null || userEmailAddress.length() < 1) {
      try {
        final GoogleLoginServiceBlockingHelper googleLoginService =
            new GoogleLoginServiceBlockingHelper(activityContext);
        userEmailAddress =
            googleLoginService.getAccount(false);
        googleLoginService.close();
      } catch (Exception e) {
        // We'd rather catch the more specific GoogleLoginServiceNotFoundException but doing so
        // caused a very strange NoClassDefFoundError when Simple tried to analyze
        // this class file.

        // TODO(user): Figure out something smarter to do here.
        Log.w(LOG_TAG, e);
      }
    }
  }


  /**
   * Returns the registered email address, as a string, for this
   * device's user.
   */
  
  public String UserEmailAddress() {
    ensureEmailAddress();
    return userEmailAddress;
  }

  /* RequestBallot will talk to the Web service and retrieve the ballot of
   * the current open poll. Depending on the service response, two events
   * might be triggered: NoOpenPoll or GotBallot.
   * When a ballot is received, the JSON response looks like this:
   *            {"isPolling" : "true",
   *            "idRequested" : "true",
   *            "question" : "What are you?",
   *            "options": [ "I'm a PC", "I'm a Mac" ] }
   */

  /**
   * Send a request ballot command to the Voting server.
   */
 
  public void RequestBallot() {
    final Runnable call = new Runnable() {
      public void run() { postRequestBallot(); }};
      AsynchUtil.runAsynchronously(call);
  }

  private void postRequestBallot(){
    AsyncCallbackPair<JSONObject> myCallback = new AsyncCallbackPair<JSONObject>() {
      public void onSuccess(JSONObject result) {
        if (result == null) {
          // Signal a Web error event to indicate that there was no response
          // to this request for a ballot.
          androidUIHandler.post(new Runnable() {
            public void run() {
              WebServiceError("The Web server did not respond to your request for a ballot");
            }
          });
          return;
        } else {
          try {
            Log.i(LOG_TAG, "postRequestBallot: ballot retrieved " + result);
            // The Web service is designed to return the JSON encoded object
            // This has to be a legal JSON encoding.  For example, true and false
            // should not be quoted if we're using getBoolean.  A bad encoding will
            // throw a JSON exception.
            isPolling = result.getBoolean(IS_POLLING_PARAMETER);
            if (isPolling){
              //populate parameter's value directly from reading JSONObject
              idRequested = result.getBoolean(ID_REQUESTED_PARAMETER);
              ballotQuestion = result.getString(BALLOT_QUESTION_PARAMETER);
              ballotOptionsString = result.getString(BALLOT_OPTIONS_PARAMETER);
              ballotOptions  = JSONArrayToArrayList(new JSONArray(ballotOptionsString));
              androidUIHandler.post(new Runnable() {
                public void run() {
                  GotBallot();
                }
              });
            } else {
              androidUIHandler.post(new Runnable() {
                public void run() {
                  NoOpenPoll();
                }
              });
            }
          } catch (JSONException e) {
            // Signal a Web error event to indicate the the server
            // returned a garbled value.  From the user's perspective,
            // there may be no practical difference between this and
            // the "no response" error above, but application writers
            // can create handlers to use these events as they choose.
            // Note that server errors that create malformed JSON
            // responses will sometimes be caught here.
            androidUIHandler.post(new Runnable() {
              public void run() {
                WebServiceError("The Web server returned a garbled object");
              }
            });
            return;
          }
        }
      }
      public void onFailure(final String message) {
        Log.w(LOG_TAG, "postRequestBallot Failure " + message);
          androidUIHandler.post(new Runnable() {
            public void run() {
              WebServiceError(message);
            }
          });
          return;
      }
    };

    WebServiceUtil.getInstance().postCommandReturningObject(
        serviceURL,
        REQUESTBALLOT_COMMAND,
        null,
        myCallback);
    return;
  }

  private ArrayList<String> JSONArrayToArrayList(JSONArray ja) throws JSONException {
    ArrayList<String> a = new ArrayList<String>();
    for (int i = 0; i < ja.length(); i++) {
      a.add(ja.getString(i));
    }
    return a;
    }


  /**
   * Event indicating that a ballot was received from the Web service.
   */
  
  public void GotBallot() {
    EventDispatcher.dispatchEvent(this, "GotBallot");
  }

  /**
   * Event indicating that the service has no open poll.
   */
  
  public void NoOpenPoll() {
    EventDispatcher.dispatchEvent(this, "NoOpenPoll");
  }

  /**
   * Send a ballot to the Web Voting server.  The userId and the choice are
   * specified by the UserId and UserChoice properties.
   */
  
  public void SendBallot() {
    final Runnable call = new Runnable() {
      public void run() { postSendBallot(userChoice, userId); }};
      AsynchUtil.runAsynchronously(call);
  }

  private void postSendBallot(String userChoice, String userEmailAddress){
    AsyncCallbackPair<String> myCallback = new AsyncCallbackPair<String>(){
      // the Web service will send back a confirmation message, but
      // the component ignores it and notes only that anything at
      // all was sent back.  We can improve this later.
      public void onSuccess(String response) {
        androidUIHandler.post(new Runnable() {
          public void run() {
            GotBallotConfirmation();
          }
        });
      }
      public void onFailure(final String message) {
        Log.w(LOG_TAG, "postSendBallot Failure " + message);
          androidUIHandler.post(new Runnable() {
            public void run() {
              WebServiceError(message);
            }
          });
          return;
      }
    };

    WebServiceUtil.getInstance().postCommand(serviceURL,
        SENDBALLOT_COMMAND,
        Lists.<NameValuePair>newArrayList(
            new BasicNameValuePair(USER_CHOICE_PARAMETER, userChoice),
            new BasicNameValuePair(USER_ID_PARAMETER, userEmailAddress)),
            myCallback);

  }

  /**
   * Event confirming that the Voting service received the ballot.
   */
  
  public void GotBallotConfirmation() {
    EventDispatcher.dispatchEvent(this, "GotBallotConfirmation");
  }

  //-----------------------------------------------------------------------------
  /**
   * Event indicating that the communication with the Web service resulted in
   * an error.
   *
   * @param message the error message
   */
  
  public void WebServiceError(String message) {
    // Invoke the application's "WebServiceError" event handler
    EventDispatcher.dispatchEvent(this, "WebServiceError", message);
  }
}
