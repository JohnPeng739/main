package com.ds.retl.error;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by john on 2017/9/7.
 */
public class ETLError implements Serializable {
    private String type = "NA";
    private String message = "";
    private String data = "";
    private long createdtime = new Date().getTime();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getCreatedtime() {
        return createdtime;
    }

    public void setCreatedtime(long createdtime) {
        this.createdtime = createdtime;
    }
}
