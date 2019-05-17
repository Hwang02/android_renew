package com.hotelnow.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by idhwang on 2018. 4. 13..
 */

public class TicketSelEntry implements Parcelable {

    private String mId;
    private int mCnt;

    public TicketSelEntry(){}

    protected TicketSelEntry(Parcel in) {
        mId = in.readString();
        mCnt = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeInt(mCnt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TicketSelEntry> CREATOR = new Creator<TicketSelEntry>() {
        @Override
        public TicketSelEntry createFromParcel(Parcel in) {
            return new TicketSelEntry(in);
        }

        @Override
        public TicketSelEntry[] newArray(int size) {
            return new TicketSelEntry[size];
        }
    };

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public int getmCnt() {
        return mCnt;
    }

    public void setmCnt(int mCnt) {
        this.mCnt = mCnt;
    }
}
