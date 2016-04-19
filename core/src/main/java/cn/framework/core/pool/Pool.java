/**
 * @项目名称: framework
 * @说明: framework2.0 新鲜出炉：<br>
 *      理念：1、配置及业务；2、快快快;3、智能;4、可视化<br>
 * @文件名称: Pool.java
 * @Date: 2015年11月6日
 * @author: wenlai
 * @type: Pool
 */
package cn.framework.core.pool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import cn.framework.core.log.LogProvider;
import cn.framework.core.utils.KVMap;
import cn.framework.core.utils.StopWatch;
import static cn.framework.core.log.LogProvider.*;

/**
 * 内存池使用说明：<br>
 * 想存入池中的元素继承自{@link PooledObject}<br>
 * 池本身继承自{@link Pool}<br>
 * 代码如下(针对jdk1.8，使用try---with):<blockquote>
 * try(PooledObject obj = pool.get();) { <br>
 * ....<br>
 * <br>
 * }
 * </blockquote>
 * 池基类
 * TODO log and view
 * 
 * @author wenlai
 */
public abstract class Pool<PooledClass extends PooledObject> {
    
    /**
     * 返回本身
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public Pool<PooledObject> scope() {
        return (Pool<PooledObject>) this;
    }
    
    /**
     * 池日志
     */
    public static Logger POOL_LOGGER = LogProvider.getLogger("pool.framework.info");
    
    /**
     * 真实大小
     */
    public final int realSize;
    
    /**
     * 创建元素
     * 
     * @return
     */
    protected abstract PooledClass create() throws Exception;
    
    /**
     * 激活元素
     * 
     * @param data
     */
    protected abstract void activateObject(PooledClass data);
    
    /**
     * 判断是否存活
     * 
     * @param data
     * @return
     */
    protected abstract boolean isActive(PooledClass data);
    
    /**
     * private costructor function
     * 
     * @param config
     * @param KVMap:id 池id,不允许id重复
     * @param KVMap:name 池名称
     * @param KVMap:size 大小
     * @param KVMap:checkPeriodSecond check元素存活周期
     * @throws Exception 如果id重复，则抛异常
     */
    public Pool(KVMap config) throws Exception {
        StopWatch watch = StopWatch.newWatch();
        POOL_LOGGER.info(String.format("获取配置信息：%1$s，开始创建连接池", config));
        this.config = config;
        this.id = config.get("id").toString();
        if (watchDog.containsKey(this.id))
            throw new Exception("id duplicate");
        this.name = config.getString("name", "unknown");
        this.size = Integer.parseInt(config.get("size").toString());
        this.idleObject = new LinkedBlockingDeque<PooledClass>();
        for (int i = 0; i < this.size; i++) {
            try {
                PooledClass newObj = create();
                newObj.container = scope();
                activateObject(newObj);
                this.idleObject.add(newObj);
            }
            catch (Exception e) {
                LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
                break;
            }
        }
        this.realSize = this.idleObject.size();
        /**
         * 添加池监视
         */
        ThreadPool.addScheduledTaskAndWaitForDone(Task.wrap(name, new Runnable() {
            
            @Override
            public synchronized void run() {
                try {
                    StopWatch watch = StopWatch.newWatch();
                    POOL_LOGGER.info(String.format("开始对池:{name:%1$s, id:%2$s}进行检测", name, id));
                    int minus = size - idleObject.size();
                    if (minus > 1) { // 增补元素
                        POOL_LOGGER.info(String.format("池:{name:%1$s, id:%2$s}缺少%3$s个元素，进行增补", name, id, minus));
                        int i = 0;
                        for (; i < minus; i++) {
                            try {
                                PooledClass newObj = create();
                                newObj.container = scope();
                                activateObject(newObj);
                                idleObject.add(newObj);
                            }
                            catch (Exception e) {
                                LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
                                break;
                            }
                        }
                        POOL_LOGGER.info(String.format("池:{name:%1$s, id:%2$s}增补了%3$s个元素", name, id, i));
                    }
                    for (PooledClass pooledClass : idleObject) { // 监测元素是否存活
                        try {
                            if (!isActive(pooledClass)) {
                                POOL_LOGGER.info(String.format("池:{name:%1$s, id:%2$s}元素校验失败，重新激活元素", name, id));
                                activateObject(pooledClass);
                                POOL_LOGGER.info(String.format("池:{name:%1$s, id:%2$s}激活元素完毕", name, id));
                            }
                        }
                        catch (Exception x) {
                            getFrameworkErrorLogger().error(x.getMessage(), x);
                            idleObject.remove(pooledClass);
                        }
                    }
                    POOL_LOGGER.info(String.format("池:{name:%1$s, id:%2$s}检测完毕，耗时:%3$s毫秒", name, id, watch.checkByOriginal()));
                }
                catch (Exception x) {
                    getFrameworkErrorLogger().error(x.getMessage(), x);
                }
            }
        }), config.getInt("checkPeriodSecond", 900));
        watchDog.put(this.id, this);
        POOL_LOGGER.info(String.format("创建池：{name:%1$s, id:%2$s}完毕，耗时:%3$s毫秒，池大小：%4$s，真实大小：%5$s", this.name, this.id, watch.checkByOriginal(), this.size, this.realSize));
    }
    
    /**
     * 获取可用的元素，如果没有，则会等待
     * 
     * @return element or null
     * @throws Exception
     */
    public PooledClass get() throws Exception {
        return get(-1);
    }
    
    /**
     * 获取可用元素，如果没有，则会根据超时时间进行等待
     * 
     * @param borrowMaxWaitMillis
     * @return element or null
     * @throws Exception
     */
    public PooledClass get(long borrowMaxWaitMillis) throws Exception {
        StopWatch watch = StopWatch.newWatch();
        POOL_LOGGER.info(String.format("从池：{name:%1$s, id:%2$s}中开始获取元素", name, id));
        PooledClass p = null;
        while (p == null)
            p = borrowMaxWaitMillis < 0 ? this.idleObject.takeFirst() : this.idleObject.pollFirst(borrowMaxWaitMillis, TimeUnit.MILLISECONDS);
        POOL_LOGGER.info(String.format("从池：{name:%1$s, id:%2$s}中获取元素成功，共耗时%3$s毫秒", name, id, watch.checkByOriginal()));
        if (!isActive(p))
            activateObject(p);
        return p;
    }
    
    /**
     * 返还元素
     * 
     * @param object
     */
    public void returnObject(PooledClass object) {
        this.idleObject.addLast(object);
        POOL_LOGGER.info(String.format("对池：{name:%1$s, id:%2$s}中返还获取元素,剩余：%3$s", name, id, this.getIdleCount()));
    }
    
    /**
     * 获取可用元素数量
     * 
     * @return
     */
    public int getIdleCount() {
        return this.idleObject.size();
    }
    
    /**
     * 空闲队列<br>
     */
    private LinkedBlockingDeque<PooledClass> idleObject;
    
    /**
     * 池名称
     */
    public final String name;
    
    /**
     * 池大小
     */
    public final int size;
    
    /**
     * 池id
     */
    public final String id;
    
    /**
     * 配置信息
     */
    public final KVMap config;
    
    /**
     * 监视器<b>内部使用</b>
     */
    final static Map<String, Pool<?>> watchDog = new HashMap<String, Pool<?>>();
}
