package cn.framework.core.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import cn.framework.core.log.LogProvider;
import static cn.framework.core.utils.Maps.*;

/**
 * http请求帮助类
 * 
 * @author wenlai
 */
public final class Requests {
    
    /**
     * a标签获取href地址
     */
    public static String A_HREF_REGEX = "<\\s*[aA]\\s+href=[\\\'\\\"](.*?)[\\\'\\\"].*/\\s*>";
    
    /**
     * 返回
     * 
     * @param content
     * @return
     */
    public static String[] getHrefs(String content) {
        return Regexs.match(A_HREF_REGEX, content);
    }
    
    /**
     * 发送get请求
     * 
     * @param url
     * @return
     */
    public static String get(String url) {
        return get(url, null, null, -1);
    }
    
    /**
     * 带超时的get请求
     * 
     * @param url
     * @param timeout 单位毫秒
     * @return
     */
    public static String get(String url, int timeout) {
        return get(url, null, null, timeout);
    }
    
    /**
     * 发送get请求
     * 
     * @param url
     * @param data
     * @return
     */
    public static String get(String url, KVMap data) {
        return get(url, data, null, -1);
    }
    
    /**
     * 发送带超时get请求
     * 
     * @param url
     * @param data
     * @param timeout
     * @return
     */
    public static String get(String url, KVMap data, int timeout) {
        return get(url, data, null, timeout);
    }
    
    /**
     * get请求
     * 
     * @param url
     * @param data
     * @param header 请求头
     * @return
     */
    public static String get(String url, KVMap data, KVMap header) {
        return get(url, data, header, -1);
    }
    
    /**
     * 发送get请求
     * 
     * @param url
     * @param data
     * @param header
     * @param timeout
     * @return
     */
    public static String get(String url, KVMap data, KVMap header, int timeout) {
        try {
            if (Arrays.isNotNullOrEmpty(data)) {
                StringBuilder query = new StringBuilder(String.format("?%1$s=%2$s", firstKey(data), firstValue(data)));
                for (int i = 1; i < data.keySet().size(); i++)
                    query.append(String.format("&%1$s=%2$s", indexKey(data, i), indexValue(data, i)));
                url = url.concat(query.toString());
            }
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(false);// get请求不需要将内容写入http正文中
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            if (timeout > 0)
                connection.setReadTimeout(timeout);
            if (Arrays.isNotNullOrEmpty(header))
                for (String key : header.keySet())
                    connection.setRequestProperty(key, header.getString(key));
            StringBuilder resultBuilder = new StringBuilder();
            try (InputStreamReader inputReader = new InputStreamReader(connection.getInputStream()); BufferedReader reader = new BufferedReader(inputReader);) {
                String line = Strings.EMPTY;
                while ((line = reader.readLine()) != null)
                    resultBuilder.append(line).append("\n");
            }
            return resultBuilder.toString();
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return Strings.EMPTY;
    }
    
    /**
     * 发送post请求
     * 
     * @param url
     * @param header
     * @param body
     * @return
     */
    public static String post(String url, KVMap header, String body) {
        return post(url, header, body, -1);
    }
    
    /**
     * 发送post请求
     * 
     * @param url 请求地址
     * @param header 请求头
     * @param body 请求体
     * @param timeout 超时时间 单位毫秒
     */
    public static String post(String url, KVMap header, String body, int timeout) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            if (timeout > 0)
                connection.setReadTimeout(timeout);
            if (Arrays.isNotNullOrEmpty(header))
                for (String key : header.keySet())
                    connection.setRequestProperty(key, header.getString(key));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(body);
            writer.flush();
            StringBuilder resultBuilder = new StringBuilder();
            try (InputStreamReader inputReader = new InputStreamReader(connection.getInputStream()); BufferedReader reader = new BufferedReader(inputReader);) {
                String line = Strings.EMPTY;
                while ((line = reader.readLine()) != null)
                    resultBuilder.append(line).append("\n");
            }
            return resultBuilder.toString();
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return Strings.EMPTY;
    }
    
    /**
     * Url decode
     * 
     * @param param
     * @param charset
     * @return
     */
    public static String decode(String param, String charset) throws Exception {
        return URLDecoder.decode(param, charset);
    }
    
    /**
     * Url encode
     * 
     * @param param
     * @param charset
     */
    public static String encode(String param, String charset) throws Exception {
        return URLEncoder.encode(param, charset);
    }
    
    /**
     * 根据请求头解析出文件名
     * 请求头的格式：火狐和google浏览器下：form-data; name="file"; filename="snmp4j--api.zip"
     * IE浏览器下：form-data; name="file"; filename="E:\snmp4j--api.zip"
     * 
     * @param contentDisposition 请求头
     * @return 文件名
     */
    public static String getFileName(String contentDisposition) {
        try {
            /**
             * String[] tempArr1 = header.split(";");代码执行完之后，在不同的浏览器下，tempArr1数组里面的内容稍有区别
             * 火狐或者google浏览器下：tempArr1={form-data,name="file",filename="snmp4j--api.zip"}
             * IE浏览器下：tempArr1={form-data,name="file",filename="E:\snmp4j--api.zip"}
             */
            String[] fileInfo = contentDisposition.split(";");
            /**
             * 火狐或者google浏览器下：tempArr2={filename,"snmp4j--api.zip"}
             * IE浏览器下：tempArr2={filename,"E:\snmp4j--api.zip"}
             */
            String[] originFilename = fileInfo[2].split("=");
            // 获取文件名，兼容各种浏览器的写法
            return originFilename[1].substring(originFilename[1].lastIndexOf("\\") + 1).replaceAll("\"", "");
        }
        catch (Exception x) {
            Exceptions.processException(x);
            return Strings.EMPTY;
        }
    }
}
