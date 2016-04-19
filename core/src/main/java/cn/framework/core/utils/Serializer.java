/**
 * @项目名称: framework
 * @文件名称: Serializer.java
 * @Date: 2015年6月18日
 * @Copyright: 2015 悦畅科技有限公司. All rights reserved.
 * 注意：本内容仅限于悦畅科技有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package cn.framework.core.utils;

import cn.framework.core.log.LogProvider;

import java.io.*;

/**
 * @author wenlai
 */
public final class Serializer {

    /**
     * 序列化
     *
     * @param instance
     *
     * @return
     */
    public static byte[] serialize(Object instance) {
        try {
            if (instance == null || !(instance instanceof Serializable)) {
                return null;
            }
            ByteArrayOutputStream objectContainer = new ByteArrayOutputStream();
            ObjectOutputStream writer = new ObjectOutputStream(objectContainer);
            writer.writeObject(instance);
            return objectContainer.toByteArray();
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
            return null;
        }
    }

    /**
     * 反序列化
     *
     * @param data
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] data) {
        try {
            if (data != null && data.length > 0) {
                ByteArrayInputStream objectContainer = new ByteArrayInputStream(data);
                ObjectInputStream reader = new ObjectInputStream(objectContainer);
                return (T) reader.readObject();
            }
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return null;
    }
}
