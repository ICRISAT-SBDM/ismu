package com.icrisat.sbdm.ismu.retrofit.gobii.metadata;

public class Pagination {
    private int currentPage;
    private int pageSize;
    private String nextPageToken;
    private int totalCount;
    private int totalPages;

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }
}