/**
 * @项目名称: core
 * @文件名称: Conf.java
 * @Date: 2015年11月20日
 * @author: wenlai
 * @type: Conf
 */
package cn.framework.core.utils;

import java.util.HashMap;
import java.util.Iterator;
import cn.framework.core.log.LogProvider;

/**
 * key(String) - value(Object) 工具类<br>
 * 1、优化api<br>
 * 2、可以foreach
 * 
 * @author wenlai
 */
public class KVMap extends HashMap<String, Object> implements Iterable<Pair>, Cloneable {
    
    /**
     * 使用key-value创建KvMap对象
     * 
     * @param key
     * @param value
     * @return
     */
    public static KVMap newKvMap(String key, Object value) {
        return new KVMap(key, value);
    }
    
    /**
     * 获取第n个元素
     * 
     * @param index 从0开始
     * @return
     */
    public <T> T get(int index) {
        return this.get(getIndexedKey(index));
    }
    
    /**
     * 根据key获取任意类型
     * 
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Object result = super.get(key);
        try {
            return result != null ? (T) result : null;
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
            return null;
        }
    }
    
    /**
     * @see HashMap#HashMap()
     */
    public KVMap() {
        super();
    }
    
    /**
     * 根据kv构造
     * 
     * @param key
     * @param value
     */
    public KVMap(String key, Object value) {
        super();
        this.addKV(key, value);
    }
    
    /**
     * long
     */
    private static final long serialVersionUID = 8838593096442964731L;
    
    /**
     * 添加一个kv对
     * 
     * @param key
     * @param value
     * @return
     */
    public KVMap addKV(String key, Object value) {
        this.put(key, value);
        return this;
    }
    
    /**
     * 添加一个kv对
     * 
     * @param key
     * @param value
     * @return
     */
    public KVMap addKV(Object key, Object value) {
        this.put(key == null ? Strings.EMPTY : key.toString(), value);
        return this;
    }
    
    /**
     * 添加一个kv对
     * 
     * @param key
     * @param value
     * @param defaultValue 默认值
     * @return
     */
    public KVMap addKV(String key, Object value, Object defaultValue) {
        this.put(key, value != null ? value : defaultValue);
        return this;
    }
    
    /**
     * 获取string类型的value值
     * 
     * @param key
     * @return
     */
    public String getString(String key) {
        Object value = this.get(key);
        return value == null ? Strings.EMPTY : value.toString();
    }
    
    /**
     * 获取string类型的value值
     * 
     * @param key
     * @param defaultValue 默认值
     * @return
     */
    public String getString(String key, String defaultValue) {
        Object value = this.get(key);
        return value == null ? defaultValue : value.toString();
    }
    
    /**
     * 获取bool类型的value值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = this.get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value.toString());
    }
    
    /**
     * 获取int类型的value值
     * 
     * @param key
     * @return 出错或没值则返回-1
     */
    public int getInt(String key) {
        try {
            Object value = this.get(key);
            return value == null ? -1 : Integer.parseInt(value.toString());
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x);
            return -1;
        }
    }
    
    /**
     * 获取int类型的value值
     * 
     * @param key
     * @return 出错或没值则返回${defaultValue}
     */
    public int getInt(String key, int defaultValue) {
        try {
            Object value = this.get(key);
            return value == null ? defaultValue : Integer.parseInt(value.toString());
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x);
            return defaultValue;
        }
    }
    
    /**
     * 获取特定位置的key值
     * 
     * @param index 很明显，遵从数组索引，从0开始
     * @return
     */
    public String getIndexedKey(int index) {
        if (index > this.size() - 1)
            return Strings.EMPTY;
        int i = 0;
        for (Pair kv : this) {
            if (i == index)
                return kv.key;
            i++;
        }
        return Strings.EMPTY;
    }
    
    // /**
    // * 插入json数据
    // *
    // * @param data
    // * @return
    // */
    // public KVMap addJsonObject(JSONObject data)
    // {
    // if (data != null && data.size() > 0)
    // for (String key : data.keySet())
    // this.addKV(key, data.get(key));
    // return this;
    // }
    
    /*
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<Pair> iterator() {
        return new KVIterator(this);
    }
    
    /*
     * @see java.util.AbstractMap#toString()
     */
    @Override
    public String toString() {
        return Jsons.toJSONStringNoQuoteFieldNames(this);
    }
    
    /**
     * 内部使用类
     * 
     * @author wenlai
     */
    private class KVIterator implements Iterator<Pair> {
        
        public KVIterator(KVMap conf) {
            if (conf != null && conf.size() > 0) {
                this.size = conf.size();
                this.resource = conf;
                this.resourceKeySets = conf.keySet().toArray(new String[0]);
            }
            else {
                this.size = 0;
            }
        }
        
        private int size;
        
        private int index = 0;
        
        private KVMap resource;
        
        private String[] resourceKeySets;
        
        /*
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            boolean res = false;
            if (this.size >= index + 1)
                res = true;
            else
                this.resource = null;
            return res;
        }
        
        /*
         * @see java.util.Iterator#next()
         */
        @Override
        public Pair next() {
            String k = this.resourceKeySets[this.index++];
            Object v = this.resource.get(k);
            return new Pair(k, v);
        }
    }
    
}
