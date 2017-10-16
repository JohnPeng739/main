package org.mx;

/**
 * 类型转换工具类，包括了各种常见的类型转换方法。
 *
 * @author : john peng date : 2016/5/29
 */
public class TypeUtils {

    /**
     * 默认的构造函数
     */
    private TypeUtils() {
        super();
    }

    /**
     * 将字节数组转换为十六进制的字符串，一个字节转换为两位长度的十六进制。
     *
     * @param byteArray 待转换的字节数组
     * @return 转换后的十六进制字符串，如果输入的字节数组为null或者长度为0， 则返回长度为0的字符串。
     */
    public static String byteArray2HexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 将整数数组转换为十六进制的字符串，一个整数转换为四位长度的十六进制。
     *
     * @param intArray 待转换的整数数组
     * @return 转换后的十六进制字符串，如果输入的整数数组为null或者长度为0， 则返回长度为0的字符串。
     */
    public static String intArray2HexString(int[] intArray) {
        if (intArray == null || intArray.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i : intArray) {
            sb.append(String.format("%04x", i));
        }
        return sb.toString();
    }

    /**
     * 将长整数数组转换为十六进制的字符串，一个长整数转换为八位长度的十六进制。
     *
     * @param longArray 待转换的长整数数组
     * @return 转换后的十六进制字符串，如果输入的长整数数组为null或者长度为0， 则返回长度为0的字符串。
     */
    public static String longArray2HexString(int[] longArray) {
        if (longArray == null || longArray.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int l : longArray) {
            sb.append(String.format("%08x", l));
        }
        return sb.toString();
    }

}
