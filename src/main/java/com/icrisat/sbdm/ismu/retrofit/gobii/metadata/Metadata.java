package com.icrisat.sbdm.ismu.retrofit.gobii.metadata;

public class Metadata {
    private DataFiles[] datafiles;
    private Pagination pagination;
    private Status[] status;

    public Status[] getStatus() {
        return status;
    }

    public DataFiles[] getDatafiles() {
        return datafiles;
    }

    public Pagination getPagination() {
        return pagination;
    }
}