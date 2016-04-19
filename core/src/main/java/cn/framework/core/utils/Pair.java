/**
 * @项目名称: core
 * @文件名称: KVPair.java
 * @Date: 2015年11月20日
 * @author: wenlai
 * @type: KVPair
 */
package cn.framework.core.utils;

/**
 * key - value 对
 *
 * @author wenlai
 */
public class Pair
{

    /**
     * 创建全新的kv对<br>
     * 建议：import static cn.framework.core.utils.Pair.*;<br>
     * 静态引用，简化代码
     *
     * @param k
     * @param v
     *
     * @return
     */
    public static Pair newPair(String k, Object v)
    {
        return new Pair(k, v);
    }

    public Pair(String k, Object v)
    {
        this.key = k;
        this.value = v;
    }

    public String key;

    public Object value;
}
