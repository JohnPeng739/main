package com.ds.retl.rest.vo.statistic;

import com.ds.retl.rest.vo.LabelValueVO;
import com.ds.retl.rest.vo.NameValueVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 实时汇聚统计数据的值对象定义
 *
 * @author : john.peng created on date : 2017/11/22
 */
public class RetlStatisticVO {
    private List<RetlNumberVO> cities;

    public class RetlNumberVO {
        private String name;
        private int total, error;

        public RetlNumberVO(String name, int total, int error) {
            super();
            this.name = name;
            this.total = total;
            this.error = error;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public void setError(int error) {
            this.error = error;
        }

        public String getName() {
            return name;
        }

        public int getTotal() {
            return total;
        }

        public int getError() {
            return error;
        }
    }

    public RetlStatisticVO() {
        super();
        cities = new ArrayList<>();
    }

    public void add(String name, int total, int error) {
        cities.add(new RetlNumberVO(name, total, error));
    }

    public void setCities(List<RetlNumberVO> cities) {
        this.cities = cities;
    }

    public List<RetlNumberVO> getCities() {
        return cities;
    }
}