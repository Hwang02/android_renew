package com.hotelnow.fragment.model;

public class KeyWordProductItem {

    private String id, order, category, image, keyword, type, evt_type, event_id, link, bannerable_id, hotel_id, hotel_name, deal_id, deal_name, grade_score;

    public KeyWordProductItem(String id, String order, String category, String image, String keyword, String type, String evt_type, String event_id, String link, String bannerable_id, String hotel_id, String hotel_name, String deal_id, String deal_name, String grade_score) {
        this.id = id;
        this.order = order;
        this.category = category;
        this.image = image;
        this.keyword = keyword;
        this.type = type;
        this.evt_type = evt_type;
        this.event_id = event_id;
        this.link = link;
        this.bannerable_id = bannerable_id;
        this.hotel_id = hotel_id;
        this.hotel_name = hotel_name;
        this.deal_id = deal_id;
        this.deal_name = deal_name;
        this.grade_score = grade_score;
    }

    public String getBannerable_id() {
        return bannerable_id;
    }

    public void setBannerable_id(String bannerable_id) {
        this.bannerable_id = bannerable_id;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public void setHotel_name(String hotel_name) {
        this.hotel_name = hotel_name;
    }

    public String getDeal_id() {
        return deal_id;
    }

    public void setDeal_id(String deal_id) {
        this.deal_id = deal_id;
    }

    public String getDeal_name() {
        return deal_name;
    }

    public void setDeal_name(String deal_name) {
        this.deal_name = deal_name;
    }

    public String getGrade_score() {
        return grade_score;
    }

    public void setGrade_score(String grade_score) {
        this.grade_score = grade_score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvt_type() {
        return evt_type;
    }

    public void setEvt_type(String evt_type) {
        this.evt_type = evt_type;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }
}
