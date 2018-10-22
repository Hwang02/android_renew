package com.hotelnow.fragment.model;

/**
 * Created by susia on 15. 12. 10..
 */
public class CouponEntry {
    private String mProduct;
    private String mTitle;
    private String mIsValid;
    private String mName;
    private String mDiscount_value;
    private String mExpiration_date;
    private String[] mOption;
    private String mMin_price;
    private String mTarget_text;
    private String[] mTarget_lists;
    private String mExpire_text;


    public CouponEntry(String product, String title, String isValid, String name, String discount_value,
                       String expiration_date, String[] option, String min_price, String target_text, String[] target_lists, String expire_text) {
        super();
        mProduct = product;
        mTitle = title;
        mIsValid = isValid;
        mName = name;
        mDiscount_value = discount_value;
        mExpiration_date = expiration_date;
        mOption = option;
        mMin_price = min_price;
        mTarget_text = target_text;
        mTarget_lists = target_lists;
        mExpire_text = expire_text;

    }

    public String getmExpire_text() {
        return mExpire_text;
    }

    public String getmProduct() {
        return mProduct;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmIsValid() {
        return mIsValid;
    }

    public String getmName() {
        return mName;
    }

    public String getmDiscount_value() {
        return mDiscount_value;
    }

    public String getmExpiration_date() {
        return mExpiration_date;
    }

    public String[] getmOption() {
        return mOption;
    }

    public String getmMin_price() {
        return mMin_price;
    }

    public String getmTarget_text() {
        return mTarget_text;
    }

    public String[] getmTarget_lists() {
        return mTarget_lists;
    }
}