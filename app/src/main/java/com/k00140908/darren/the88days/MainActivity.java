package com.k00140908.darren.the88days;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.k00140908.darren.the88days.ListFragments.FindBackpackersListFragment;
import com.k00140908.darren.the88days.ListFragments.FindCarsListFragment;
import com.k00140908.darren.the88days.ListFragments.FindFarmsListFragment;
import com.k00140908.darren.the88days.ManageFriends.ChatFragment;
import com.k00140908.darren.the88days.ManageFriends.FriendRequestsListFragment;
import com.k00140908.darren.the88days.ManageFriends.FriendsListFragment;
import com.k00140908.darren.the88days.ManageFriends.ManageGCM.RegistrationIntentService;
import com.k00140908.darren.the88days.ManageFriends.MessagesFragment;
import com.k00140908.darren.the88days.MapFragments.FindBackpackersMapFragment;
import com.k00140908.darren.the88days.MapFragments.FindCarsMapFragment;
import com.k00140908.darren.the88days.MapFragments.FindFarmsMapFragment;
import com.k00140908.darren.the88days.Model.Car;
import com.k00140908.darren.the88days.Model.Farm;
import com.k00140908.darren.the88days.Model.RetrofitErrors;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.PagerAdapters.FindBackpackersPagerAdapterFragment;
import com.k00140908.darren.the88days.PagerAdapters.FindCarsPagerAdapterFragment;
import com.k00140908.darren.the88days.PagerAdapters.FindFarmsPagerAdapterFragment;
import com.k00140908.darren.the88days.PagerAdapters.FriendsPagerAdapterFragment;
import com.k00140908.darren.the88days.RetrofitInterfaces.BackpackerAPI;
import com.k00140908.darren.the88days.RetrofitInterfaces.CarAPI;
import com.k00140908.darren.the88days.RetrofitInterfaces.FarmAPI;
import com.k00140908.darren.the88days.SearchViews.BackpackerViewFragment;
import com.k00140908.darren.the88days.SearchViews.CarViewFragment;
import com.k00140908.darren.the88days.SearchViews.FarmViewFragment;
import com.k00140908.darren.the88days.UserInputScreens.AddCarAdvertFragment;
import com.k00140908.darren.the88days.UserInputScreens.AddFarmFragment;
import com.k00140908.darren.the88days.UserInputScreens.FeedbackFragment;
import com.k00140908.darren.the88days.UserInputScreens.FindBackpackerOptionsFragment;
import com.k00140908.darren.the88days.UserInputScreens.FindCarOptionsFragment;
import com.k00140908.darren.the88days.UserInputScreens.FindFarmOptionsFragment;
import com.k00140908.darren.the88days.UserInputScreens.ManageCarFragment;
import com.k00140908.darren.the88days.UserInputScreens.SettingsFragment;
import com.k00140908.darren.the88days.db.MessengerDB;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

//import com.k00140908.darren.the88days.ChatStuff.MessagesOldFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FindFarmsListFragment.OnFragmentInteractionListener,
        FindFarmsMapFragment.OnFragmentInteractionListener,
        FindFarmsPagerAdapterFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        FindBackpackersPagerAdapterFragment.OnFragmentInteractionListener,
        FindCarsPagerAdapterFragment.OnFragmentInteractionListener,
        FindCarsListFragment.OnFragmentInteractionListener,
        FindCarsMapFragment.OnFragmentInteractionListener,
        FriendsPagerAdapterFragment.OnFragmentInteractionListener,
        FriendsListFragment.OnFragmentInteractionListener,
        FriendRequestsListFragment.OnFragmentInteractionListener,
        MessagesFragment.OnFragmentInteractionListener,
        FindBackpackersListFragment.OnFragmentInteractionListener,
        FindBackpackersMapFragment.OnFragmentInteractionListener,
        FarmViewFragment.OnFragmentInteractionListener,
        BackpackerViewFragment.OnFragmentInteractionListener,
        CarViewFragment.OnFragmentInteractionListener,
        FeedbackFragment.OnFragmentInteractionListener,

        AddFarmFragment.OnFragmentInteractionListener,
        FindBackpackerOptionsFragment.OnFragmentInteractionListener,
        FindFarmOptionsFragment.OnFragmentInteractionListener,
        FindCarOptionsFragment.OnFragmentInteractionListener,
        ManageCarFragment.OnFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SettingsFragment.OnFragmentInteractionListener,
        AddCarAdvertFragment.OnFragmentInteractionListener,
        ChatFragment.OnFragmentInteractionListener

