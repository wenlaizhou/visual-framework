/**
 * @项目名称: core
 * @文件名称: Bootstrap.java
 * @Date: 2015年11月18日
 * @author: wenlai
 * @type: Bootstrap
 */
package cn.framework.core;


/**
 * @author wenlai
 */
public class Bootstrap {

    /**
     * 应用入口<br>
     * core 
     * java.util.logging.config.file<br>
     * java.util.logging.manager
     *
     * @param args 输入参数
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args != null && args.length > 0) {
            FrameworkStart.START(args);
        }
        else {
            FrameworkStart.START(new String[]{"-h"});
        }
    }

}
