package org.mx.tools.ffee.repository;

import org.mx.tools.ffee.dal.entity.AccessLog;
import org.mx.tools.ffee.dal.entity.AccessLogEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * 描述：
 *
 * @author : john date : 2018/10/8 上午11:07
 */
public interface AccessLogRepository extends Repository<AccessLogEntity, String> {
    @Query("SELECT al FROM AccessLogEntity al WHERE al.accountId = ?1 ORDER BY al.createdTime DESC")
    List<AccessLog> findAccessLogByAccountId(String accountId);

    @Query("SELECT al FROM AccessLogEntity al, FamilyMemberEntity fme " +
            "WHERE al.accountId = fme.account.id AND fme.family.id = ?1 ORDER BY al.createdTime DESC")
    List<AccessLog> findAccessLogByFamilyId(String familyId);
}
