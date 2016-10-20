package com.k00140908.darren.the88days.PagerAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.k00140908.darren.the88days.ManageFriends.FriendRequestsListFragment;
import com.k00140908.darren.the88days.ManageFriends.FriendsListFragment;
import com.k00140908.darren.the88days.ManageFriends.MessagesFragment;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.UserInputScreens.HideKeyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darre on 22/02/2016.
 */
public class FriendsPagerAdapterFragment extends Fragment {

    int notification;
    private OnFragmentInteractionListener mListener;
    ViewPager viewPager;

    public FriendsPagerAdapterFragment() {
    }

    public static FindCarsPagerAdapterFragment newInstance(String param1, String param2) {
        FindCarsPagerAdapterFragment fragment = new FindCarsPagerAdapterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friends_adapter, container, false);


        
        mListener.SetupToolbar(0,"Friends");

        if(getArguments() != null) {
         notification = getArguments().getInt("Notification");
        }
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        //////////////////////////////
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                SharedPreferences prefs = getActivity().getSharedPreferences("userDetails", 0);

                if(position == 0){  // if you want the second page, for example
                    //Toast.makeText(getActivity(), "Enter Message List", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor userLocalDatabaseEditor = prefs.edit();
                    userLocalDatabaseEditor.putBoolean("MessageListOpen", true);
                    userLocalDatabaseEditor.commit();
                    //Toast.makeText(getActivity(), "Enter Message List", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Toast.makeText(getActivity(), "Exit Message List", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor userLocalDatabaseEditor = prefs.edit();
                    userLocalDatabaseEditor.putBoolean("MessageListOpen", false);
                    userLocalDatabaseEditor.commit();
                    //Toast.makeText(getActivity(), "Exit Message List to Friends List", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ///////////////////////////////////


        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        HideKeyboard.hideKeyboard(getActivity());
        return v;
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
    public boolean checkMessageListVisible()
    {
        if(viewPager.getCurrentItem()==0)
        {
            return true;
        }
        else
        {
            return false;
        }
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

        void SetupToolbar(int i, String friends);

        boolean checkReturnFrag();

        void setCalledFromNotification();
    }
    // from setup
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager()); // This fixed the issue//
        adapter.addFragment(new MessagesFragment(), "Messages");
        adapter.addFragment(new FriendsListFragment(), "Friends");
        adapter.addFragment(new FriendRequestsListFragment(), "Friend Requests");
        viewPager.setAdapter(adapter);

        if(notification==1)
        {
            mListener.setCalledFromNotification();
        }
        else if (notification == 2)
        {
            viewPager.setCurrentItem(2);
        }
        else if (notification == 3)
        {
            viewPager.setCurrentItem(1);
        }
    }

    //from sample
    static class Adapter extends FragmentPagerAdapter {
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
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
