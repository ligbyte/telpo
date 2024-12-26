package com.stkj.cashier.setting.model;



public class OfflineSetBean {
    private String fId;

    private double limitMoney;

    private Integer limitCount;

    private String machineNumber;

    private String fCreatortime;

    private String fCompanyid;

    public String getfId() {
        return fId;
    }

    public void setfId(String fId) {
        this.fId = fId;
    }

    public double getLimitMoney() {
        return limitMoney;
    }

    public void setLimitMoney(double limitMoney) {
        this.limitMoney = limitMoney;
    }

    public Integer getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    public String getMachineNumber() {
        return machineNumber;
    }

    public void setMachineNumber(String machineNumber) {
        this.machineNumber = machineNumber;
    }

    public String getfCreatortime() {
        return fCreatortime;
    }

    public void setfCreatortime(String fCreatortime) {
        this.fCreatortime = fCreatortime;
    }

    public String getfCompanyid() {
        return fCompanyid;
    }

    public void setfCompanyid(String fCompanyid) {
        this.fCompanyid = fCompanyid;
    }
}

