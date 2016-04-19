package cn.framework.core.utils;

import java.util.*;

/**
 * 数组及arraylist帮助类
 *
 * @author wenlai
 */
public final class Arrays {

    /**
     * 将数据添加到末尾
     *
     * @param list 原始数据
     * @param args 添加数据
     */
    public static <T> void appendArray(List<T> list, T[] args) {
        if (Arrays.isNotNullOrEmpty(list) && Arrays.isNotNullOrEmpty(args)) {
            for (T arg : args)
                list.add(arg);
        }
    }

    /**
     * 判断数组是否为空
     *
     * @param args 数组
     *
     * @return 不为空
     */
    public static boolean isNotNullOrEmpty(Object[] args) {
        return args != null && args.length > 0;
    }

    /**
     * 判断集合是否为空
     *
     * @param args
     *
     * @return
     */
    public static boolean isNotNullOrEmpty(Collection<?> args) {
        return args != null && args.size() > 0;
    }

    /**
     * 判断Map类型参数是否为空
     *
     * @param args
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean isNotNullOrEmpty(Map args) {
        return args != null && !args.isEmpty();
    }

    /**
     * 打印数组，按照数组排列toString得到的值
     *
     * @param args 数组
     *
     * @return 返回值类似[1, 2, 3, 4]
     */
    public static String print(final Object[] args) {
        if (null == args) {
            return "null";
        }
        int iMax = args.length - 1;
        if (-1 == iMax) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(String.valueOf(args[i]));
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }

    /**
     * 打印数组
     *
     * @param args
     */
    public static void printAll(final Object[] args) {
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                System.out.println(arg);
            }
        }
    }

    /**
     * 将数组转换成ArrayList
     *
     * @param array
     *
     * @return
     */
    public static <T> ArrayList<T> toArrayList(final T[] array) {
        ArrayList<T> result = new ArrayList<T>();
        if (isNotNullOrEmpty(array)) {
            for (T item : array)
                result.add(item);
        }
        return result;
    }

    /**
     * 随机获取集合中的一项
     *
     * @param array
     *
     * @return
     */
    public static <T> T randomItem(final ArrayList<T> array) {
        if (!Arrays.isNotNullOrEmpty(array)) {
            return null;
        }
        return array.get(Math.abs(new Random((new Date()).getTime()).nextInt()) % array.size());
    }
}
