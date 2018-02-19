package org.mx.tools.ffee.repository;

import org.mx.tools.ffee.dal.entity.IncomeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

/**
 * 描述： 收入信息查询接口，基于Hibernate Repository实现。
 *
 * @author John.Peng
 *         Date time 2018/2/19 下午4:08
 */
public interface IncomeRepository extends Repository<IncomeEntity, String> {
    @Query("select sum(money) from IncomeEntity income where income.createdTime >= ?1 and income.createdTime < ?2")
    double sumIncomeTotalBetween(long startDatetime, long endDatetime);
}
