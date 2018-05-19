package org.mx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.error.UserInterfaceSystemErrorException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

/**
 * 数据摘要工具类
 *
 * @author : john.peng date : 2017/9/15
 */
public class DigestUtils {
    private static final Log logger = LogFactory.getLog(DigestUtils.class);

    /**
     * 默认的构造函数
     */
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
     * 将输入的字节数组使用Base64编码成一个字符串
     *
     * @param input 待编码的字节数组
     * @return Base64编码的字符串
     */
    public static String toBase64(byte[] input) {
        if (input == null || input.length <= 0) {
            return "";
        }
        return Base64.getEncoder().encodeToString(input);
    }

    /**
     * 将输入的Base64编码的字符串解码
     *
     * @param base64 待解码的Base64字符串
     * @return 解码后的数据
     */
    public static byte[] fromBase64(String base64) {
        if (StringUtils.isBlank(base64)) {
            return new byte[0];
        }
        return Base64.getDecoder().decode(base64);
    }

    /**
     * 使用选定的摘要算法对数据进行处理
     *
     * @param digestAlgorithm 摘要算法
     * @param encodeAlgorithm 编码算法，比如：BASE，HEX等
     * @param input           待摘要的数据
     * @return 摘要后的数据
     * @see #encodeString(String, byte[])
     */
    private static String digest(String digestAlgorithm, String encodeAlgorithm, String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(digestAlgorithm);
            digest.update(input.getBytes());
            return encodeString(encodeAlgorithm, digest.digest());
        } catch (NoSuchAlgorithmException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The encode algorithm['%s'] not supported.", digestAlgorithm));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_NO_SUCH_ALGORITHM);
        }
    }

    /**
     * 对二进制数据进行编码，输出为字符串。
     *
     * @param algorithm 编码算法，目前仅支持：BASE64和HEX两种算法
     * @param input     待编码的数据
     * @return 编码后的数据
     */
    private static String encodeString(String algorithm, byte[] input) {
        switch (algorithm) {
            case "BASE64":
                return Base64.getEncoder().encodeToString(input);
            case "HEX":
                return TypeUtils.byteArray2HexString(input);
            default:
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The encode algorithm['%s'] not supported.", algorithm));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_NO_SUCH_ALGORITHM);
        }
    }

    /**
     * 采用MD5算法进行摘要，并进行Base64编码
     *
     * @param src 待摘要的字符串
     * @return 摘要并编码后的字符串
     * @see #digest(String, String, String)
     */
    public static String md5(String src) {
        return digest("MD5", "BASE64", src);
    }

    /**
     * 采用SHA－1算法进行摘要，并进行Base64编码
     *
     * @param src 待摘要的字符串
     * @return 摘要并编码后的字符串
     * @see #digest(String, String, String)
     */
    public static String sha1(String src) {
        return digest("SHA-1", "BASE64", src);
    }

    /**
     * 采用SHA－256算法进行摘要，并进行Base64编码。
     *
     * @param src 待摘要的字符串
     * @return 摘要并编码后的字符串
     * @see #digest(String, String, String)
     */
    public static String sha256(String src) {
        return digest("SHA-256", "BASE64", src);
    }
}
