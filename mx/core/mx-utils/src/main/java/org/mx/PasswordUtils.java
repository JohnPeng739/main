package org.mx;

/**
 * 密码工具类，包括跟密码操作相关的方法。
 *
 * @author : john peng date : 2016/5/29
 */
public class PasswordUtils {
    public static final int PASSWORD_DIGITAL = 1;

    private PasswordUtils() {
        super();
    }

    /**
     * 密码强度检测，使用：密码长度（是否大于6位）、是否包含数字、小写、大写或者常规符号等字符来进行检测。
     *
     * @param password 待检测的密码串
     * @return 密码强度值，0为最弱，5为最强。
     */
    public static PasswordStrengthen checkStrengthen(String password) {
        if (StringUtils.isBlank(password)) {
            return PasswordStrengthen.LOW;
        }
        int length = password.length();
        int containTypes = 0;
        if (length > 6) {
            if (password.matches(".*[\\d]+.*")) {
                // 包含了数字
                containTypes++;
            }
            if (password.matches(".*[a-z]+.*")) {
                // 包含了小写字母
                containTypes++;
            }
            if (password.matches(".*[A-Z]+.*")) {
                // 包含了大写字母
                containTypes++;
            }
            if (password.matches(".*[`~!@#$%^&()\\-=_+,./<>?'|{}\\[\\]\\\\]+.*")) {
                // 包含了常规的符号
                containTypes++;
            }
        }
        switch (containTypes) {
            case 4:
                // 长度大于6，有四种组合
                return PasswordStrengthen.HIGHER;
            case 3:
                // 长度大于6，有三种组合
                return PasswordStrengthen.HIGH;
            case 2:
                // 长度大于6，有两种组合
                return PasswordStrengthen.MEDIUM;
            default:
                // 长度大于6，仅有一种组合，或者长度小于6
                return PasswordStrengthen.LOW;
        }
    }

    /**
     * 密码强度枚举定义
     */
    public enum PasswordStrengthen {
        /**
         * 密码强度低
         */
        LOW,
        /**
         * 密码强度中
         */
        MEDIUM,
        /**
         * 密码强度高
         */
        HIGH,
        /**
         * 密码强度较高
         */
        HIGHER
    }

}
