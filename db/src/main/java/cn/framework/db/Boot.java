/**
 * @项目名称: db
 * @文件名称: Boot.java
 * @Date: 2015年11月18日
 * @author: wenlai
 * @type: Boot
 */
package cn.framework.db;

import cn.framework.core.utils.Md5s;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author wenlai
 */
public class Boot {

    //    private static DataSource dataSourceInstance = null;

    /**
     * 应用入口，测试使用
     *
     * @param args 启动参数
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {


        System.out.println(Md5s.compute("asd" + new Date()));


        System.out.println(UUID.randomUUID());
        System.out.println(UUID.randomUUID().toString().length());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        /*
            Date and Time Pattern
            Result
            "yyyy.MM.dd G 'at' HH:mm:ss z"
            2001.07.04 AD at 12:08:56 PDT
            "EEE, MMM d, ''yy"
            Wed, Jul 4, '01
            "h:mm a"
            12:08 PM
            "hh 'o''clock' a, zzzz"
            12 o'clock PM, Pacific Daylight Time
            "K:mm a, z"
            0:08 PM, PDT
            "yyyyy.MMMMM.dd GGG hh:mm aaa"
            02001.July.04 AD 12:08 PM
            "EEE, d MMM yyyy HH:mm:ss Z"
            Wed, 4 Jul 2001 12:08:56 -0700
            "yyMMddHHmmssZ"
            010704120856-0700
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
            2001-07-04T12:08:56.235-0700
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
            2001-07-04T12:08:56.235-07:00
            "YYYY-'W'ww-u"
            2001-W27-3
         */
        System.out.println(format.format(new Date()));


