package com.k00140908.darren.the88days.UserInputScreens;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.k00140908.darren.the88days.Model.Car;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.RetrofitInterfaces.CarAPI;
import com.k00140908.darren.the88days.RetrofitInterfaces.FarmAPI;
import com.k00140908.darren.the88days.UserLocalStore;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

//import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddCarAdvertFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddCarAdvertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCarAdvertFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditText mCarDescription;
    public EditText mCarTitle;
    public EditText mCarPrice;
    public EditText mCarMobile;

    String fromUsDate;
            String toUsDate;
//
//    Title:2002 Holden Astra
//    AvailableFrom:04/12/16
//    AvailableTo:04/25/16
//    Description:Lovely car comes with all sleeping gear and kitchen stuff for travelling!! :)
//    Long:151.208353
//    Lat:-33.884993
//    Price:1900
//    ContactNumber:085145789542

    UserLocalStore userLocalStore;
    User user= null;
    final static String ENDPOINT ="https://the88days.azurewebsites.net/";

    View mAddCarFormView;
    View mProgressView;

TextView dateTextViewfrom;
    TextView dateTextViewto;
    Button dateButton;
    Spinner CarLocationSpinner;
    Car car;
    private OnFragmentInteractionListener mListener;

    public AddCarAdvertFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCarAdvertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddCarAdvertFragment newInstance(String param1, String param2) {
        AddCarAdvertFragment fragment = new AddCarAdvertFragment();
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
        View view =  inflater.inflate(R.layout.fragment_add_car_advert, container, false);

         mListener.SetupToolbar(1, "Adding Car Advert");

        userLocalStore = new UserLocalStore(getContext());
        car= userLocalStore.getUsersCar();

        dateTextViewfrom = (TextView) view.findViewById(R.id.date_textviewfrom);
        dateTextViewto = (TextView) view.findViewById(R.id.date_textviewto);

        mCarTitle = (EditText) view.findViewById(R.id.car_title);
        mCarDescription = (EditText) view.findViewById(R.id.car_description);
        mCarPrice = (EditText) view.findViewById(R.id.car_price);
        mCarMobile = (EditText) view.findViewById(R.id.car_mobile);


        Button mSaveCarButton = (Button) view.findViewById(R.id.save_car_button);
        mSaveCarButton.setOnClickListener(this);

        dateTextViewfrom.setText("Please select Available from Date");
        dateTextViewto.setText("Please select Available to Date");

        Button dateButtonfrom = (Button) view.findViewById(R.id.date_button_from);
        Button dateButtonto = (Button) view.findViewById(R.id.date_button_to);
        // Show a datepicker when the dateButton is clicked
        dateButtonfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddCarAdvertFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)

                );
                dpd.setThemeDark(true);
                //dpd.vibrate(vibrateDate.isChecked());
                dpd.dismissOnPause(true);
                //dpd.showYearPickerFirst(showYearFirst.isChecked());
//                if (modeCustomAccentDate.isChecked()) {
//                    dpd.setAccentColor(Color.parseColor("#9C27B0"));
//                }
//                if (titleDate.isChecked()) {
//                    dpd.setTitle("DatePicker Title");
//                }
//                if (limitDates.isChecked()) {
//                    Calendar[] dates = new Calendar[13];
//                    for (int i = -6; i <= 6; i++) {
//                        Calendar date = Calendar.getInstance();
//                        date.add(Calendar.MONTH, i);
//                        dates[i + 6] = date;
//                    }
//                    dpd.setSelectableDays(dates);
//                }
//                if (highlightDates.isChecked()) {
//                    Calendar[] dates = new Calendar[13];
//                    for (int i = -6; i <= 6; i++) {
//                        Calendar date = Calendar.getInstance();
//                        date.add(Calendar.WEEK_OF_YEAR, i);
//                        dates[i + 6] = date;
//                    }
                    //dpd.setHighlightedDays(dates);
                //}
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialogfrom");
            }
        });
        dateButtonto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddCarAdvertFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                        //Calendar.DATE
                );
                dpd.setThemeDark(true);
                //dpd.vibrate(vibrateDate.isChecked());
                dpd.dismissOnPause(true);
                //dpd.showYearPickerFirst(showYearFirst.isChecked());
//                if (modeCustomAccentDate.isChecked()) {
//                    dpd.setAccentColor(Color.parseColor("#9C27B0"));
//                }
//                if (titleDate.isChecked()) {
//                    dpd.setTitle("DatePicker Title");
//                }
//                if (limitDates.isChecked()) {
//                    Calendar[] dates = new Calendar[13];
//                    for (int i = -6; i <= 6; i++) {
//                        Calendar date = Calendar.getInstance();
//                        date.add(Calendar.MONTH, i);
//                        dates[i + 6] = date;
//                    }
//                    dpd.setSelectableDays(dates);
//                }
//                if (highlightDates.isChecked()) {
//                    Calendar[] dates = new Calendar[13];
//                    for (int i = -6; i <= 6; i++) {
//                        Calendar date = Calendar.getInstance();
//                        date.add(Calendar.WEEK_OF_YEAR, i);
//                        dates[i + 6] = date;
//                    }
                //dpd.setHighlightedDays(dates);
                //}
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialogto");
            }
        });


