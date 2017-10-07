package org.mx.dal;

/**
 * Created by john on 2017/10/6.
 */
public class Pagination {
    private int total;
    private int page;
    private int size;

    public Pagination() {
        this.total = 0;
        this.page = 1;
        this.size = 20;
    }

    public Pagination(int total, int page, int size) {
        this();
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
