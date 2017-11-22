package com.ds.retl.rest;


import com.ds.retl.rest.vo.NameValueVO;
import com.ds.retl.rest.vo.statistic.RetlStatisticVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.rest.vo.DataVO;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 关于实时汇聚相关统计数据查询的RESTful服务
 *
 * @author : john.peng created on date : 2017/11/22
 */
@Component
@Path("rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RetlStatisticResource {
    private static final Log logger = LogFactory.getLog(RetlStatisticResource.class);

    @Path("retl-statistic")
    @GET
    public DataVO<RetlStatisticVO> getRetlStatistic() {
        // 尚未真正实现，这里模拟山东省的数据
        List<NameValueVO> total = new ArrayList<>();
        List<NameValueVO> error = new ArrayList<>();
        String[] cities = {"济南市", "青岛市", "烟台市", "德州市", "泰安市", "临沂市", "威海市", "东营市", "济宁市", "日照市",
                "淄博市", "枣庄市", "潍坊市", "滨州市", "菏泽市", "莱芜市", "聊城市"};
        Random random = new Random();
        for (String city : cities) {
            NameValueVO vot = new NameValueVO(city, random.nextInt(5000));
            NameValueVO voe = new NameValueVO(city, random.nextInt(Integer.valueOf(vot.getValue())));
            total.add(vot);
            error.add(voe);
        }
        return new DataVO<>(new RetlStatisticVO(total, error));
    }
}
