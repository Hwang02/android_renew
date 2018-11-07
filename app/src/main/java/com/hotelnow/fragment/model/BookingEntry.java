package com.hotelnow.fragment.model;

/**
 * Created by susia on 15. 12. 21..
 */
public class BookingEntry {
    private String mBid;
    private String mStatus;
    private String mHotelName;
    private String mRoomName;
    private String mCheckinDate;
    private String mCheckoutDate;
    private int mReviewCnt;
    private String mRoomId;
    private String mHotelId;
    private String isRwritable;
    private String status_display;
    private String review_writable_words_1;
    private String review_writable_words_2;
    private String room_img;
    private String status_detail;

    public BookingEntry(String id, String status, String hotel_name, String room_name, String room_img, String checkin_date, String checkout_date, String rid, String hid,
                        int myreview_cnt, String review_writable, String status_display, String review_writable_words_1, String review_writable_words_2, String status_detail) {
        super();
        mBid = id;
        mStatus = status;
        mHotelName = hotel_name;
        mRoomName = room_name;
        mCheckinDate = checkin_date;
        mCheckoutDate = checkout_date;
        mReviewCnt = myreview_cnt;
        mRoomId = rid;
        mHotelId = hid;
        isRwritable = review_writable;
        this.status_display = status_display;
        this.review_writable_words_1 = review_writable_words_1;
        this.review_writable_words_2 = review_writable_words_2;
        this.room_img = room_img;
        this.status_detail = status_detail;
    }

    public String getStatus_detail() {
        return status_detail;
    }

    public String getRoom_img() {
        return room_img;
    }

    public String getReview_writable_words_1() {
        return review_writable_words_1;
    }

    public String getReview_writable_words_2() {
        return review_writable_words_2;
    }

    public String getStatus_display() {
        return status_display;
    }

    public String getIsRwritable() {
        return isRwritable;
    }

    public String getmHotelId() {
        return mHotelId;
    }

    public String getmRoomId() {
        return mRoomId;
    }

    public int getmReviewCnt() {
        return mReviewCnt;
    }

    public String getId() {
        return mBid;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getHotelName() {
        return mHotelName;
    }

    public String getRoomName() {
        return mRoomName;
    }

    public String getCheckinDate() {
        return mCheckinDate;
    }

    public String getCheckoutDate() {
        return mCheckoutDate;
    }
}
