package org.mx.comps.notify.test.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.notify.processor.NotifyProcessListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TestNotifyProcessListener implements NotifyProcessListener {
    private static final Log logger = LogFactory.getLog((TestNotifyProcessListener.class));

    @Override
    public void before(JSONObject data) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Notify process before, message: %s", data.toJSONString()));
        }
    }

    @Override
    public void after(JSONObject data, boolean success, Set<String> invalidDevices) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Notify process after, result: %s, message: %s, invalid devices: %s",
                    success, data.toJSONString(), StringUtils.merge(invalidDevices)));
        }
    }
}
