package org.mx.tools.ffee.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class QrCodeUtils {
    private static final Log logger = LogFactory.getLog(QrCodeUtils.class);

    public static void createQrCode(int width, int height, String content, OutputStream out) {
        HashMap<EncodeHintType, Object> map = new HashMap<>();
        map.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        map.put(EncodeHintType.MARGIN, 2);
        try {
            content = new String(content.getBytes("UTF-8"), "ISO-8859-1");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
            MatrixToImageWriter.writeToStream(bitMatrix, "png", out);
        } catch (WriterException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Encode the QrCode fail, content: %s, width: %d, height: %d.",
                        content, width, height));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.QRCODE_ENCODE_FAIL
            );
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Write the QrCode fail, format: png, content: %s, width: %d, height: %d.",
                        content, width, height));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.QRCODE_IO_FAIL
            );
        }
    }

    public static void createQrCode(int width, int height, String content, String filePath) {
        try (FileOutputStream fout = new FileOutputStream(filePath)) {
            createQrCode(width, height, content, fout);
            fout.flush();
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Write the QrCode into the file fail, path: %s.", filePath));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.QRCODE_IO_FAIL
            );
        }
    }

    public static QrCodeResult parseQrCode(InputStream in) {
        try {
            MultiFormatReader reader = new MultiFormatReader();
            BufferedImage image = ImageIO.read(in);
            Map<DecodeHintType, Object> map = new HashMap<>();
            map.put(DecodeHintType.CHARACTER_SET, "ISO-8859-1");
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            Result result = reader.decode(binaryBitmap, map);
            return new QrCodeResult(result);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Read the QrCode fail.", ex);
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.QRCODE_IO_FAIL
            );
        } catch (NotFoundException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("The QrCode not found any content.", ex);
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.QRCODE_DECODE_FAIL
            );
        }
    }

    public static QrCodeResult parseQrCode(String filePath) {
        try (FileInputStream fin = new FileInputStream(filePath)) {
            return parseQrCode(fin);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Read the QrCode from the file fail, path: %s.", filePath));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.QRCODE_IO_FAIL
            );
        }
    }

    public static class QrCodeResult {
        private BarcodeFormat format;
        private String content;
        private int numBits;
        private long timestamp;

        public QrCodeResult(Result result) throws IOException {
            super();
            this.format = result.getBarcodeFormat();
            this.numBits = result.getNumBits();
            this.content = new String(result.getText().getBytes("ISO-8859-1"), "UTF-8");
            this.timestamp = result.getTimestamp();
        }

        public BarcodeFormat getFormat() {
            return format;
        }

        public String getContent() {
            return content;
        }

        public int getNumBits() {
            return numBits;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
