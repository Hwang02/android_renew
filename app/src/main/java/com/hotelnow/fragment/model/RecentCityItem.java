package com.hotelnow.fragment.model;

public class RecentCityItem {
    private String sel_city_id;
    private String sel_city_ko;
    private String sel_subcity_id;
    private String sel_subcity_ko;
    private String sel_option;

    public RecentCityItem(String sel_city_id, String sel_city_ko, String sel_subcity_id, String sel_subcity_ko, String sel_option) {
        this.sel_city_id = sel_city_id;
        this.sel_city_ko = sel_city_ko;
        this.sel_subcity_id = sel_subcity_id;
        this.sel_subcity_ko = sel_subcity_ko;
        this.sel_option = sel_option;
    }

    public String getSel_city_id() {
        return sel_city_id;
    }

    public void setSel_city_id(String sel_city_id) {
        this.sel_city_id = sel_city_id;
    }

    public String getSel_city_ko() {
        return sel_city_ko;
    }

    public void setSel_city_ko(String sel_city_ko) {
        this.sel_city_ko = sel_city_ko;
    }

    public String getSel_subcity_id() {
        return sel_subcity_id;
    }

    public void setSel_subcity_id(String sel_subcity_id) {
        this.sel_subcity_id = sel_subcity_id;
    }

    public String getSel_subcity_ko() {
        return sel_subcity_ko;
    }

    public void setSel_subcity_ko(String sel_subcity_ko) {
        this.sel_subcity_ko = sel_subcity_ko;
    }

    public String getSel_option() {
        return sel_option;
    }

    public void setSel_option(String sel_option) {
        this.sel_option = sel_option;
    }
}
