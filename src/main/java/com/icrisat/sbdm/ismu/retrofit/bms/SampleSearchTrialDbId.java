package com.icrisat.sbdm.ismu.retrofit.bms;

public class SampleSearchTrialDbId {

    private String page;
    private String pageSize;
    private String trialDbId;

    public SampleSearchTrialDbId() {
        this.page = "0";
        this.pageSize = "1000";
        this.trialDbId = "";
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public void setTrialDbIds(String trialDbId) {
        this.trialDbId = trialDbId;
    }
}
