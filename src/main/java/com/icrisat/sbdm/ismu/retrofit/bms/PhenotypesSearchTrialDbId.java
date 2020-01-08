package com.icrisat.sbdm.ismu.retrofit.bms;

import java.util.ArrayList;
import java.util.List;

public class PhenotypesSearchTrialDbId {

    private String page;
    private String pageSize;
    private List<String> trialDbIds;
    private String observationLevel;


    public PhenotypesSearchTrialDbId() {
        this.page = "0";
        this.pageSize = "1000";
        this.observationLevel="MEANS";
        this.trialDbIds = new ArrayList<>();
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public void setTrialDbIds(List<String> trialDbIds) {
        this.trialDbIds = trialDbIds;
    }
}
