package com.icrisat.sbdm.ismu.retrofit;

public class Pagination {
    private int currentPage;
    private int pageSize;
    private int totalCount;
    private int totalPages;

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }
}