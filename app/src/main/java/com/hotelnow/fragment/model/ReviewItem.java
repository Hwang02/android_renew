package com.hotelnow.fragment.model;

public class ReviewItem {

    private String hotel_name, masked_name, total_rating, view_yn, comment, owner_comment, room_name, stay_cnt,  created_at, updated_at;

    public ReviewItem(String hotel_name, String masked_name, String total_rating, String view_yn, String comment, String owner_comment, String room_name, String stay_cnt, String created_at, String updated_at) {
        super();
        this.hotel_name = hotel_name;
        this.masked_name = masked_name;
        this.total_rating = total_rating;
        this.view_yn = view_yn;
        this.comment = comment;
        this.owner_comment = owner_comment;
        this.room_name = room_name;
        this.stay_cnt = stay_cnt;
        this.created_at = created_at;
        this.updated_at = updated_at;

    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public void setHotel_name(String hotel_name) {
        this.hotel_name = hotel_name;
    }

    public String getMasked_name() {
        return masked_name;
    }

    public void setMasked_name(String masked_name) {
        this.masked_name = masked_name;
    }

    public String getTotal_rating() {
        return total_rating;
    }

    public void setTotal_rating(String total_rating) {
        this.total_rating = total_rating;
    }

    public String getView_yn() {
        return view_yn;
    }

    public void setView_yn(String view_yn) {
        this.view_yn = view_yn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOwner_comment() {
        return owner_comment;
    }

    public void setOwner_comment(String owner_comment) {
        this.owner_comment = owner_comment;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getStay_cnt() {
        return stay_cnt;
    }

    public void setStay_cnt(String stay_cnt) {
        this.stay_cnt = stay_cnt;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
