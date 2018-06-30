package org.mx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.error.UserInterfaceSystemErrorException;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.UUID;
import java.util.zip.CRC32;

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
     * 获取输入字节数组的32位CRC校验码
     *
     * @param bytes 字节数组
     * @return 32位长度的CRC校验码
     * @see CRC32
     */
    public static long crc32(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        if (bytes != null) {
            crc32.update(bytes);
        }
        return crc32.getValue();
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
     * 命令行方式将指定HTTPs证书导入到指定的Keystore中
     *
     * @param keystorePath Keystore库文件路径
     * @param password     访问Keystore库的密码
     * @param host         HTTPs主机
     * @param port         HTTPs端口
     */
    public static void importCert2Keystore(String keystorePath, String password, String host, int port) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream fin = new FileInputStream(new File(keystorePath));
            keyStore.load(fin, password.toCharArray());
            fin.close();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            X509TrustManager x509TrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
            SavingTrustManager savingTrustManager = new SavingTrustManager(x509TrustManager);
            sslContext.init(null, new TrustManager[]{savingTrustManager}, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
            sslSocket.setSoTimeout(10000);
            try {
                sslSocket.startHandshake();
                sslSocket.close();
                System.out.println("The certificate is already trusted.");
                return;
            } catch (Exception ex) {
                System.out.println("The certificate is not be trusted.");
            }
            X509Certificate[] chain = savingTrustManager.chain;
            if (chain == null) {
                System.out.println("Could not obtain server certificate chain.");
                return;
            }
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            int index = 1;
            for (X509Certificate cert : chain) {
                System.out.println(String.format("Cert %d, subject: %s, issuer: %s.", index++, cert.getSubjectDN(),
                        cert.getIssuerDN()));
                sha1.update(cert.getEncoded());
                md5.update(cert.getEncoded());
            }
            System.out.println("Select a certificate for import: [1]");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = reader.readLine().trim();
                try {
                    index = Integer.parseInt(line);
                    X509Certificate cert = chain[index];
                    keyStore.setCertificateEntry(host, cert);
                    FileOutputStream fout = new FileOutputStream(keystorePath);
                    keyStore.store(fout, password.toCharArray());
                    fout.flush();
                    fout.close();
                    System.out.println(String.format("Import certificate successfully, subject: %s, issuer: %s.",
                            chain[index].getSubjectDN(), chain[index].getIssuerDN()));
                    return;
                } catch (NumberFormatException ex) {
                    System.out.println(String.format("The number[%s] format invalid.", line));
                    ex.printStackTrace();
                }
            }
        } catch (KeyStoreException ex) {
            System.out.println(String.format("Keystore[%s] operate fail.", keystorePath));
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            System.out.println(String.format("The file[%s] not found.", keystorePath));
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException | IOException | CertificateException ex) {
            System.out.println(String.format("Load keystore[%s] fail.", keystorePath));
            ex.printStackTrace();
        } catch (KeyManagementException ex) {
            System.out.println("Init the SSL context fail.");
            ex.printStackTrace();
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

    private static class SavingTrustManager implements X509TrustManager {
        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }
}
