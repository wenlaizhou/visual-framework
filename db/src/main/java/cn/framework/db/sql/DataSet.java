/**
 * @项目名称: db
 * @文件名称: DataSet.java
 * @Date: 2015年12月21日
 * @author: wenlai
 * @type: DataSet
 */
package cn.framework.db.sql;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import cn.framework.core.log.LogProvider;
import cn.framework.core.pool.ResourceProcessor;
import cn.framework.core.utils.KVMap;
import cn.framework.core.utils.Pair;

/**
 * DataSet包装类<br>
 * 支持直接使用foreach<br>
 * 可以使用两种情况<br>
 * 查询型结果：直接使用get，或foreach来进行行扫描遍历数据<br>
 * 每一行都是一个{@link KVMap}，key为列名，value为列值<br>
 * <br>
 * 非查询型结果：直接使用getNonQueryResult()获取非查询结果值
 * 
 * @author wenlai
 */
public class DataSet extends ArrayList<KVMap> implements Serializable, Closeable {
    
    /**
     * 将{@link DataSet}转换成json字符串<br>
     * <b>慎用，有性能危险</b>
     */
    @Override
    public String toString() {
        if (nonQueryResult > -1) {
            StringBuilder result = new StringBuilder("{data : ");
            result.append(nonQueryResult);
            result.append("}");
            return result.toString();
        }
        StringBuilder result = new StringBuilder("[");
        for (KVMap row : this) {
            StringBuilder rowB = new StringBuilder("{");
            for (Pair pair : row) {
                rowB.append(pair.key).append(":");
                if (pair.value instanceof Object) {
                    if (pair.value != null) {
                        rowB.append("'").append(pair.value).append("',");
                    }
                    else {
                        rowB.append("null,");
                    }
                }
                else {
                    rowB.append(pair.value).append(",");
                }
            }
            rowB = new StringBuilder(rowB.subSequence(0, rowB.length() - 1));
            rowB.append("},");
            result.append(rowB);
            rowB = null;
        }
        if (result.length() > 1)
            result = new StringBuilder(result.subSequence(0, result.length() - 1));
        return result.append("]").toString();
    }
    
    /**
     * 非查询结果
     */
    private int nonQueryResult = -1;
    
    /**
     * 获取第一行元素
     * 
     * @return
     */
    public KVMap selectSingle() {
        if (this.size() > 0) {
            return this.get(0);
        }
        return null;
    }
    
    /**
     * 构造DataSet
     * 
     * @param data
     */
    public DataSet(ResultSet data) {
        super();
        try {
            ResultSetMetaData metaData = data.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (data.next()) {
                KVMap result = new KVMap();
                for (int i = 1; i <= columnCount; i++) {
                    Object columnData = null;
                    switch (metaData.getColumnType(i)) {
                        case Types.TIME :
                        case Types.DATE :
                        case Types.TIMESTAMP :
                        case Types.TIME_WITH_TIMEZONE :
                        case Types.TIMESTAMP_WITH_TIMEZONE :
                            Object time = data.getObject(i);
                            if (time != null)
                                columnData = String.format("%1$tF %1$tT", time);
                            break;
                        default :
                            columnData = data.getObject(i);
                            break;
                    }
                    result.addKV(metaData.getColumnName(i), columnData);
                }
                this.add(result);
            }
            ResourceProcessor.closeResource(data);
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
    }
    
    /**
     * 
     * 获取非查询型执行结果
     * 
     * @return
     */
    public int getNonQueryResult() {
        return this.nonQueryResult;
    }
    
    /**
     * 构造
     * 
     * @param nonQueryResult 非查询型执行结果
     */
    public DataSet(int nonQueryResult) {
        super();
        this.nonQueryResult = nonQueryResult;
    }
    
    /**
     * long
     */
    private static final long serialVersionUID = -3687745194431194109L;
    
    /**
     * @see Closeable#close()
     */
    @Override
    public void close() throws IOException {
        try {
            this.clear();
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
    }
    
}
