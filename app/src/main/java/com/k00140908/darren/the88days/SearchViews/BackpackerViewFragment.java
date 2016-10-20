package com.k00140908.darren.the88days.SearchViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.RetrofitInterfaces.BackpackerAPI;
import com.k00140908.darren.the88days.UserLocalStore;
import com.squareup.picasso.Picasso;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BackpackerViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BackpackerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class BackpackerViewFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean backgroundTaskInProgress = false;
    private OnFragmentInteractionListener mListener;



    private ImageView mImageView;
    private static final String TAG = "upload";

    UserLocalStore userLocalStore;
    TextView etName, etAge, etUsername;

    public String mUserId;
    private View mProgressView;
    private View mBackpackerView;
    private Button mFriendButton;

    final static String ENDPOINT ="https://the88days.azurewebsites.net/";
    User user = null;

    String mCurrentPhotoPath;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BackpackerViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BackpackerViewFragment newInstance(String param1, String param2) {
        BackpackerViewFragment fragment = new BackpackerViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public BackpackerViewFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_backpacker_view, container, false);

        mListener.SetupToolbar(1, "Backpacker Profile");

        Bundle b = getArguments();

        mUserId=b.getString("userId");



        mFriendButton = (Button) v.findViewById(R.id.friendButton);
        mFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendButton.setEnabled(false);
                AddFriend(mUserId);
            }
        });

        mImageView = (ImageView) v.findViewById(R.id.profilephoto);

        mImageView.setOnClickListener(this);


        etUsername = (TextView) v.findViewById(R.id.textViewUserName);
        etName = (TextView) v.findViewById(R.id.textViewNationality);
        etAge = (TextView) v.findViewById(R.id.textViewWorkStatus);

        userLocalStore = new UserLocalStore(getContext());

        User user = userLocalStore.getLoggedInUser();


        if(b!=null) {
            Toast.makeText(getActivity(), b.getString("username"), Toast.LENGTH_SHORT).show();
            etName.setText(b.getString("nationality"));
            etUsername.setText(b.getString("username"));
            etAge.setText(b.getString("workStatus"));
        }


        mBackpackerView = v.findViewById(R.id.baclpacker_view_form);
        mProgressView = v.findViewById(R.id.friend_status_progress);
        String urlThumnail = null;
        if (b.getString("profilephoto") != null) {
            urlThumnail = b.getString("profilephoto");
        }
        if (urlThumnail != "") {
            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(getContext())
                    .load(urlThumnail).error(R.drawable.ic_contact_picture)
                    .into(mImageView);
        }

        checkOnlineFriendsStatus(mUserId);
        return v;
    }

    private void checkOnlineFriendsStatus(String userId) {
        backgroundTaskInProgress = true;
        showProgress(true);

        user = userLocalStore.getLoggedInUser();

        RestAdapter.Builder adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + user.getAccess_token());
                    }
                });

        BackpackerAPI api = adapter.build().create(BackpackerAPI.class);

        api.GetInfoFriendRequest(userId, new retrofit.Callback<User>() {
            @Override
            public void success(User friendsStat, Response response) {
                if (friendsStat.getUserName().equals("NoRequestsSent"))
                {
                    mFriendButton.setText("Send Friend Request");
                    mFriendButton.setEnabled(true);
                }
                else if (friendsStat.getUserName().equals("friends"))
                {
                    mFriendButton.setText("Friends");
                    mFriendButton.setEnabled(false);
                }
                else if (friendsStat.getUserName().equals("RequestSentByMe"))
                {
                    mFriendButton.setText("Friend Request Sent");
                    mFriendButton.setEnabled(false);
                }
                else if (friendsStat.getUserName().equals("RequestSentByOther"))
                {
                    mFriendButton.setText("Request Sent From Backpacker");
                    mFriendButton.setEnabled(false);
                }
                showProgress(false);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showProgress(false);
                mFriendButton.setEnabled(false);
            }
        });
    }
    public void AddFriend(String userId) {
        backgroundTaskInProgress = true;
        showProgress(true);

        user = userLocalStore.getLoggedInUser();

        RestAdapter.Builder adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + user.getAccess_token());
                    }
                });

        BackpackerAPI api = adapter.build().create(BackpackerAPI.class);

        api.SendRequest(userId, new retrofit.Callback<User>() {
            @Override
            public void success(User friendsStat, Response response) {

                mFriendButton.setText("Friend Request Sent");
                mFriendButton.setEnabled(false);
                showProgress(false);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showProgress(false);
                mFriendButton.setEnabled(true);
            }
        });
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
        switch (item.getItemId()) {
            case android.R.id.home:

                // onDetach();
                break;
        }
        return true;
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

    @Override
    public void onClick(View v) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void SetupToolbar(int i, String s);
    }
    /**
     * Shows the progress UI and hides the backpackeradapter form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mBackpackerView.setVisibility(show ? View.GONE : View.VISIBLE);
            mBackpackerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBackpackerView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mBackpackerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
