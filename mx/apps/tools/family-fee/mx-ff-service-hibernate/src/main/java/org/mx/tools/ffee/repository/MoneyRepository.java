package org.mx.tools.ffee.repository;

import org.mx.tools.ffee.dal.entity.Income;
import org.mx.tools.ffee.dal.entity.MoneyItemEntity;
import org.mx.tools.ffee.dal.entity.Spending;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface MoneyRepository extends Repository<MoneyItemEntity, String> {
    @Query("SELECT i FROM IncomeEntity i WHERE i.owner.id = ?1 AND i.occurTime >= ?2 AND i.occurTime <= ?3")
    List<Income> findIncomesByAccountId(String accountId, long beginTime, long endTime);

    @Query("SELECT i FROM IncomeEntity i, FamilyMemberEntity fm " +
            "WHERE i.owner.id = fm.account.id AND i.occurTime >= ?2 AND i.occurTime <= ?3 AND fm.family.id = ?1")
    List<Income> findIncomesByFamilyId(String family, long beginTime, long endTime);

    @Query("SELECT s FROM SpendingEntity s WHERE s.owner.id = ?1 AND s.occurTime >= ?2 AND s.occurTime <= ?3")
    List<Spending> findSpendingsByAccountId(String accountId, long beginTime, long endTime);

    @Query("SELECT s FROM SpendingEntity s, FamilyMemberEntity fm " +
            "WHERE s.owner.id = fm.account.id AND s.occurTime >= ?2 AND s.occurTime <= ?3 AND fm.family.id = ?1")
    List<Spending> findSpendingsByFamilyId(String family, long beginTime, long endTime);
}