//        mFarmDescription = (EditText) view.findViewById(R.id.farm_description);
//        mFarmAddress = (EditText) view.findViewById(R.id.farm_address);
//        mFarmName = (EditText) view.findViewById(R.id.farm_name);

//        Button mFeedbackButton = (Button) view.findViewById(R.id.save_farm_button);
//        mFeedbackButton.setOnClickListener(this);

        mAddCarFormView = view.findViewById(R.id.new_car_form);
        mProgressView = view.findViewById(R.id.new_car_progress);

        CarLocationSpinner = (Spinner) view.findViewById(R.id.car_advert_location_spinner);

        String[] CarLocations = new String[] { "Use Current Location", "Adelaide", "Brisbane", "Broome","Bundaberg",
                "Canberra", "Cairns", "Darwin", "Melbourne", "Perth", "Townsville", "Rockhampton", "Sydney"};

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, CarLocations);

        CarLocationSpinner.setAdapter(adapter2);
        int b =0;
        for (String item: CarLocations)
        {
            if(item.equals(car.getCityLocation()))
            {
                CarLocationSpinner.setSelection(b);
                break;
            }
            b++;
        }

        CarLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
               car.setCityLocation((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_car_button:

                attemptSaveCar();

                break;
        }
    }

    private void attemptSaveCar() {
        // Store values at the time of the login attempt.
//        String Description = mFarmDescription.getText().toString();
//        String Address = mFarmAddress.getText().toString();
//        String Name = mFarmName.getText().toString();
        boolean cancel = false;
        View focusView = null;
//
//        // Check for a valid username, if the user entered one.
//        if (TextUtils.isEmpty(Description)) {
//            mFarmDescription.setError("Description Required");
//            focusView = mFarmDescription;
//            cancel = true;
//        }
//
//        // Check for a valid username, if the user entered one.
//        if (TextUtils.isEmpty(Address)) {
//            mFarmAddress.setError("Address Required");
//            focusView = mFarmAddress;
//            cancel = true;
//        }
//        // Check for a valid username, if the user entered one.
//        if (TextUtils.isEmpty(Name)) {
//            mFarmName.setError("Name Required");
//            focusView = mFarmName;
//            cancel = true;
//        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
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

            CarAPI api = adapter.build().create(CarAPI.class);

            String from = car.getAvailableFrom().toString();
            String to = car.getAvailableFrom().toString();


           String Title =mCarTitle.getText().toString();
            String AvailableFrom =dateTextViewfrom.getText().toString();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");


            String  AvailableTo =dateTextViewto.getText().toString();
            String  Description =mCarDescription.getText().toString();
          double  Long  =     user.getAlong();
           double Lat =user.getLat();
            double Price= Double.valueOf(mCarPrice.getText().toString());
            String ContactNumber= mCarMobile.getText().toString();

            String location =car.getCityLocation();

            api.uploadCar(mCarTitle.getText().toString(), fromUsDate, toUsDate, mCarDescription.getText().toString(),
                    user.getAlong(), user.getLat(), Double.valueOf(mCarPrice.getText().toString()), mCarMobile.getText().toString(), car.getCityLocation(), new Callback<User>() {

//                uploadCar(@Field("Title") String Title ,@Field("AvailableFrom") String AvailableFrom,@Field("AvailableTo") String AvailableTo,
//                          @Field("Description") String Description,@Field("Long") double along, @Field("Lat") double lat,@Field("Price") double Price,@Field("ContactNumber") double ContactNumber, Callback <User> callback);


//      ****Sample upload****
//                Title:2002 Holden Astra
//                AvailableFrom:04/12/16
//                AvailableTo:04/25/16
//                Description:Lovely car comes with all sleeping gear and kitchen stuff for travelling!! :)
//                Long:151.208353
//                Lat:-33.884993
//                Price:1900
//                ContactNumber:085145789542


                @Override
                public void failure(final RetrofitError error) {

                    String errorType = error.getKind().toString();
                    if (errorType.equals("NETWORK")) {
                        Toast.makeText(getActivity(), "Sorry an error has occurred please check your internet connection and try again.", Toast.LENGTH_LONG).show();
                        showProgress(false);
                    } else {
                        Toast.makeText(getActivity(), "Sorry an unknown error has occurred please try again.", Toast.LENGTH_LONG).show();
                        showProgress(false);
                    }
                }

                @Override
                public void success(User userReg, Response response) {

                    HideKeyboard.hideKeyboard(getActivity());
                    mListener.LoadManageCar();
                    onDetach();
                }
            });
        }
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
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            String date;
            String date2;
            date = dayOfMonth+"/"+(++monthOfYear)+"/"+year;
            date2 =(++monthOfYear)+"/"+dayOfMonth+"/"+year;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
            Date selectedDate = null;
            Date selectedDate2 = null;
            try {
                selectedDate = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if(view.getTag().equals("Datepickerdialogfrom"))
            {
                fromUsDate=date2;
                dateTextViewfrom.setText(date);
                car.setAvailableFrom(selectedDate);
            }
            if(view.getTag().equals("Datepickerdialogto"))
            {
                toUsDate=date2;
                dateTextViewto.setText(date);
                car.setAvailableTo(selectedDate);
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

        void SetupToolbar(int i, String s);

            void LoadManageCar();
        }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAddCarFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAddCarFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAddCarFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mAddCarFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
