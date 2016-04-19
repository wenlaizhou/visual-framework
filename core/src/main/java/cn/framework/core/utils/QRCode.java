/**
 * @项目名称: wechat
 * @文件名称: QrCode.java
 * @Date: 2016年1月11日
 * @author: wenlai
 * @type: QrCode
 */
package cn.framework.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import cn.framework.core.log.LogProvider;
import cn.framework.core.utils.Files;
import cn.framework.core.utils.Strings;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

/**
 * @author wenlai
 *
 */
public class QRCode {
    
    /**
     * 读取二维码
     * @param data 数据流
     * @return
     */
    public static String readQRCode(byte[] data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return readQRCode(inputStream);
    }
    
    /**
     * 读取二维码
     * @param filePath 文件路径
     * @return
     */
    public static String readQRCode(String filePath) {
        if (!Files.exist(filePath))
            return Strings.EMPTY;
        return readQRCode(Files.newInputStream(filePath, StandardOpenOption.READ));
    }
    
    /**
     * 读取二维码
     * @param input 输入流
     * @return
     */
    public static String readQRCode(InputStream input) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(ImageIO.read(input));
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            Result result = new MultiFormatReader().decode(bitmap, hints);
            return result.getText();
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return Strings.EMPTY;
    }
    
    /**
     * 创建二维码
     * @param content
     * @return
     */
    public static byte[] generateQRCode(String content) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(getMatrix(content), "jpg", stream);
            return stream.toByteArray();
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * 生成二维码
     * @param content
     * @param file
     */
    public static void generateQRCode(String content, File file) {
        try {
            if (!ImageIO.write(MatrixToImageWriter.toBufferedImage(getMatrix(content)), "jpg", file)) {
                LogProvider.getFrameworkErrorLogger().error("生成二维码错误");
            }
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
    }
    
    /**
     * 生成二维码
     * @param content
     * @param filePath
     */
    public static void generateQRCode(String content, String filePath) {
        try {
            MatrixToImageWriter.writeToPath(getMatrix(content), "jpg", Paths.get(filePath));
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
    }
    
    /**
     * 生成二维码
     * @param content
     * @param output
     */
    public static void generateQRCode(String content, OutputStream output) {
        try {
            MatrixToImageWriter.writeToStream(getMatrix(content), "jpg", output);
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
    }
    
    /**
     * 获取二维码矩阵
     * @param content
     * @return
     */
    public static BitMatrix getMatrix(String content) {
        return getMatrix(content, 300, 300);
    }
    
    /**
     * 获取二维码矩阵
     * @param content
     * @param width
     * @param height
     * @return
     */
    public static BitMatrix getMatrix(String content, int width, int height) {
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            return new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        }
        catch (WriterException e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return null;
    }
}
