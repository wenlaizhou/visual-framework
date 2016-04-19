package cn.framework.cache.resource;

/**
 * project code
 * package cn.framework.cache.resource
 * create at 16/4/6 下午4:11
 *
 * @author wenlai
 */
public interface EventSubscriber {
    boolean subscriber(CachedEvent event);
}
