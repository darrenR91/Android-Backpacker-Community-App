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
import com.k00140908.darren.the88days.Model.Farm;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.UserInputScreens.HideKeyboard;
import com.k00140908.darren.the88days.UserLocalStore;

import java.util.ArrayList;

public class FindFarmsMapFragment extends Fragment implements ClusterManager.OnClusterItemInfoWindowClickListener<Farm> ,OnMapReadyCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private GoogleMap mMap;
    private ArrayList<Farm> mListFarms = new ArrayList<>();

    UserLocalStore userLocalStore;
    User user= null;

    private ClusterManager<Farm> mClusterManager;

    private SupportMapFragment mSupportMapFragment;

    public FindFarmsMapFragment() {
        // Required empty public constructor
    }
    public static FindFarmsMapFragment newInstance(String param1, String param2) {
        FindFarmsMapFragment fragment = new FindFarmsMapFragment();
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

       View rootView = inflater.inflate(R.layout.fragment_find_farms_map, container, false);

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
            fragmentTransaction.replace(R.id.farm_map, mSupportMapFragment).commit();
            mSupportMapFragment.getMapAsync(this);
        }
        else
        {
            setupMapData();
        }

    }
    public void setupMapData() {

        user = userLocalStore.getLoggedInUser();
        LatLng nearby = new LatLng(user.getLat(),user.getAlong());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nearby, 7));

        mListFarms = mListener.getFarmsList();

        if (mListFarms.isEmpty()) {
            //Toast.makeText(getActivity(), "No Map data", Toast.LENGTH_SHORT).show();
            //mSwipeRefreshLayout.setRefreshing(false);
        }
        else {

            //Toast.makeText(getActivity(), "Load Map data", Toast.LENGTH_SHORT).show();
            mMap.clear();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nearby, 7));

            mClusterManager = new ClusterManager<Farm>(getActivity(), mMap);
            mClusterManager.setRenderer(new FarmRender());
            mMap.setOnCameraChangeListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            mMap.setOnInfoWindowClickListener(mClusterManager);
            mClusterManager.setOnClusterItemInfoWindowClickListener(this);
            mClusterManager.addItems(mListFarms);
            mClusterManager.cluster();

        }
        mListener.setUpdatedFarmList(true);
    }
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
    public ArrayList<Farm> getCurrentFarmList()
    {
        return mListFarms;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {

            googleMap.getUiSettings().setAllGesturesEnabled(true);
            mMap = googleMap;
            setupMapData();
        }
    }

    @Override
    public void onClusterItemInfoWindowClick(Farm farm) {
        //Toast.makeText(getActivity(), "Load Farm", Toast.LENGTH_SHORT).show();
        mListener.ViewFarmProfilebyMap(farm);
    }
    private class FarmRender extends DefaultClusterRenderer<Farm> {

        public FarmRender() {
            super(getActivity(), mMap, mClusterManager);
        }

        @Override
                 protected void onBeforeClusterItemRendered(Farm farm, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.barn_icon)).title(farm.getName());
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

        ArrayList<Farm> getFarmsList();

        void setUpdatedFarmList(boolean updated);

        boolean getUpdatedFarmList();

        void ViewFarmProfilebyMap(Farm farm);
    }
}