        //
        //
        //
        //
        //        //        Springs b = Springs.getInstance("/Users/junrayz/Documents/code/db/spring.xml");
        //        //        System.out.println(b);
        //        //        DruidDataSource ds = Springs.get("dataSource");
        //        //        System.out.println(ds);
        //        //        ApplicationContext context = new FileSystemXmlApplicationContext("file:/Users/junrayz/Documents/code/db/spring.xml");
        //        //        DruidDataSource ds = context.getBean("dataSource", DruidDataSource.class);
        //        //        PreparedStatement ps = ds.getConnection().prepareStatement("select now();");
        //        //        ResultSet rs = ps.executeQuery();
        //        //        while(rs.next()) {
        //        //            System.out.println(rs.getObject(1));
        //        //        }
        //
        //        //        FrameworkContainer tomcat = new FrameworkContainer(Projects.CONF_DIR + "/druid.xml");
        //        //        tomcat.init();
        //        //        tomcat.start();
        //
        //        File file = new File(Projects.WORK_DIR + "/berkley/");
        //        if (!file.exists()) {
        //            Files.createDirectories(Paths.get(file.getPath()));
        //        }
        //
        //        EnvironmentConfig envConfig = new EnvironmentConfig();
        //        envConfig.setAllowCreate(true);
        //        envConfig.setTransactional(true);
        //        // Identify the node
        //        ReplicationConfig repConfig = new ReplicationConfig();
        //        repConfig.setGroupName("wenlai-group");
        //        repConfig.setNodeName("Mercury");
        //        repConfig.setNodeHostPort("localhost:5001");
        //        // This is the first node, so its helper is itself
        //        repConfig.setHelperHosts("localhost:5002");
        //        //        DbEnableReplication replication = new DbEnableReplication(file, "wenlai-group", "wenlai-node1", "localhost:5003");
        //        //        replication.convert();
        //        //        ReplicatedEnvironment repEnv = new ReplicatedEnvironment(file, repConfig, envConfig);
        //        Environment env = new Environment(file, envConfig);
        //        DatabaseConfig config = new DatabaseConfig();
        //        config.setAllowCreate(true);
        //        config.setAllowCreateVoid(true);
        //        config.setCacheMode(CacheMode.DEFAULT);
        //        config.setReplicated(true);
        //        config.setReadOnly(false);
        //        config.setDeferredWrite(true);
        //
        //        //        TransactionConfig tnc = new TransactionConfig(); // 事务级别
        //        //        repEnv.beginTransaction(null, tnc);
        //
        //        env.beginTransaction(null, null);
        //        Database db = env.openDatabase(null, "wenlai", config);
        //        for (int i = 0; i < 100000; i++) {
        //
        //            DatabaseEntry key = new DatabaseEntry();
        //            key.setData(("wenlai-test" + i).getBytes(Charsets.UTF_8));
        //
        //            DatabaseEntry value = new DatabaseEntry(Integer.toString(i).getBytes(Charsets.UTF_8));
        //            db.put(null, key, value);
        //            key = value = null;
        //        }
        //        db.sync();
        //
        //        //        StoredClassCatalog table = new StoredClassCatalog(db);
        //        //        EntryBinding bind = new SerialBinding<>(table, Object.class);
        //        //        StringBinding.stringToEntry("", new DatabaseEntry());
        //
        //                /* Create a new, transactional database environment */
        //
        //        //        File file = new File(Projects.WORK_DIR + "/berkley");
        //        //        if (!file.exists()) {
        //        //            Files.createDirectory(Paths.get(file.getPath()));
        //        //        }
        //        //        EnvironmentConfig envConfig = new EnvironmentConfig();
        //        //        envConfig.setTransactional(true);
        //        //        envConfig.setAllowCreate(true);
        //        //        Environment exampleEnv = new Environment(file, envConfig);
        //        //
        //        //        /* Make a database within that environment */
        //        //        Transaction txn = exampleEnv.beginTransaction(null, null);
        //        //        DatabaseConfig dbConfig = new DatabaseConfig();
        //        //        dbConfig.setTransactional(true);
        //        //        dbConfig.setAllowCreate(true);
        //        //        dbConfig.setSortedDuplicates(true);
        //        //        Database exampleDb = exampleEnv.openDatabase(txn, "bindingsDb", dbConfig);
        //        //
        //        //        /*
        //        //         * In our example, the database record is composed of an integer
        //        //         * key and and instance of the MyData class as data.
        //        //         *
        //        //         * A class catalog database is needed for storing class descriptions
        //        //         * for the serial binding used below.  This avoids storing class
        //        //         * descriptions redundantly in each record.
        //        //         */
        //        //        DatabaseConfig catalogConfig = new DatabaseConfig();
        //        //        catalogConfig.setTransactional(true);
        //        //        catalogConfig.setAllowCreate(true);
        //        //        Database catalogDb = exampleEnv.openDatabase(txn, "catalogDb", catalogConfig);
        //        //        StoredClassCatalog catalog = new StoredClassCatalog(catalogDb);
        //        //
        //        //        /*
        //        //         * Create a serial binding for MyData data objects.  Serial bindings
        //        //         * can be used to store any Serializable object.
        //        //         */
        //        //        EntryBinding dataBinding = new SerialBinding(catalog, Object.class);
        //        //
        //        //        txn.commit();
        //        //
        //        //
        //        //        /*
        //        //         * Further below we'll use a tuple binding (IntegerBinding
        //        //         * specifically) for integer keys.  Tuples, unlike serialized Java
        //        //         * objects, have a well defined sort order.
        //        //         */
        //        //
        //        //        /* DatabaseEntry represents the key and data of each record */
        //        //        DatabaseEntry keyEntry = new DatabaseEntry();
        //        //        DatabaseEntry dataEntry = new DatabaseEntry();
        //        //
        //        //        if (false) {
        //        //
        //        //                    /* put some data in */
        //        //            for (int i = 0; i < 20; i++) {
        //        //
        //        //                StringBuilder stars = new StringBuilder();
        //        //                for (int j = 0; j < i; j++) {
        //        //                    stars.append('*');
        //        //                }
        //        //                //                MyData data = new MyData(i, stars.toString());
        //        //
        //        //                IntegerBinding.intToEntry(i, keyEntry);
        //        //                dataBinding.objectToEntry(i, dataEntry);
        //        //
        //        //                txn = exampleEnv.beginTransaction(null, null);
        //        //                OperationStatus status = exampleDb.put(txn, keyEntry, dataEntry);
        //        //
        //        //                        /*
        //        //                         * Note that put will throw a DatabaseException when
        //        //                         * error conditions are found such as deadlock.
        //        //                         * However, the status return conveys a variety of
        //        //                         * information. For example, the put might succeed,
        //        //                         * or it might not succeed if the record exists
        //        //                         * and duplicates were not
        //        //                         */
        //        //                if (status != OperationStatus.SUCCESS) {
        //        //                    throw new RuntimeException("Data insertion got status " + status);
        //        //                }
        //        //                txn.commit();
        //        //            }
        //        //        }
        //        //        else {
        //        //
        //        //                    /* retrieve the data */
        //        //            Cursor cursor = exampleDb.openCursor(null, null);
        //        //
        //        //            while (cursor.getNext(keyEntry, dataEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
        //        //
        //        //                int key = IntegerBinding.entryToInt(keyEntry);
        //        //                Object data = dataBinding.entryToObject(dataEntry);
        //        //
        //        //                System.out.println("key=" + key + " data=" + data);
        //        //            }
        //        //            cursor.close();
        //        //        }
        //        //
        //        //        catalogDb.close();
        //        //        exampleDb.close();
        //        //        exampleEnv.close();
        //        //    }
        //
        //        //    public static DataSource getDatasource() {
        //        //        if (dataSourceInstance != null) {
        //        //            return dataSourceInstance;
        //        //        }
        //        //        synchronized (Boot.class) {
        //        //            if (dataSourceInstance == null) {
        //        //                try {
        //        //                    Property.set("url", "jdbc:mysql://101.201.211.1:3336/hr");
        //        //                    Property.set("username", "root");
        //        //                    Property.set("password", "Etcp2012@Etcp2012");
        //        //                    Property.set("filters", "stat,log4j");
        //        //                    DruidDataSource dataSource = new DruidDataSource();
        //        //                    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        //        //                    dataSource.setUsername("root");
        //        //                    dataSource.setPassword("Etcp2012@Etcp2012");
        //        //                    dataSource.setUrl("jdbc:mysql://101.201.211.1:3336/hr");
        //        //                    dataSource.setInitialSize(5);
        //        //                    dataSource.setMinIdle(1);
        //        //                    dataSource.setMaxActive(10);
        //        //                    dataSource.setFilters("stat.log4j");// 启用监控统计功能
        //        //                    dataSource.setPoolPreparedStatements(false); // for mysql
        //        //                    dataSourceInstance = dataSource;
        //        //                }
        //        //                catch (Exception x) {
        //        //                    Exceptions.processException(x);
        //        //                }
        //        //            }
        //        //        }
        //        //        return dataSourceInstance;
        //        //    }
        //
        //        //    public static class Myinitor implements WebApplicationInitializer {
        //        //
        //        //        @Override
        //        //        public void onStartup(ServletContext servletContext) throws ServletException {
        //        //            System.out.println("init on start up");
        //        //            AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        //        //            context.setConfigLocations("file:" + Projects.CONF_DIR + "/spring.xml");
        //        //            System.out.println(context.getBean("dataSource"));
        //        //        }
        //        //
        //        //
        //        ////        public void init() {
        //        ////            System.out.println("init");
        //        ////        }
        //        //
        //        //        public static void init () {
        //        //            System.out.println("init static");
        //        //        }
        //        //
    }
}
