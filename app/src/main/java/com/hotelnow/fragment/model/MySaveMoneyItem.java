package com.hotelnow.fragment.model;

public class MySaveMoneyItem {

    private Integer mid, mincome, mspent, mremain, mamount;
    private String mname, mtype, museyn, mcreatedat;

    public MySaveMoneyItem(String name, int id, String type, int income, int spent, int remain, int amount, String useyn, String created_at) {
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
