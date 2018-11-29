package com.hotelnow.fragment.model;

import org.json.JSONObject;

public class Popupitem {
    private JSONObject mObj;

    public Popupitem(JSONObject obj) {
        super();
        mObj = obj;
    }

    public JSONObject getObj() {
        return mObj;
    }

}
