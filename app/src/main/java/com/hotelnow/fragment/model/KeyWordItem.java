package com.hotelnow.fragment.model;

public class KeyWordItem {

    private int Keyword_id;
    private String Keyword;

    public KeyWordItem(int Keyword_id, String Keyword){
        this.Keyword_id = Keyword_id;
        this.Keyword = Keyword;
    }

    public int getKeyword_id() {
        return Keyword_id;
    }

    public void setKeyword_id(int keyword_id) {
        Keyword_id = keyword_id;
    }

    public String getKeyword() {
        return Keyword;
    }

    public void setKeyword(String keyword) {
        Keyword = keyword;
    }
}
