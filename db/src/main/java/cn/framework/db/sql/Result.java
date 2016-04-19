/**
 * @项目名称: framework
 * @文件名称: Result.java
 * @Date: 2015年11月6日
 * @author: wenlai
 * @type: Result
 */
package cn.framework.db.sql;

import cn.framework.core.utils.Jsons;
import cn.framework.core.utils.KVMap;
import com.alibaba.fastjson.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;

/**
 * data为DB结果集<br>
 * key - 执行的sqlid <br>
 * value : {@link DataSet}<br>
 * success ：执行过程中是否有异常，<br>
 * 多个sql中其中任意一条执行异常，即为false
 *
 * @author wenlai
 */
public class Result implements Closeable {

    /**
     * 返回结构化数据：<br>
     * key - sqlid <br>
     * value : {@link DataSet} <br>
     */
    public KVMap data;

    /**
     * 执行过程中是否有异常，<br>
     * 多个sql中其中任意一条执行异常，即为false
     */
    public boolean success;

    /**
     * message,结果集消息
     */
    public String message;

    /**
     * 构建结果集
     */
    public Result() {
        this.success = true;
        this.data = new KVMap();
    }

    /**
     * 设置是否成功
     *
     * @param success
     *
     * @return
     */
    public Result setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    /**
     * 设置data，并返回this指针
     *
     * @param id
     * @param rs
     *
     * @return
     */
    public Result addData(String id, ResultSet rs) {
        this.data.addKV(id, new DataSet(rs));
        return this;
    }

    /**
     * 设置data，并返回this指针
     *
     * @param id
     * @param updateCount update影响行数
     *
     * @return
     */
    public Result addData(String id, int updateCount) {
        this.data.addKV(id, new DataSet(updateCount));
        return this;
    }

    /**
     * 向result中添加任意数据
     *
     * @param id
     * @param data
     *
     * @return
     */
    public Result addData(String id, Object data) {
        this.data.addKV(id, data);
        return this;
    }

    /**
     * 设置message，并返回this
     *
     * @param message
     *
     * @return
     */
    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 将结果集转换成json格式数据，<b>慎用，耗时较长</b>
     */
    @Override
    public String toString() {
        JSONObject res = new JSONObject();
        res.put("result", success);
        res.put("message", message);
        res.put("data", data);
        return Jsons.toJSONStringNoQuoteFieldNames(res);
    }

    /*
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        // if (this.data != null && !this.data.isEmpty()) {
        // for (Pair pair : this.data) {
        // try {
        // if (pair.value instanceof DataSet)
        // ((DataSet) pair.value).close();
        // }
        // catch (Exception x) {
        // LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        // }
        // }
        // }
    }
}
