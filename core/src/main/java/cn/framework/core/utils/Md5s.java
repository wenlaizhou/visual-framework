package cn.framework.core.utils;

import com.google.common.base.Charsets;

import java.security.MessageDigest;

/**
 * MD5计算帮助类
 *
 * @author wenlai
 */
public final class Md5s {

    /**
     * 计算md5值<br>
     * 返回大写md5值
     *
     * @param content 要计算的字符串
     */
    public static String compute(String content) {
        if (!Strings.isNotNullOrEmpty(content)) {
            return null;
        }
        try {
            MessageDigest digestInstance = MessageDigest.getInstance("MD5");
            digestInstance.update(content.getBytes(Charsets.UTF_8));
            byte[] md = digestInstance.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < md.length; i++) {
                int val = (md[i]) & 0xff;
                if (val < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(val));
            }
            return sb.toString().toUpperCase();
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return Strings.EMPTY;
    }
}
