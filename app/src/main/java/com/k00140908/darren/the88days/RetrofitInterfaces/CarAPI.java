package com.k00140908.darren.the88days.RetrofitInterfaces;

import com.k00140908.darren.the88days.Model.Car;
import com.k00140908.darren.the88days.Model.Farm;
import com.k00140908.darren.the88days.Model.User;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by darre on 13/02/2016.
 */
public interface CarAPI {

    @FormUrlEncoded
    @POST("/api/Account/RegisterExternalToken")
    public void externalRegister(@Field("Provider") String Provider, @Field("Token") String Token, Callback<User> callback);

    @FormUrlEncoded
    @POST("/api/Account/AndroidUpdateRegister")
    public void updateUser(@Field("UserName") String UserName ,@Field("Email") String Email,@Field("Nationality") String Nationality, @Field("WorkStatus") String WorkStatus,@Field("Lat") double Lat,@Field("Long") double Long,@Field("Privacy") boolean Privacy,
                           @Field("ProfilePicture") String ProfilePicture,Callback <User> callback);

    @GET("/api/Cars/car")
    public void getCars(@Query("radius") int radius,@Query("aLong") double aLong, @Query("Lat") double lat,@Query("availableFrom") String mAvailableFrom,
                        @Query("availableTo") String mAvailableTo,@Query("priceFrom") String mPriceFrom,@Query("priceTo") String mPriceTo,
                        @Query("location") String location,Callback <ArrayList<Car>> callback);

    @GET("/api/Cars/getMyCar")
    public void getMyCar(Callback <Car> callback);


    @FormUrlEncoded
    @POST("/api/Cars/")
    public void uploadCar(@Field("Title") String Title ,@Field("AvailableFrom") String AvailableFrom,@Field("AvailableTo") String AvailableTo,
                           @Field("Description") String Description,@Field("Long") double along, @Field("Lat") double lat,@Field("Price") double Price,@Field("ContactNumber") String ContactNumber, @Field("CityLocation") String CityLocation, Callback <User> callback);

    @Multipart
    @POST("/api/ManageImages/UploadCar")
    void upload(@Part("image") TypedFile file,
                Callback<Object> cb);
}