package com.k00140908.darren.the88days.MapFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.UserInputScreens.HideKeyboard;
import com.k00140908.darren.the88days.UserLocalStore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindBackpackersMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindBackpackersMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindBackpackersMapFragment extends Fragment implements ClusterManager.OnClusterItemInfoWindowClickListener<User> ,OnMapReadyCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    UserLocalStore userLocalStore;
    User user= null;

    private GoogleMap mMap;
    private ArrayList<User> mListBackpackers = new ArrayList<>();

    private ClusterManager<User> mClusterManager;

    private SupportMapFragment mSupportMapFragment;

    //    public FindBackpackersMapFragment(ArrayList<User> ListBackpackers) {
//        this.mListBackpackers=ListBackpackers;
//    }
    public FindBackpackersMapFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindBackpackersMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindBackpackersMapFragment newInstance(String param1, String param2) {
        FindBackpackersMapFragment fragment = new FindBackpackersMapFragment();
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

            View rootView = inflater.inflate(R.layout.fragment_find_backpackers_map, container, false);

        userLocalStore = new UserLocalStore(getContext());

            //if(savedInstanceState == null) {
                //Toast.makeText(getActivity(), "Only rotated", Toast.LENGTH_SHORT).show();
                setupMap();
            //}

            HideKeyboard.hideKeyboard(getActivity());

            return rootView;


    }


    public void setupMap()
    {
        if (mMap == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapwhere, mSupportMapFragment).commit();
            mSupportMapFragment.getMapAsync(this);
        }
        else
        {
            setupMapData();
        }

    }
    public void setupMapData() {
//        if(mMap==null) {
//            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
//        }
        user = userLocalStore.getLoggedInUser();
        LatLng nearby = new LatLng(user.getLat(),user.getAlong());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nearby, 7));

        mListBackpackers = mListener.getBackpackersList();

        if (mListBackpackers.isEmpty()) {
            //Toast.makeText(getActivity(), "No Map data", Toast.LENGTH_SHORT).show();
            //mSwipeRefreshLayout.setRefreshing(false);
        }
        else {
//            for(User user : mListBackpackers)
//            {
//                LatLng Backpacker = new LatLng(user.getLat(),user.getAlong());
//                mMap.addMarker(new MarkerOptions().position(Backpacker).title(user.getUserName()));
//            }
            //Toast.makeText(getActivity(), "Load Map data", Toast.LENGTH_SHORT).show();
            mMap.clear();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nearby, 7));

            mClusterManager = new ClusterManager<User>(getActivity(), mMap);
//            if(mClusterManager!=null)
//            {
//                mClusterManager.clearItems();
//            }
//            else
//            {
//                mClusterManager = new ClusterManager<User>(getActivity(), mMap);
//            }
            mClusterManager.setRenderer(new BackpackerRender());
            mMap.setOnCameraChangeListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            mMap.setOnInfoWindowClickListener(mClusterManager);
            mClusterManager.setOnClusterItemInfoWindowClickListener(this);
            mClusterManager.addItems(mListBackpackers);
            mClusterManager.cluster();

        }
        mListener.setUpdatedBackpackerList(true);
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
    public ArrayList<User> getCurrentBackpackerList()
    {
        return mListBackpackers;
    }
    @Override
    public void onDetach() {
//        Fragment fragment = (getFragmentManager().findFragmentById(R.id.mapwhere));
//        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        ft.remove(fragment);
//        ft.commit();
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onClusterItemInfoWindowClick(User user) {
        //Toast.makeText(getActivity(), "Load Profile", Toast.LENGTH_SHORT).show();
        mListener.ViewBackpackerProfilebyMap(user);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {

            googleMap.getUiSettings().setAllGesturesEnabled(true);
            mMap = googleMap;
            setupMapData();
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
    private class BackpackerRender extends DefaultClusterRenderer<User> {

        public BackpackerRender() {
            super(getActivity(), mMap, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(User person, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.backpacker_medium)).title(person.getUserName());
        }
        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 3;
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        ArrayList<User> getBackpackersList();

        void setUpdatedBackpackerList(boolean updated);

        boolean getUpdatedBackpackerList();

        void ViewBackpackerProfilebyMap(User user);
    }
}
