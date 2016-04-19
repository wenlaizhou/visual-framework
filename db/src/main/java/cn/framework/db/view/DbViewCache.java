package cn.framework.db.view;

import cn.framework.core.utils.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * project code
 * package cn.framework.db.view
 * create at 16/3/24 上午11:15
 *
 * @author wenlai
 */
@Repository("dBViewCache")
public class DbViewCache {


    private Cache<String, String> cacheInstance;

    @PostConstruct
    public void init() {
        CacheBuilder builder = CacheBuilder.newBuilder();
        builder.maximumSize(1000);
        builder.expireAfterWrite(5, TimeUnit.SECONDS);
        builder.expireAfterAccess(5, TimeUnit.SECONDS);
        this.cacheInstance = builder.build();
    }

    /**
     * 是否存在
     *
     * @param key key
     *
     * @return
     */
    public boolean exist(String key) {
        String value = this.cacheInstance.getIfPresent(key);
        return Strings.isNotNullOrEmpty(value);
    }

    /**
     * 存入缓存
     *
     * @param key   key
     * @param value value
     */
    public void put(String key, String value) {
        this.cacheInstance.put(key, value);
    }

    /**
     * 获取缓存
     *
     * @param key key
     *
     * @return
     */
    public String get(String key) {
        return this.cacheInstance.getIfPresent(key);
    }

}
