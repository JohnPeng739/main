package org.mx;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 集合工具类
 *
 * @author : john.peng date : 2017/9/6
 */
public class CollectionUtils {
    private CollectionUtils() {
        super();
    }

    /**
     * 向集合Set中添加一个泛型对象
     *
     * @param set   集合
     * @param value 泛型对象
     * @param <T>   泛型类型
     * @return 添加后的集合对象
     */
    public static <T> Set<T> add(Set<T> set, T value) {
        if (set == null) {
            set = new HashSet<T>();
        }
        if (value != null && !contain(set, value)) {
            set.add(value);
        }
        return set;
    }

    /**
     * 从列表集合中删除指定的泛型对象
     *
     * @param list  列表集合
     * @param value 泛型对象
     * @param <T>   泛型类型
     * @return 删除成功返回true，否则返回false
     */
    public static <T> boolean remove(List<T> list, T value) {
        if (list == null || value == null) {
            return false;
        }
        for (T t : list) {
            if (t.hashCode() == value.hashCode()) {
                list.remove(t);
                return true;
            }
        }
        return false;
    }

    /**
     * 从集合Set中删除指定的泛型对象
     *
     * @param set   集合对象
     * @param value 泛型对象
     * @param <T>   泛型类型
     * @return 删除成功返回true，否则返回false
     */
    public static <T> boolean remove(Set<T> set, T value) {
        if (set == null || value == null) {
            return false;
        }
        for (T t : set) {
            if (t.hashCode() == value.hashCode()) {
                set.remove(t);
                return true;
            }
        }
        return false;
    }

    /**
     * 判断列表集合中是否存在指定的泛型对象
     *
     * @param list  列表集合
     * @param value 泛型对象
     * @param <T>   泛型类型
     * @return 泛型对象存在返回true，否则返回false
     */
    public static <T> boolean contain(List<T> list, T value) {
        if (list == null || value == null) {
            return false;
        }
        for (T t : list) {
            if (t.hashCode() == value.hashCode()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断集合对象中是否存在指定的泛型对象
     *
     * @param set   集合对象
     * @param value 泛型对象
     * @param <T>   泛型类型
     * @return 泛型对象存在返回true，否则返回false
     */
    public static <T> boolean contain(Set<T> set, T value) {
        if (set == null || value == null) {
            return false;
        }
        for (T t : set) {
            if (t.hashCode() == value.hashCode()) {
                return true;
            }
        }
        return false;
    }
}
