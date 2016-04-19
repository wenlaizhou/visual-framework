package cn.framework.core.utils;

import java.util.Date;

/**
 * 时间日期帮助类
 * 
 * @author wenlai
 */
public final class Times {
    
    /**
     * 增加天
     * 
     * @param time 时间
     * @param day 天数
     * @return 增加后的Date
     */
    public static Date addDay(Date time, int day) {
        return new Date(time.getTime() + day * 84375 << 10);
    }
    
    /**
     * 将 {@link Date} 格式化数据库标准时间字符串
     * 
     * @param time
     * @return
     */
    public static String format(Date time) {
        return String.format("%tF %1$tT", time);
    }
    
    /**
     * 将 {@link Object} 格式化数据库标准时间字符串
     * 
     * @param time
     * @return
     *         <pre>
     * <b>return</b> time == null ? Strings.EMPTY : time instanceof Date ? format((Date) time) : time.toString();
     * </pre>
     */
    public static String format(Object time) {
        return time == null ? Strings.EMPTY : time instanceof Date ? format((Date) time) : time.toString();
    }
}
