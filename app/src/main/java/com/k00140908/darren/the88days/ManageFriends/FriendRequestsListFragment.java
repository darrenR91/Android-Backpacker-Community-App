package com.k00140908.darren.the88days.ManageFriends;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.PagerAdapters.CustomItemClickListener;
import com.k00140908.darren.the88days.PagerAdapters.FindBackpackersPagerAdapterFragment;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.RecycledListViewAdapters.AdapterFriendsRequestList;
import com.k00140908.darren.the88days.RetrofitInterfaces.BackpackerAPI;
import com.k00140908.darren.the88days.UserLocalStore;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendRequestsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendRequestsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendRequestsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private ArrayList<User> mListFriendsRequests = new ArrayList<>();

    private AdapterFriendsRequestList mAdapter;

    private FindBackpackersPagerAdapterFragment a;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    FloatingActionButton fab;

    private RecyclerView mRecyclerFriendRequests;
    int defaultRadius=100;
    private View mProgressView;
    private View mFriendRequestView;

    final static String ENDPOINT ="https://the88days.azurewebsites.net/";
    User user = null;
    UserLocalStore userLocalStore;


    private OnFragmentInteractionListener mListener;

    public FriendRequestsListFragment() {
        // Required empty public constructor
    }


    public static FriendRequestsListFragment newInstance(String param1, String param2) {
        FriendRequestsListFragment fragment = new FriendRequestsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_friend_request, container, false);

        mFriendRequestView = view.findViewById(R.id.listFriendsRequest);
        mProgressView = view.findViewById(R.id.friends_request_list__progress);
        showProgress(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeFriendsRequestList);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerFriendRequests = (RecyclerView) view.findViewById(R.id.listFriendsRequest);
        //set the layout manager before trying to display data
        mRecyclerFriendRequests.setLayoutManager(new LinearLayoutManager(getActivity()));

        userLocalStore = new UserLocalStore(getContext());
        if (mAdapter == null) {
            mAdapter = new AdapterFriendsRequestList(getActivity(), new CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    User a = mListFriendsRequests.get(position);
                    selectOptions(position,a.getUserId());
                }
            });

        }
        mRecyclerFriendRequests.setAdapter(mAdapter);
        GetFriendRequestList();

        SharedPreferences prefs = getActivity().getSharedPreferences("userDetails", 0);
        boolean Visibile =  mListener.checkMessageListVisible();

        if (!Visibile)
        {
            SharedPreferences.Editor userLocalDatabaseEditor = prefs.edit();
            userLocalDatabaseEditor.putBoolean("MessageListOpen", false);
            userLocalDatabaseEditor.commit();
            //Toast.makeText(getActivity(), "Exit Message List To Friends Request List", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
    public void selectOptions(final int position, final String friendId) {
        final CharSequence[] items = {"Accept", "Decline","View Profile", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Friend Request List Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Accept")) {
                    Toast.makeText(getActivity(), "Accept", Toast.LENGTH_SHORT).show();
                    FriendRequestResponse(true,position,friendId);
                } else if (items[item].equals("Decline")) {
                    Toast.makeText(getActivity(), "Decline", Toast.LENGTH_SHORT).show();
                    FriendRequestResponse(false,position,friendId);
                } else if (items[item].equals("View Profile")) {
                    Toast.makeText(getActivity(), "Load Profile", Toast.LENGTH_SHORT).show();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    @Override
    public void onRefresh() {
        GetFriendRequestList();
    }
    public void GetFriendRequestList() {
        //backgroundTaskInProgress = true;
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

        api.GetFriendRequests(new Callback<ArrayList<User>>() {
            @Override
            public void success(ArrayList<User> users, Response response) {
                mListFriendsRequests = users;
                showProgress(false);
                mAdapter.setFriendsRequestList(mListFriendsRequests);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showProgress(false);
                Toast.makeText(getActivity(), "Please Try Again an Error has occurred.", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    public void FriendRequestResponse(boolean reply, final int position,String friendId) {
        //backgroundTaskInProgress = true;
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

        api.FriendRequestResponse(reply, friendId, new Callback<ArrayList<User>>() {
            @Override
            public void success(ArrayList<User> users, Response response) {
                mListFriendsRequests.remove(position);
                showProgress(false);
                mAdapter.setFriendsRequestList(mListFriendsRequests);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showProgress(false);
                Toast.makeText(getActivity(), "Please Try Again an Error has occurred.", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        boolean checkMessageListVisible();
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

            mFriendRequestView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFriendRequestView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFriendRequestView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mFriendRequestView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
