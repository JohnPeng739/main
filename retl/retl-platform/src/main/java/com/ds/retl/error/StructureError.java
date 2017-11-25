package com.ds.retl.error;

/**
 * 数据归一化处理错误异常定义，在对采集数据进行归一化处理是发生的异常。
 *
 * @author : john.peng created on date : 2017/9/7
 */
public class StructureError extends ETLError {
    /**
     * 默认的构造函数
     */
    public StructureError() {
        super();
        super.setType("structure error");
    }

    /**
     * 默认的构造函数
     *
     * @param message 异常消息
     * @param data    异常关联的数据对象
     */
    public StructureError(String message, String data) {
        this();
        super.setMessage(message);
        super.setData(data);
    }
}
