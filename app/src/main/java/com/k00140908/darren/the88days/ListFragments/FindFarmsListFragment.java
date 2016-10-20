package com.k00140908.darren.the88days.ListFragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.k00140908.darren.the88days.Model.Farm;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.PagerAdapters.CustomItemClickListener;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.RecycledListViewAdapters.AdapterFarms;
import com.k00140908.darren.the88days.UserInputScreens.HideKeyboard;
import com.k00140908.darren.the88days.UserLocalStore;

import java.util.ArrayList;


public class FindFarmsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private ArrayList<Farm> mListFarms = new ArrayList<>();

    private AdapterFarms mAdapter;

    FloatingActionButton fab;


    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerFarms;
    int defaultRadius=100;
    private View mProgressView;
    private View mAdapterFormView;

    final static String ENDPOINT ="https://the88days.azurewebsites.net/";
    User user = null;
    UserLocalStore userLocalStore;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FindFarmsListFragment() {
        // Required empty public constructor
    }
    public static FindFarmsListFragment newInstance(String param1, String param2) {
        FindFarmsListFragment fragment = new FindFarmsListFragment();
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
            View view = inflater.inflate(R.layout.fragment_find_farms_list, container, false);



            mAdapterFormView = view.findViewById(R.id.listFarms);
            mProgressView = view.findViewById(R.id.farms_progress);
            showProgress(true);

            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeFarmsList);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerFarms = (RecyclerView) view.findViewById(R.id.listFarms);
            //set the layout manager before trying to display data
        mRecyclerFarms.setLayoutManager(new LinearLayoutManager(getActivity()));

            userLocalStore = new UserLocalStore(getContext());
            if (mAdapter == null) {
                mAdapter = new AdapterFarms(getActivity(), new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        //Toast.makeText(getActivity(), "Clicked Item: " + position, Toast.LENGTH_SHORT).show();
                        mListener.ViewFarmProfilebyList(position);

                    }
                });

            }
        mRecyclerFarms.setAdapter(mAdapter);

            //mListener.retrieveFarmList(0);
            user = userLocalStore.getLoggedInUser();


        fab = (FloatingActionButton) view.findViewById(R.id.farm_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.loadSettings(1);
                Toast.makeText(getActivity(), "Change Radius for farms", Toast.LENGTH_SHORT).show();
            }
        });

        if(mListener.getUpdatedFarmList()) {
            GetFarms();
        }
        else
        {
            fab.show();
        }
        HideKeyboard.hideKeyboard(getActivity());
            //mListener.setUpdatedFarmList(true);
            return view;




    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public ArrayList<Farm> getCurrentFarmList()
    {
        return mListFarms;
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
        defaultRadius=300;
        mListener.retrieveFarmList(defaultRadius);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        ArrayList<Farm> getFarmsList();

        void setUpdatedFarmList(boolean updated);

        void setFarmsList(ArrayList<Farm> mListFarms);

        void retrieveFarmList(int radius);

        boolean getUpdatedFarmList();

        void ViewFarmProfilebyList(int position);

        void loadSettings(int i);

        boolean getRefinedFarmList();

        void SetupToolbar(int i, String feedback);
    }
    public void GetFarms()
    {

        mListFarms = mListener.getFarmsList();
        if (mListFarms.isEmpty()) {
                Toast.makeText(getActivity(), "Your All Alone Sorry!!\n Please spread the word to build network of Farms to help each other :)", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
        }
        else {
            mAdapter.setFarmsList(mListFarms);
            mSwipeRefreshLayout.setRefreshing(false);
        }
        showProgress(false);
        fab.show();
    }

    /**
     * Shows the progress UI and hides the Farmadapter form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAdapterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAdapterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAdapterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mAdapterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
