package org.mx.comps.notify.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.notify.processor.NotifyProcessor;
import org.mx.service.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 通知服务器Restful资源定义类
 *
 * @author : john.peng created on date : 2017/1/8
 */
@Component
@Path("rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotifyServerResource {
    private static final Log logger = LogFactory.getLog(NotifyServerResource.class);

    @Autowired
    private NotifyProcessor notifyProcessor = null;

    @Path("notify/send")
    @POST
    public DataVO<Boolean> sendNotify(String notifyCommand) {
        JSONObject json = JSON.parseObject(notifyCommand);
        notifyProcessor.notifyProcess(json);
        return new DataVO<>(true);
    }
}
