package org.mx;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.mx.base64.Base64;

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

	private static String digest(String digestAlgorithm, String encodeAlgorithm, String input)
			throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(digestAlgorithm);
		digest.update(input.getBytes());
		return encodeString(encodeAlgorithm, digest.digest());
	}

	private static String encodeString(String algorithm, byte[] input) throws NoSuchAlgorithmException {
		switch (algorithm) {
		case "BASE64":
			return Base64.encodeToString(input, Base64.DEFAULT | Base64.NO_WRAP);
		case "HEX":
			return StringUtils.byte2HexString(input);
		default:
			throw new NoSuchAlgorithmException(String.format("The encode algorithm['%s'] not supported.", algorithm));
		}
	}

	/**
	 * 采用MD5算法进行摘要，并进行Base64编码
	 * 
	 * @param src
	 *            待摘要的字符串
	 * @return 摘要并编码后的字符串
	 * @throws NoSuchAlgorithmException 没有指定的算法
	 */
	public static String md5(String src) throws NoSuchAlgorithmException {
		return digest("MD5", "BASE64", src);
	}

	/**
	 * 采用SHA－1算法进行摘要，并进行Base64编码
	 * 
	 * @param src
	 *            待摘要的字符串
	 * @return 摘要并编码后的字符串
	 * @throws NoSuchAlgorithmException 没有指定的算法
	 */
	public static String sha1(String src) throws NoSuchAlgorithmException {
		return digest("SHA-1", "BASE64", src);
	}

	/**
	 * 采用SHA－256算法进行摘要，并进行Base64编码。
	 * 
	 * @param src
	 *            待摘要的字符串
	 * @return 摘要并编码后的字符串
	 * @throws NoSuchAlgorithmException 没有指定的算法
	 */
	public static String sha256(String src) throws NoSuchAlgorithmException {
		return digest("SHA-256", "BASE64", src);
	}
}
