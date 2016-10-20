package com.k00140908.darren.the88days.ManageFriends.ManageGCM;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.k00140908.darren.the88days.MainActivity;
import com.k00140908.darren.the88days.Model.RetrofitErrors;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.RetrofitInterfaces.BackpackerAPI;
import com.k00140908.darren.the88days.UserLocalStore;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by darre on 06/04/2016.
 */
public class RegistrationIntentService extends IntentService {


    User user= null;
    UserLocalStore userLocalStore;

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean("sentTokenToServer", true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean("sentTokenToServer", false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent("registrationComplete");
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        String ENDPOINT ="https://the88days.azurewebsites.net/";
        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        Log.i(TAG, "GCM Registration Token: " + token);
        RestAdapter.Builder adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + user.getAccess_token());
                    }
                });

        BackpackerAPI api = adapter.build().create(BackpackerAPI.class);

        api.updateUser("", "", user.getNationality(), user.getWorkStatus(), 0, 0, user.isPrivacy(), "", token, new Callback<User>() {

            @Override
            public void failure(final RetrofitError error) {

                RetrofitErrors errors = (RetrofitErrors) error.getBodyAs(RetrofitErrors.class);
                if (errors != null) {
                    String errorlist = errors.getModelStateString();
                    //Toast.makeText(getActivity(), errorlist, Toast.LENGTH_LONG).show();
                } else {

                    String errorType = error.getKind().toString();
                    if (errorType.equals("NETWORK")) {
                        //Toast.makeText(getActivity(), "Sorry an error has occurred please check your internet connection and try again.", Toast.LENGTH_LONG).show();
                    } else {
                        //Toast.makeText(getActivity(), "Sorry an unknown error has occurred please try again.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void success(User userReg, Response response) {

                //Toast.makeText(getApplication(), "GCM Reg updated!", Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
