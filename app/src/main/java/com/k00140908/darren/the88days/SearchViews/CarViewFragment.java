package com.k00140908.darren.the88days.SearchViews;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.UserLocalStore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CarViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CarViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm a");

    public TextView mCarDescription;
    public TextView mCarTitle;
    public TextView mCarPrice;
    public TextView mCarMobile;
    public TextView mSeller;
    private ImageView mImageView;
    UserLocalStore userLocalStore;
    User user= null;
    final static String ENDPOINT ="https://the88days.azurewebsites.net/";

    View mAddCarFormView;
    View mProgressView;

    TextView dateTextViewfrom;
    TextView dateTextViewto;
    Button dateButton;
    Spinner CarLocationSpinner;

    private OnFragmentInteractionListener mListener;

    public CarViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarViewFragment newInstance(String param1, String param2) {
        CarViewFragment fragment = new CarViewFragment();
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
        View view =  inflater.inflate(R.layout.fragment_car_view, container, false);
        mListener.SetupToolbar(1,"Car Details");

        Bundle b = getArguments();

        String UserName;
        String ContactNumber;
        String UserNameId;
        String Description;
        String Title;
        String CityLocation;
        String AvailableFrom;
        String AvailableTo;
        String carPhoto;
        String mPosition;
        String Price;

        mImageView = (ImageView) view.findViewById(R.id.carphoto);
        dateTextViewfrom = (TextView) view.findViewById(R.id.car_date_from);
        dateTextViewto = (TextView) view.findViewById(R.id.car_date_to);
        mCarTitle = (TextView) view.findViewById(R.id.car_title);
        mCarDescription = (TextView) view.findViewById(R.id.car_description);
        mCarPrice = (TextView) view.findViewById(R.id.car_price);
        mCarMobile = (TextView) view.findViewById(R.id.car_contact_number);

        if (b!=null)
        {
            //mSeller.setText(b.getString("UserName"));
            mCarMobile.setText(b.getString("ContactNumber"));
            //.setText(b.getString("UserNameId"));
            mCarDescription.setText(b.getString("Description"));
            mCarTitle.setText(b.getString("Title"));
           // .setText(b.getString("CityLocation"));
           // .setText(b.getString("AvailableFrom"));
           // .setText(b.getString("AvailableTo"));
         carPhoto = b.getString("CarPhoto");
            //.setText(b.getString("mPosition"));
            String price = Double.toString(b.getDouble("Price"));
            mCarPrice.setText(price);
        }


//        dateTextViewfrom.setText(sdf.format(b.getString("AvailableFrom")));
//        dateTextViewto.setText(sdf.format(b.getString("AvailableFrom")));

        dateTextViewfrom.setText(b.getString("AvailableFrom"));
        dateTextViewto.setText(b.getString("AvailableTo"));

            Picasso.with(getContext())
                    .load(b.getString("CarPhoto")).error(R.drawable.ic_contact_picture)
                    .into(mImageView);









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

        void SetupToolbar(int i, String s);
    }
}
