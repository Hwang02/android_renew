package com.hotelnow.model;

/**
 * Created by susia on 15. 12. 10..
 */
public class CardEntry {
    private String cardno;
    private String cardtype;
    private String cardcd;
    private String id;
    private String cardnm;


    public CardEntry(String cardno, String cardtype, String cardcd, String id, String cardnm) {
        super();
        this.cardno = cardno;
        this.cardtype = cardtype;
        this.cardcd = cardcd;
        this.id = id;
        this.cardnm = cardnm;
    }

    public String getCardnm() {
        return cardnm;
    }

    public String getCardno() {
        return cardno;
    }

    public String getCardtype() {
        return cardtype;
    }

    public String getCardcd() {
        return cardcd;
    }

    public String getId() {
        return id;
    }
}