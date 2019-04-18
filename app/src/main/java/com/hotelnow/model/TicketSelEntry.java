package com.hotelnow.model;

import java.io.Serializable;

/**
 * Created by idhwang on 2018. 4. 13..
 */

public class TicketSelEntry implements Serializable {

    private String mId;
    private int mCnt;

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
