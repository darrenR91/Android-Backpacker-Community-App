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

import com.k00140908.darren.the88days.Model.Car;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.PagerAdapters.CustomItemClickListener;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.RecycledListViewAdapters.AdapterCars;
import com.k00140908.darren.the88days.UserLocalStore;

import java.util.ArrayList;

public class FindCarsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private ArrayList<Car> mListCars = new ArrayList<>();

    private AdapterCars mAdapter;

    FloatingActionButton fab;
    //private FindCarsPagerAdapterFragment a;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerCars;
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

    public FindCarsListFragment() {
        // Required empty public constructor
    }
    public static FindCarsListFragment newInstance(String param1, String param2) {
        FindCarsListFragment fragment = new FindCarsListFragment();
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
            View view = inflater.inflate(R.layout.fragment_find_cars_list, container, false);

            mAdapterFormView = view.findViewById(R.id.listCars);
            mProgressView = view.findViewById(R.id.cars_progress);
            showProgress(true);

            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeCarsList);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerCars = (RecyclerView) view.findViewById(R.id.listCars);
            //set the layout manager before trying to display data
        mRecyclerCars.setLayoutManager(new LinearLayoutManager(getActivity()));

            userLocalStore = new UserLocalStore(getContext());
            if (mAdapter == null) {
                mAdapter = new AdapterCars(getActivity(), new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Toast.makeText(getActivity(), "Clicked Item: "+position,Toast.LENGTH_SHORT).show();
                        mListener.ViewCarProfilebyList(position);
                    }
                });

            }
        mRecyclerCars.setAdapter(mAdapter);

            //mListener.retrieveCarList(0);
            user = userLocalStore.getLoggedInUser();



        fab = (FloatingActionButton) view.findViewById(R.id.car_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.loadSettings(3);
                Toast.makeText(getActivity(), "Change Radius for cars", Toast.LENGTH_SHORT).show();
            }
        });

        if(mListener.getUpdatedCarList()) {
            GetCars();
        }
        else
        {
            fab.show();
        }
            //mListener.setUpdatedCarList(true);
            return view;




    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public ArrayList<Car> getCurrentCarList()
    {
        return mListCars;
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
        mListener.retrieveCarList(defaultRadius);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        ArrayList<Car> getCarsList();

        void setUpdatedCarList(boolean updated);

        void setCarsList(ArrayList<Car> mListCars);

        void retrieveCarList(int radius);

        boolean getUpdatedCarList();

        void ViewCarProfilebyList(int position);

        void loadSettings(int i);
    }
    public void GetCars()
    {

        mListCars = mListener.getCarsList();
        if (mListCars.isEmpty()) {
                Toast.makeText(getActivity(), "Your All Alone Sorry!!\n Please spread the word to build network of Cars to help each other :)", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
        }
        else {
            mAdapter.setCarsList(mListCars);
            mSwipeRefreshLayout.setRefreshing(false);
        }
        showProgress(false);
        fab.show();
    }

    /**
     * Shows the progress UI and hides the Caradapter form.
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
