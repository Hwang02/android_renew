package com.hotelnow.fragment.model;

public class ActivityHotDealItem {
    private String deal_id, name, normal_price, sale_price, sale_rate, latitude, longitude, benefit_text, img_url, location, category, distance_real, coupon_count;

    public ActivityHotDealItem(String deal_id, String name, String normal_price, String sale_price, String sale_rate, String latitude, String longitude, String benefit_text, String img_url, String location, String category, String distance_real, String coupon_count) {
        this.deal_id = deal_id;
        this.name = name;
        this.normal_price = normal_price;
        this.sale_price = sale_price;
        this.sale_rate = sale_rate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.benefit_text = benefit_text;
        this.img_url = img_url;
        this.location = location;
        this.category = category;
        this.distance_real = distance_real;
        this.coupon_count = coupon_count;
    }

    public String getDeal_id() {
        return deal_id;
    }

    public void setDeal_id(String deal_id) {
        this.deal_id = deal_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNormal_price() {
        return normal_price;
    }

    public void setNormal_price(String normal_price) {
        this.normal_price = normal_price;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public String getSale_rate() {
        return sale_rate;
    }

    public void setSale_rate(String sale_rate) {
        this.sale_rate = sale_rate;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getBenefit_text() {
        return benefit_text;
    }

    public void setBenefit_text(String benefit_text) {
        this.benefit_text = benefit_text;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDistance_real() {
        return distance_real;
    }

    public void setDistance_real(String distance_real) {
        this.distance_real = distance_real;
    }

    public String getCoupon_count() {
        return coupon_count;
    }

    public void setCoupon_count(String coupon_count) {
        this.coupon_count = coupon_count;
    }
}
