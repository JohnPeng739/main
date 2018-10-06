package org.mx.tools.ffee.repository;

import org.mx.tools.ffee.dal.entity.BudgetItemEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface BudgetRepository extends Repository<BudgetItemEntity, String> {
    @Query("SELECT SUM(bi.money) FROM BudgetItemEntity bi WHERE bi.family.id = ?1 AND bi.year = ?2")
    double findBudgetTotalByFamily(String familyId, int year);

    @Query("SELECT bi.year AS year, SUM(bi.money) AS money FROM BudgetItemEntity bi " +
            "WHERE bi.family.id = ?1 GROUP BY bi.year ORDER BY bi.year")
    List<Object[]> findBudgetTotalGroupByYearByFamily(String familyId);
}
