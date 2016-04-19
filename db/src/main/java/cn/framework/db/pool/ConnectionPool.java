/**
 * @项目名称: framework
 * @文件名称: Connection.java
 * @Date: 2015年11月11日
 * @author: wenlai
 * @type: Connection
 */
package cn.framework.db.pool;

import java.util.HashMap;
import java.util.Map;
import cn.framework.core.log.LogProvider;
import cn.framework.core.pool.Pool;
import cn.framework.core.utils.KVMap;
import cn.framework.db.connection.DbConnection;

/**
 * TODO log
 * 
 * @author wenlai
 */
public final class ConnectionPool extends Pool<DbConnection> {
    
    /**
     * 创建连接池
     * 
     * @param config 参数
     * @throws Exception
     */
    public static void createPool(KVMap config) throws Exception {
        new ConnectionPool(config);
    }
    
    /**
     * 根据配置构造连接池<br>
     * 原始配置如下:{@link Pool#Pool(KVMap)} <br>
     * <table>
     * <tbody>
     * <th>no</th>
     * <th>key</th>
     * <th>description</th>
     * <tr>
     * <td>1</td>
     * <td>url</td>
     * <td>数据库url</td>
     * </tr>
     * <tr>
     * <td>2</td>
     * <td>username</td>
     * <td>用户名</td>
     * </tr>
     * <tr>
     * <td>3</td>
     * <td>pwd</td>
     * <td>密码</td>
     * </tr>
     * </tbody>
     * </table>
     * 
     * @param config 参数
     * @throws Exception
     */
    private ConnectionPool(KVMap config) throws Exception {
        super(config);
        poolContainer.put(id, this);
    }
    
    /*
     * @see cn.etcp.framework.pool.Pool#create()
     */
    @Override
    protected DbConnection create() throws Exception {
        return new DbConnection(this.config.getString("url"), this.config.getString("username"), this.config.getString("pwd"));
    }
    
    /*
     * @see cn.etcp.framework.pool.Pool#initData(java.lang.Object)
     */
    @Override
    protected void activateObject(DbConnection data) {
        data.activate();
    }
    
    /*
     * @see cn.etcp.framework.pool.Pool#checkActive(java.lang.Object)
     */
    @Override
    protected boolean isActive(DbConnection data) {
        return data.checkAlive();
    }
    
    /**
     * 获取连接
     * 
     * @param conn
     * @return
     */
    public static DbConnection getConnection(String conn) {
        try {
            ConnectionPool pool = poolContainer.get(conn);
            return pool != null ? pool.get() : null;
        }
        catch (Exception e) {
            System.out.println("获取数据库连接失败");
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * 获取连接
     * 
     * @param conn
     * @return
     */
    public static DbConnection getConnection(String conn, int timeoutMilisecond) {
        try {
            return poolContainer.get(conn).get(timeoutMilisecond);
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return null;
    }
    
    private static final Map<String, ConnectionPool> poolContainer = new HashMap<String, ConnectionPool>();
}
