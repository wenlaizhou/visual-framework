/**
 * @项目名称: framework
 * @文件名称: Connection.java
 * @Date: 2015年11月11日
 * @author: wenlai
 * @type: Connection
 */
package cn.framework.db.connection;

import cn.framework.core.log.LogProvider;
import cn.framework.core.pool.PooledObject;
import cn.framework.core.utils.StopWatch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 数据库连接
 *
 * @author wenlai
 */
public class DbConnection extends PooledObject {

    /**
     * 连接handler
     */
    private Connection conn;
    /**
     * url
     */
    private String url;
    /**
     * username
     */
    private String user;
    /**
     * password
     */
    private String pwd;

    /**
     * 构造
     *
     * @param url  访问url
     * @param user 用户
     * @param pwd  密码
     *
     * @throws Throwable
     */
    public DbConnection(String url, String user, String pwd) throws Exception {
        try {
            LogProvider.getFrameworkInfoLogger().info("创建连接{} , {}, {}", url, user, pwd);
            this.url = url;
            this.user = user;
            this.pwd = pwd;
            this.conn = DriverManager.getConnection(this.url, this.user, this.pwd);
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取当前连接
     *
     * @return
     */
    public final Connection getConnection() {
        return this.conn;
    }

    /**
     * 开始事务
     *
     * @throws Exception
     */
    public void startTransaction() throws Exception {
        this.conn.setAutoCommit(false);
    }

    /**
     * 结束事务
     *
     * @return
     */
    public boolean endTransaction() {
        try {
            if (!this.conn.getAutoCommit()) {
                this.conn.commit();
            }
            return true;
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
            rollback();
            return false;
        }
        finally {
            try {
                this.conn.setAutoCommit(true);
            }
            catch (Exception x) {
                LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
            }
        }
    }

    /**
     * 回滚
     */
    public void rollback() {
        try {
            if (!this.conn.getAutoCommit()) {
                this.conn.rollback();
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
    }

    /**
     * check是否连接存活
     *
     * @return
     */
    public boolean checkAlive() {
        StopWatch watch = StopWatch.newWatch();
        try {
            if (!(!conn.isClosed() && conn.isValid(0))) {
                return false;
            }
            try (Statement statement = this.conn.createStatement();) {
                return statement.execute("select now();");
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
            return false;
        }
        finally {
            LogProvider.getLogger("database").info(String.format("调用健康检查完毕，共耗时：%1$s", watch.checkByLastTime()));
        }
    }

    /**
     * 激活当前连接
     */
    public synchronized void activate() {
        StopWatch watch = StopWatch.newWatch();
        boolean result = false;
        try {

            if (!checkAlive()) {
                LogProvider.getFrameworkInfoLogger().info("开始重新激活连接");
                try {
                    this.conn.close();
                }
                catch (Exception e) {
                    LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
                }
                this.conn = DriverManager.getConnection(this.url, this.user, this.pwd);
                try (Statement statement = this.conn.createStatement();) {
                    result = statement.execute("select now();");
                }
            }
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        finally {
            LogProvider.getLogger("database").info(String.format("激活完毕，共耗时：%1$s，激活结果：%2$s", watch.checkByLastTime(), result));
        }
    }
}
