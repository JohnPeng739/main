package com.ds.retl.rest.vo.statistic;

import com.ds.retl.rest.vo.LabelValueVO;
import com.ds.retl.rest.vo.NameValueVO;

import java.util.List;

/**
 * 实时汇聚统计数据的值对象定义
 *
 * @author : john.peng created on date : 2017/11/22
 */
public class RetlStatisticVO {
    List<NameValueVO> total;
    List<NameValueVO> error;

    public RetlStatisticVO() {
        super();
    }

    public RetlStatisticVO(List<NameValueVO> total, List<NameValueVO> error) {
        this();
        this.total = total;
        this.error = error;
    }

    public void setTotal(List<NameValueVO> total) {
        this.total = total;
    }

    public void setError(List<NameValueVO> error) {
        this.error = error;
    }

    public List<NameValueVO> getTotal() {
        return total;
    }

    public List<NameValueVO> getError() {
        return error;
    }
}
