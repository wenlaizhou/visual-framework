package cn.framework.core.utils;

import cn.framework.core.log.LogProvider;

import java.util.Date;

/**
 * String 辅助类
 *
 * @author wenlai
 */
public final class Strings {

    /**
     * 空字符串
     */
    public final static String EMPTY = "";
    public final static String CLASS_SUFFIX = ".class";
    public final static String FILE_SLASH = "/";
    public final static String DOT = ".";

    /**
     * 将字符串转换成int<br>
     * 默认为-1
     *
     * @param intValue
     *
     * @return
     */
    public static int parseInt(String intValue) {
        return parseInt(intValue, -1);
    }

    /**
     * 将字符串转换成int
     *
     * @param intValue     默认值
     * @param defaultValue
     *
     * @return
     */
    public static int parseInt(String intValue, int defaultValue) {
        try {
            return Integer.parseInt(intValue);
        }
        catch (Throwable e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
            return defaultValue;
        }
    }

    /**
     * 字符串拼接，分隔符在左侧<br>
     * 例如：传入【"a","b","c","d"】, ":"<br>
     * 传出 ":a:b:c:d"
     *
     * @param args
     * @param seperator 分隔符
     *
     * @return
     */
    public static String leftJoin(Object[] args, String seperator) {
        if (Arrays.isNotNullOrEmpty(args)) {
            try {
                StringBuilder builder = new StringBuilder();
                for (Object arg : args)
                    builder.append(seperator).append(arg);
                return builder.toString();
            }
            catch (Throwable x) {
                LogProvider.getFrameworkErrorLogger().error(x);
                return seperator;
            }
        }
        return EMPTY;
    }

    /**
     * 字符串拼接，分隔符在右侧<br>
     * 例如：传入【"a","b","c","d"】, ":"<br>
     * 传出 "a:b:c:d:"
     *
     * @param args
     * @param seperator 分隔符
     *
     * @return
     */
    public static String rightJoin(Object[] args, String seperator) {
        if (Arrays.isNotNullOrEmpty(args)) {
            try {
                StringBuilder builder = new StringBuilder();
                for (Object arg : args)
                    builder.append(arg).append(seperator);
                return builder.toString();
            }
            catch (Throwable x) {
                LogProvider.getFrameworkErrorLogger().error(x);
                return seperator;
            }
        }
        return EMPTY;
    }

    /**
     * 字符串拼接<br>
     * 例如：传入【"a","b","c","d"】, ":"<br>
     * 传出 "a:b:c:d"
     *
     * @param args
     * @param seperator 分隔符
     *
     * @return
     */
    public static String join(Object[] args, String seperator) {
        if (Arrays.isNotNullOrEmpty(args)) {
            try {
                StringBuilder builder = new StringBuilder(args[0].toString());
                for (int index = 1; index < args.length; index++)
                    builder.append(seperator).append(args[index]);
                return builder.toString();
            }
            catch (Throwable x) {
                LogProvider.getFrameworkErrorLogger().error(x);
                return seperator;
            }
        }
        return EMPTY;
    }

    /**
     * 判断不为null或空字符串
     *
     * @param string 要检测的字符串
     *
     * @return
     */
    public static boolean isNotNullOrEmpty(String string) {
        return string != null && !string.isEmpty() && !string.trim().isEmpty();
    }

    /**
     * 判断为null或空字符串
     *
     * @param string
     *
     * @return
     */
    public static boolean isNullOrEmpty(String string) {
        if (string == null) {
            return true;
        }
        return string.isEmpty();
    }

    /**
     * 判断为null时返回空字符串
     *
     * @param string 要检测的字符串
     *
     * @return
     */
    public static String nullToEmpty(String string) {
        return (string == null) ? "" : string;
    }

    /**
     * 格式化字符串<br>
     * 自动将日期类型{@link Date}转换成数据库时间格式 2015-09-08 12:30:10.200
     *
     * @param pattern 格式化的字符 ${key} 或直接 key替换
     * @param params  key-value对
     *
     * @return
     */
    public static String format(String pattern, KVMap params) {
        String result = pattern;
        if (params != null && params.size() > 0) {
            for (Pair kvPair : params) {
                String param = String.format("${%1$s}", kvPair.key);
                String value = kvPair.value != null ? kvPair.value instanceof Date ? String.format("%1$tF %1$tT.%1$tL", kvPair.value) : kvPair.value.toString() : EMPTY;
                if (result.indexOf(param) > -1) {
                    result = result.replace(param, value);
                }
                else {
                    result = result.replace(kvPair.key, value);
                }
            }
        }
        return result;
    }

    /**
     * 格式化字符串<br>
     * 自动将日期类型{@link Date}转换成数据库时间格式 2015-09-08 12:30:10.200
     *
     * @param pattern 格式化的字符 ${key} 或直接 key替换
     * @param params  params key-value对
     *
     * @return
     */
    public static String format(String pattern, Pair... params) {
        String result = pattern;
        if (params != null && params.length > 0) {
            for (Pair kvPair : params) {
                String param = String.format("${%1$s}", kvPair.key);
                String value = Strings.EMPTY;
                if (kvPair.value != null) {
                    if (kvPair.value instanceof Date) {
                        value = String.format("%1$tF %1$tT.%1$tL", (Date) kvPair.value);
                    }
                    else {
                        value = kvPair.value.toString();
                    }
                }
                if (result.contains(param)) {
                    result = result.replace(param, value);
                }
                else {
                    result = result.replace(kvPair.key, value);
                }
            }
        }
        return result;
    }

    /**
     * 获取第一行数据
     *
     * @param content
     *
     * @return
     */
    public static String firstLine(String content) {
        int splitPos1 = content.indexOf("\r\n");
        int splitPos2 = content.indexOf("\n");
        return content.substring(0, splitPos1 > 0 ? splitPos1 : splitPos2 > 0 ? splitPos2 : content.length());
    }

    /**
     * 连接字符串
     *
     * @param strings strs
     *
     * @return
     */
    public static String append(String... strings) {
        if (strings != null && strings.length > 0) {
            StringBuilder res = new StringBuilder();
            for (String str : strings) {
                res.append(str);
            }
            return res.toString();
        }
        return Strings.EMPTY;
    }

    /**
     * 连接字符串
     *
     * @param strings
     *
     * @return
     */
    public static String append(Object... strings) {
        if (strings != null && strings.length > 0) {
            StringBuilder res = new StringBuilder();
            for (Object str : strings) {
                res.append(str);
            }
            return res.toString();
        }
        return Strings.EMPTY;
    }

    /**
     * 转换bool类型
     *
     * @param value
     * @param defaultValue 默认值
     *
     * @return
     */
    public static boolean parseBool(String value, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(value);
        }
        catch (Exception x) {
            return defaultValue;
        }
    }

    /**
     * 转换bool类型
     *
     * @param value value
     *
     * @return
     */
    public static boolean parseBool(String value) {
        return parseBool(value, false);
    }
}
