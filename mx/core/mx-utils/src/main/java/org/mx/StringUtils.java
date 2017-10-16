package org.mx;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 字符串工具类，包括一些常用的字符串相关方法。
 *
 * @author : john peng date : 2016/5/29
 */
public class StringUtils {
    /**
     * 默认的字串分隔符为半角逗号
     */
    public static final char DEFAULT_SEPARATOR = ',';

    /**
     * 默认的字符分割符，包含了：逗号，分号，空格，TAB，回车换行。
     */
    public static final String DELIMITERS = ",; \t\n";

    /**
     * 默认的构造函数
     */
    private StringUtils() {
        super();
    }

    /**
     * 采用Base64算法进行编码，将字节数据转换为字符串。
     *
     * @param value 待编码的字节数据
     * @return 编码后的字符串
     */
    public static String byte2Base64String(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    /**
     * 采用Base64算法进行解码，将字符串转换为字节数据
     *
     * @param base64Str Base64编码的字符串
     * @return 解码后的字节数据
     */
    public static byte[] Base64String2Byte(String base64Str) {
        return Base64.getDecoder().decode(base64Str);
    }

    /**
     * 将一个字节编码为长度为2的十六进制标示的字符串
     *
     * @param b 字节
     * @return 十六进制编码的字符串
     */
    public static String byte2HexString(byte b) {
        String hex = Integer.toHexString(b);
        int length = hex.length();
        switch (length) {
            case 0:
                return "00";
            case 1:
                return String.format("0%s", hex);
            case 2:
                return hex;
            default:
                return hex.substring(length - 2);
        }
    }

    /**
     * 将十六进制表示的字符串解码为字节数组，长度为2的字符串表示一个字节。
     *
     * @param input 十六进制字符串
     * @return 字节数组
     * @throws NumberFormatException 转换过程发生的异常
     */
    public static byte[] hexString2Byte(String input) throws NumberFormatException {
        if (isBlank(input)) {
            return null;
        }
        int length = input.length();
        if (length % 2 != 0) {
            throw new NumberFormatException(
                    String.format("The Hex String['%s']'s length is not a even number.", input));
        }
        byte[] bs = new byte[length / 2];
        for (int index = 0; index < length; index += 2) {
            bs[index / 2] = Byte.valueOf(input.substring(index, 2), 16);
        }
        return bs;
    }

    /**
     * 将字节数组编码为十六进制标示的字符串，每个字节编码为长度为2的字符串
     *
     * @param bs 字节数组
     * @return 十六进制编码的字符串
     * @see #byte2HexString(byte)
     */
    public static String byte2HexString(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bs) {
            sb.append(byte2HexString(b));
        }
        return sb.toString();
    }

    /**
     * 判断输入的字符串是否为空，可能是：null，或者字符串长度为0。
     *
     * @param str 待判断的字符串
     * @return true表示为空，否则表示为非空。
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        } else {
            str = str.trim();
            if (str.length() <= 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 判断字符串是否为汉字 只要出现汉字 就返回true
     *
     * @param str 判断字符串
     * @return 是否为汉字
     */
    public static boolean isChinese(String str) {
        boolean flg = false;
        if (!isBlank(str)) {
            char[] ch = str.toCharArray();
            for (int i = 0; i < ch.length; i++) {
                char c = ch[i];
                if (isChinese(c)) {
                    flg = true;
                    break;
                }
            }
        }
        return flg;
    }

    /**
     * 判断单个字符是否为汉字
     *
     * @param c 判断字符
     * @return 是否为汉字
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) {
            return true;
        }
        return false;
    }

    /**
     * 组合数据对象列表成为一个字符串
     *
     * @param list 数据对象列表
     * @return 组合后的字符串
     */
    public static String merge(List<?> list) {
        return merge(list, DEFAULT_SEPARATOR);
    }

    /**
     * 组合数据对象列表成为一个字符串
     *
     * @param list     数据对象列表
     * @param separate 组合分割符
     * @return 组合后的字符串
     */
    public static String merge(List<?> list, char separate) {
        return merge(list, String.valueOf(separate));
    }

