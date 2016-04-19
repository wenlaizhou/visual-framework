/**
 * @项目名称: framework
 * @文件名称: RemoteConf.java
 * @Date: 2015年6月26日
 * @Copyright: 2015 悦畅科技有限公司. All rights reserved.
 *             注意：本内容仅限于悦畅科技有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package cn.framework.core.utils;

import org.w3c.dom.Node;
import cn.framework.core.log.LogProvider;

/**
 * @author wenlai
 */
public final class ConfigCenter {
    
    /**
     * 从配置中心获取配置
     * 
     * @param host
     * @param port
     * @param password
     * @param path
     * @return
     */
    public static Node get(String host, int port, String password, String path) {
        try {
            String url = String.format("http://%1$s:%2$s", host, port);
            KVMap param = new KVMap();
            if (Strings.isNotNullOrEmpty(password))
                param.addKV("pwd", password);
            param.addKV("path", path);
            return Xmls.document(Requests.get(url, param));
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
            return null;
        }
    }
}
