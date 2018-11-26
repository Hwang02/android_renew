package com.hotelnow.fragment.model;

public class MySaveMoneyItem {

    private Integer mid, mincome, mspent, mremain, mamount;
    private String mname, mtype, museyn, mcreatedat, end_date, type_dp, change_dp;

    public MySaveMoneyItem(String name, int id, String type, int income, int spent, int remain, int amount, String useyn, String created_at, String end_date, String type_dp, String change_dp) {
        super();
        mname = name;
        mid = id;
        mtype = type;
        mincome = income;
        mspent = spent;
        mremain = remain;
        mamount = amount;
        museyn = useyn;
        mcreatedat = created_at;
        this.end_date = end_date;
        this.change_dp = change_dp;
        this.type_dp = type_dp;

    }

    public String getType_dp() {
        return type_dp;
    }

    public void setType_dp(String type_dp) {
        this.type_dp = type_dp;
    }

    public String getChange_dp() {
        return change_dp;
    }

    public void setChange_dp(String change_dp) {
        this.change_dp = change_dp;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public Integer getMincome() {
        return mincome;
    }

    public void setMincome(Integer mincome) {
        this.mincome = mincome;
    }

    public Integer getMspent() {
        return mspent;
    }

    public void setMspent(Integer mspent) {
        this.mspent = mspent;
    }

    public Integer getMremain() {
        return mremain;
    }

    public void setMremain(Integer mremain) {
        this.mremain = mremain;
    }

    public Integer getMamount() {
        return mamount;
    }

    public void setMamount(Integer mamount) {
        this.mamount = mamount;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    public String getMuseyn() {
        return museyn;
    }

    public void setMuseyn(String museyn) {
        this.museyn = museyn;
    }

    public String getMcreatedat() {
        return mcreatedat;
    }

    public void setMcreatedat(String mcreatedat) {
        this.mcreatedat = mcreatedat;
    }
}
