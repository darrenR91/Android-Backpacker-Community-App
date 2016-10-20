package com.k00140908.darren.the88days.PagerAdapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.k00140908.darren.the88days.ListFragments.FindBackpackersListFragment;
import com.k00140908.darren.the88days.MapFragments.FindBackpackersMapFragment;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.UserLocalStore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindBackpackersPagerAdapterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindBackpackersPagerAdapterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindBackpackersPagerAdapterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public FragmentManager mFragmentManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

boolean updated=false;
    Adapter adapter;
    private GoogleMap mMap;
    final static String ENDPOINT ="https://the88days.azurewebsites.net/";
    User user = null;
    UserLocalStore userLocalStore;
    private ArrayList<User> mListBackpackers = new ArrayList<>();
    private View mProgressView;
    private View mAdapterFormView;
    ViewPager viewPager;
    private OnFragmentInteractionListener mListener;

    public FindBackpackersPagerAdapterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFarmsPagerAdapterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindBackpackersPagerAdapterFragment newInstance(String param1, String param2) {
        FindBackpackersPagerAdapterFragment fragment = new FindBackpackersPagerAdapterFragment();
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
        View v = inflater.inflate(R.layout.fragment_find_backpackers_pager_adapter, container, false);

        
//        if (savedInstanceState != null) {
//            // Restore value of members from saved state
//        }
//
//else {

        mListener.SetupToolbar(0,"Finding Backpackers");

        if(mListener.checkReturnFrag()) {
            return v;
        }
        
            mListener.retrieveBackpackerList(0);

            mProgressView = v.findViewById(R.id.login_progress);

            //mAdapterFormView = v.findViewById(R.id.viewpager);

            //showProgress(true);
            //mListener.retrieveBackpackerList(50);

            viewPager = (ViewPager) v.findViewById(R.id.viewpager);
            if (viewPager != null) {
                setupViewPager(viewPager);
            }

            TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
            //tabLayout.setOnTabSelectedListener(this);

            //sleeping();
      ///  }
            return v;

    }

    public void sleeping() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void updateFromMain()
    {
        FindBackpackersListFragment frag1 = (FindBackpackersListFragment)viewPager.getAdapter().instantiateItem(viewPager, 0);
        frag1.GetBackpackers();
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

        void retrieveBackpackerList(int radius);

        boolean getUpdatedBackpackerList();

        void setUpdatedBackpackerList(boolean updated);

        void SetupToolbar(int i, String s);

        boolean checkReturnFrag();
    }

    // from setup
    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getChildFragmentManager()); // This fixed the issue//
        adapter.addFragment((new FindBackpackersListFragment()), "List");
        adapter.addFragment(new FindBackpackersMapFragment(), "Map");
        viewPager.setAdapter(adapter);

        }

                //from sample
                class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);

        }

        @Override
        public int getCount() {

            FindBackpackersListFragment frag1 = (FindBackpackersListFragment)viewPager.getAdapter().instantiateItem(viewPager, 0);
            FindBackpackersMapFragment frag2 = (FindBackpackersMapFragment)viewPager.getAdapter().instantiateItem(viewPager, 1);

            if(!frag1.getCurrentBackpackerList().equals(frag2.getCurrentBackpackerList()))
                {
                    frag1.GetBackpackers();
                    frag2.setupMap();
                    //adapter.notifyDataSetChanged();

                    //adapter.notify();
                    //return POSITION_NONE;
                }
            else
                {
                    return mFragments.size();
                }
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
//        @Override
//        public int getItemPosition(Object object) {
//
//            FindBackpackersListFragment frag1 = (FindBackpackersListFragment)viewPager.getAdapter().instantiateItem(viewPager, 0);
//            FindBackpackersMapFragment frag2 = (FindBackpackersMapFragment)viewPager.getAdapter().instantiateItem(viewPager, 1);
//
//            if(!frag1.getCurrentBackpackerList().equals(frag2.getCurrentBackpackerList()))
//            {
//                return -2;
//            }
//            else
//            {
//                return -1;
//            }
//        }
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
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
