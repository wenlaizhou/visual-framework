package cn.framework.core.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import cn.framework.core.log.LogProvider;

/**
 * 数据库帮助使用类
 * 提供简单的，实时关闭连接的插入、查询、修改数据库操作
 * 
 * @author wenlai
 */
public final class Dbs {
    
    /**
     * 返回无结果的查询
     * 
     * @param url 访问数据库的url
     * @param username 访问用户名
     * @param pwd 访问密码
     * @param sql sql语句
     * @param params sql参数值
     * @return 返回是否执行成功
     */
    public static boolean executeNonQuery(final String url, final String username, final String pwd, final String sql, final Object... params) {
        try (Connection executeConnection = DriverManager.getConnection(url, username, pwd); PreparedStatement sqlStatement = executeConnection.prepareStatement(sql);) {
            return setSqlParams(sqlStatement, params).execute();
        }
        catch (Throwable e) {
            LogProvider.getFrameworkErrorLogger().error(e);
            return false;
        }
    }
    
    /**
     * 返回查询集合，使用try-with管理资源
     * 
     * @param url 访问数据库的url
     * @param username 访问用户名
     * @param pwd 访问密码
     * @param sql sql语句
     * @param params sql参数值
     * @return 返回符合try-with格式资源
     */
    @SuppressWarnings("resource")
    public static ResultResource executeResultSet(final String url, final String username, final String pwd, final String sql, final Object... params) {
        try {
            Connection executeConnection = DriverManager.getConnection(url, username, pwd);
            PreparedStatement sqlStatement = executeConnection.prepareStatement(sql);
            return new ResultResource().setConn(executeConnection).setState(sqlStatement).setResult(setSqlParams(sqlStatement, params).executeQuery());
        }
        catch (Throwable e) {
            LogProvider.getFrameworkErrorLogger().error(e);
            return null;
        }
    }
    
    /**
     * 修改及插入查询
     * 
     * @param url 访问数据库的url
     * @param username 访问用户名
     * @param pwd 访问密码
     * @param sql sql语句
     * @param params sql参数值
     * @return 有效行数
     */
    public static int executeUpdate(final String url, final String username, final String pwd, final String sql, final Object... params) {
        try (Connection executeConnection = DriverManager.getConnection(url, username, pwd); PreparedStatement sqlStatement = executeConnection.prepareStatement(sql);) {
            return setSqlParams(sqlStatement, params).executeUpdate();
        }
        catch (Throwable e) {
            LogProvider.getFrameworkErrorLogger().error(e);
            return -1;
        }
    }
    
    /**
     * 设置PreparedStatement带?的参数
     * 
     * @param queryState 附加sql查询状态容器
     * @param params 要设定的参数
     * @throws SQLException 设置值异常
     */
    private static PreparedStatement setSqlParams(final PreparedStatement queryState, final Object... params) throws SQLException {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++)
                queryState.setObject(i + 1, params[i]);
        }
        return queryState;
    }
    
    /**
     * 符合try-with资源管理的ResultSet容器
     * 
     * @author wenlai
     */
    public static class ResultResource implements AutoCloseable {
        
        /**
         * 容器内部ResultSet
         */
        public ResultSet Result;
        
        /**
         * 容器内部保存的链接
         */
        private Connection Dbconnection;
        
        /**
         * 容器内部的sql状态
         */
        private PreparedStatement State;
        
        /**
         * 设置链接
         * 
         * @param conn
         *        return {@link ResultResource}
         */
        public ResultResource setConn(Connection conn) {
            this.Dbconnection = conn;
            return this;
        }
        
        /**
         * 设置sql状态
         * 
         * @param state
         * @return {@link ResultResource}
         */
        public ResultResource setState(PreparedStatement state) {
            this.State = state;
            return this;
        }
        
        /**
         * 设置Result值
         * 
         * @param result
         * @return {@link ResultResource}
         */
        public ResultResource setResult(ResultSet result) {
            this.Result = result;
            return this;
        }
        
        /**
         * 执行回收动作
         */
        @Override
        public void close() throws Exception {
            if (this.Result != null)
                this.Result.close();
            if (this.State != null)
                this.State.close();
            if (this.Dbconnection != null)
                this.Dbconnection.close();
        }
    }
}
