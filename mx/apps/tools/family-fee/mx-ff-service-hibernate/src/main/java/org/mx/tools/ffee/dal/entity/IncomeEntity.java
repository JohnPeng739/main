package org.mx.tools.ffee.dal.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 描述： 收入明细信息实体类，基于Hibernate实现。
 *
 * @author: John.Peng
 * @date: 2018/2/18 上午11:36
 */
@Entity
@Table(name = "TB_INCOME")
public class IncomeEntity extends MoneyItemEntity implements Income {
}
