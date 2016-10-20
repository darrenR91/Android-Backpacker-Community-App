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
import com.k00140908.darren.the88days.Model.Car;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.UserLocalStore;

import java.util.ArrayList;

public class FindCarsMapFragment extends Fragment implements ClusterManager.OnClusterItemInfoWindowClickListener<Car> ,OnMapReadyCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    UserLocalStore userLocalStore;
    User user= null;

    private OnFragmentInteractionListener mListener;

    private GoogleMap mMap;
    private ArrayList<Car> mListCars = new ArrayList<>();

    private ClusterManager<Car> mClusterManager;

    private SupportMapFragment mSupportMapFragment;

    public FindCarsMapFragment() {
        // Required empty public constructor
    }
    public static FindCarsMapFragment newInstance(String param1, String param2) {
        FindCarsMapFragment fragment = new FindCarsMapFragment();
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

       View rootView = inflater.inflate(R.layout.fragment_find_cars_map, container, false);

        userLocalStore = new UserLocalStore(getContext());


                if(savedInstanceState == null) {
                    //Toast.makeText(getActivity(), "Only rotated", Toast.LENGTH_SHORT).show();
                    setupMap();
                }
            return rootView;


    }
    public void setupMap()
    {
        if (mMap == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.car_map, mSupportMapFragment).commit();
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
        //Toast.makeText(getActivity(), nearby.toString(), Toast.LENGTH_SHORT).show();
        mListCars = mListener.getCarsList();

        if (mListCars.isEmpty()) {

            //mSwipeRefreshLayout.setRefreshing(false);
        }
        else {

            //Toast.makeText(getActivity(), "Load Map data", Toast.LENGTH_SHORT).show();
            mMap.clear();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nearby, 7));

            mClusterManager = new ClusterManager<Car>(getActivity(), mMap);
            mClusterManager.setRenderer(new CarRender());
            mMap.setOnCameraChangeListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            mMap.setOnInfoWindowClickListener(mClusterManager);
            mClusterManager.setOnClusterItemInfoWindowClickListener(this);
            mClusterManager.addItems(mListCars);
            mClusterManager.cluster();

        }
        mListener.setUpdatedCarList(true);
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
    public ArrayList<Car> getCurrentCarList()
    {
        return mListCars;
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
    public void onClusterItemInfoWindowClick(Car car) {
        //Toast.makeText(getActivity(), "Load Car", Toast.LENGTH_SHORT).show();
        mListener.ViewCarProfilebyMap(car);
    }
    private class CarRender extends DefaultClusterRenderer<Car> {

        public CarRender() {
            super(getActivity(), mMap, mClusterManager);
        }

        @Override
                 protected void onBeforeClusterItemRendered(Car car, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon)).title(car.getTitle());
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

        ArrayList<Car> getCarsList();

        void setUpdatedCarList(boolean updated);

        boolean getUpdatedCarList();

        void ViewCarProfilebyMap(Car car);
    }
}
