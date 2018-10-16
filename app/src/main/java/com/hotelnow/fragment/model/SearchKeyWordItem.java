package com.hotelnow.fragment.model;

public class SearchKeyWordItem {

    private int keyword_id;
    private String keyword;

    public SearchKeyWordItem(int keyword_id, String keyword) {
        this.keyword_id = keyword_id;
        this.keyword = keyword;
    }

    public int getKeyword_id() {
        return keyword_id;
    }

    public void setKeyword_id(int keyword_id) {
        this.keyword_id = keyword_id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
