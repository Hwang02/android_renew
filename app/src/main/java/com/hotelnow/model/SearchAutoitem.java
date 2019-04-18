package com.hotelnow.model;

public class SearchAutoitem {

    private String flag;
    private String id;
    private String name;
    private String city;
    private String sub_city;
    private String name_sub_city;

    public SearchAutoitem(String flag, String id, String name, String city, String sub_city, String name_sub_city) {
        this.flag = flag;
        this.id = id;
        this.name = name;
        this.city = city;
        this.sub_city = sub_city;
        this.name_sub_city = name_sub_city;
    }

    public String getName_sub_city() {
        return name_sub_city;
    }

    public void setName_sub_city(String name_sub_city) {
        this.name_sub_city = name_sub_city;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSub_city() {
        return sub_city;
    }

    public void setSub_city(String sub_city) {
        this.sub_city = sub_city;
    }
}
