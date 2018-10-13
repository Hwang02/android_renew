package com.hotelnow.fragment.model;

public class FacilitySelItem {

    private int sel_img;
    private String sel_title;

    public FacilitySelItem(int sel_img, String sel_title){
        this.sel_img = sel_img;
        this.sel_title = sel_title;
    }

    public int getSel_img() {
        return sel_img;
    }

    public void setSel_img(int sel_img) {
        this.sel_img = sel_img;
    }

    public String getSel_title() {
        return sel_title;
    }

    public void setSel_title(String sel_title) {
        this.sel_title = sel_title;
    }
}
