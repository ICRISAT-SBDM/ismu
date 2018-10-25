package com.icrisat.sbdm.ismu.retrofit;

public class Pagination {
    private int pageNumber;
    private int pageSize;
    private int totalCount;
    private int totalPages;

    public int getPageNumber() {
        return pageNumber;
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