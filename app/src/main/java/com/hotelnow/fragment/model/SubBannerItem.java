package com.hotelnow.fragment.model;



public class SubBannerItem {

    private String id, order, category, image, keyword, type, evt_type, event_id,  link;

    public SubBannerItem(String id, String order, String category, String image, String keyword, String type, String evt_type, String event_id, String link) {
        this.id = id;
        this.order = order;
        this.category = category;
        this.image = image;
        this.keyword = keyword;
        this.type = type;
        this.evt_type = evt_type;
        this.event_id = event_id;
        this.link = link;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvt_type() {
        return evt_type;
    }

    public void setEvt_type(String evt_type) {
        this.evt_type = evt_type;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }
}
