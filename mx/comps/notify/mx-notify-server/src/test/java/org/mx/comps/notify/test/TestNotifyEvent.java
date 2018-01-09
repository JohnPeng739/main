package org.mx.comps.notify.test;

public class TestNotifyEvent {
    private String id, address, memo;

    public TestNotifyEvent(String id, String address, String memo) {
        super();
        this.id = id;
        this.address = address;
        this.memo = memo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getMemo() {
        return memo;
    }
}
