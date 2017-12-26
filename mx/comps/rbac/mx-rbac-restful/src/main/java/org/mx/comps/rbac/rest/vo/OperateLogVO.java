package org.mx.comps.rbac.rest.vo;

import org.mx.dal.entity.OperateLog;
import org.mx.service.rest.vo.BaseVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 操作日志值对象定义
 *
 * @author : john.peng created on date : 2017/12/26
 */
public class OperateLogVO extends BaseVO {
    private String content;

    public static void transform(OperateLog log, OperateLogVO vo) {
        if (log == null || vo == null) {
            return;
        }
        BaseVO.transform(log, vo);
        vo.content = log.getContent();
    }

    public static List<OperateLogVO> transformLogVOs(Collection<OperateLog> logs) {
        if (logs == null) {
            return null;
        }
        List<OperateLogVO> vos = new ArrayList<>();
        logs.forEach(log -> {
            OperateLogVO vo = new OperateLogVO();
            OperateLogVO.transform(log, vo);
            vos.add(vo);
        });
        return vos;
    }
}
