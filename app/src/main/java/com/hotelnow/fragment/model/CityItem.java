package com.hotelnow.fragment.model;

public class CityItem {

    private String city_ko;
    private String city_code;

    public CityItem(String city_ko, String city_code){
        this.city_ko = city_ko;
        this.city_code = city_code;
    }

    public String getCity_ko() {
        return city_ko;
    }

    public void setCity_ko(String city_ko) {
        this.city_ko = city_ko;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }
}
