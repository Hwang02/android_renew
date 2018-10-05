package com.hotelnow.fragment.model;

public class SubCityItem {

    private String subcity_code;
    private String subcity_ko;
    private String city_code;

    public SubCityItem(String city_code, String subcity_ko, String subcity_code){
        this.city_code = city_code;
        this.subcity_ko = subcity_ko;
        this.subcity_code = subcity_code;
    }

    public String getSubcity_code() {
        return subcity_code;
    }

    public void setSubcity_code(String subcity_code) {
        this.subcity_code = subcity_code;
    }

    public String getSubcity_ko() {
        return subcity_ko;
    }

    public void setSubcity_ko(String subcity_ko) {
        this.subcity_ko = subcity_ko;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }
}
