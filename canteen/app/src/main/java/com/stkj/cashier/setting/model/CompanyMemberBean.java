package com.stkj.cashier.setting.model;

import com.stkj.cashier.setting.model.db.CompanyMemberdbEntity;

import java.util.List;

public class CompanyMemberBean {
    private List<CompanyMemberdbEntity> results = null;

    private int totalCount;

    private int totalPage;

    private int pageIndex;

    private int pageSize;


    public List<CompanyMemberdbEntity> getResults() {
        return results;
    }

    public void setResults(List<CompanyMemberdbEntity> results) {
        this.results = results;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
