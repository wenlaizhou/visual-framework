/**
 * @项目名称: framework2
 * @文件名称: ServiceRegister.java
 * @Date: 2015年10月9日
 */
package cn.framework.rest.register;

import cn.framework.core.log.LogProvider;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Projects;
import com.google.common.reflect.ClassPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author wenlai
 */
public class ServiceRegister extends ResourceConfig { // PackagesResourceConfig

    private static String[] PACKAGES = null;

    /**
     * 注入配置信息
     */
    public ServiceRegister() {
        try {
            this.setClassLoader(Projects.MAIN_CLASS_LOADER);
            this.register(MultiPartFeature.class);
            this.register(JacksonFeature.class);
            // if (PACKAGES != null)
            // this.packages(PACKAGES);
            if (PACKAGES != null && PACKAGES.length > 0) {
                for (String pkg : PACKAGES) {
                    ClassPath cp = ClassPath.from(Projects.MAIN_CLASS_LOADER);
                    cp.getTopLevelClassesRecursive(pkg).forEach(info -> {
                        try {
                            this.register(Class.forName(info.getName()));
                        }
                        catch (Exception x) {
                            Exceptions.processException(x);
                        }
                    });
                }
            }
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getLocalizedMessage(), x);
        }
    }
}
