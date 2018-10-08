package com.thebrownarrow.model;

/**
 * Created by iblinfotech on 04/03/17.
 */

public class MyLocation {
    private double latitude;
    private double longitude;
    private boolean isfocus = false;

    public MyLocation(double latitude, double longitude, boolean isfocus) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.isfocus = isfocus;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isIsfocus() {
        return isfocus;
    }

    public void setIsfocus(boolean isfocus) {
        this.isfocus = isfocus;
    }
}
