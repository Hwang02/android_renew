package com.hotelnow.fragment.model;

public class TopItem {
    private String location, location_id, location_subid, ec_date, ee_date;

    public TopItem(String location, String location_id, String location_subid, String ec_date, String ee_date) {
        this.location = location;
        this.location_id = location_id;
        this.location_subid = location_subid;
        this.ec_date = ec_date;
        this.ee_date = ee_date;

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getLocation_subid() {
        return location_subid;
    }

    public void setLocation_subid(String location_subid) {
        this.location_subid = location_subid;
    }

    public String getEc_date() {
        return ec_date;
    }

    public void setEc_date(String ec_date) {
        this.ec_date = ec_date;
    }

    public String getEe_date() {
        return ee_date;
    }

    public void setEe_date(String ee_date) {
        this.ee_date = ee_date;
    }
}