{
    //**************GCM NEW DATA************************//

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;






    //**************GCM NEW DATA END************************//
    boolean ReturnFromNestedFrag = false;
    int retrys = 0;
    int retrysWith401 = 0;
    private ArrayList<User> mListBackpackers = new ArrayList<>();
    private ArrayList<Farm> mListFarms = new ArrayList<>();
    private ArrayList<Car> mListCars = new ArrayList<>();
    Boolean updatedBackpackerSearch = false;
    Boolean updatedFarmSearch = false;
    Boolean updatedCarSearch = false;
    private int radiusFarmList = 0;

    private boolean refineBackpackerList = false;
    private boolean refineFarmList = false;
    private boolean refineCarList = false;

    private int radiusBackpackerList = 0;
    private int radiusCarList = 0;
    private boolean calledFromNotification=false;

    final static String ENDPOINT = "https://the88days.azurewebsites.net/";
    UserLocalStore userLocalStore;
    User user = null;


    private GoogleApiClient client;
    ActionBarDrawerToggle toggle;

    MessengerDB messagesDB;

    ///Finding Cars Additional Details
    String mAvailableFrom="";
    String mAvailableTo="";
    String mPriceFrom="";
    String mPriceTo="";
    String mCityLocation="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Always call the superclass first

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            radiusBackpackerList = savedInstanceState.getInt("backpackerSearchRadius");
            radiusFarmList = savedInstanceState.getInt("farmSearchRadius");
        }


        userLocalStore = new UserLocalStore(this);
        if (authenticate() == true) {

            messagesDB = new MessengerDB(getApplicationContext());

            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


            setSupportActionBar(toolbar);

            client = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            // if GPS is not enabled, start GPS settings activity
            LocationManager locationManager =
                    (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "Please enable GPS!",
                        Toast.LENGTH_LONG).show();
                Intent intent =
                        new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

            client.connect();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!toggle.isDrawerIndicatorEnabled()) {
                        toggle.setDrawerIndicatorEnabled(true);
                        FragmentManager fm = getSupportFragmentManager();
                        fm.popBackStack();
                    }
                }
            });
            if (getIntent().getStringExtra("Notifications")!= null )
            {
                FragmentManager fm = getSupportFragmentManager();
                navigationView.getMenu().getItem(4).setChecked(true);

                Bundle bundle = new Bundle();
                Fragment FriendsPagerAdapterFragment = new FriendsPagerAdapterFragment();

                if (getIntent().getStringExtra("Notifications").equals("NewMessage"))
                {
                    bundle.putInt("Notification", 1);
                    FriendsPagerAdapterFragment.setArguments(bundle);
                    fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fm.beginTransaction().replace(R.id.content_frame, FriendsPagerAdapterFragment).commit();
                }
                else if (getIntent().getStringExtra("Notifications").equals("NewFriendRequest"))
                {
                    bundle.putInt("Notification", 2);
                    FriendsPagerAdapterFragment.setArguments(bundle);
                    fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fm.beginTransaction().replace(R.id.content_frame, FriendsPagerAdapterFragment).commit();
                }
                else if (getIntent().getStringExtra("Notifications").equals("AcceptedFriendRequest"))
                {
                    bundle.putInt("Notification", 3);
                    FriendsPagerAdapterFragment.setArguments(bundle);
                    fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fm.beginTransaction().replace(R.id.content_frame, FriendsPagerAdapterFragment).commit();
                }

            }
            else if (findViewById(R.id.content_frame) != null) {


                if (savedInstanceState != null) {
                    return;
                }

                FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                tx.replace(R.id.content_frame, new ProfileFragment());
                tx.commit();
                navigationView.getMenu().getItem(0).setChecked(true);
            }
            ///TEST PURPOSE ONLY/////////
