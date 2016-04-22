package cn.framework.cache.resource;

import cn.framework.cache.init.FrameworkCache;
import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.event.ThreadPoolMonitor;
import cn.framework.core.pool.Task;
import cn.framework.core.pool.ThreadPool;
import cn.framework.core.utils.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.ClassPath;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * project code
 * package cn.framework.cache.resource
 * create at 16/3/30 下午6:52
 *
 * @author wenlai
 */
@Service(CacheQ.BEAN_NAME)
public class CacheQ implements InitProvider {

    public static final String BEAN_NAME = "cacheQ";

    public final static Logger logger = Exceptions.logProcessor().logger("cacheQ");

    private static boolean INITED = false;

    private HashMap<String, String> patterns = new HashMap<>();

    private HashMap<String, String> names = new HashMap<>();

    private EventBus eventBus;

    private Executor executor = ThreadPoolMonitor.add(Executors.newFixedThreadPool(10));

    private com.google.common.cache.Cache<String, CachedEvent> localQueue;

    @Override
    public synchronized void init(Context context) {
        if (INITED) {
            return;
        }
        INITED = true;
        this.eventBus = new AsyncEventBus("cacheQ", this.executor);
        try {
            try {
                Node eventBusNode = Xmls.xpathNode("//event-bus", context.getConf());
                if (eventBusNode != null) {
                    ClassPath.from(Projects.MAIN_CLASS_LOADER).getTopLevelClasses(Xmls.childAttribute("package", "value", eventBusNode)).parallelStream().forEach(classInfo -> { //TODO config pkg name
                        try {
                            Class clazz = Class.forName(classInfo.getName());
                            if (EventSubscriber.class.isAssignableFrom(clazz)) {
                                if (clazz.getDeclaredAnnotation(Pattern.class) != null) {
                                    String pattern = ((Pattern) clazz.getDeclaredAnnotation(Pattern.class)).value();
                                    String beanName = Springs.getBeanName(clazz);
                                    System.out.println(Strings.format("register event bus subscriber : pattern : ${pattern}, beanName : ${bean}", Pair.newPair("pattern", pattern), Pair.newPair("bean", beanName)));
                                    this.patterns.put(pattern, beanName);
                                }
                                if (clazz.getDeclaredAnnotation(Name.class) != null) {
                                    String name = ((Name) clazz.getDeclaredAnnotation(Name.class)).value();
                                    String beanName = Springs.getBeanName(clazz);
                                    System.out.println(Strings.format("register event bus subscriber : name : ${name}, beanName : ${bean}", Pair.newPair("name", name), Pair.newPair("bean", beanName)));
                                    this.names.put(name, beanName);
                                }
                            }
                        }
                        catch (Exception x) {
                            Exceptions.processException(x);
                        }
                    });
                }
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }

            this.localQueue = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(20 * 60 * 1000, TimeUnit.MILLISECONDS).removalListener(new RemovalListener<String, CachedEvent>() {
                @Override
                public void onRemoval(RemovalNotification<String, CachedEvent> notification) {
                    logger.info("local cache remove : {}", notification.getValue());
                }
            }).build();

            ThreadPool.addScheduledTaskAndWaitForDone(Task.wrap("cacheQscanner", () -> {
                //launch ehcache
                //launch localcache
                try {
                    FrameworkCache handler = Springs.get(FrameworkCache.BEAN_NAME);
                    Cache cache;
                    if (handler != null && (cache = handler.getCache(CacheQ.BEAN_NAME)) != null) {
                        cache.getKeys().forEach(k -> {
                            Element e = cache.get(k);
                            if (e != null && e.getObjectValue() instanceof CachedEvent) {
                                logger.info("重发未被订阅消息", e.getObjectValue());
                                this.directPut((CachedEvent) e.getObjectValue());
                            }
                        });
                    }
                }
                catch (Exception x) {
                    Exceptions.processException(x);
                }
                try {
                    this.localQueue.asMap().forEach((k, v) -> {
                        logger.info("重发未被订阅消息", v);
                        this.directPut(v);
                    });
                }
                catch (Exception x) {
                    Exceptions.processException(x);
                }

            }), 60000);
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        finally {
            this.eventBus.register(this);
        }
    }

    /**
     * 获取监控数据
     *
     * @return k-id v-CachedEvent
     */
    public Map<String, CachedEvent> monitor() {
        return this.localQueue.asMap();
    }

    /**
     * 直接投递消息
     *
     * @param message
     */
    void directPut(CachedEvent message) {
        this.eventBus.post(message);
    }

    /**
     * 投递延迟消息
     *
     * @param message
     * @param delaySeconds
     */
    void directPut(CachedEvent message, int delaySeconds) {
        ThreadPool.addDelayTask(Task.wrap("cacheQ-delay-event", () -> {
            directPut(message);
        }), delaySeconds);
    }

    /**
     * 入队
     *
     * @param message
     */
    public void enQ(CachedEvent message) {
        if (message == null) {
            logger.error("get null message!");
            return;
        }
        try {
            logger.info("en queue {}", message);
            FrameworkCache handler = Springs.get(FrameworkCache.BEAN_NAME);
            Cache cache;
            if (handler != null && (cache = handler.getCache(CacheQ.BEAN_NAME)) != null) {
                cache.put(new Element(message.getId(), message));
            }
            else {
                this.localQueue.put(message.getId(), message);
                directPut(message);
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
    }

    /**
     * 消息路由
     *
     * @param message
     *
     * @throws Exception
     */
    @Subscribe
    @AllowConcurrentEvents
    public void router(CachedEvent message) throws Exception {

        if (Strings.isNotNullOrEmpty(message.getTo())) {
            String bean = this.names.get(message.getTo());
            if (Strings.isNotNullOrEmpty(bean)) {
                logger.info("get subscriber : {} {}", bean, message);
                sendEvent(message, bean);
            }
        }

        this.patterns.keySet().forEach(pattern -> {
            if (Regexs.test(pattern, message.getTitle())) {
                logger.info("get subscriber : {} {}", pattern, message);
                ThreadPool.addTask(Task.wrap("event-bus", () -> {
                    try {
                        sendEvent(message, this.patterns.get(pattern));
                    }
                    catch (Exception x) {
                        Exceptions.processException(x);
                    }
                }));
            }
        });
    }

    private void sendEvent(CachedEvent message, String bean) {
        EventSubscriber subscriber = Springs.get(bean);
        if (subscriber != null) {
            try {
                subscriber.subscriber(message);
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }
            finally {
                try {
                    FrameworkCache handler = Springs.get(FrameworkCache.BEAN_NAME);
                    Cache cache;
                    if (handler != null && (cache = handler.getCache(CacheQ.BEAN_NAME)) != null) {
                        cache.remove(message.getId());
                    }
                }
                catch (Exception x) {
                    Exceptions.processException(x);
                }
                finally {
                    this.localQueue.invalidate(message.getId());
                }
            }
        }
    }
}
