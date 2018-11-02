package com.hotelnow.fragment.model;

public class PrivateDealItem {

    private String id, name, category_code, category, landscape, review_score, grade_score, sale_rate;
    private boolean islike;

    public PrivateDealItem(String id, String name, String category_code, String category, String landscape, String review_score, String grade_score, String sale_rate, boolean islike) {
        this.id = id;
        this.name = name;
        this.category_code = category_code;
        this.category = category;
        this.landscape = landscape;
        this.review_score = review_score;
        this.grade_score = grade_score;
        this.sale_rate = sale_rate;
        this.islike = islike;
    }

    public boolean isIslike() {
        return islike;
    }

    public void setIslike(boolean islike) {
        this.islike = islike;
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

    public String getCategory_code() {
        return category_code;
    }

    public void setCategory_code(String category_code) {
        this.category_code = category_code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLandscape() {
        return landscape;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
    }

    public String getReview_score() {
        return review_score;
    }

    public void setReview_score(String review_score) {
        this.review_score = review_score;
    }

    public String getGrade_score() {
        return grade_score;
    }

    public void setGrade_score(String grade_score) {
        this.grade_score = grade_score;
    }

    public String getSale_rate() {
        return sale_rate;
    }

    public void setSale_rate(String sale_rate) {
        this.sale_rate = sale_rate;
    }
}
