package com.shidou.commonlibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo on 17-9-27.
 */

public class Pageable<T> {
    private int page = 0;
    private int pageSize = 0;
    private int totalPage = 0;
    private int totalSize = 0;
    private List<T> data = new ArrayList<>();

    public Pageable() {
    }

    public Pageable(int page, int pageSize, int totalPage, int totalSize, List<T> data) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalSize = totalSize;
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Pageable{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", totalPage=" + totalPage +
                ", totalSize=" + totalSize +
                ", data=" + data +
                '}';
    }
}
