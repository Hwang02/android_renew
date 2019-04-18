package com.hotelnow.model;


public class RecentItem {

    private String sel_id, sel_option;

    public RecentItem(String sel_id, String sel_option) {
        this.sel_id = sel_id;
        this.sel_option = sel_option;
    }

    public String getSel_id() {
        return sel_id;
    }

    public void setSel_id(String sel_id) {
        this.sel_id = sel_id;
    }

    public String getSel_option() {
        return sel_option;
    }

    public void setSel_option(String sel_option) {
        this.sel_option = sel_option;
    }
}
