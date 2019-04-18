package com.hotelnow.model;

import java.io.Serializable;

/**
 * Created by idhwang on 2018. 4. 13..
 */

public class TicketInfoEntry implements Serializable {

    private String mTitle;
    private String mMessage;

    public TicketInfoEntry(String title, String message) {
        mTitle = title;
        mMessage = message;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}
