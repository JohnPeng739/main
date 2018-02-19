package org.mx.tools.ffee.repository;

import org.mx.tools.ffee.dal.entity.Spending;
import org.mx.tools.ffee.dal.entity.SpendingEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * 描述： 收入信息查询接口，基于Hibernate Repository实现。
 *
 * @author John.Peng
 *         Date time 2018/2/19 下午4:08
 */
public interface SpendingRepository extends Repository<SpendingEntity, String> {
    @Query("select sum(money) from SpendingEntity spending where spending.createdTime >= ?1 and spending.createdTime < ?1")
    double sumSpendingTotalBetween(long startDatetime, long endDatetime);

    @Query("select spending from SpendingEntity  spending where spending.createdTime >= ?1 and spending.createdTime < ?2")
    List<Spending> listSpendingsBetween(long startDatetime, long endDatetime);
}
