package cn.framework.core.utils;

/**
 * project code
 * package cn.framework.core.utils
 * create at 16-3-11 下午3:31
 *
 * @author wenlai
 */
public class KVPair<K, V> {

    private K key;

    private V value;

    public KVPair(K key, V value) {
        this.key(key);
        this.value(value);
    }

    public void key(K key) {
        this.key = key;
    }

    public K key() {
        return this.key;
    }

    public void value(V value) {
        this.value = value;
    }

    public V value() {
        return this.value;
    }
}
