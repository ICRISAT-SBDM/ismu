package com.icrisat.sbdm.ismu.retrofit;

import java.util.List;

public class Metadata {
    private Pagination pagination;
    private Status[] status;
    private List<String> datafiles;

    public Status[] getStatus() {
        return status;
    }

    public List<String> getDatafiles() {
        return datafiles;
    }

    public Pagination getPagination() {
        return pagination;
    }
}