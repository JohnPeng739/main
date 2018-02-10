package org.mx.comps.rbac.rest.vo;

import org.mx.dal.entity.OperateLog;
import org.mx.service.rest.vo.BaseVO;

import java.util.*;

/**
 * 操作日志值对象定义
 *
 * @author : john.peng created on date : 2017/12/26
 */
public class OperateLogVO extends BaseVO {
    private String content;

    public static OperateLogVO transform(OperateLog operateLog) {
        if (operateLog == null) {
            return null;
        }
        OperateLogVO operateLogVO = new OperateLogVO();
        BaseVO.transform(operateLog, operateLogVO);
        operateLogVO.content = operateLog.getContent();
        return operateLogVO;
    }

    public static List<OperateLogVO> transform(Collection<OperateLog> operateLogs) {
        List<OperateLogVO> operateLogVOS = new ArrayList<>();
        if (operateLogs == null || operateLogs.isEmpty()) {
            return operateLogVOS;
        }
        operateLogs.forEach(operateLog -> {
            OperateLogVO operateLogVO = OperateLogVO.transform(operateLog);
            if (operateLogVO != null) {
                operateLogVOS.add(operateLogVO);
            }
        });
        return operateLogVOS;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
