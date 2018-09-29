package com.hotelnow.fragment.model;

public class StayHotDealItem {

    private String hotel_id, name, category, landscape, street1, latitude, longuitude, privateDealYN, sale_price, sale_rate, items_quantity, special_msg, grade_score;

    public StayHotDealItem(String hotel_id, String name, String category, String landscape, String street1, String latitude, String longuitude, String privateDealYN, String sale_price, String sale_rate, String items_quantity, String special_msg, String grade_score) {
        this.hotel_id = hotel_id;
        this.name = name;
        this.category = category;
        this.landscape = landscape;
        this.street1 = street1;
        this.latitude = latitude;
        this.longuitude = longuitude;
        this.privateDealYN = privateDealYN;
        this.sale_price = sale_price;
        this.sale_rate = sale_rate;
        this.items_quantity = items_quantity;
        this.special_msg = special_msg;
        this.grade_score = grade_score;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getLandscape() {
        return landscape;
    }

    public String getStreet1() {
        return street1;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLonguitude() {
        return longuitude;
    }

    public String getPrivateDealYN() {
        return privateDealYN;
    }

    public String getSale_price() {
        return sale_price;
    }

    public String getSale_rate() {
        return sale_rate;
    }

    public String getItems_quantity() {
        return items_quantity;
    }

    public String getSpecial_msg() {
        return special_msg;
    }

    public String getGrade_score() {
        return grade_score;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLonguitude(String longuitude) {
        this.longuitude = longuitude;
    }

    public void setPrivateDealYN(String privateDealYN) {
        this.privateDealYN = privateDealYN;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public void setSale_rate(String sale_rate) {
        this.sale_rate = sale_rate;
    }

    public void setItems_quantity(String items_quantity) {
        this.items_quantity = items_quantity;
    }

    public void setSpecial_msg(String special_msg) {
        this.special_msg = special_msg;
    }

    public void setGrade_score(String grade_score) {
        this.grade_score = grade_score;
    }
}
