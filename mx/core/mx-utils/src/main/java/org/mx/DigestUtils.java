package org.mx;

import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 数据摘要工具类
 *
 * @author : john.peng date : 2017/9/15
 */
public class DigestUtils {
    private DigestUtils() {
        super();
    }

    /**
     * 获取一个唯一的UUID码
     *
     * @return UUID码
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 使用选定的摘要算法对数据进行处理
     *
     * @param digestAlgorithm 摘要算法
     * @param encodeAlgorithm 编码算法，比如：BASE，HEX等
     * @param input           待摘要的数据
     * @return 摘要后的数据
     * @throws NoSuchAlgorithmException 指定的算法不存在
     * @see #encodeString(String, byte[])
     */
    private static String digest(String digestAlgorithm, String encodeAlgorithm, String input)
            throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(digestAlgorithm);
        digest.update(input.getBytes());
        return encodeString(encodeAlgorithm, digest.digest());
    }

    /**
     * 对二进制数据进行编码，输出为字符串。
     *
     * @param algorithm 编码算法，目前仅支持：BASE64和HEX两种算法
     * @param input     待编码的数据
     * @return 编码后的数据
     * @throws NoSuchAlgorithmException 指定的算法不存在
     */
    private static String encodeString(String algorithm, byte[] input) throws NoSuchAlgorithmException {
        switch (algorithm) {
            case "BASE64":
                return new BASE64Encoder().encode(input);
            case "HEX":
                return StringUtils.byte2HexString(input);
            default:
                throw new NoSuchAlgorithmException(String.format("The encode algorithm['%s'] not supported.", algorithm));
        }
    }

    /**
     * 采用MD5算法进行摘要，并进行Base64编码
     *
     * @param src 待摘要的字符串
     * @return 摘要并编码后的字符串
     * @throws NoSuchAlgorithmException 没有指定的算法
     * @see #digest(String, String, String)
     */
    public static String md5(String src) throws NoSuchAlgorithmException {
        return digest("MD5", "BASE64", src);
    }

    /**
     * 采用SHA－1算法进行摘要，并进行Base64编码
     *
     * @param src 待摘要的字符串
     * @return 摘要并编码后的字符串
     * @throws NoSuchAlgorithmException 没有指定的算法
     * @see #digest(String, String, String)
     */
    public static String sha1(String src) throws NoSuchAlgorithmException {
        return digest("SHA-1", "BASE64", src);
    }

    /**
     * 采用SHA－256算法进行摘要，并进行Base64编码。
     *
     * @param src 待摘要的字符串
     * @return 摘要并编码后的字符串
     * @throws NoSuchAlgorithmException 没有指定的算法
     * @see #digest(String, String, String)
     */
    public static String sha256(String src) throws NoSuchAlgorithmException {
        return digest("SHA-256", "BASE64", src);
    }
}
