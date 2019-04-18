package com.hotelnow.model;

public class StayHotDealItem {

    private String id, name, category_code, category, landscape, special_msg, review_score, grade_score, normal_price, sale_price, sale_rate, items_quantity, is_private_deal, is_hot_deal, is_add_reserve;
    private int coupon_count;

    public StayHotDealItem(String id, String name, String category_code, String category, String landscape, String special_msg, String review_score, String grade_score, String sale_price, String normal_price,
                           String sale_rate, String items_quantity, String is_private_deal, String is_hot_deal, String is_add_reserve, int coupon_count) {
        this.id = id;
        this.name = name;
        this.category_code = category_code;
        this.category = category;
        this.landscape = landscape;
        this.special_msg = special_msg;
        this.review_score = review_score;
        this.grade_score = grade_score;
        this.sale_price = sale_price;
        this.normal_price = normal_price;
        this.sale_rate = sale_rate;
        this.items_quantity = items_quantity;
        this.is_private_deal = is_private_deal;
        this.is_hot_deal = is_hot_deal;
        this.is_add_reserve = is_add_reserve;
        this.coupon_count = coupon_count;
    }

    public int getCoupon_count() {
        return coupon_count;
    }

    public void setCoupon_count(int coupon_count) {
        this.coupon_count = coupon_count;
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

    public String getLandscape() {
        return landscape;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
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

    public String getItems_quantity() {
        return items_quantity;
    }

    public void setItems_quantity(String items_quantity) {
        this.items_quantity = items_quantity;
    }
}
