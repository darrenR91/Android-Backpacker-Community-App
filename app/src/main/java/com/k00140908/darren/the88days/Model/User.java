package com.k00140908.darren.the88days.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by darre on 24/01/2016.
 */
public class User implements ClusterItem {

    String userId;
    String userName;
    String email;
    String access_token;
    String refresh_token;
    String profilePhoto;
    String nationality;
    String workStatus;
    double lat,along;
    boolean privacy;
    Double distance;
    LatLng mPosition = null;

    //Required for chat
    int count;
    long lastMsg;

    public User()
    {
        //default
    }
    public User(String username,String token ,String refreshToken ) {

        this.userName = username;
        this.access_token = token;
        this.refresh_token = refreshToken;
    }
    public User(String userId, String userName, String email,String nationality, String workStatus, boolean privacy, double lat, double aLong,String access_token, String refreshTokenTicket,String profilePhoto) {
        this.userName = userName;
        this.email = email;
        this.access_token = access_token;
        this.refresh_token = refreshTokenTicket;
        this.nationality = nationality;
        this.workStatus = workStatus;
        this.lat = lat;
        this.along = aLong;
        this.privacy = privacy;
        this.mPosition= new LatLng(lat,aLong);
        this.profilePhoto = profilePhoto;
        this.userId = userId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_Token() {
        return refresh_token;
    }

    public void setRefresh_Token(String refreshTokenTicket) {
        this.refresh_token = refreshTokenTicket;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getAlong() {
        return along;
    }

    public void setAlong(double along) {
        this.along = along;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public void setmPosition(LatLng mPosition) {
        this.mPosition = mPosition;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(long lastMsg) {
        this.lastMsg = lastMsg;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
