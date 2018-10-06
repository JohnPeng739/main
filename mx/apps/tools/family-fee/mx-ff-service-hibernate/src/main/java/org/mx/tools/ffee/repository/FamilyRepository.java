package org.mx.tools.ffee.repository;

import org.mx.tools.ffee.dal.entity.AccessLog;
import org.mx.tools.ffee.dal.entity.FamilyEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface FamilyRepository extends Repository<FamilyEntity, String> {
    @Query(value = "SELECT f.id " +
            "FROM TB_FAMILY f, TB_FAMILY_MEMBER m, TB_FFEE_ACCOUNT a " +
            "WHERE f.ID = m.FAMILY_ID AND m.ACCOUNT_ID = a.ID AND a.OPEN_ID = ?1", nativeQuery = true)
    String findFamilyIdByOpenId(String openId);

    @Query(value = "SELECT f.id " +
            "FROM TB_FAMILY f, TB_FAMILY_MEMBER m, " +
            "WHERE f.ID = m.FAMILY_ID AND m.ACCOUNT_ID = ?1", nativeQuery = true)
    String findFamilyIdByAccountId(String accountId);

    @Query("SELECT al " +
            "FROM AccessLogEntity al, FamilyMemberEntity fm " +
            "WHERE al.account.id = fm.account.id AND fm.family.id = ?1")
    List<AccessLog> findAccessLogByFamilyId(String familyId);
}
