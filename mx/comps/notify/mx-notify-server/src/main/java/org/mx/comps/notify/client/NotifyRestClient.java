package org.mx.comps.notify.client;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.service.rest.client.RestClientInvoke;
import org.mx.service.rest.client.RestInvokeException;
import org.mx.service.rest.vo.DataVO;

/**
 * 基于Restful接口调用的通知客户端
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class NotifyRestClient {
    private static final Log logger = LogFactory.getLog(NotifyRestClient.class);

    private RestClientInvoke invoke = null;
    private String url = null;

    /**
     * 默认的构造函数
     */
    private NotifyRestClient() {
        super();
        this.invoke = new RestClientInvoke();
    }

    /**
     * 默认的构造函数
     *
     * @param url 推送服务器提供的Restfule服务地址
     */
    public NotifyRestClient(String url) {
        this();
        this.url = url;
    }

    /**
     * 向推送服务器发生一个推送通知
     *
     * @param notify 推送通知对象
     * @param <T>    泛型定义
     * @return 发送成功返回true，否则返回false
     */
    public <T extends NotifyBean> boolean sendNotify(T notify) {
        if (StringUtils.isBlank(url)) {
            if (logger.isErrorEnabled()) {
                logger.error("The url is blank.");
            }
            return false;
        }
        try {
            DataVO<Boolean> result = invoke.post(url, JSON.toJSONString(notify), DataVO.class);
            return result.getData();
        } catch (RestInvokeException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Invoke post %s fail.", url), ex);
            }
            return false;
        }
    }

    /**
     * 关闭客户端，销毁资源
     */
    public void close() {
        if (invoke != null) {
            invoke.close();
        }
    }
}
