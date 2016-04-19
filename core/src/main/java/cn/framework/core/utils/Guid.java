package cn.framework.core.utils;

import java.util.UUID;

/**
 * project code
 * package cn.framework.core.utils
 * create at 16/4/17 上午11:44
 *
 * @author wenlai
 */
public class Guid {

    /**
     * 按照长度生成guid
     *
     * @param length 长度
     *
     * @return
     */
    public static String guid(int length) {
        String result = UUID.randomUUID().toString();
        if (length == 36) {
            return result;
        }
        if (length == 32) {
            return result.replace("-", "");
        }
        return result.substring(length);
    }

    /**
     * 生成32位随机字符,并用-分割<br/>
     * <p>
     * 总共36位字符
     *
     * @return
     */
    public static String guid() {
        return guid(36);
    }

}