    /**
     * 组合数据对象列表成为一个字符串
     *
     * @param list     数据对象列表
     * @param separate 组合分割符
     * @return 组合后的字符串
     */
    public static String merge(List<?> list, String separate) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toString());
            if (i < list.size() - 1) {
                sb.append(separate);
            }
        }
        return sb.toString();
    }

    /**
     * 组合字符串数组成为一个字符串，使用逗号作为分割符。
     *
     * @param str 字符串数组
     * @return 组合后的字符串
     * @see #merge(String[])
     */
    public static String merge(String[] str) {
        return merge(str, DEFAULT_SEPARATOR);
    }

    /**
     * 组合字符串数组成为一个字符串
     *
     * @param str      字符串数组
     * @param separate 组合分割符
     * @return 组合后的字符串
     */
    public static String merge(String[] str, char separate) {
        return merge(str, String.valueOf(separate));
    }

    /**
     * 组合字符串数组成为一个字符串
     *
     * @param str      字符串数组
     * @param separate 组合分割符
     * @return 组合后的字符串
     */
    public static String merge(String[] str, String separate) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length; i++) {
            sb.append(str[i].toString());
            if (i < str.length - 1) {
                sb.append(separate);
            }
        }
        return sb.toString();
    }

    /**
     * 组合指定重复次数的字符串称为一个字符串
     *
     * @param str    字符串
     * @param length 重复次数
     * @return 组合后的字符串
     * @see #merge(String, int, char)
     */
    public static String merge(String str, int length) {
        return merge(str, length, ',');
    }

    /**
     * 组合指定重复次数的字符串称为一个字符串
     *
     * @param str      字符串
     * @param length   重复次数
     * @param separate 组合分割符
     * @return 组合后的字符串
     */
    public static String merge(String str, int length, String separate) {
        return merge(str, length, String.valueOf(separate));
    }

    /**
     * 组合指定重复次数的字符串称为一个字符串
     *
     * @param str      字符串
     * @param length   重复次数
     * @param separate 组合分割符
     * @return 组合后的字符串
     */
    public static String merge(String str, int length, char separate) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(str);
            if (i < length - 1) {
                sb.append(separate);
            }
        }
        return sb.toString();
    }

    /**
     * 使用默认的分割符对输入的字符进行分割，同时对分割的项去除空字符和忽略空字符
     *
     * @param s 待分割的字符串
     * @return 分割后的字符数组
     * @see #split(String, boolean, boolean)
     * @see #DELIMITERS
     */
    public static String[] split(String s) {
        return split(s, DELIMITERS, true, true);
    }

    /**
     * 使用默认的分割符对输入的字符进行分割
     *
     * @param s                 待分割的字符串
     * @param trimTokens        对分割的项去除空字符
     * @param ignoreEmptyTokens 忽略空字符
     * @return 分割后的字符数组
     * @see #split(String, String, boolean, boolean)
     * @see #DELIMITERS
     */
    public static String[] split(String s, boolean trimTokens, boolean ignoreEmptyTokens) {
        return split(s, DELIMITERS, trimTokens, ignoreEmptyTokens);
    }

    /**
     * 使用特定的分割符对输入的字符串进行分割
     *
     * @param s                 待分割的字符串
     * @param delimiters        分割符（可以同时是多个分割符组合）
     * @param trimTokens        对分割的项去除空字符
     * @param ignoreEmptyTokens 忽略空字符
     * @return 分割后的字符数组
     */
    public static String[] split(String s, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (s == null) {
            s = "";
        }
        StringTokenizer st = new StringTokenizer(s, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!(ignoreEmptyTokens && token.length() == 0)) {
                tokens.add(token);
            }
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    /**
     * 将指定的字符串按照长度进行截断，截断后添加“...”
     *
     * @param src    字符串
     * @param length 指定长度
     * @return 截取处理后的字符串
     */
    public static String truncate(String src, int length) {
        if (src == null || src.length() <= length) {
            return src;
        }
        return String.format("%s...", src.substring(0, length));
    }

    /**
     * 将XML串进行转义编码
     *
     * @param str XML串
     * @return 转义编码后的串
     * @see #xmlUnescape(String)
     */
    public static String xmlEscape(String str) {
        if (str == null || str.length() <= 0) {
            return "";
        }
        for (int i = str.indexOf('\n'); i >= 0; i = str.indexOf('\n')) {
            str = str.substring(0, i) + "<br>" + str.substring(i + 1);
        }
        return str.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("'", "&apos;")
                .replaceAll("\"", "&quot;");
    }

    /**
     * 将转义过的XML串恢复编码
     *
     * @param str 转义过的XML字符串
     * @return 恢复后的XML数据
     * @see #xmlEscape(String)
     */
    public static String xmlUnescape(String str) {
        if (str == null || str.length() <= 0) {
            return "";
        }
        str = str.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&apos;", "'")
                .replaceAll("&quot;", "\"");
        for (int i = str.indexOf("<br>"); i >= 0; i = str.indexOf("<br>")) {
            str = str.substring(0, i) + "\n" + str.substring(i + 5);
        }
        return str;
    }

}
