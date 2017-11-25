/**
 *
 */
package com.dscomm.storm.sds;

import java.io.Serializable;

/**
 * @author john
 */
public class MessageDistinationInfor implements Serializable, Cloneable {
    private static final long serialVersionUID = -680419464131061036L;
    /**
     * 直接发送的消息，需要接收消息的节点完成相应的业务工作，一般针对to的目标范围。
     */
    public static final String TYPE_DIRECT = "DIRECT";
    /**
     * 报告的消息，仅仅是通报一下，一般针对cc的目标范围。
     */
    public static final String TYPE_REPORT = "REPORT";
    private String jmsUrl = "", jmsUser = "", jmsPassowrd = "", jmsQueue = "",
            regionCode = "", jmsType = TYPE_DIRECT;

    /**
     * 默认的构造函数
     */
    public MessageDistinationInfor() {
        super();
    }

    /**
     * @return the jmsUrl
     */
    public String getJmsUrl() {
        return jmsUrl;
    }

    /**
     * @param jmsUrl the jmsUrl to set
     */
    public void setJmsUrl(String jmsUrl) {
        this.jmsUrl = jmsUrl;
    }

    /**
     * @return the jmsUser
     */
    public String getJmsUser() {
        return jmsUser;
    }

    /**
     * @param jmsUser the jmsUser to set
     */
    public void setJmsUser(String jmsUser) {
        this.jmsUser = jmsUser;
    }

    /**
     * @return the jmsPassowrd
     */
    public String getJmsPassowrd() {
        return jmsPassowrd;
    }

    /**
     * @param jmsPassowrd the jmsPassowrd to set
     */
    public void setJmsPassowrd(String jmsPassowrd) {
        this.jmsPassowrd = jmsPassowrd;
    }

    /**
     * @return the jmsQueue
     */
    public String getJmsQueue() {
        return jmsQueue;
    }

    /**
     * @param jmsQueue the jmsQueue to set
     */
    public void setJmsQueue(String jmsQueue) {
        this.jmsQueue = jmsQueue;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * @return the jmsType
     */
    public String getJmsType() {
        return jmsType;
    }

    /**
     * @param jmsType the jmsType to set
     */
    public void setJmsType(String jmsType) {
        this.jmsType = jmsType;
    }

    public MessageDistinationInfor clone() {
        MessageDistinationInfor o = null;
        try {
            o = (MessageDistinationInfor) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
