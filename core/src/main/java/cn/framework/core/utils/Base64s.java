/**
 * @项目名称: framework
 * @文件名称: Base64s.java
 * @Date: 2015年6月18日
 * @Copyright: 2015 悦畅科技有限公司. All rights reserved.
 *             注意：本内容仅限于悦畅科技有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package cn.framework.core.utils;

import java.nio.charset.Charset;
import java.util.Base64;
import static cn.framework.core.utils.Exceptions.*;

/**
 * @author wenlai
 *
 */
public final class Base64s {
    
    /**
     * 解码
     * 
     * @param src 原始串
     * @param code 编码
     * @return
     */
    public static String decode(String src, String code) {
        try {
            return new String(Base64.getDecoder().decode(src.getBytes(Charset.forName(Strings.isNotNullOrEmpty(code) ? code : "utf-8"))));
        }
        catch (Throwable x) {
            processException(x);
            return Strings.EMPTY;
        }
    }
    
    /**
     * base64加密
     * 
     * @param src
     * @return
     */
    public static String encode(String src) {
        try {
            return new String(Base64.getEncoder().encode(src.getBytes(Charset.forName("UTF-8"))));
        }
        catch (Throwable x) {
            processException(x);
            return Strings.EMPTY;
        }
    }
    
    /**
     * base64加密并返回字节数组
     * 
     * @param src
     * @return
     */
    public static byte[] encodeAndReturnBytes(String src) {
        try {
            return Base64.getEncoder().encode(src.getBytes(Charset.forName("UTF-8")));
        }
        catch (Throwable x) {
            processException(x);
            return null;
        }
    }
    
    /**
     * base64加密并返回字符串
     * 
     * @param src
     * @return
     */
    public static String encodeAndReturnString(byte[] src) {
        try {
            return new String(Base64.getEncoder().encode(src));
        }
        catch (Throwable x) {
            processException(x);
            return Strings.EMPTY;
        }
    }
    
    /**
     * base64加密
     * 
     * @param src
     * @return
     */
    public static byte[] encode(byte[] src) {
        try {
            return Base64.getEncoder().encode(src);
        }
        catch (Throwable x) {
            processException(x);
            return null;
        }
    }
    
    /**
     * base64解密
     * 
     * @param src
     * @return
     */
    public static String decode(String src) {
        try {
            return new String(Base64.getDecoder().decode(src.getBytes(Charset.forName("UTF-8"))));
        }
        catch (Throwable x) {
            processException(x);
            return Strings.EMPTY;
        }
    }
    
    /**
     * base64解密并返回字节数组
     * 
     * @param src
     * @return
     */
    public static byte[] decodeAndReturnBytes(String src) {
        try {
            return Base64.getDecoder().decode(src.getBytes(Charset.forName("UTF-8")));
        }
        catch (Throwable x) {
            processException(x);
            return null;
        }
    }
    
    /**
     * base64解密
     * 
     * @param src
     * @return
     */
    public static byte[] decode(byte[] src) {
        try {
            return Base64.getDecoder().decode(src);
        }
        catch (Throwable x) {
            processException(x);
            return null;
        }
    }
}
