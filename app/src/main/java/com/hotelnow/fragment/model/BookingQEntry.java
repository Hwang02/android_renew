package com.hotelnow.fragment.model;

/**
 * Created by susia on 15. 12. 21..
 */
public class BookingQEntry {
    private String id;
    private String status;
    private String deal_name;
    private String created_at_format;
    private String img_url;
    private String is_review_writable;
    private int total_ticket_count;
    private String not_used_ticket_count;
    private String used_ticket_count;
    private String cancel_ticket_count;
    private String status_display;
    private String review_writable_words_1;
    private String review_writable_words_2;
    private String status_detail;
    private String deal_id;

    public BookingQEntry(String id, String status, String deal_name, String created_at_format, String img_url, String is_review_writable,
                         int total_ticket_count, String not_used_ticket_count, String used_ticket_count, String cancel_ticket_count, String status_display,
                         String review_writable_words_1, String review_writable_words_2, String status_detail, String deal_id) {
        super();
        this.id = id;
        this.status = status;
        this.deal_name = deal_name;
        this.created_at_format = created_at_format;
        this.img_url = img_url;
        this.is_review_writable = is_review_writable;
        this.total_ticket_count = total_ticket_count;
        this.not_used_ticket_count = not_used_ticket_count;
        this.used_ticket_count = used_ticket_count;
        this.cancel_ticket_count = cancel_ticket_count;
        this.status_display = status_display;
        this.review_writable_words_1 = review_writable_words_1;
        this.review_writable_words_2 = review_writable_words_2;
        this.status_detail = status_detail;
        this.deal_id = deal_id;

    }


    public void setStatus_detail(String status_detail) {
        this.status_detail = status_detail;
    }

    public String getDeal_id() {
        return deal_id;
    }

    public void setDeal_id(String deal_id) {
        this.deal_id = deal_id;
    }

    public String getStatus_detail() {
        return status_detail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeal_name() {
        return deal_name;
    }

    public void setDeal_name(String deal_name) {
        this.deal_name = deal_name;
    }

    public String getCreated_at_format() {
        return created_at_format;
    }

    public void setCreated_at_format(String created_at_format) {
        this.created_at_format = created_at_format;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getIs_review_writable() {
        return is_review_writable;
    }

    public void setIs_review_writable(String is_review_writable) {
        this.is_review_writable = is_review_writable;
    }

    public int getTotal_ticket_count() {
        return total_ticket_count;
    }

    public void setTotal_ticket_count(int total_ticket_count) {
        this.total_ticket_count = total_ticket_count;
    }

    public String getNot_used_ticket_count() {
        return not_used_ticket_count;
    }

    public void setNot_used_ticket_count(String not_used_ticket_count) {
        this.not_used_ticket_count = not_used_ticket_count;
    }

    public String getUsed_ticket_count() {
        return used_ticket_count;
    }

    public void setUsed_ticket_count(String used_ticket_count) {
        this.used_ticket_count = used_ticket_count;
    }

    public String getCancel_ticket_count() {
        return cancel_ticket_count;
    }

    public void setCancel_ticket_count(String cancel_ticket_count) {
        this.cancel_ticket_count = cancel_ticket_count;
    }

    public String getStatus_display() {
        return status_display;
    }

    public void setStatus_display(String status_display) {
        this.status_display = status_display;
    }

    public String getReview_writable_words_1() {
        return review_writable_words_1;
    }

    public void setReview_writable_words_1(String review_writable_words_1) {
        this.review_writable_words_1 = review_writable_words_1;
    }

    public String getReview_writable_words_2() {
        return review_writable_words_2;
    }

    public void setReview_writable_words_2(String review_writable_words_2) {
        this.review_writable_words_2 = review_writable_words_2;
    }
}
