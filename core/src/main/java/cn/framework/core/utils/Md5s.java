package cn.framework.core.utils;

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
    public static String compute(String content) throws Exception {
        if (!Strings.isNotNullOrEmpty(content))
            return null;
        MessageDigest digestInstance = MessageDigest.getInstance("MD5");
        digestInstance.update(content.getBytes("utf-8"));
        byte[] md = digestInstance.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < md.length; i++) {
            int val = (md[i]) & 0xff;
            if (val < 16)
                sb.append("0");
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toUpperCase();
    }
}
