package com.hotelnow.fragment.model;

/**
 * Created by susia on 16. 1. 12..
 */
public class TicketEntry {
    private String mDeal_id;
    private String mName;
    private String mNormal_price;
    private String mSale_price;
    private String mImg_url;
    private String mLocation;
    private String mCategory;
    private String mSale_rate;
    private String mBenefit_text;
    private int mCoupon_count;

    public TicketEntry(String deal_id, String name, String normal_price, String sale_price, String img_url, String location, String category, String sale_rate, String benefit_text, int coupon_count) {
        super();
        mDeal_id = deal_id;
        mName = name;
        mNormal_price = normal_price;
        mSale_price = sale_price;
        mImg_url = img_url;
        mLocation = location;
        mCategory = category;
        mSale_rate = sale_rate;
        mBenefit_text = benefit_text;
        mCoupon_count = coupon_count;
    }

    public int getmCoupon_count() {
        return mCoupon_count;
    }

    public String getmDeal_id() {
        return mDeal_id;
    }

    public String getmName() {
        return mName;
    }

    public String getmNormal_price() {
        return mNormal_price;
    }

    public String getmSale_price() {
        return mSale_price;
    }

    public String getmImg_url() {
        return mImg_url;
    }

    public String getmLocation() {
        return mLocation;
    }

    public String getmCategory() {
        return mCategory;
    }

    public String getmSale_rate() {
        return mSale_rate;
    }

    public String getmBenefit_text() {
        return mBenefit_text;
    }
}
