package com.icrisat.sbdm.ismu.retrofit.bms;

public class SampleSearchStudyDbId {

    private String page;
    private String pageSize;
    private String studyDbId;

    public SampleSearchStudyDbId() {
        this.page = "0";
        this.pageSize = "1000";
        this.studyDbId = "";
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public void setStudyDbIds(String studyDbId) {
        this.studyDbId = studyDbId;
    }
}
