/**
 * @项目名称: framework
 * @文件名称: Https.java
 * @Date: 2015年10月13日
 * @author: wenlai
 * @type: Https
 */
package cn.framework.core.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import cn.framework.core.log.LogProvider;

/**
 * @author wenlai
 */
public class Https {
    
    /**
     * https请求服务接口
     * 
     * @param method 请求方式
     * @param url 请求地址
     * @param header 请求头
     * @param body 请求内容
     * @return 获取结果
     */
    public static String request(METHOD method, String url, KVMap header, byte[] body) {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setRequestMethod(method.name());
            conn.setDoOutput(true);
            if (header != null)
                for (Pair param : header)
                    conn.setRequestProperty(param.key, param.value.toString());
            conn.connect();
            if (body != null && body.length > 0)
                conn.getOutputStream().write(body);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream());) {
                if (reader != null) {
                    try (BufferedReader bufferedReader = new BufferedReader(reader);) {
                        if (bufferedReader != null) {
                            String line;
                            StringBuilder result = new StringBuilder();
                            while ((line = bufferedReader.readLine()) != null)
                                result.append("\n").append(line);
                            return result.toString();
                        }
                    }
                }
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return Strings.EMPTY;
    }
    
    /**
     * 内部使用
     * 
     * @author wenlai
     */
    private static class TrustAnyTrustManager implements X509TrustManager {
        
        /*
         * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
         */
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
            
        }
        
        /*
         * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
         */
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
            
        }
        
        /*
         * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
         */
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
    
    /**
     * 内部使用
     * 
     * @author wenlai
     */
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    
    /**
     * 请求方法
     * 
     * @author wenlai
     */
    public enum METHOD {
        /**
         * get
         */
        GET,
        /**
         * post
         */
        POST
    }
}
