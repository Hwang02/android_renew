package com.hotelnow.fragment.model;

public class ThemeItem {

    private String id, name, landscape, product_id, theme_id, theme_flag, back_color;

    public ThemeItem(String id, String name, String landscape, String product_id, String theme_id, String theme_flag, String back_color) {
        this.id = id;
        this.name = name;
        this.landscape = landscape;
        this.product_id = product_id;
        this.theme_id = theme_id;
        this.theme_flag = theme_flag;
        this.back_color = back_color;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLandscape() {
        return landscape;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getTheme_id() {
        return theme_id;
    }

    public String getTheme_flag() {
        return theme_flag;
    }

    public String getBack_color() {
        return back_color;
    }
}
