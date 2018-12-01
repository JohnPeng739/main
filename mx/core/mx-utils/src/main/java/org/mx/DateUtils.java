package org.mx;

import org.mx.error.UserInterfaceSystemErrorException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理工具类，包括常用的日期数据处理的方法。
 *
 * @author ： john peng date ： 2016/05/31
 */
public class DateUtils {

    /**
     * 默认的构造函数
     */
    private DateUtils() {
        super();
    }

    /**
     * 在一个指定的日期对象上进行日期计算
     *
     * @param date   进行计算的基准日期对象
     * @param field  日期计算的字段类型，支持：年、月、日、小时、分、秒的数据计算
     * @param amount 需要计算的数量，如果是负数，表示相减
     * @return 计算好的日期对象
     */
    public static Date add(Date date, FieldType field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field.getField(), amount);
        return calendar.getTime();
    }

    /**
     * 时间格式转换函数，转换为指定格式的时间字符串。
     *
     * @param date    待格式化的时间对象
     * @param pattern 时间格式
     * @return 输出的时间字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 时间格式转换函数，转换为时间对象。
     *
     * @param dateString 待解析的时间字符串
     * @param pattern    时间格式
     * @return 输出的时间对象
     * @throws ParseException 日期解析异常
     */
    public static Date parse(String dateString, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(dateString);
    }

    /**
     * 将指定的日期转换为10位长度的日期字串，日期格式为：yyyy-MM-dd。
     *
     * @param date 日期对象
     * @return 日期字串
     */
    public static String get10Date(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 获得一个10位长度的当前日期字串
     *
     * @return 日期字串
     * @see #get10Date(Date)
     */
    public static String get10DateNow() {
        return get10Date(new Date());
    }

    /**
     * 将指定的日期转换为12位长度的时间字串，日期格式为：HH:mm:ss.SSS。
     *
     * @param date 日期对象
     * @return 时间字串
     */
    public static String get12Time(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(date);
    }

    /**
     * 获得一个12位长度的当前时间字串
     *
     * @return 时间字串
     * @see #get12Time(Date)
     */
    public static String get12TimeNow() {
        return get12Time(new Date());
    }

    /**
     * 将指定的日期转换为19位长度的日期字串，日期格式为：yyyy-MM-dd HH:mm:ss。
     *
     * @param date 日期对象
     * @return 日期字串
     */
    public static String get19Date(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 获得一个19位长度的当前日期字串
     *
     * @return 日期字串
     * @see #get19Date(Date)
     */
    public static String get19DateNow() {
        return get19Date(new Date());
    }

    /**
     * 将指定的日期转换为23位长度的日期字串，日期格式为：yyyy-MM-dd HH:mm:ss.SSS。
     *
     * @param date 日期对象
     * @return 日期字串
     */
    public static String get23Date(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(date);
    }

    /**
     * 获得一个23位长度的当前日期字串
     *
     * @return 日期字串
     * @see #get23Date(Date)
     */
    public static String get23DateNow() {
        return get23Date(new Date());
    }

    /**
     * 将指定的日期转换为5位长度的时间字串，日期格式为：HH:mm。
     *
     * @param date 日期对象
     * @return 时间字串
     */
    public static String get5Time(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    /**
     * 获得一个5位长度的当前时间字串
     *
     * @return 时间字串
     * @see #get5Time(Date)
     */
    public static String get5TimeNow() {
        return get5Time(new Date());
    }

    /**
     * 将指定的日期转换为8位长度的时间字串，日期格式为：HH:mm:ss。
     *
     * @param date 日期对象
     * @return 时间字串
     */
    public static String get8Time(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 获得一个8位长度的当前时间字串
     *
     * @return 时间字串
     * @see #get8Time(Date)
     */
    public static String get8TimeNow() {
        return get8Time(new Date());
    }

    /**
     * 获取指定日期最大的时间，包括年月日时分秒和毫秒，如：2009/10/10 23:59:59.999
     *
     * @param date 指定的日期对象
     * @return 日期对象
     * @deprecated 推荐使用 {@link DatetimeRange#range(int, int, int, int, int, int)} 替代使用。
     */
    public static Date getMaxTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获取当天最大的时间，包括年月日时分秒和毫秒，如：2009/10/10 23:59:59.999
     *
     * @return 日期对象
     * @deprecated 推荐使用 {@link DatetimeRange#range(int, int, int, int, int, int)} 替代使用。
     */
    public static Date getMaxTimeNow() {
        return getMaxTime(new Date());
    }

    /**
     * 获取指定日期最小的时间，包括年月日，时分秒和毫秒均为0，如：2009/10/10 00:00:00.000
     *
     * @param date 指定的日期对象
     * @return 日期对象
     * @deprecated 推荐使用 {@link DatetimeRange#range(int, int, int, int, int, int)} 替代使用。
     */
    public static Date getMinTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取当天最小的时间，包括年月日，时分秒和毫秒均为0，如：2009/10/10 00:00:00.000
     *
     * @return 日期对象
     * @see #getMinTime(Date)
     * @deprecated 推荐使用 {@link DatetimeRange#range(int, int, int, int, int, int)} 替代使用。
     */
    public static Date getMinTimeNow() {
        return getMinTime(new Date());
    }

    /**
     * 合并两个Date类型的数据为一个Date对象，一般是针对日期和时间的合并。
     *
     * @param date 日期对象
     * @param time 时间对象
     * @return 包含日期和时间的Date对象
     */
    public static Date mergeDateTime(Date date, Date time) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        if (time != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(time);
            calendar.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, cal.get(Calendar.SECOND));
        }
        return calendar.getTime();
    }

    /**
     * 将一个日期类型转换为长整型
     *
     * @param date 日期
     * @return 长整型值
     */
    public static Long date2Long(Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    /**
     * 将一个长整数转换为日期类型
     *
     * @param time 时间
     * @return 日期类型的值
     */
    public static Date long2Date(Long time) {
        if (time == null) {
            return null;
        }
        return new Date(time);
    }

    /**
     * 字段类型枚举定义
     *
     * @author : john.peng date : 2017/9/15
     */
    public enum FieldType {
        /**
         * 年
         */
        YEAR(Calendar.YEAR),
        /**
         * 月
         */
        MONTH(Calendar.MONTH),
        /**
         * 日
         */
        DAY(Calendar.DAY_OF_MONTH),
        /**
         * 小时
         */
        HOUR(Calendar.HOUR_OF_DAY),
        /**
         * 分钟
         */
        MINUTE(
                Calendar.MINUTE),
        /**
         * 秒钟
         */
        SECOND(Calendar.SECOND);

        int field;

        FieldType(int field) {
            this.field = field;
        }

        int getField() {
            return field;
        }
    }

    /**
     * 时间段定义
     */
    public static class DatetimeRange {
        private long lowerLimit, upperLimit;

        /**
         * 构造函数
         *
         * @param lowerLimit 时间下限
         * @param upperLimit 时间上限
         */
        public DatetimeRange(long lowerLimit, long upperLimit) {
            super();
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
        }

        private static DatetimeRange calendar(int... params) {
            if (params == null || params.length <= 0) {
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                );
            }
            int[] units = new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY,
                    Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND};
            int length = params.length;
            Calendar calMin = Calendar.getInstance(), calMax = Calendar.getInstance();
            for (int index = 0; index < length; index++) {
                calMin.set(units[index], params[index]);
                calMax.set(units[index], params[index]);
            }
            for (int index = length; index < units.length; index++) {
                calMin.set(units[index], calMin.getActualMinimum(units[index]));
                calMax.set(units[index], calMax.getActualMaximum(units[index]));
            }
            return new DatetimeRange(calMin.getTimeInMillis(), calMax.getTimeInMillis());
        }

        /**
         * 返回上周时间段，周一～周日
         *
         * @return 时间段
         */
        public static DatetimeRange lastWeek() {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            // 周日为0， 周六为6
            calendar.add(Calendar.DAY_OF_MONTH, dayOfWeek * -1);
            int year1 = calendar.get(Calendar.YEAR);
            int month1 = calendar.get(Calendar.MONTH);
            int day1 = calendar.get(Calendar.DAY_OF_MONTH);
            DatetimeRange range1 = range(year1, month1, day1);
            calendar.add(Calendar.DAY_OF_MONTH, -7);
            int year0 = calendar.get(Calendar.YEAR);
            int month0 = calendar.get(Calendar.MONTH);
            int day0 = calendar.get(Calendar.DAY_OF_MONTH);
            DatetimeRange range0 = range(year0, month0, day0);
            return new DatetimeRange(range0.getLowerLimit(), range1.getUpperLimit());
        }

        public static DatetimeRange lastMonth() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) - 1;
            if (month < 0) {
                month += 1;
                year -= 1;
            }
            return range(year, month);
        }

        public static DatetimeRange lastYear() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR) - 1;
            return range(year);
        }

        public static DatetimeRange currentWeek() {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            // 周日为0， 周六为6
            calendar.add(Calendar.DAY_OF_MONTH, dayOfWeek * -1 + 1);
            int year0 = calendar.get(Calendar.YEAR);
            int month0 = calendar.get(Calendar.MONTH);
            int day0 = calendar.get(Calendar.DAY_OF_MONTH);
            DatetimeRange range0 = range(year0, month0, day0);
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            int year1 = calendar.get(Calendar.YEAR);
            int month1 = calendar.get(Calendar.MONTH);
            int day1 = calendar.get(Calendar.DAY_OF_MONTH);
            DatetimeRange range1 = range(year1, month1, day1);
            return new DatetimeRange(range0.getLowerLimit(), range1.getUpperLimit());
        }

        public static DatetimeRange currentMonth() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            return range(year, month);
        }
        public static DatetimeRange currentYear() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            return range(year);
        }

        public static DatetimeRange nextWeek() {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            // 周日为0， 周六为6
            calendar.add(Calendar.DAY_OF_MONTH, dayOfWeek * -1 + 7);
            int year0 = calendar.get(Calendar.YEAR);
            int month0 = calendar.get(Calendar.MONTH);
            int day0 = calendar.get(Calendar.DAY_OF_MONTH);
            DatetimeRange range0 = range(year0, month0, day0);
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            int year1 = calendar.get(Calendar.YEAR);
            int month1 = calendar.get(Calendar.MONTH);
            int day1 = calendar.get(Calendar.DAY_OF_MONTH);
            DatetimeRange range1 = range(year1, month1, day1);
            return new DatetimeRange(range0.getLowerLimit(), range1.getUpperLimit());
        }

        public static DatetimeRange nextMonth() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            if (month > 11) {
                month -= 1;
                year += 1;
            }
            return range(year, month);
        }

        public static DatetimeRange nextYear() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR) + 1;
            return range(year);
        }

        public static DatetimeRange range(int year) {
            return calendar(year);
        }

        public static DatetimeRange range(int year, int month) {
            return calendar(year, month);
        }

        public static DatetimeRange range(int year, int month, int day) {
            return calendar(year, month, day);
        }

        public static DatetimeRange range(int year, int month, int day, int hour) {
            return calendar(year, month, day, hour);
        }

        public static DatetimeRange range(int year, int month, int day, int hour, int minute) {
            return calendar(year, month, day, hour, minute);
        }

        public static DatetimeRange range(int year, int month, int day, int hour, int minute, int second) {
            return calendar(year, month, day, hour, minute, second);
        }

        /**
         * 获取时间下限
         *
         * @return 时间下限
         */
        public long getLowerLimit() {
            return lowerLimit;
        }

        /**
         * 获取时间上限
         *
         * @return 时间上限
         */
        public long getUpperLimit() {
            return upperLimit;
        }

        @Override
        public String toString() {
            return "DatetimeRange{" +
                    "lowerLimit=" + lowerLimit +
                    ", upperLimit=" + upperLimit +
                    '}';
        }
    }
}
