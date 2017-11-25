/**
 *
 */
package org.mx.storm.common;

import java.io.Serializable;

/**
 * @author john
 */
public class SpoutConfigure implements Serializable {
    private static final long serialVersionUID = 6835697389444723005L;
    private String name = "simpleSpout";
    private Class<?> spoutClass = null;
    private int parallelNum = 1;

    /**
     * 默认的构造函数
     */
    public SpoutConfigure() {
        super();
    }

    public SpoutConfigure(String name, Class<?> spoutClass, int parallelNum) {
        this();
        this.name = name;
        this.spoutClass = spoutClass;
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
     * @return the spoutClass
     */
    public Class<?> getSpoutClass() {
        return spoutClass;
    }

    /**
     * @param spoutClass the spoutClass to set
     */
    public void setSpoutClass(Class<?> spoutClass) {
        this.spoutClass = spoutClass;
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

}
