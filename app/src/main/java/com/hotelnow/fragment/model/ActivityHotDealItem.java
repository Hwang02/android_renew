package com.hotelnow.fragment.model;

public class ActivityHotDealItem {
    private String id, name, sale_price, sale_rate, latitude, longitude, benefit_text, img_url, location, category_code, category, review_score, grade_score, is_hot_deal, is_add_reserve;
    private boolean islike;

    public ActivityHotDealItem(String id, String name, String sale_price, String sale_rate, String latitude, String longitude, String benefit_text, String img_url, String location,
                               String category_code, String category, String review_score, String grade_score, String is_hot_deal, String is_add_reserve, boolean islike ) {
        this.id = id;
        this.name = name;
        this.sale_price = sale_price;
        this.sale_rate = sale_rate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.benefit_text = benefit_text;
        this.img_url = img_url;
        this.location = location;
        this.category_code = category_code;
        this.category = category;
        this.review_score = review_score;
        this.grade_score = grade_score;
        this.is_hot_deal = is_hot_deal;
        this.is_add_reserve = is_add_reserve;
        this.islike = islike;
    }

    public boolean isIslike() {
        return islike;
    }

    public void setIslike(boolean islike) {
        this.islike = islike;
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

    public String getCategory_code() {
        return category_code;
    }

    public void setCategory_code(String category_code) {
        this.category_code = category_code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
}
