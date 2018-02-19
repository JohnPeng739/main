package org.mx.tools.ffee.dal.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 描述： 支出明细实体类，基于Hibernate实现。
 *
 * @author John.Peng
 *         Date time 2018/2/18 上午11:35
 */
@Entity
@Table(name = "TB_SPENDING")
public class SpendingEntity extends MoneyItemEntity implements Spending {
}
