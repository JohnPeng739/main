package org.mx.dal.utils;

import org.mx.dal.Pagination;
import org.mx.dal.entity.Base;
import org.mx.dal.entity.ElasticGeoPointBaseEntity;
import org.mx.dal.entity.GeoPointLocation;
import org.mx.dal.service.GeneralAccessor;

import java.util.List;

/**
 * 描述：访问Elastic的接口定义
 *
 * @author John.Peng
 * Date time 2018/4/1 上午9:32
 */
public interface ElasticUtil {
    /**
     * 销毁Elastic连接
     *
     * @throws Exception 销毁过程中发生的异常
     */
    void destroy() throws Exception;

    /**
     * 初始化Elastic连接
     */
    void init();

    /**
     * 创建指定实体对应的ES索引
     *
     * @param clazz 实现了ElasticBaseEntity的实体类
     * @param <T>   泛型定义
     */
    <T extends Base> void createIndex(Class<T> clazz);

    /**
     * 删除指定实体对应的ES索引
     *
     * @param clazz 实现了ElasticBaseEntity的实体类
     * @param <T>   泛型定义
     */
    <T extends Base> void deleteIndex(Class<T> clazz);

    /**
     * 删除制定实体列表对应的ES索引
     *
     * @param ts  实体列表
     * @param <T> 泛型定义
     */
    <T extends Base> void deleteIndex(List<T> ts);

    /**
     * 获取指定索引对应的实体类名
     *
     * @param index 索引名
     * @return 实体类名
     */
    Class<?> getIndexClass(String index);

    /**
     * 根据输入的条件进行检索
     *
     * @param group      条件组
     * @param clazz      对应的实体类，实体必须使用{@link org.mx.dal.annotation.ElasticIndex}进行注解
     * @param pagination 分页对象
     * @param <T>        泛型定义
     * @return 查询响应对象列表
     */
    <T extends Base> List<T> search(GeneralAccessor.ConditionGroup group, Class<? extends Base> clazz,
                                    Pagination pagination);

    /**
     * 根据输入的条件进行检索
     *
     * @param group      条件组
     * @param orderGroup 排序组
     * @param classes    对应的实体类集合，实体必须使用{@link org.mx.dal.annotation.ElasticIndex}进行注解
     * @param pagination 分页对象
     * @param <T>        泛型定义
     * @return 查询响应对象列表
     */
    <T extends Base> List<T> search(GeneralAccessor.ConditionGroup group,
                                    GeneralAccessor.RecordOrderGroup orderGroup,
                                    List<Class<? extends Base>> classes,
                                    Pagination pagination);

    /**
     * 根据条件进行计数
     *
     * @param group   条件组
     * @param classes 对应的的实体类集合
     * @param <T>     泛型定义
     * @return 符合条件的记录总数
     */
    <T extends Base> long count(GeneralAccessor.ConditionGroup group,
                                List<Class<? extends Base>> classes);

    /**
     * 根据输入的中心点和距离，搜索出符合条件的点位数据
     *
     * @param centerPoint    中心点
     * @param distanceMeters 距离（单位为米）
     * @param group          条件组
     * @param classes        实体类列表
     * @param <T>            泛型定义
     * @return 符合条件的点位数据列表，并计算了具体距离（distance属性）
     */
    <T extends ElasticGeoPointBaseEntity> List<T> geoNearBy(GeoPointLocation centerPoint, double distanceMeters,
                                                            GeneralAccessor.ConditionGroup group,
                                                            List<Class<? extends Base>> classes);

    /**
     * 根据输入的中心点和距离，搜索出符合条件的点位数据
     *
     * @param centerPoint    中心点
     * @param distanceMeters 距离（单位为米）
     * @param group          条件组
     * @param classes        实体类列表
     * @param pagination     分页对象
     * @param <T>            泛型定义
     * @return 符合条件的点位数据列表，并计算了具体距离（distance属性）
     */
    <T extends ElasticGeoPointBaseEntity> List<T> geoNearBy(GeoPointLocation centerPoint, double distanceMeters,
                                                            GeneralAccessor.ConditionGroup group,
                                                            List<Class<? extends Base>> classes,
                                                            Pagination pagination);

    /**
     * 根据输入的多边形范围，搜索出符合条件的点位数据；如果同时设置的中心点，则还会计算出距中心点的距离。
     *
     * @param centerPoint 中心点，可以为空
     * @param polygon     多边形点位列表
     * @param group       条件组
     * @param classes     实体类列表
     * @param <T>         泛型定义
     * @return 符合条件的点位数据列表，如果设置了中心点的话，还将计算具体距离（distance属性）
     */
    <T extends ElasticGeoPointBaseEntity> List<T> geoWithInPolygon(GeoPointLocation centerPoint,
                                                                   List<GeoPointLocation> polygon,
                                                                   GeneralAccessor.ConditionGroup group,
                                                                   List<Class<? extends Base>> classes);

    /**
     * 根据输入的多边形范围，搜索出符合条件的点位数据；如果同时设置的中心点，则还会计算出距中心点的距离。
     *
     * @param centerPoint 中心点，可以为空
     * @param polygon     多边形点位列表
     * @param group       条件组
     * @param classes     实体类列表
     * @param pagination  分页对象
     * @param <T>         泛型定义
     * @return 符合条件的点位数据列表，如果设置了中心点的话，还将计算具体距离（distance属性）
     */
    <T extends ElasticGeoPointBaseEntity> List<T> geoWithInPolygon(GeoPointLocation centerPoint,
                                                                   List<GeoPointLocation> polygon,
                                                                   GeneralAccessor.ConditionGroup group,
                                                                   List<Class<? extends Base>> classes,
                                                                   Pagination pagination);

    /**
     * 根据指定的ID获取实体对象
     *
     * @param id    关键字ID
     * @param clazz 实体类，实体必须使用{@link org.mx.dal.annotation.ElasticIndex}进行注解
     * @param <T>   泛型定义
     * @return 如果不存在，则返回null。
     */
    <T extends Base> T getById(String id, Class<T> clazz);

    /**
     * 索引指定的实体对象
     *
     * @param t     实体对象，实体必须使用{@link org.mx.dal.annotation.ElasticIndex}进行注解
     * @param <T>   泛型定义
     * @param isNew 是否新实体
     * @return 索引成功后的实体对象
     */
    <T extends Base> T index(T t, boolean isNew);

    /**
     * 索引指定的实体对象列表
     *
     * @param ts     实体对象列表，实体必须使用{@link org.mx.dal.annotation.ElasticIndex}进行注解
     * @param <T>    泛型定义
     * @param isNews 是否新实体列表，与ts中的元素一一对应
     * @return 索引成功后的实体对象列表
     */
    <T extends Base> List<T> index(List<T> ts, List<Boolean> isNews);
}
