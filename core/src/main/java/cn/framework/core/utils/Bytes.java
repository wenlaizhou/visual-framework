/**
 * @项目名称: core
 * @文件名称: Bytes.java
 * @Date: 2015年12月30日
 * @author: wenlai
 * @type: Bytes
 */
package cn.framework.core.utils;

import static java.lang.System.*;
import cn.framework.core.log.LogProvider;

/**
 * @author wenlai
 *
 */
public class Bytes {
    
    public static byte[] subBytes(byte[] data, int offset, int length) {
        if (data != null && data.length >= offset + length) {
            byte[] result = new byte[length];
            arraycopy(data, offset, result, 0, length);
            return result;
        }
        return null;
    }
    
    public static byte[] subBytes(byte[] data, int length) {
        try {
            if (data != null && data.length >= length) {
                byte[] result = new byte[length];
                arraycopy(data, 0, result, 0, length);
                return result;
            }
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * 将二进制数组转换成int
     * 
     * @param data
     * @return
     */
    public static int data2Int(byte[] data) {
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            byte d = data[i];
            result += (d & 0xFF) << (8 * i);
        }
        return result;
    }
    
    /**
     * 将int转换成二进制数组
     * 
     * @param num
     * @param size
     * @return
     */
    public static byte[] int2Data(int num, int size) {
        byte[] result = new byte[size];
        for (int i = 0; (i < 4) && (i < size); i++) {
            result[i] = (byte) (num >> 8 * i & 0xFF);
        }
        return result;
    }
    
}
