package com.k00140908.darren.the88days;

import android.content.Context;
import android.content.SharedPreferences;

import com.k00140908.darren.the88days.Model.Car;
import com.k00140908.darren.the88days.Model.User;

//import java.sql.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by darre on 24/01/2016.
 */
public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("username", user.getUserName());
        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public User getLoggedInUser() {
        if (userLocalDatabase.getBoolean("loggedIn", false) == false) {
            return null;
        }

        String userName = userLocalDatabase.getString("userName", "");
        String userId = userLocalDatabase.getString("userId", "");
        String email = userLocalDatabase.getString("email", "");
        String nationality = userLocalDatabase.getString("nationality", "");
        String workStatus = userLocalDatabase.getString("workStatus", "");
        Double lat = ((double) userLocalDatabase.getFloat("lat", 0));
        Double along = ((double) userLocalDatabase.getFloat("along", 0));
        Boolean privacy = userLocalDatabase.getBoolean("privacy", false);
        String profilePhoto = userLocalDatabase.getString("profilePhoto", "");
        String access_token = userLocalDatabase.getString("access_token", "");
        String refreshTokenTicket = userLocalDatabase.getString("refreshTokenTicket", "");

        User user = new User(userId,userName, email, nationality, workStatus, privacy,lat,along,access_token, refreshTokenTicket,profilePhoto);

        return user;
    }
    public User newUser() {

        String userName = "";
        String email ="";
        String nationality ="";
        String workStatus ="";
        Double lat =0.0;
        Double along = 0.0;
        Boolean privacy =false;
        String access_token ="";
        String refreshTokenTicket = "";
        String profilePhoto = "";
        String userId="";

        User user = new User(userId,userName, email, nationality, workStatus, privacy,lat,along,access_token, refreshTokenTicket,profilePhoto);


        return user;
    }
    public void setUsersDetails(User user) {

        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("userName", user.getUserName());
        userLocalDatabaseEditor.putString("userId", user.getUserId());
        userLocalDatabaseEditor.putString("email", user.getEmail());
        userLocalDatabaseEditor.putString("nationality", user.getNationality());
        userLocalDatabaseEditor.putString("workStatus", user.getWorkStatus());
        userLocalDatabaseEditor.putFloat("lat", (float) user.getLat());
        userLocalDatabaseEditor.putFloat("along", (float) user.getAlong());
        userLocalDatabaseEditor.putBoolean("privacy", user.isPrivacy());
        userLocalDatabaseEditor.putString("profilePhoto", user.getProfilePhoto());
        userLocalDatabaseEditor.putString("access_token", user.getAccess_token());
        userLocalDatabaseEditor.putString("refreshTokenTicket", user.getRefresh_Token());
        userLocalDatabaseEditor.commit();



        }
    public void storeFacebookRegisterDetails(User user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("userName", user.getUserName());
        userLocalDatabaseEditor.putString("access_token", user.getAccess_token());
        //userLocalDatabaseEditor.putString("profilePicture", user.);
        userLocalDatabaseEditor.commit();
    }
    public User getFacebookRegisterDetails() {

        String username = userLocalDatabase.getString("userName", "");
        String token = userLocalDatabase.getString("access_token", "");
        String profilePicture = userLocalDatabase.getString("profilePicture", "");

        User user = new User(username, token, profilePicture);
        return user;
    }
    public boolean checkValidUser() {
        if (userLocalDatabase.getBoolean("loggedIn", false) == false) {
            return false;
        }

        String email = userLocalDatabase.getString("email", "");
        if (email==null){
            return false;
        }

        return true;
    }

    public Car getUsersCar() {
        String date ="01/01/2016";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date selectedDate = null;

        int carId = userLocalDatabase.getInt("carId", 0);
        String title = userLocalDatabase.getString("title", "");
        String description = userLocalDatabase.getString("description", "");
        String carPhoto = userLocalDatabase.getString("carPhoto", "");
        Date availableFrom = null;
        try {
            availableFrom = sdf.parse(userLocalDatabase.getString("availableFrom", date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date availableTo = null;
        try {
            availableTo = sdf.parse(userLocalDatabase.getString("availableTo", date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Double price = ((double) userLocalDatabase.getFloat("price", 0));
        Double lat = ((double) userLocalDatabase.getFloat("lat", 0));
        Double along = ((double) userLocalDatabase.getFloat("along", 0));
        String userNameId = userLocalDatabase.getString("userNameId", "");
        String contactNumber = userLocalDatabase.getString("contactNumber", "");


        Car car = new Car(carId, title, description, along, lat, price, carPhoto,availableFrom, availableTo, userNameId,contactNumber,0.0);
        return car;
    }
    public Car newCar() {
        String date ="01/01/2016";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date selectedDate = null;
        try {
            selectedDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Car car = new Car(0, "","", 0.0,0.0,0.0, "",selectedDate,selectedDate, "","",0.0);

        return car;
    }
    public void setUsersCarDetails(Car car) {

        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        //userLocalDatabaseEditor.putInt("carId", car.getCarId());
        userLocalDatabaseEditor.putString("title", car.getTitle());
        userLocalDatabaseEditor.putString("description", car.getDescription());
        userLocalDatabaseEditor.putString("carPhoto", car.getCarPhoto());
        userLocalDatabaseEditor.putString("availableFrom", car.getAvailableFrom().toString());
        userLocalDatabaseEditor.putString("availableTo",car.getAvailableTo().toString());
        userLocalDatabaseEditor.putFloat("price", car.getPrice().floatValue());
        userLocalDatabaseEditor.putFloat("along", car.getaLong().floatValue());
        userLocalDatabaseEditor.putFloat("lat", car.getLat().floatValue());
        userLocalDatabaseEditor.putString("userNameId", car.getUserNameId());
        userLocalDatabaseEditor.putString("contactNumber", car.getContactNumber());
        userLocalDatabaseEditor.commit();
    }



    }
