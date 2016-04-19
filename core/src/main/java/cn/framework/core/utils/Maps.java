/**
 * @项目名称: framework
 * @文件名称: HashMaps.java
 * @Date: 2015年6月25日
 * @Copyright: 2015 悦畅科技有限公司. All rights reserved.
 * 注意：本内容仅限于悦畅科技有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package cn.framework.core.utils;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * Map帮助类
 *
 * @author wenlai
 */
public final class Maps {

    /**
     * 获取map的第一个元素
     *
     * @param map
     * @return
     */
    public static <K, V> V firstValue(Map<K, V> map) {
        if (map == null || map.size() <= 0) {
            return null;
        }
        return map.get(map.keySet().iterator().next());
    }

    /**
     *
     * @param map
     * @return
     */
    public static <K, V> K firstKey(Map<K, V> map) {
        if (map == null || map.size() <= 0) {
            return null;
        }
        return map.keySet().iterator().next();
    }

    /**
     * 获取map中第n个元素
     *
     * @param map
     * @param n
     * @return
     */
    public static <K, V> V indexValue(Map<K, V> map, int n) {
        if (map == null || map.size() <= n) {
            return null;
        }
        int index = 0;
        for (K key : map.keySet()) {
            if (index == n) {
                return map.get(key);
            }
            index++;
        }
        return null;
    }

    /**
     * 获取map中的第n个key
     *
     * @param map
     * @param n
     * @return
     */
    public static <K, V> K indexKey(Map<K, V> map, int n) {
        if (map == null || map.size() <= n) {
            return null;
        }
        int index = 0;
        for (K key : map.keySet()) {
            if (index == n) {
                return key;
            }
            index++;
        }
        return null;
    }

    /**
     * 打印map
     *
     * @param map
     * @return
     */
    public static String print(Map<?, ?> map) {
        return JSON.toJSONString(map);
    }

    /**
     * 判断是否有值
     *
     * @param m
     * @return
     */
    public static boolean isNotNullOrEmpty(@SuppressWarnings("rawtypes") Map m) {
        return m != null && m.keySet() != null && m.keySet().size() > 0;
    }

}
