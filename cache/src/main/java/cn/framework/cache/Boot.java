/**
 * @项目名称: cache
 * @文件名称: Boot.java
 * @Date: 2015年11月21日
 * @author: wenlai
 * @type: Boot
 */
package cn.framework.cache;

/**
 * @author wenlai
 */
public class Boot {

    /**
     * 应用入口，测试使用
     *
     * @param args
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        //        RMICacheManagerPeerProviderFactory factory = new RMICacheManagerPeerProviderFactory();
        //
        //
        //        JedisPool pool = new JedisPool("");
        //
        //        JedisCluster jedisCluster = new JedisCluster(null, 1);


        //        FrameworkStart.START(args);

        /**
         * 伙伴发现 peerProvider
         */
        // 手动发现 :
        //        <cacheManagerPeerProviderFactory
        //        class="net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory"
        //        properties="peerDiscovery=manual,
        //        rmiUrls=//server2:40001/sampleCache11|//server2:40001/sampleCache12"/>
        //                The following is the configuration required for server2:
        //        <cacheManagerPeerProviderFactory
        //        class="net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory"
        //        properties="peerDiscovery=manual,
        //        rmiUrls=//server1:40001/sampleCache11|//server1:40001/sampleCache12"/>
        // 多播发现 :
        //<cacheManagerPeerProviderFactory
        //class="net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory"
        //properties="peerDiscovery=automatic, multicastGroupAddress=230.0.0.1,
        //multicastGroupPort=4446, timeToLive=32"/>

        /**
         * 监听配置
         */
        //        <cacheManagerPeerListenerFactory
        //        class="net.sf.ehcache.distribution.RMICacheManagerPeerListenerFactory"
        //        properties="hostName=localhost, port=40001,
        //        socketTimeoutMillis=2000"/>

        //        CacheManager
        //        The CacheManager class is used to manage caches. Creation of, access to, and removal of caches is controlled by a named CacheManager.

        //        CacheManager Creation Modes
        //        CacheManager supports two creation modes: singleton and instance. The two types can exist in the same JVM. However, multiple CacheManagers with the same name are not allowed to exist in the same JVM. CacheManager() constructors creating non-Singleton CacheManagers can violate this rule, causing a NullPointerException. If your code might create multiple CacheManagers of the same name in the same JVM, avoid this error by using the static CacheManager.create() methods, which always return the named (or default unnamed) CacheManager if it already exists in that JVM. If the named (or default unnamed) CacheManager does not exist, the CacheManager.create() methods create it.
        //                For singletons, calling CacheManager.create(...) returns the existing singleton CacheManager with the configured name (if it exists) or creates the singleton based on the passed-in configuration.
        //                To work from configuration, use the CacheManager.newInstance(...) method, which parses the passed-in configuration to either get the existing named CacheManager or create that CacheManager if it doesn't exist.
        //        To review, the behavior of the CacheManager creation methods is as follows:
        //        *CacheManager.newInstance(Configuration configuration) – Create a new CacheManager or return the existing one named in the configuration.
        //        *CacheManager.create() – Create a new singleton CacheManager with default configuration, or return the existing singleton. This is the same as CacheManager.getInstance().
        //                *CacheManager.create(Configuration configuration) – Create a singleton CacheManager with the passed-in configuration, or return the existing singleton.
        //        *new CacheManager(Configuration configuration) – Create a new CacheManager, or throw an exception if the CacheManager named in the configuration already exists or if the parameter (configuration) is null.
        //                Note that in instance-mode (non-singleton), where multiple CacheManagers can be created and used concurrently in the same JVM, each CacheManager requires its own configuration.
        //                If the Caches under management use the disk store, the disk-store path specified in each CacheManager configuration should be unique. This is because when a new CacheManager is created, a check is made to ensure that no other CacheManagers are using the same disk-store path. Depending upon your persistence strategy, Ehcache will automatically resolve a disk-store path conflict, or it will let you know that you must explicitly configure the disk-store path.
        //                If managed caches use only the memory store, there are no special considerations.

