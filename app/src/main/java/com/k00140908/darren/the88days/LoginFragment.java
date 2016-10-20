package com.k00140908.darren.the88days;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.k00140908.darren.the88days.Model.RetrofitErrors;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.RetrofitInterfaces.BackpackerAPI;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment
    implements View.OnClickListener {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private OnFragmentInteractionListener mListener;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    UserLocalStore userLocalStore;
    User user= null;

    final static String ENDPOINT ="https://the88days.azurewebsites.net/";

    View view;

    private CallbackManager mCallbackManager;
    private FacebookCallback<LoginResult> mCallback= new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            if (profile !=null) {
                //imageView.setImageURI(profile.getProfilePictureUri(100, 100));
                String token = accessToken.getToken().toString();
                String username = profile.getName().toString();
                String profilePicture = String.valueOf(profile.getProfilePictureUri(100, 100));

                User user = new User(username, token, "");
                userLocalStore.storeFacebookRegisterDetails(user);

                CheckExistingAccount(user);

                showProgress(true);

                }
        }
        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {
            Toast.makeText(getActivity(), "Sorry an error has occurred please check your internet connection and try again.", Toast.LENGTH_LONG).show();
        }
    };
    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);

        userLocalStore = new UserLocalStore(getContext());

        user= userLocalStore.newUser();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.email);


        mPasswordView = (EditText) view.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    //attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Button mRegisterButton = (Button) view.findViewById(R.id.email_register_button);
        mRegisterButton.setOnClickListener(this);

        Button mEmailSignInButton = (Button) view.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(this);

        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.login_progress);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        //mTextDetails = (TextView) view.findViewById(R.id.mTextDetails);
        //imageView = (ImageView) view.findViewById(R.id.imageView);
        //loginButton.setReadPermissions("user_friends");
        // If using in a fragment
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mCallback);

        Bundle b = getArguments();
        if(b!=null) {
            Toast.makeText(getActivity(), "Account created successfully.\n Please log on.", Toast.LENGTH_SHORT).show();
            mPasswordView.setText(b.getString("password"));
            mEmailView.setText(b.getString("username"));
        }



        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        //mTextDetails = (TextView) view.findViewById(R.id.mTextDetails);
        //imageView = (ImageView) view.findViewById(R.id.imageView);
        //loginButton.setReadPermissions("user_friends");
        // If using in a fragment
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mCallback);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //@Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_sign_in_button:

                attemptLogin();

                break;
            case R.id.email_register_button:

                mListener.onFragmentInteraction(1);
                break;
        }
    }
      private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String UserName = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username, if the user entered one.
        if (TextUtils.isEmpty(UserName)) {
            mEmailView.setError(getString(R.string.error_username_required));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(ENDPOINT)
                    .build();

            BackpackerAPI api = adapter.create(BackpackerAPI.class);

            api.Login(UserName, password, new Callback<User>() {

                @Override
                public void failure(final RetrofitError error) {


                    RetrofitErrors errors = (RetrofitErrors) error.getBodyAs(RetrofitErrors.class);
                    if (errors != null) {
                        if(errors.getModelStateString()!=null) {
                            String errorlist = errors.getModelStateString();
                            Toast.makeText(getActivity(), errorlist, Toast.LENGTH_LONG).show();
                            showProgress(false);
                        }
                        else
                        {
                            Toast.makeText(getActivity(), errors.getMessage().toString(), Toast.LENGTH_LONG).show();
                            showProgress(false);
                        }

                    }
                    else
                     {

                        String errorType = error.getKind().toString();
                        if (errorType.equals("NETWORK")) {
                            Toast.makeText(getActivity(), "Sorry an error has occurred please check your internet connection and try again.", Toast.LENGTH_LONG).show();
                            showProgress(false);
                        } else {
                            Toast.makeText(getActivity(), "Sorry an unknown error has occurred please try again.", Toast.LENGTH_LONG).show();
                            showProgress(false);
                        }
                        mListener.onFragmentInteraction(1);
                    }


                }

                @Override
                public void success(User userReg, Response response) {

                    user.setAccess_token(userReg.getAccess_token());
                    user.setRefresh_Token(userReg.getRefresh_Token());
                    user.setUserName(userReg.getUserName());
                    user.setUserId(userReg.getUserId());
                    user.setNationality(userReg.getNationality());
                    user.setWorkStatus(userReg.getWorkStatus());
                    user.setProfilePhoto(userReg.getProfilePhoto());

                    userLocalStore.setUsersDetails(user);
                    userLocalStore.setUserLoggedIn(true);

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();


                }
            });
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        mCallbackManager= CallbackManager.Factory.create();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(1);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private void CheckExistingAccount(User userCheck)
    {
        showProgress(true);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();

        BackpackerAPI api = adapter.create(BackpackerAPI.class);

        api.CheckForExistingAccount("Facebook", userCheck.getAccess_token(), new Callback< User >() {

            @Override
            public void failure(final RetrofitError error) {

                String errorType = error.getKind().toString();
                if (errorType.equals("NETWORK")) {
                    Toast.makeText(getActivity(), "Sorry an error has occurred please check your internet connection and try again.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Sorry an unknown error has occurred please try again.", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void success(User userReg, Response response) {

                if(userReg.getUserName().equals("Not Registered"))
                {
                    mListener.onFragmentInteraction(2);
                }
                else {
                    user.setAccess_token(userReg.getAccess_token());
                    user.setRefresh_Token(userReg.getRefresh_Token());
                    user.setUserName(userReg.getUserName());
                    user.setUserId(userReg.getUserId());
                    user.setNationality(userReg.getNationality());
                    user.setWorkStatus(userReg.getWorkStatus());
                    user.setProfilePhoto(userReg.getProfilePhoto());

                    userLocalStore.setUsersDetails(user);
                    userLocalStore.setUserLoggedIn(true);

                    LoginManager.getInstance().logOut();//finished with fb account so log out

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });
        showProgress(false);
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int position);
    }
}
