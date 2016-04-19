/**
 * @项目名称: framework
 * @文件名称: Jsps.java
 * @Date: 2015年10月26日
 * @author: wenlai
 * @type: Jsps
 */
package cn.framework.core.utils;

import java.io.InputStream;
import org.apache.jasper.JspC;
import cn.framework.core.log.LogProvider;

/**
 * @author wenlai
 *
 */
public final class Jsps {
    
    /**
     * 编译制定目录下的jsp文件
     * 
     * @param outputDir 输出目录
     * @param jspDir jsp文件所在目录
     * @param pkg jsp包名
     */
    public static void compile(String outputDir, String jspDir, String pkg) {
        JspC jsp = new JspC();
        jsp.setCompile(true);
        jsp.setClassDebugInfo(true);
        jsp.setBlockExternal(true);
        jsp.setOutputDir(outputDir);
        jsp.setUriroot(jspDir);
        jsp.setPackage(pkg);
        jsp.execute();
    }
    
    /**
     * 编译ClassLoader下的jsp文件
     * 
     * @param outputDir 输出目录
     * @param pkg jsp包名
     */
    public static void compile(String outputDir, String pkg) {
        Files.createDirectory(outputDir);
        String[] jsps = Packages.scan(pkg, "jsp");
        String pkgPath = pkg.replace(".", "/");
        String jspDir = new StringBuilder(outputDir).append("/").append(pkgPath).toString();
        Files.createDirectory(jspDir);
        if (jsps != null && jsps.length > 0) {
            for (String jspPath : jsps) {
                jspPath = Files.getName(jspPath);
                try (InputStream stream = ClassLoader.getSystemResourceAsStream(new StringBuilder(pkgPath).append("/").append(jspPath).toString());) {
                    if (stream != null && stream.available() > 0) {
                        byte[] data = new byte[stream.available()];
                        stream.read(data);
                        Files.write(new StringBuilder(jspDir).append("/").append(Files.getName(jspPath)).toString(), data);
                    }
                }
                catch (Exception e) {
                    LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
                }
            }
            
            compile(outputDir, jspDir, pkg);
        }
    }
}
