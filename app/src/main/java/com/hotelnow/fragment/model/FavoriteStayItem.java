package com.hotelnow.fragment.model;

public class FavoriteStayItem {

    private String id, name, category, street1, street2, landscape, sale_price,
            sale_rate, special_msg, grade_score, real_grade_score,
            is_private_deal, is_hot_deal, is_add_reserve;
    private int items_quantity, coupon_count;

    public FavoriteStayItem(String id, String name, String category, String street1, String street2, String landscape, String sale_price,
                            String sale_rate, int items_quantity, String special_msg, String grade_score, String real_grade_score,
                            String is_private_deal, String is_hot_deal, String is_add_reserve, int coupon_count)
    {
        this.id = id;
        this.name = name;
        this.category = category;
        this.street1 = street1;
        this.street2 = street2;
        this.landscape = landscape;
        this.sale_price = sale_price;
        this.sale_rate = sale_rate;
        this.items_quantity = items_quantity;
        this.special_msg = special_msg;
        this.grade_score = grade_score;
        this.real_grade_score = real_grade_score;
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
}
