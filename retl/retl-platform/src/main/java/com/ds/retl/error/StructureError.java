package com.ds.retl.error;

/**
 * Created by john on 2017/9/7.
 */
public class StructureError extends ETLError {
    public StructureError() {
        super();
        super.setType("structure error");
    }

    public StructureError(String message, String data) {
        this();
        super.setMessage(message);
        super.setData(data);
    }
}