//            user = userLocalStore.getLoggedInUser();
//            user.setAccess_token("Testing");
//            userLocalStore.setUsersDetails(user);
//            user.getAccess_token();
            ////////////////////////
            //**************GCM NEW DATA************************//

            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    boolean sentToken = sharedPreferences
                            .getBoolean("sentTokenToServer", false);
                    if (sentToken) {
                        //mInformationTextView.setText(getString(R.string.gcm_send_message));
                        //Toast.makeText(MainActivity.this, getString(R.string.gcm_send_message), Toast.LENGTH_SHORT).show();
                    } else {
                        //mInformationTextView.setText(getString(R.string.token_error_message));
                        //Toast.makeText(MainActivity.this,getString(R.string.token_error_message), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            // Registering BroadcastReceiver
            registerReceiver();

            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }

            //**************GCM NEW DATA END************************//
        }
    }
    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter("registrationComplete"));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current radius
        savedInstanceState.putInt("backpackerSearchRadius", radiusBackpackerList);
        savedInstanceState.putInt("farmSearchRadius", radiusFarmList);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //Toast.makeText(MainActivity.this, "called menu item function", Toast.LENGTH_SHORT).show();

        switch (item.getItemId()) {
            case R.id.action_settings:
                //Toast.makeText(MainActivity.this, "called menu item settings", Toast.LENGTH_SHORT).show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //FragmentManager fm = getFragmentManager();
        FragmentManager fm = getSupportFragmentManager();
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        ReturnFromNestedFrag = true;

        if (id == R.id.profile) {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            fm.beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();

            //fragmentTransaction.remove(yourfragment).commit()
            // Handle the camera action
        } else if (id == R.id.find_farms) {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.content_frame, new FindFarmsPagerAdapterFragment(), "FarmsPagerAdapter").commit();
            // Handle the camera action
        } else if (id == R.id.find_backpackers) {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.content_frame, new FindBackpackersPagerAdapterFragment(), "BackpackersPagerAdapter").commit();
            // Handle the camera action
        } else if (id == R.id.find_cars) {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.content_frame, new FindCarsPagerAdapterFragment(), "CarsPagerAdapter").commit();
            // Handle the camera action
        } else if (id == R.id.friends) {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.content_frame, new FriendsPagerAdapterFragment(), "FriendsPagerAdapter").commit();
            // Handle the camera action
        } else if (id == R.id.manage_car) {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.content_frame, new ManageCarFragment()).commit();
            // Handle the camera action
        } else if (id == R.id.add_farms) {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.content_frame, new AddFarmFragment()).commit();
            // Handle the camera action
        }
