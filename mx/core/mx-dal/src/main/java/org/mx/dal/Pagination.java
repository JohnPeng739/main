package org.mx.dal;

/**
 * 分页对象定义
 *
 * @author : john.peng date : 2017/10/6
 */
public class Pagination {
    private int total;
    private int page;
    private int size;

    /**
     * 默认的构造函数
     */
    public Pagination() {
        this.total = 0;
        this.page = 1;
        this.size = 20;
    }

    /**
     * 默认的构造函数
     *
     * @param total 记录总数
     * @param page  当前页码，从1开始计数
     * @param size  每页记录数
     */
    public Pagination(int total, int page, int size) {
        this();
        this.total = total;
        this.page = page;
        this.size = size;
    }

    /**
     * 获取记录总数
     *
     * @return 记录总数
     */
    public int getTotal() {
        return this.total;
    }

    /**
     * 设置记录总数
     *
     * @param total 记录总数
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * 获取当前页码
     *
     * @return 当前页码
     */
    public int getPage() {
        return this.page;
    }

    /**
     * 设置当前页码
     *
     * @param page 当前页码
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * 获取每页记录数
     *
     * @return 每页记录数
     */
    public int getSize() {
        return this.size;
    }

    /**
     * 设置每页记录数
     *
     * @param size 每页记录数
     */
    public void setSize(int size) {
        this.size = size;
    }
}
