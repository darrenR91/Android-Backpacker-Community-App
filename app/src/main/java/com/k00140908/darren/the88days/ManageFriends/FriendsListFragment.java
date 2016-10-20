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
import com.k00140908.darren.the88days.RecycledListViewAdapters.AdapterFriendsList;
import com.k00140908.darren.the88days.RetrofitInterfaces.BackpackerAPI;
import com.k00140908.darren.the88days.UserLocalStore;
import com.k00140908.darren.the88days.db.MessengerDB;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ArrayList<User> mListFriends = new ArrayList<>();

    private AdapterFriendsList mAdapter;

    private FindBackpackersPagerAdapterFragment a;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    FloatingActionButton fab;

    private RecyclerView mRecyclerFriendLists;
    int defaultRadius=100;
    private View mProgressView;
    private View mFriendsListView;

    final static String ENDPOINT ="https://the88days.azurewebsites.net/";
    User user = null;
    UserLocalStore userLocalStore;

    MessengerDB messengerDB;
    private OnFragmentInteractionListener mListener;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsListFragment newInstance(String param1, String param2) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_friends_list, container, false);

        if(mListener.checkReturnFrag()) {
            return view;
        }
        messengerDB = new MessengerDB(getContext());
        userLocalStore = new UserLocalStore(getContext());
        mFriendsListView = view.findViewById(R.id.listFriends);
        mProgressView = view.findViewById(R.id.friends_list__progress);
        showProgress(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeFriendsList);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerFriendLists = (RecyclerView) view.findViewById(R.id.listFriends);
        //set the layout manager before trying to display data
        mRecyclerFriendLists.setLayoutManager(new LinearLayoutManager(getActivity()));

        userLocalStore = new UserLocalStore(getContext());
        if (mAdapter == null) {
            mAdapter = new AdapterFriendsList(getActivity(), new CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    //Toast.makeText(getActivity(), "Clicked Item: " + position, Toast.LENGTH_SHORT).show();
                    //mListener.ViewBackpackerProfilebyList(position);
                    User a = mListFriends.get(position);
                    selectOptions(a);
                }
            });

        }
        mRecyclerFriendLists.setAdapter(mAdapter);
        GetFriendList();

        //getParentFragment().getContext().checkCurrentlyVisible();
        //Toast.makeText(getActivity(), "Friends Loaded", Toast.LENGTH_SHORT).show();

        SharedPreferences prefs = getActivity().getSharedPreferences("userDetails", 0);
        boolean Visibile =  mListener.checkMessageListVisible();


        boolean calledFromNotification =  mListener.isCalledFromNotification();

        if (calledFromNotification)
        {
            SharedPreferences.Editor userLocalDatabaseEditor = prefs.edit();
            userLocalDatabaseEditor.putBoolean("MessageListOpen", true);
            userLocalDatabaseEditor.commit();
            //Toast.makeText(getActivity(), "Enter Message List From notification", Toast.LENGTH_SHORT).show();
        }
        else if (!Visibile)
        {
            SharedPreferences.Editor userLocalDatabaseEditor = prefs.edit();
            userLocalDatabaseEditor.putBoolean("MessageListOpen", false);
            userLocalDatabaseEditor.commit();
            //Toast.makeText(getActivity(), "Exit Message List to Friends List", Toast.LENGTH_SHORT).show();
        }

        return view;
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
        GetFriendList();
    }
    public void GetFriendList() {
        //backgroundTaskInProgress = true;
        showProgress(true);

        user = userLocalStore.getLoggedInUser();

        final RestAdapter.Builder adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + user.getAccess_token());
                    }
                });

        BackpackerAPI api = adapter.build().create(BackpackerAPI.class);

        api.GetFriends(new Callback<ArrayList<User>>() {
            @Override
            public void success(ArrayList<User> users, Response response) {
                showProgress(false);
                mListFriends = users;
                mAdapter.setFriendsList(mListFriends);
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
    public void selectOptions(final User friend) {
        final CharSequence[] items = {"Send Message", "View Profile", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Friend List Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Send Message")) {
                    //Toast.makeText(getActivity(), "Load Chat to "+friend.getUserName(), Toast.LENGTH_SHORT).show();
                    //messengerDB.inser tNewChat(friend.getUserId(),friend.getUserName());
                    mListener.LoadChatFragment(friend);
                } else if (items[item].equals("View Profile")) {
                    Toast.makeText(getActivity(), "Load Profile of "+friend.getUserName(), Toast.LENGTH_SHORT).show();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        boolean checkMessageListVisible();

        boolean checkReturnFrag();

        boolean isCalledFromNotification();

        void LoadChatFragment(User friend);
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

            mFriendsListView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFriendsListView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFriendsListView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mFriendsListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
