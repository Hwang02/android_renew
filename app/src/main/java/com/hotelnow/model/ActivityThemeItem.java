package com.hotelnow.model;

public class ActivityThemeItem {

    private String qcategory_ko;
    private String qcategory_id;

    public ActivityThemeItem(String qcategory_ko, String qcategory_id) {
        this.qcategory_ko = qcategory_ko;
        this.qcategory_id = qcategory_id;
    }

    public String getQcategory_ko() {
        return qcategory_ko;
    }

    public void setQcategory_ko(String qcategory_ko) {
        this.qcategory_ko = qcategory_ko;
    }

    public String getQcategory_id() {
        return qcategory_id;
    }

    public void setQcategory_id(String qcategory_id) {
        this.qcategory_id = qcategory_id;
    }
}
