/**
 *
 */
package org.mx.storm.common;

import java.io.Serializable;

/**
 * @author john
 */
public class BoltConfigure implements Serializable {
    private static final long serialVersionUID = 4173006249608567829L;

    public static final String GROUP_TYPE_SHUFFLE = "shuffle";
    public static final String GROUP_TYPE_CUSTOM = "custom";
    public static final String GROUP_TYPE_FIELDS = "fields";
    public static final String GROUP_TYPE_DIRECT = "direct";
    public static final String GROUP_TYPE_NONE = "none";

    private String name = "simpleBolt";
    private Class<?> boltClass = null;
    private int parallelNum = 1;
    private String groupType = "shuffle";
    private String groupComponentName = "";
    private String streamId = "";
    private String[] fields = null;

    /**
     * 默认的构造函数
     */
    public BoltConfigure() {
        super();
    }

    public BoltConfigure(String name, Class<?> boltClass, int parallelNum) {
        this();
        this.name = name;
        this.boltClass = boltClass;
        this.parallelNum = parallelNum;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the boltClass
     */
    public Class<?> getBoltClass() {
        return boltClass;
    }

    /**
     * @param boltClass the boltClass to set
     */
    public void setBoltClass(Class<?> boltClass) {
        this.boltClass = boltClass;
    }

    /**
     * @return the parallelNum
     */
    public int getParallelNum() {
        return parallelNum;
    }

    /**
     * @param parallelNum the parallelNum to set
     */
    public void setParallelNum(int parallelNum) {
        this.parallelNum = parallelNum;
    }

    /**
     * @return the groupType
     */
    public String getGroupType() {
        return groupType;
    }

    /**
     * @param groupType the groupType to set
     */
    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    /**
     * @return the groupComponentName
     */
    public String getGroupComponentName() {
        return groupComponentName;
    }

    /**
     * @param groupComponentName the groupComponentName to set
     */
    public void setGroupComponentName(String groupComponentName) {
        this.groupComponentName = groupComponentName;
    }

    /**
     * @return the streamId
     */
    public String getStreamId() {
        return streamId;
    }

    /**
     * @param streamId the streamId to set
     */
    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    /**
     * @return the fields
     */
    public String[] getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(String[] fields) {
        this.fields = fields;
    }

}