//        else if (id == R.id.preferences) {
//            Toast.makeText(MainActivity.this, "Token Removed", Toast.LENGTH_SHORT).show(); //TEST PURPOSES
//            user = userLocalStore.getLoggedInUser();
//            user.setAccess_token("Testing");
//            userLocalStore.setUsersDetails(user);
//            user.getAccess_token();
//                // Handle the camera action
         else if (id == R.id.log_out) {
            userLocalStore.clearUserData();

            messagesDB.ClearDB();

            userLocalStore.setUserLoggedIn(false);
            //LoginManager.getInstance().logOut();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        ReturnFromNestedFrag = false;
        toggle.setDrawerIndicatorEnabled(true);
        return true;
    }

    private boolean authenticate() {
        if (userLocalStore.checkValidUser() == false) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            this.finish();
            return false;
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void LoadChatFragment(User a) {

        FragmentManager fm = getSupportFragmentManager();
        //User a = user;
        Bundle bundle = new Bundle();
        bundle.putString("profilephoto", a.getProfilePhoto());
        bundle.putString("workStatus", a.getWorkStatus());
        bundle.putString("nationality", a.getNationality());
        bundle.putString("username", a.getUserName());
        bundle.putString("userId", a.getUserId());
        Fragment ChatFragment = new ChatFragment();
        ChatFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.content_frame, ChatFragment,"ChatWindow").addToBackStack(null).commit();
    }

    @Override
    public boolean checkMessageListVisible() {
        FragmentManager fm = getSupportFragmentManager();
        FriendsPagerAdapterFragment FriendsPagerAdapter = (FriendsPagerAdapterFragment) fm.findFragmentByTag("FriendsPagerAdapter");
        if(FriendsPagerAdapter!=null) {
            return FriendsPagerAdapter.checkMessageListVisible();
        }
        else
        {
            return false;
        }
    }

    @Override
    public void setRefinedFarmList(boolean b) {
        refineFarmList = b;
    }

    @Override
    public void setRefinedBackpackerList(boolean b) {
        refineBackpackerList = b;
    }


    @Override
    public void SetupToolbar(int BackArrowNeeded, String title) {

        if (BackArrowNeeded == 1) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); //This is needed
            toggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setTitle(title);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true); //This is needed
            toggle.setDrawerIndicatorEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void setRefinedCarList(boolean b) {
        refineCarList = b;
    }

    @Override
    public void LoadAddCar() {
        FragmentManager fm = getSupportFragmentManager();
        client.connect();
        fm.beginTransaction().replace(R.id.content_frame, new AddCarAdvertFragment()).addToBackStack(null).commit();
    }

    @Override
    public void loadCarEditor() {

    }

    @Override
    public void LoadManageCar() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fm.beginTransaction().replace(R.id.content_frame, new ManageCarFragment()).commit();
        Toast.makeText(MainActivity.this, "Car Added Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadSettings() {
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction().replace(R.id.content_frame, new SettingsFragment()).addToBackStack(null).commit();
    }

    @Override
    public void LoadFarms() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fm.beginTransaction().replace(R.id.content_frame, new FindFarmsPagerAdapterFragment(), "FarmsPagerAdapter").commit();
        Toast.makeText(MainActivity.this, "Farm Added Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadAddFeedback(int farmId) {
        FragmentManager fm = getSupportFragmentManager();
        User a = user;
        Bundle bundle = new Bundle();
        bundle.putInt("farmId", farmId);
        Fragment AddFeedbackFragment = new FeedbackFragment();
        AddFeedbackFragment.setArguments(bundle);
        fm.beginTransaction().replace(R.id.content_frame, AddFeedbackFragment).addToBackStack(null).commit();

    }

    @Override
    public void loadSettings(int i) {
        FragmentManager fm = getSupportFragmentManager();

        if (i == 1) {
            fm.beginTransaction().replace(R.id.content_frame, new FindFarmOptionsFragment()).addToBackStack(null).commit();
        } else if (i == 2) {
            fm.beginTransaction().replace(R.id.content_frame, new FindBackpackerOptionsFragment()).addToBackStack(null).commit();
        } else if (i == 3) {
            fm.beginTransaction().replace(R.id.content_frame, new FindCarOptionsFragment()).addToBackStack(null).commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean getRefinedBackpackerList() {
        return refineBackpackerList;
    }

    @Override
    public boolean getRefinedFarmList() {
        return refineFarmList;
    }


    public void setRadiusBackpackerList(int radiusBackpackerList) {
        this.radiusBackpackerList = radiusBackpackerList;
    }

    public void setRadiusCarList(int radiusCarList) {
        this.radiusCarList = radiusCarList;
    }

    @Override
    public void setAdditionalCarSearchOptions(String AvailableFrom, String AvailableTo, String Location, String PriceFrom, String PriceTo) {
        mAvailableFrom=AvailableFrom;
        mAvailableTo=AvailableTo;
        mPriceFrom=PriceFrom;
        mPriceTo=PriceTo;
        mCityLocation = Location;
    }
//    @Override
//    public void getAdditionalCarSearchOptions() {
//        mAvailableFrom=AvailableFrom;
//        mAvailableTo=AvailableTo;
//        mPriceFrom=PriceFrom;
//        mPriceTo=PriceTo;
//        mCityLocation = Location;
//    }
    public void setRadiusFarmList(int radiusFarmList) {
        this.radiusFarmList = radiusFarmList;
        //this.getSupportFragmentManager().popBackStack();
    }
////////////////////////////////////////////////////////////////Finding Backpackers Functionality//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void retrieveBackpackerList(int radius) {
        client.connect();
        final FragmentManager fm = getSupportFragmentManager();
        if (radius != 0) {
            radiusBackpackerList = radius;

        } else {
            if (0 == radiusBackpackerList) {
                radiusBackpackerList = 100;
            }
        }


        user = userLocalStore.getLoggedInUser();

        RestAdapter.Builder adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + user.getAccess_token());
                    }
                });
        BackpackerAPI api = adapter.build().create(BackpackerAPI.class);

        api.getBackpackers(radiusBackpackerList, new Callback<ArrayList<User>>() {

            @Override
            public void success(ArrayList<User> users, Response response) {
                //showProgress(false);

                mListBackpackers = users;
                for (User userUpdate : mListBackpackers) {
                    userUpdate.setmPosition(new LatLng(userUpdate.getLat(), userUpdate.getAlong()));
                }
                if (mListBackpackers.isEmpty()) {
                    //Toast.makeText(MainActivity.this, "Your All Alone Sorry!!\n Please spread the word to build network of backpackers to help each other :)", Toast.LENGTH_SHORT).show();
                }

                FindBackpackersPagerAdapterFragment BackpackersPagerAdapter = (FindBackpackersPagerAdapterFragment) fm.findFragmentByTag("BackpackersPagerAdapter");
                BackpackersPagerAdapter.updateFromMain();


//                if (null != fm.findFragmentByTag("BackpackersPagerAdapter")) {
//                    FindBackpackersPagerAdapterFragment BackpackersPagerAdapter = (FindBackpackersPagerAdapterFragment) fm.findFragmentByTag("BackpackersPagerAdapter");//TEST PERPOSES
//                    BackpackersPagerAdapter.updateFromMain();//TEST PURPOSES RE-ADD
//                } else if (null != fm.findFragmentByTag("FarmsPagerAdapter")) {
//                    FindFarmsPagerAdapterFragment FarmsPagerAdapterFragment = (FindFarmsPagerAdapterFragment) fm.findFragmentByTag("FarmsPagerAdapter");//TEST PERPOSES
//                    FarmsPagerAdapterFragment.updateFromMain();
//                }

            }

            @Override
            public void failure(final RetrofitError error) {
                if (error.getResponse().getStatus() == 401) {
                    RefreshToken(1);
                } else {
                    FindBackpackersPagerAdapterFragment BackpackersPagerAdapter = (FindBackpackersPagerAdapterFragment) fm.findFragmentByTag("BackpackersPagerAdapter");
                    BackpackersPagerAdapter.updateFromMain();
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public ArrayList<User> getBackpackersList() {
        return mListBackpackers;
    }

    @Override
    public void setUpdatedBackpackerList(boolean updated) {
        updatedBackpackerSearch = updated;
    }

    @Override
    public void setBackpackerList(ArrayList<User> ListBackpackers) {
        mListBackpackers = ListBackpackers;
    }

    @Override
    public boolean getUpdatedBackpackerList() {
        return updatedBackpackerSearch;
    }

    @Override
    public void ViewBackpackerProfilebyMap(User user) {
        FragmentManager fm = getSupportFragmentManager();
        User a = user;
        Bundle bundle = new Bundle();
        bundle.putString("profilephoto", a.getProfilePhoto());
        bundle.putString("workStatus", a.getWorkStatus());
        bundle.putString("nationality", a.getNationality());
        bundle.putString("username", a.getUserName());
        bundle.putString("userId", a.getUserId());
        Fragment BackpackerViewFragment = new BackpackerViewFragment();
        BackpackerViewFragment.setArguments(bundle);
        fm.beginTransaction().replace(R.id.content_frame, BackpackerViewFragment).addToBackStack(null).commit();
        //toggle.setDrawerIndicatorEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void ViewBackpackerProfilebyList(int position) {
        User a = mListBackpackers.get(position);
        //Toast.makeText(MainActivity.this, "Loading " + a.getUserName() + " :)", Toast.LENGTH_SHORT).show();


        //FragmentManager fm = getFragmentManager();
        FragmentManager fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("profilephoto", a.getProfilePhoto());
        bundle.putString("workStatus", a.getWorkStatus());
        bundle.putString("nationality", a.getNationality());
        bundle.putString("username", a.getUserName());
        bundle.putString("userId", a.getUserId());
        Fragment BackpackerViewFragment = new BackpackerViewFragment();
        BackpackerViewFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.content_frame, BackpackerViewFragment).addToBackStack(null).commit();
        //toggle.setDrawerIndicatorEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        getSupportActionBar().setTitle("Profile");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setHomeButtonEnabled(true);
    }

////////////////////////////////////////////////////////////////Finding Backpackers Functionality End//////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////Finding Farms Functionality //////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void retrieveFarmList(int radius) {
        client.connect();
        final FragmentManager fm = getSupportFragmentManager();
        if (radius != 0) {
            radiusFarmList = radius;

        } else {
            if (0 == radiusFarmList) {
                radiusFarmList = 100;
            }
        }

//        double along = -8.585644;
//        double lat = 52.215601;


        user = userLocalStore.getLoggedInUser();
        //Toast.makeText(MainActivity.this, user.getAccess_token(), Toast.LENGTH_SHORT).show();
        RestAdapter.Builder adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + user.getAccess_token());
                    }
                });
        FarmAPI api = adapter.build().create(FarmAPI.class);

        api.getFarms(radiusFarmList, user.getAlong(), user.getLat(), new Callback<ArrayList<Farm>>() {

            @Override
            public void success(ArrayList<Farm> users, Response response) {
                //showProgress(false);

                mListFarms = users;
                for (Farm userUpdate : mListFarms) {
                    userUpdate.setmPosition(new LatLng(userUpdate.getLat(), userUpdate.getAlong()));
                }
                if (mListFarms.isEmpty()) {
                    //Toast.makeText(MainActivity.this, "Your All Alone Sorry!!\n Please spread the word to build network of backpackers to help each other :)", Toast.LENGTH_SHORT).show();
                }
                FindFarmsPagerAdapterFragment FarmsPagerAdapterFragment = (FindFarmsPagerAdapterFragment) fm.findFragmentByTag("FarmsPagerAdapter");
                FarmsPagerAdapterFragment.updateFromMain();

            }

            @Override
            public void failure(final RetrofitError error) {
                if(error.getResponse()!=null) {
                    if (error.getResponse().getStatus() == 401) {
                        RefreshToken(2);
                    } else {
                        FindFarmsPagerAdapterFragment FarmsPagerAdapterFragment = (FindFarmsPagerAdapterFragment) fm.findFragmentByTag("FarmsPagerAdapter");
                        FarmsPagerAdapterFragment.updateFromMain();
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{Toast.makeText(MainActivity.this, "Sorry an unknown error has occurred", Toast.LENGTH_SHORT).show();}
            }
        });
    }

    public void RefreshToken(final int retrofitCall) {

        RestAdapter.Builder adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + user.getAccess_token());
                    }
                });
        BackpackerAPI api = adapter.build().create(BackpackerAPI.class);

        //User NewRefreshedUser =
        api.RefreshToken("refresh_token", user.getRefresh_Token(), new Callback<User>() {


            @Override
            public void success(User NewRefreshedUser, Response response) {
                retrys = 0;
                retrysWith401 = 0;
                user.setAccess_token(NewRefreshedUser.getAccess_token());
                user.setRefresh_Token(NewRefreshedUser.getRefresh_Token());
                userLocalStore.setUsersDetails(user);
                if (retrofitCall == 1) {
                    retrieveBackpackerList(0);
                } else if (retrofitCall == 2) {
                    retrieveFarmList(0);
                } else if (retrofitCall == 3) {
                    retrieveCarList(0);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

                if (retrofitError.getResponse().getStatus() == 401) {
                    retrysWith401++;
                    if (retrysWith401 > 2) {
                        userLocalStore.clearUserData();
                        userLocalStore.setUserLoggedIn(false);
                        //LoginManager.getInstance().logOut();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(MainActivity.this, "Unfortunately you have been logged out due to an extended time of not using this App \n Please log back in.", Toast.LENGTH_LONG).show();
                    }
                }
                else if(retrys > 5) {
                    Toast.makeText(MainActivity.this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                    retrysWith401 = 0;
                    retrys = 0;
                }
                else {
                    retrys++;
                    RefreshToken(retrofitCall);

                }
            }
        });


    }

    @Override
    public ArrayList<Farm> getFarmsList() {
        return mListFarms;
    }

    @Override
    public void setFarmsList(ArrayList<Farm> ListFarms) {
        mListFarms = ListFarms;
    }

    @Override
    public void setUpdatedFarmList(boolean updated) {
        updatedFarmSearch = updated;
    }

    @Override
    public boolean checkReturnFrag() {
        return ReturnFromNestedFrag;
    }

    @Override
    public boolean isCalledFromNotification() {
        if(calledFromNotification)
        {
            calledFromNotification=false;
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void setCalledFromNotification() {
        calledFromNotification = true;
    }

    @Override
    public boolean getUpdatedFarmList() {
        return updatedFarmSearch;
    }

    @Override
    public void ViewFarmProfilebyList(int position) {
        Farm a = mListFarms.get(position);
        //Toast.makeText(MainActivity.this, "Loading " + a.getUserName() + " :)", Toast.LENGTH_SHORT).show();


        //FragmentManager fm = getFragmentManager();
        FragmentManager fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("name", a.getName());
        bundle.putString("address", a.getAddress());
        bundle.putInt("farmId", a.getFarmId());
        Fragment FarmViewFragment = new FarmViewFragment();
        FarmViewFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.content_frame, FarmViewFragment).addToBackStack(null).commit();
        //toggle.setDrawerIndicatorEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void ViewFarmProfilebyMap(Farm farm) {
        FragmentManager fm = getSupportFragmentManager();
        Farm a = farm;
        Bundle bundle = new Bundle();
        bundle.putString("name", a.getName());
        bundle.putString("address", a.getAddress());
        bundle.putInt("farmId", a.getFarmId());
        Fragment FarmViewFragment = new FarmViewFragment();
        FarmViewFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.content_frame, FarmViewFragment).addToBackStack(null).commit();
        //toggle.setDrawerIndicatorEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
////////////////////////////////////////////////////////////Finding Farms Functionality End///////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////Finding Cars Functionality //////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void retrieveCarList(int radius) {

        client.connect();
        final FragmentManager fm = getSupportFragmentManager();
        if (radius != 0) {
            radiusCarList = radius;

        } else {
            if (0 == radiusCarList) {
                radiusCarList = 100;
            }
        }

        //double along = -8.585644;
        //double lat = 52.215601;


        user = userLocalStore.getLoggedInUser();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss")
                .create();

        RestAdapter.Builder adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + user.getAccess_token());
                    }
                });
        CarAPI api = adapter.build().create(CarAPI.class);


        api.getCars(radiusCarList, user.getAlong(), user.getLat(),mAvailableFrom,mAvailableTo,mPriceFrom,mPriceTo,mCityLocation, new Callback<ArrayList<Car>>() {

            @Override
            public void success(ArrayList<Car> cars, Response response) {
                //showProgress(false);

                mListCars = cars;
                for (Car userUpdate : mListCars) {
                    userUpdate.setmPosition(new LatLng(userUpdate.getLat(), userUpdate.getaLong()));
                }
                if (mListCars.isEmpty()) {
                    //Toast.makeText(MainActivity.this, "Your All Alone Sorry!!\n Please spread the word to build network of backpackers to help each other :)", Toast.LENGTH_SHORT).show();
                }
                FindCarsPagerAdapterFragment CarsPagerAdapterFragment = (FindCarsPagerAdapterFragment) fm.findFragmentByTag("CarsPagerAdapter");
                CarsPagerAdapterFragment.updateFromMain();

            }

            @Override
            public void failure(final RetrofitError error) {
                if (error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        RefreshToken(3);
                    } else {
                        FindCarsPagerAdapterFragment CarsPagerAdapterFragment = (FindCarsPagerAdapterFragment) fm.findFragmentByTag("CarsPagerAdapter");
                        CarsPagerAdapterFragment.updateFromMain();
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            else{
                Toast.makeText(MainActivity.this, "Sorry an unknown error has occurred", Toast.LENGTH_SHORT).show();
            }
        }
        });
    }

    @Override
    public ArrayList<Car> getCarsList() {
        return mListCars;
    }

    @Override
    public void setCarsList(ArrayList<Car> ListCars) {
        mListCars = ListCars;
    }

    @Override
    public void setUpdatedCarList(boolean updated) {
        updatedCarSearch = updated;
    }

    @Override
    public boolean getUpdatedCarList() {
        return updatedCarSearch;
    }

    @Override
    public void ViewCarProfilebyList(int position) {
        Car a = mListCars.get(position);
        //Toast.makeText(MainActivity.this, "Loading " + a.getUserName() + " :)", Toast.LENGTH_SHORT).show();


        //FragmentManager fm = getFragmentManager();
        FragmentManager fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        //bundle.putString("UserName", a.getUserName());
        bundle.putString("ContactNumber", a.getContactNumber());
        bundle.putString("UserNameId", a.getUserNameId());
        bundle.putString("Description", a.getDescription());
        bundle.putString("Title", a.getTitle());
        bundle.putString("CityLocation", a.getCityLocation());
        bundle.putString("AvailableFrom", String.valueOf(a.getAvailableFrom()));
        bundle.putString("AvailableTo", String.valueOf(a.getAvailableTo()));
        bundle.putString("CarPhoto", a.getCarPhoto());
        bundle.putString("mPosition", String.valueOf(a.getmPosition()));
        bundle.putDouble("Price", a.getPrice());
        Fragment CarViewFragment = new CarViewFragment();
        CarViewFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.content_frame, CarViewFragment).addToBackStack(null).commit();
        //toggle.setDrawerIndicatorEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void ViewCarProfilebyMap(Car car) {
        Car a = car;
