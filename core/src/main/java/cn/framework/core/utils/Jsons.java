package cn.framework.core.utils;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import cn.framework.core.log.LogProvider;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * fastjson帮助类
 * 
 * @author wenlai
 */
public final class Jsons {
    
    /**
     * 将JSONObject转换为KVMap
     * 
     * @param data
     * @return
     */
    public static KVMap json2Kv(JSONObject data) {
        KVMap result = new KVMap();
        for (String key : data.keySet())
            result.put(key, data.get(key));
        return result;
    }
    
    /**
     * ResultSet转换成json字符串
     * 
     * @param resultData
     * @return
     */
    public static JSONArray resultSet2JSON(ResultSet resultData) {
        try (ResultSet resultSet = resultData;) {
            JSONArray result = new JSONArray();
            if (resultSet != null) {
                ResultSetMetaData meta = resultSet.getMetaData();
                while (resultSet.next()) {
                    JSONObject data = new JSONObject();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        Object columnData = null;
                        switch (meta.getColumnType(i)) {
                            case Types.TIME :
                            case Types.DATE :
                            case Types.TIMESTAMP :
                            case Types.TIME_WITH_TIMEZONE :
                            case Types.TIMESTAMP_WITH_TIMEZONE :
                                columnData = String.format("%1$tF %1$tT", resultSet.getObject(i));
                                break;
                            default :
                                columnData = resultSet.getObject(i);
                                break;
                        }
                        data.put(meta.getColumnName(i), columnData);
                    }
                    result.add(data);
                }
            }
            return result;
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return null;
    }
    
    /**
     * 判断JSONObject对象是否不为空
     * 
     * @param jsonObject 要判断的对象
     * @return
     */
    public static boolean jsonObjectIsNotNullOrEmpty(JSONObject jsonObject) {
        return jsonObject != null && jsonObject.size() > 0;
    }
    
    /**
     * 将一个json对象转换成字符串，key值没有括号包围
     * 
     * @param jsonObject
     * @return
     */
    public static String toJSONStringNoQuoteFieldNames(JSONObject jsonObject) {
        JSONSerializer serializer = new JSONSerializer();
        serializer.config(SerializerFeature.QuoteFieldNames, false);
        serializer.write(jsonObject);
        return serializer.toString();
    }
    
    /**
     * 将一个对象转换成字符串，key值没有括号包围
     * 
     * @param obj
     * @return
     */
    public static String toJSONStringNoQuoteFieldNames(Object obj) {
        JSONSerializer serializer = new JSONSerializer();
        serializer.config(SerializerFeature.QuoteFieldNames, false);
        serializer.write(obj);
        return serializer.toString();
    }
    
    /**
     * 从channel中读取json数据
     * 
     * @param channel
     * @return if exception: return null
     */
    public static JSONObject read(AsynchronousSocketChannel channel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024 * 8);
            channel.read(buffer).get();
            return JSON.parseObject(new String(buffer.array()));
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
            return null;
        }
    }
}
