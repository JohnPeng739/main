package com.ds.retl.jms;

import org.apache.storm.jms.JmsProvider;

import javax.jms.Destination;
import java.util.Map;

/**
 * 在一个会话中提供多个JmsProvider的接口定义
 *
 * @author : john.peng created on date : 2017/11/9
 */
public interface JmsMultiProvider extends JmsProvider {
    /**
     * 获取Jms目标集合
     *
     * @return Jms目标集合
     * @throws Exception 获取过程中发生的异常
     */
    Map<String, Destination> destinations() throws Exception;

}
