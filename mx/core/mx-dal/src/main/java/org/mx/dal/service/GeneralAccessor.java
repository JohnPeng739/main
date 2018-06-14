package org.mx.dal.service;


import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;

import java.util.List;

/**
 * 通用的实体存取接口定义
 *
 * @author : john.peng date : 2017/8/18
 */
public interface GeneralAccessor {
    /**
     * 对指定的实体类型进行计数，实体接口必须继承Base接口。
     *
     * @param clazz 实体接口类型
     * @param <T>   实现Base接口的泛型对象类型
     * @return 指定实体的数量
     */
    <T extends Base> long count(Class<T> clazz);

    /**
     * 对指定的实体类型进行计数，实体接口必须继承Base接口。
     *
     * @param clazz   实体接口类型
     * @param isValid 如果设置为true，仅返回有效的记录；否则对所有记录计数。
     * @param <T>     实现Base接口的泛型对象类型
     * @return 指定实体的数量
     */
    <T extends Base> long count(Class<T> clazz, boolean isValid);

    /**
     * 获取指定实体类型的数据集合，实体接口必须继承Base接口。
     *
     * @param clazz 实体接口类型
     * @param <T>   实现Base接口的泛型对象类型
     * @return 指定实体对象类别
     */
    <T extends Base> List<T> list(Class<T> clazz);

    /**
     * 获取指定实体类型的数据集合，实体接口必须继承Base接口。
     *
     * @param clazz   实体接口类型
     * @param isValid 如果设置为true，仅返回有效的记录；否则返回所有记录。
     * @param <T>     实现Base接口的泛型对象类型
     * @return 指定实体对象类别
     */
    <T extends Base> List<T> list(Class<T> clazz, boolean isValid);

    /**
     * 根据分页信息获取指定实体类型的数据子集合，实体接口必须继承Base接口。
     *
     * @param pagination 分页信息
     * @param clazz      实体接口类型
     * @param <T>        实现Base接口的泛型对象类型
     * @return 指定实体对象集合
     */
    <T extends Base> List<T> list(Pagination pagination, Class<T> clazz);

    /**
     * 根据分页信息获取指定实体类型的数据子集合，实体接口必须继承Base接口。
     *
     * @param pagination 分页信息
     * @param clazz      实体接口类型
     * @param isValid    如果设置为true，仅返回有效的记录；否则返回所有记录。
     * @param <T>        实现Base接口的泛型对象类型
     * @return 指定实体对象集合
     */
    <T extends Base> List<T> list(Pagination pagination, Class<T> clazz, boolean isValid);

    /**
     * 根据实体ID和实体接口类型获取实体，实体接口必须继承Base接口。
     *
     * @param id    实体ID
     * @param clazz 实体接口类
     * @param <T>   实现Base接口的泛型对象类型
     * @return 实体，如果实体不存在，则返回null。
     */
    <T extends Base> T getById(String id, Class<T> clazz);

    /**
     * 根据指定字段的值获取数据记录集合，多个条件采用and组合。
     *
     * @param tuples 条件元组（包括字段名和字段值）
     * @param clazz  实体接口类
     * @param <T>    实现Base接口的泛型对象类型
     * @return 实体对象集合
     * @see ConditionTuple
     */
    <T extends Base> List<T> find(List<ConditionTuple> tuples, Class<T> clazz);

    /**
     * 根据指定字段的值获取一条数据记录，多个条件采用and组合。
     *
     * @param tuples 条件元组（包括字段名和字段值）
     * @param clazz  实体接口类
     * @param <T>    实现Base接口的泛型对象类型
     * @return 实体对象，如果不存在则返回null
     * @see ConditionTuple
     * @see #find(List, Class)
     */
    <T extends Base> T findOne(List<ConditionTuple> tuples, Class<T> clazz);

