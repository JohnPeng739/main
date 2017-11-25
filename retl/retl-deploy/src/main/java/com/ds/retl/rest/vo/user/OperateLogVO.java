package com.ds.retl.rest.vo.user;

import com.ds.retl.dal.entity.UserOperateLog;
import org.mx.rest.vo.BaseVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2017/10/8.
 */
public class OperateLogVO extends BaseVO {
    private String message;

    public static List<OperateLogVO> transform(List<UserOperateLog> logs) {
        List<OperateLogVO> list = new ArrayList<>();
        if (logs != null && logs.size() > 0) {
            logs.forEach(log -> {
                OperateLogVO vo = new OperateLogVO();
                OperateLogVO.transform(log, vo);
                list.add(vo);
            });
        }
        return list;
    }

    public static void transform(UserOperateLog log, OperateLogVO logVO) {
        if (log == null || logVO == null) {
            return;
        }
        BaseVO.transform(log, logVO);
        logVO.message = log.getContent();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
