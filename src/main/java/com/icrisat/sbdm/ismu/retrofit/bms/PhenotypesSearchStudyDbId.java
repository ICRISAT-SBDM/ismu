package com.icrisat.sbdm.ismu.retrofit.bms;

import java.util.ArrayList;
import java.util.List;

public class PhenotypesSearchStudyDbId {

    private String page;
    private String pageSize;
    private List<String> studyDbIds;
    private String observationLevel;

    public PhenotypesSearchStudyDbId() {
        this.page = "0";
        this.pageSize = "1000";
        this.observationLevel="MEANS";
        this.studyDbIds = new ArrayList<>();
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public void setStudyDbIds(List<String> studyDbIds) {
        this.studyDbIds = studyDbIds;
    }
}