    /**
     * 保存指定的实体。如果可能，自动保存一条操作日志。
     *
     * @param t   实体对象
     * @param <T> 实现Base接口的泛型对象类型
     * @return 返回保存后的实体对象
     */
    <T extends Base> T save(T t);

    /**
     * 物理清楚指定实体类型的所有数据。
     *
     * @param clazz 实体对象类型
     * @param <T>   实现Base接口的泛型对象类型
     */
    <T extends Base> void clear(Class<T> clazz);

    /**
     * 逻辑删除指定关键字ID的数据实体
     *
     * @param id    关键字ID
     * @param clazz 实体接口类
     * @param <T>   实现Base接口的泛型对象类型
     * @return 删除的实体
     */
    <T extends Base> T remove(String id, Class<T> clazz);

    /**
     * 逻辑删除指定关键字ID的数据实体
     *
     * @param id          关键字ID
     * @param clazz       实体接口类
     * @param logicRemove 设置为true表示逻辑删除，否则物理删除。
     * @param <T>         实现Base接口的泛型对象类型
     * @return 删除的实体
     */
    <T extends Base> T remove(String id, Class<T> clazz, boolean logicRemove);

    /**
     * 逻辑删除指定的实体
     *
     * @param t   实体对象
     * @param <T> 实现Base接口的泛型对象类型
     * @return 删除的实体
     * @see #remove(Base, boolean)
     */
    <T extends Base> T remove(T t);

    /**
     * 删除指定的实体，支持逻辑删除和物理删除。
     *
     * @param t           实体对象
     * @param <T>         实现Base接口的泛型对象类型
     * @param logicRemove 设置为true表示逻辑删除，否则物理删除。
     * @return 删除的实体
     */
    <T extends Base> T remove(T t, boolean logicRemove);

    /**
     * 查询条件定义
     *
     * @author : john.peng date : 2017/8/18
     */
    class ConditionTuple {
        public String field;
        public ConditionOperate operate = ConditionOperate.CONTAIN;
        public Object value;

        /**
         * 默认的构造函数
         */
        public ConditionTuple() {
            super();
        }

        /**
         * 默认的构造函数
         *
         * @param field 字段名
         * @param value 字段值
         */
        public ConditionTuple(String field, Object value) {
            this();
            this.field = field;
            this.value = value;
        }

        /**
         * 默认的构造函数
         *
         * @param field   字段名
         * @param operate 操作类型
         * @param value   字段值
         */
        public ConditionTuple(String field, ConditionOperate operate, Object value) {
            this();
            this.field = field;
            this.operate = operate;
            this.value = value;
        }

        public static ConditionTuple contain(String field, Object value) {
            return new ConditionTuple(field, ConditionOperate.CONTAIN, value);
        }

        public static ConditionTuple eq(String field, Object value) {
            return new ConditionTuple(field, ConditionOperate.EQ, value);
        }

        public static ConditionTuple gt(String field, Object value) {
            return new ConditionTuple(field, ConditionOperate.GT, value);
        }

        public static ConditionTuple lt(String field, Object value) {
            return new ConditionTuple(field, ConditionOperate.LT, value);
        }

        public static ConditionTuple gte(String field, Object value) {
            return new ConditionTuple(field, ConditionOperate.GTE, value);
        }

        public static ConditionTuple lte(String field, Object value) {
            return new ConditionTuple(field, ConditionOperate.LTE, value);
        }

        @Override
        public String toString() {
            return "ConditionTuple{" +
                    "field='" + field + '\'' +
                    ", operate='" + operate + '\'' +
                    ", value=" + value + '}';
        }

        public enum ConditionOperate {
            /**
             * 等于
             */
            EQ,
            /**
             * 大于
             */
            GT,
            /**
             * 小于
             */
            LT,
            /**
             * 大于等于
             */
            GTE,
            /**
             * 小于等于
             */
            LTE,
            /**
             * 包含
             */
            CONTAIN
        }
    }
}