//        FragmentManager fm = getSupportFragmentManager();
//        User a = user;
//        Bundle bundle = new Bundle();
//        bundle.putString("profilephoto", a.getProfilePhoto());
//        bundle.putString("workStatus", a.getWorkStatus());
//        bundle.putString("nationality", a.getNationality());
//        bundle.putString("username", a.getUserName());
//        bundle.putString("userId", a.getUserId());
//        Fragment BackpackerViewFragment = new BackpackerViewFragment();
//        BackpackerViewFragment.setArguments(bundle);
//        fm.beginTransaction().replace(R.id.content_frame, BackpackerViewFragment).addToBackStack(null).commit();
//        //toggle.setDrawerIndicatorEnabled(false);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        toggle.setDrawerIndicatorEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //FragmentManager fm = getFragmentManager();
        FragmentManager fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        //bundle.putString("UserName", a.getUserName());
        bundle.putString("ContactNumber", a.getContactNumber());
        bundle.putString("UserNameId", a.getUserNameId());
        bundle.putString("Description", a.getDescription());
        bundle.putString("Title", a.getTitle());
        bundle.putString("CityLocation", a.getCityLocation());
        bundle.putString("AvailableFrom", String.valueOf(a.getAvailableFrom()));
        bundle.putString("AvailableTo", String.valueOf(a.getAvailableTo()));
        bundle.putString("CarPhoto", a.getCarPhoto());
        bundle.putString("mPosition", String.valueOf(a.getmPosition()));
        bundle.putDouble("Price", a.getPrice());
        Fragment CarViewFragment = new CarViewFragment();
        CarViewFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.content_frame, CarViewFragment).addToBackStack(null).commit();
        //toggle.setDrawerIndicatorEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
