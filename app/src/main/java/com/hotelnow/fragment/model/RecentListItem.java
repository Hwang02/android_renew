package com.hotelnow.fragment.model;

public class RecentListItem {

    private String flag, id, name, now, view_yn, img_url;

    public RecentListItem(String flag, String id, String name, String now, String view_yn, String img_url) {
        this.flag = flag;
        this.id = id;
        this.name = name;
        this.now = now;
        this.view_yn = view_yn;
        this.img_url = img_url;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getView_yn() {
        return view_yn;
    }

    public void setView_yn(String view_yn) {
        this.view_yn = view_yn;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
