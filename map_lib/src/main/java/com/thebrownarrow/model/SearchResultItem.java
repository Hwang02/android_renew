package com.thebrownarrow.model;

import java.io.Serializable;

public class SearchResultItem implements Serializable {

    private String id, hotel_id, name, address, category, street1, street2, privateDealYN, landscape, sale_price,
    normal_price, sale_rate, special_msg, review_score, grade_score, real_grade_score, distance, distance_real, normal_price_avg,
            city, is_private_deal, is_hot_deal, is_add_reserve;
    private int items_quantity, coupon_count;
    private double latitude, longuitude;
    private boolean isfocus = false;
    private long gapDay;

    public SearchResultItem(String id, String hotel_id, String name, String address, String category, String street1, String street2, double latitude, double longuitude, String privateDealYN, String landscape, String sale_price,
                            String normal_price, String sale_rate, int items_quantity, String special_msg, String review_score, String grade_score, String real_grade_score, String distance, String distance_real, String normal_price_avg,
                            String city, String is_private_deal, String is_hot_deal, String is_add_reserve, int coupon_count, boolean isfocus, long gapDay)
    {
        this.id = id;
        this.hotel_id =  hotel_id;
        this.name = name;
        this.address = address;
        this.category = category;
        this.street1 = street1;
        this.street2 = street2;
        this.latitude = latitude;
        this.longuitude = longuitude;
        this.privateDealYN = privateDealYN;
        this.landscape = landscape;
        this.sale_price = sale_price;
        this.normal_price = normal_price;
        this.sale_rate = sale_rate;
        this.items_quantity = items_quantity;
        this.special_msg = special_msg;
        this.review_score = review_score;
        this.grade_score = grade_score;
        this.real_grade_score = real_grade_score;
        this.distance = distance;
        this.distance_real = distance_real;
        this.normal_price_avg = normal_price_avg;
        this.city = city;
        this.is_private_deal = is_private_deal;
        this.is_hot_deal = is_hot_deal;
        this.is_add_reserve = is_add_reserve;
        this.coupon_count = coupon_count;
        this.isfocus = isfocus;
        this.gapDay = gapDay;
    }

    public long getGapDay() {
        return gapDay;
    }

    public void setGapDay(long gapDay) {
        this.gapDay = gapDay;
    }

    public boolean isIsfocus() {
        return isfocus;
    }

    public void setIsfocus(boolean isfocus) {
        this.isfocus = isfocus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLonguitude() {
        return longuitude;
    }

    public void setLonguitude(double longuitude) {
        this.longuitude = longuitude;
    }

    public String getPrivateDealYN() {
        return privateDealYN;
    }

    public void setPrivateDealYN(String privateDealYN) {
        this.privateDealYN = privateDealYN;
    }

    public String getLandscape() {
        return landscape;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public String getNormal_price() {
        return normal_price;
    }

    public void setNormal_price(String normal_price) {
        this.normal_price = normal_price;
    }

    public String getSale_rate() {
        return sale_rate;
    }

    public void setSale_rate(String sale_rate) {
        this.sale_rate = sale_rate;
    }

    public int getItems_quantity() {
        return items_quantity;
    }

    public void setItems_quantity(int items_quantity) {
        this.items_quantity = items_quantity;
    }

    public String getSpecial_msg() {
        return special_msg;
    }

    public void setSpecial_msg(String special_msg) {
        this.special_msg = special_msg;
    }

    public String getReview_score() {
        return review_score;
    }

    public void setReview_score(String review_score) {
        this.review_score = review_score;
    }

    public String getGrade_score() {
        return grade_score;
    }

    public void setGrade_score(String grade_score) {
        this.grade_score = grade_score;
    }

    public String getReal_grade_score() {
        return real_grade_score;
    }

    public void setReal_grade_score(String real_grade_score) {
        this.real_grade_score = real_grade_score;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance_real() {
        return distance_real;
    }

    public void setDistance_real(String distance_real) {
        this.distance_real = distance_real;
    }

    public String getNormal_price_avg() {
        return normal_price_avg;
    }

    public void setNormal_price_avg(String normal_price_avg) {
        this.normal_price_avg = normal_price_avg;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIs_private_deal() {
        return is_private_deal;
    }

    public void setIs_private_deal(String is_private_deal) {
        this.is_private_deal = is_private_deal;
    }

    public String getIs_hot_deal() {
        return is_hot_deal;
    }

    public void setIs_hot_deal(String is_hot_deal) {
        this.is_hot_deal = is_hot_deal;
    }

    public String getIs_add_reserve() {
        return is_add_reserve;
    }

    public void setIs_add_reserve(String is_add_reserve) {
        this.is_add_reserve = is_add_reserve;
    }

    public int getCoupon_count() {
        return coupon_count;
    }

    public void setCoupon_count(int coupon_count) {
        this.coupon_count = coupon_count;
    }
}
