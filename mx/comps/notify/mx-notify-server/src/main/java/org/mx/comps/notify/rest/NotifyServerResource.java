package org.mx.comps.notify.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.notify.online.OnlineDevice;
import org.mx.comps.notify.online.OnlineManager;
import org.mx.comps.notify.processor.NotifyProcessor;
import org.mx.comps.notify.rest.vo.OnlineDeviceVO;
import org.mx.dal.Pagination;
import org.mx.service.rest.vo.DataVO;
import org.mx.service.rest.vo.PaginationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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

    private NotifyProcessor notifyProcessor;

    private OnlineManager onlineManager;

    @Autowired
    public NotifyServerResource(NotifyProcessor notifyProcessor, OnlineManager onlineManager) {
        super();
        this.notifyProcessor = notifyProcessor;
        this.onlineManager = onlineManager;
    }

    /**
     * 推送通知
     *
     * @param notifyCommand JSON格式的通知命令字符串
     * @return 返回true表示处理成功
     */
    @Path("notify/send")
    @POST
    public DataVO<Boolean> sendNotify(String notifyCommand) {
        JSONObject json = JSON.parseObject(notifyCommand);
        notifyProcessor.notifyProcess(json);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Send notify successfully, command: %s.", notifyCommand));
        }
        return new DataVO<>(true);
    }

    /**
     * 获取符合条件的在线设备列表
     *
     * @param state 指定的状态
     * @param later 注册时间晚于指定时间
     * @param early 注册时间早于指定时间
     * @return 在线设备列表
     */
    @Path("onlines")
    @GET
    public DataVO<List<OnlineDeviceVO>> getOnlineDevices(@QueryParam("state") String state,
                                                         @QueryParam("later") long later,
                                                         @QueryParam("early") long early) {
        List<Predicate<OnlineDevice>> filters = new ArrayList<>();
        if (!StringUtils.isBlank(state)) {
            filters.add(onlineDevice -> state.equals(onlineDevice.getState()));
        }
        if (later > 0) {
            filters.add(onlineDevice -> onlineDevice.getRegistryTime() >= later);
        }
        if (early > 0) {
            filters.add(onlineDevice -> onlineDevice.getRegistryTime() <= early);
        }
        Set<OnlineDevice> set = onlineManager.getOnlineDevices(filters);
        List<OnlineDevice> list = new ArrayList<>(set);
        // 按照注册时间排序
        list.sort((od1, od2) -> (int) (od1.getRegistryTime() - od2.getRegistryTime()));
        return new DataVO<>(OnlineDeviceVO.transform(list));
    }

    /**
     * 获取符合条件的在线设备列表
     *
     * @param state      指定的状态
     * @param later      注册时间晚于指定时间
     * @param early      注册时间早于指定时间
     * @param pagination 分页对象
     * @return 在线设备列表
     */
    @Path("onlines")
    @POST
    public PaginationDataVO<List<OnlineDeviceVO>> getOnlineDevices(@QueryParam("state") String state,
                                                                   @QueryParam("later") long later,
                                                                   @QueryParam("early") long early,
                                                                   Pagination pagination) {
        List<Predicate<OnlineDevice>> filters = new ArrayList<>();
        if (!StringUtils.isBlank(state)) {
            filters.add(onlineDevice -> state.equals(onlineDevice.getState()));
        }
        if (later > 0) {
            filters.add(onlineDevice -> onlineDevice.getRegistryTime() >= later);
        }
        if (early > 0) {
            filters.add(onlineDevice -> onlineDevice.getRegistryTime() <= early);
        }
        Set<OnlineDevice> set = onlineManager.getOnlineDevices(filters);
        if (pagination == null) {
            pagination = new Pagination();
        }
        List<OnlineDevice> list = new ArrayList<>(set);
        // 按照注册时间排序
        list.sort((od1, od2) -> (int) (od1.getRegistryTime() - od2.getRegistryTime()));
        pagination.setTotal(set.size());
        List<OnlineDevice> result = new ArrayList<>();
        int skip = (pagination.getPage() - 1) * pagination.getSize();
        int num = Math.min(pagination.getSize(), list.size() - skip);
        for (int index = skip; index < skip + num; index++) {
            result.add(list.get(index));
        }
        return new PaginationDataVO<>(pagination, OnlineDeviceVO.transform(result));
    }
}
