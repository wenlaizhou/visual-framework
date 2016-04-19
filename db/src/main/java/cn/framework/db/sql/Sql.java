/**
 * @项目名称: framework
 * @文件名称: Sql.java
 * @Date: 2015年11月6日
 * @author: wenlai
 * @type: Sql
 */
package cn.framework.db.sql;

import cn.framework.core.utils.Strings;

import java.util.List;
import java.util.Map;

/**
 * @author wenlai
 */
public class Sql {

    /**
     * 语句类型
     */
    public SQL_TYPE type;

    /**
     * 是否返回执行id
     */
    public boolean returnId = false;

    /**
     * id，如果未配置sqlid，则使用序号进行id赋值，从0开始
     */
    public String id;

    /**
     * connectionId，如果设置为空，则默认继承procedureId
     */
    public String connectionId = Strings.EMPTY;

    /**
     * sql 语句
     */
    public String sql;

    /**
     * 是否需要缓存
     */
    public boolean cached = false;

    /**
     * 缓存时间
     */
    public int cachedMiliseconds;
    /**
     * 是否是查询
     */
    public boolean isQuery;
    /**
     * 是否具有sql语句中字符替换的参数<br>
     * select * from {param}
     */
    public boolean hasLanguageParam = false;
    /**
     * 是否具有关联sql执行结果参数
     */
    public boolean hasResultParam = false;
    /**
     * 是否具有计算参数
     */
    public boolean hasExpressParam = false;
    /**
     * 参数列表
     */
    List<String> params;
    /**
     * 计算参数
     */
    Map<String, String> expressParams;
    /**
     * sql语句中字符替换的参数
     */
    List<String> languageParams;
    /**
     * 由于参数有同一参数出现多次情况<br>
     * 故出现此参数,用来校验参数数量是否正确
     */
    List<String> realParams;
    /**
     * sql执行结果<br>
     */
    List<SqlResult> sqlResultParams;

    /**
     * sql语句类型
     *
     * @author wenlai
     */
    public static enum SQL_TYPE {
        INSERT, UPDATE, SELECT, DELETE, SHOW
    }

    /**
     * sql执行结果
     *
     * @author wenlai
     */
    public static class SqlResult {

        /**
         * 占位符
         */
        public String placeHolder;

        /**
         * sql id
         */
        public String id;

        /**
         * 索引
         */
        public int index;

        /**
         * 列名称
         */
        public String column;

        /**
         * 是否是update执行结果
         */
        public boolean isNonQueryResult = false;
    }
}
