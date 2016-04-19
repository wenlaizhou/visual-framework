/**
 * @项目名称: framework
 * @文件名称: Des.java
 * @Date: 2015年6月18日
 * @Copyright: 2015 悦畅科技有限公司. All rights reserved.
 *             注意：本内容仅限于悦畅科技有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package cn.framework.core.utils;

import java.nio.charset.Charset;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import cn.framework.core.log.LogProvider;

/**
 * @author wenlai
 *
 */
public final class DESs {
    
    /**
     * des对称加密
     * 
     * @param content 要加密串
     * @param secureKey 秘钥
     * @return
     */
    public static String encrypt(String content, Key secureKey) {
        try {
            Cipher encryptor = Cipher.getInstance("DES");
            encryptor.init(Cipher.ENCRYPT_MODE, secureKey);
            return Base64s.encodeAndReturnString(encryptor.doFinal(content.getBytes(Charset.forName("utf-8"))));
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return Strings.EMPTY;
    }
    
    /**
     * des对称解密
     * 
     * @param content
     * @param secureKey
     * @return
     */
    public static String decrypt(String content, Key secureKey) {
        try {
            Cipher encryptor = Cipher.getInstance("DES");
            encryptor.init(Cipher.DECRYPT_MODE, secureKey);
            return new String(encryptor.doFinal(Base64s.decodeAndReturnBytes(content)));
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return Strings.EMPTY;
    }
    
    /**
     * 根据参数生成加密解密key
     * 
     * @param key
     * @return
     */
    public static final Key generateKey(String key) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("DES");
            generator.init(new SecureRandom(key.getBytes()));
            return generator.generateKey();
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return null;
    }
}