        //        Cache
        //        A Cache is a thread-safe logical representation of a set of data elements, analogous to a cache region in many caching systems. Once a reference to a cache is obtained (through a CacheManager), logical actions can be performed. The physical implementation of these actions is relegated to the stores. For more information about the stores, see "Configuring Storage Tiers" in the Configuration Guide for Ehcache.
        //                Caches are instantiated from configuration or programmatically using one of the Cache() constructors. Certain cache characteristics, such as Automatic Resource Control (ARC)-related sizing, and pinning, must be set using configuration.
        //        Cache methods can be used to get information about the cache (for example, getCacheManager(), isNodeBulkLoadEnabled(), and isSearchable()), or perform certain cache-wide operations (for example, flush, load, initialize, and dispose).
        //        The methods provided in the Cache class also allow you to work with cache elements (for example, get, set, remove, and replace) as well as get information about the them (for example, isExpired, isPinned).

        //        Element
        //        An element is an atomic entry in a cache. It has a key, a value, and a record of accesses. Elements are put into and removed from caches. They can also expire and be removed by the cache, depending on the cache settings.
        //        There is an API for Objects in addition to the one for Serializable. Non-serializable Objects can be stored only in heap. If an attempt is made to persist them, they are discarded with a DEBUG-level log message but no error.
        //                The APIs are identical except for the return methods from Element: getKeyValue() and getObjectValue() are used by the Object API in place of getKey() and getValue().

        //        CacheManager manager = CacheManager.newInstance();
        //        The following creates a CacheManager based on a specified configuration file.
        //                CacheManager manager = CacheManager.newInstance("src/config/ehcache.xml");

        //        CacheManager manager = CacheManager.create();
        //        Cache testCache = new Cache(
        //                new CacheConfiguration("testCache", maxEntriesLocalHeap)
        //                        .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
        //                        .eternal(false)
        //                        .timeToLiveSeconds(60)
        //                        .timeToIdleSeconds(30)
        //                        .diskExpiryThreadIntervalSeconds(0)
        //                        .persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));
        //        manager.addCache(testCache);

        //        CacheManager.getInstance().shutdown();
        //        The following shuts down a CacheManager instance, assuming you have a reference to the CacheManager called manager:
        //        manager.shutdown();


        //        Implementing a CacheManager Event Listener Factory and CacheManager Event Listener
        //        CacheManagerEventListenerFactory is an abstract factory for creating CacheManager listeners. Implementers should provide their own concrete factory extending this abstract factory. It can then be configured in ehcache.xml.
        //                The factory class needs to be a concrete subclass of the abstract factory CacheManagerEventListenerFactory, which is reproduced below:
        //        /**
        //         * An abstract factory for creating {@link CacheManagerEventListener}s.
        //         * Implementers should provide their own concrete factory extending this
        //         * factory. It can then be configured in ehcache.xml.
        //         *
        //         */
        //        public abstract class CacheManagerEventListenerFactory {
        //
        //        }

        //        cache.getCacheEventNotificationService().registerListener(myListener);

        //        Declarative Configuration
        //        To configure an exception handler declaratively, add the cacheExceptionHandlerFactory element to ehcache.xml as shown in the following example:
        //        <cache ...>
        //        <cacheExceptionHandlerFactory
        //        class="net.sf.ehcache.exceptionhandler.CountingExceptionHandlerFactory"
        //        properties="logLevel=FINE"/>
        //        </cache>

        //        CacheManager cacheManager = ...
        //        Ehcache cache = cacheManger.getCache("exampleCache");
        //        ExceptionHandler handler = new ExampleExceptionHandler(...);
        //        cache.setCacheLoader(handler);
        //        Ehcache proxiedCache = ExceptionHandlingDynamicCacheProxy.createProxy(cache);
        //        cacheManager.replaceCacheWithDecoratedCache(cache, proxiedCache);

        //
        //        Built-in Decorators
        //                BlockingCache
        //        This is a Blocking decorator for Ehcache that allows concurrent read access to elements already in the cache. If the element is null, other reads will block until an element with the same key is put into the cache. This decorator is useful for constructing read-through or self-populating caches. BlockingCache is used by CachingFilter.
        //                SelfPopulatingCache
        //        A self-populating decorator for Ehcache that creates entries on demand. Clients of the cache simply call it without needing knowledge of whether the entry exists in the cache. If null, the entry is created. The cache is designed to be refreshed. Refreshes operate on the backing cache, and do not degrade performance of get calls.
        //        SelfPopulatingCache extends BlockingCache. Multiple threads attempting to access a null element will block until the first thread completes. If refresh is being called the threads do not block - they return the stale data. This is very useful for engineering highly scalable systems.
        //        Caches with Exception Handling
        //        Caches with exception handlers are decorated. For information about adding an exception handler to a cache, see Cache Exception Handlers.


