package com.hotelnow.fragment.model;

public class ThemeSpecialItem {

    private String id, title, sub_title, img_main_top, img_main_list, theme_flag, subject, detail, notice, img_background;

    public ThemeSpecialItem(String id, String title, String sub_title, String img_main_top, String img_main_list, String theme_flag, String subject, String detail, String notice, String img_background) {
        this.id = id;
        this.title = title;
        this.sub_title = sub_title;
        this.img_main_top = img_main_top;
        this.img_main_list = img_main_list;
        this.theme_flag = theme_flag;
        this.subject = subject;
        this.detail = detail;
        this.notice = notice;
        this.img_background = img_background;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getImg_main_top() {
        return img_main_top;
    }

    public void setImg_main_top(String img_main_top) {
        this.img_main_top = img_main_top;
    }

    public String getImg_main_list() {
        return img_main_list;
    }

    public void setImg_main_list(String img_main_list) {
        this.img_main_list = img_main_list;
    }

    public String getTheme_flag() {
        return theme_flag;
    }

    public void setTheme_flag(String theme_flag) {
        this.theme_flag = theme_flag;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getImg_background() {
        return img_background;
    }

    public void setImg_background(String img_background) {
        this.img_background = img_background;
    }
}