////////////////////////////////////////////////////////////Finding Cars Functionality End///////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////GCM SECTION /////////////////////////////////////////////////////////////////////

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        Location location = LocationServices.FusedLocationApi.getLastLocation(client);
//
//        if (location != null) {
//            client.disconnect();
//        }
        Location Currentlocation = LocationServices.FusedLocationApi.getLastLocation(
                client);
        if (Currentlocation != null) {

            user = userLocalStore.getLoggedInUser();

            user.setLat(Currentlocation.getLatitude());
            double along = Currentlocation.getLongitude();
            user.setAlong(Currentlocation.getLongitude());
            client.disconnect();


            RestAdapter.Builder adapter = new RestAdapter.Builder()
                    .setEndpoint(ENDPOINT)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addHeader("Authorization", "Bearer " + user.getAccess_token());
                        }
                    });

            BackpackerAPI api = adapter.build().create(BackpackerAPI.class);

            api.updateUser("", "", user.getNationality(), user.getWorkStatus(), user.getLat(),user.getAlong(), user.isPrivacy(), "", "", new Callback<User>() {

                @Override
                public void failure(final RetrofitError error) {

                    RetrofitErrors errors = (RetrofitErrors) error.getBodyAs(RetrofitErrors.class);
                    if (errors != null) {
                        String errorlist = errors.getModelStateString();
                        //Toast.makeText(getActivity(), errorlist, Toast.LENGTH_LONG).show();
                    } else {

                        String errorType = error.getKind().toString();
                        if (errorType.equals("NETWORK")) {
                            //Toast.makeText(getActivity(), "Sorry an error has occurred please check your internet connection and try again.", Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(getActivity(), "Sorry an unknown error has occurred please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void success(User userReg, Response response) {

                    userLocalStore.setUsersDetails(user);
                }
            });

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


/////////////////////////////////////////////////////END GCM SECTION /////////////////////////////////////////////////////////////////////
}