        //        <cacheDecoratorFactory
        //        class="com.company.SomethingCacheDecoratorFactory"
        //        properties="property1=36 ..." />

        //        cacheManager.replaceCacheWithDecoratedCache(cache, newBlockingCache);

        //        Cache extensions are a general-purpose mechanism to allow generic extensions to a cache. Cache extensions are tied into the cache lifecycle. For that reason, this interface has the lifecycle methods.
        //        Cache extensions are created using the CacheExtensionFactory, which has a createCacheCacheExtension() method that takes as a parameter a Cache and properties. It can thus call back into any public method on Cache, including, of course, the load methods. Cache extensions are suitable for timing services, where you want to create a timer to perform cache operations. (Another way of adding Cache behavior is to decorate a cache. For an example of, Blocking and Self Populating Caches.)
        //        Because a CacheExtension holds a reference to a Cache, the CacheExtension can do things such as registering a CacheEventListener or even a CacheManagerEventListener, all from within a CacheExtension, creating more opportunities for customization.

        //        <cache ...>
        //        <cacheExtensionFactory
        //        class="com.example.FileWatchingCacheRefresherExtensionFactory"
        //        properties="refreshIntervalMillis=18000, loaderTimeout=3000,
        //        flushPeriod=whatever, someOtherProperty=someValue ..."/>
        //                </cache>

        //        TestCacheExtension testCacheExtension = new TestCacheExtension(cache, ...);
        //        testCacheExtension.init();
        //        cache.registerCacheExtension(testCacheExtension);

        //        Cache Eviction Algorithms

        //        Built-in Memory Store Eviction Algorithms
        //        Least Recently Used (LRU) -Dnet.sf.ehcache.use.classic.lru=true
        //        Least Frequently Used (LFU)
        //        First In First Out (FIFO)

        //        JConsole Example
        //        This example shows how to register CacheStatistics in the JDK platform MBeanServer, which works with the JConsole management agent.
        //        CacheManager manager = new CacheManager();
        //        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        //        ManagementService.registerMBeans(manager, mBeanServer, false, false, false,
        //                true);
        //        ehcache offical code
        //        final CacheManager cacheManager = new CacheManager();
        //
        //        // create the cache called "hello-world"
        //        final Cache cache = cacheManager.getCache("hello-world");
        //
        //        Ehcache ehcache = null;
        //
        //        // create a key to map the data to
        //        final String key = "greeting";
        //
        //        // Create a data element
        //        final Element putGreeting = new Element(key, "Hello, World!");
        //
        //        // Put the element into the data store
        //        cache.put(putGreeting);
        //
        //        // Retrieve the data element
        //        final Element getGreeting = cache.get(key);
        //
        //        // Print the valu e
        //        System.out.println(getGreeting.getObjectValue());

        //        Special System Properties
        //        net.sf.ehcache.disabled
        //        Setting this system property to true (using java -Dnet.sf.ehcache.disabled=true in the Java command line) disables caching in ehcache. If disabled, no elements can be added to a cache (puts are silently discarded).
        //                net.sf.ehcache.use.classic.lru
        //        When LRU is selected as the eviction policy, set this system property to true (using java -Dnet.sf.ehcache.use.classic.lru=true in the Java command line) to use the older LruMemoryStore implementation. This is provided for ease of migration.
        //        System.out.println(Projects.CACHE_DIR);
        //

        //        EhCacheProvider pro = Springs.get(EhCacheProvider.BEAN_NAME);
        //        pro.init(null);
        //
        //        Cache test = pro.getManager().getCache("session");
        //
        //        //        pro.getManager().shutdown();
        //
        //        //        Results results = test.createQuery().includeKeys().addCriteria(Query.KEY.ilike("sessionA:*")).execute();
        //
        //        System.out.println(test.getStatistics());
        //
        //        System.out.println(pro.getCache(CacheQ.BEAN_NAME).getStatistics());
        //
        //        Springs.getContext();
        //
        //
        //        CachedEvent event = new CachedEvent();
        //        event.setFrom("main");
        //        event.setMessage("hello");
        //        event.setTitle("hello me");
        //
        //        Springs.get(CacheQ.BEAN_NAME, CacheQ.class).enQ(event);


    }

}