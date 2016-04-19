/**
 * @项目名称: framework
 * @文件名称: Packages.java
 * @Date: 2015年6月18日
 */
package cn.framework.rest.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import cn.framework.core.log.LogProvider;
import cn.framework.core.utils.Arrays;
import cn.framework.rest.tag.*;
import cn.framework.rest.utils.Packages.ServicePathContainer.*;

/**
 * @author wenlai
 *
 */
public final class Packages {
    
    /**
     * 加载jar、class或目录
     * 
     * @param libPaths 待加载库列表，可以是文件列表也可以是目录列表
     * @throws Exception
     */
    public static void load(String[] libPaths) throws Exception {
        if (Arrays.isNotNullOrEmpty(libPaths)) {
            ArrayList<URL> urlList = new ArrayList<URL>();
            for (String p : libPaths) {
                File file = new File(p);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        File[] fileList = file.listFiles();
                        if (Arrays.isNotNullOrEmpty(fileList))
                            for (File c : fileList)
                                if (c.getName().endsWith(".class") || c.getName().endsWith(".jar"))
                                    urlList.add(c.toURI().toURL());
                    }
                    else if (file.isFile())
                        if (file.getName().endsWith(".class") || file.getName().endsWith(".jar"))
                            urlList.add(file.toURI().toURL());
                }
            }
            if (Arrays.isNotNullOrEmpty(urlList)) {
                URLClassLoader loader = new URLClassLoader(urlList.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
                Thread.currentThread().setContextClassLoader(loader);
            }
        }
    }
    
    /**
     * 扫描包，并将包中的Rest服务信息全部提取出来
     * 
     * @param packageName
     * @return
     */
    public static Map<String, ServicePathContainer> scanServicePackage(String packageName) {
        System.out.println("start scan :" + packageName);
        Map<String, ServicePathContainer> result = new HashMap<String, ServicePathContainer>();
        try {
            String[] classes = cn.framework.core.utils.Packages.scanClasses(packageName);
            if (Arrays.isNotNullOrEmpty(classes)) {
                for (String className : classes) {
                    System.out.println("get classname : " + className);
                    String classShortName = className;
                    Class<?> clazz = Class.forName(className);
                    if (clazz != null) {
                        Path path = clazz.getDeclaredAnnotation(Path.class);
                        if (path != null) {
                            ServicePathContainer container = new ServicePathContainer();
                            container.methods = new HashMap<String, ServicePathContainer.ServiceDescription>();
                            container.className = className;
                            container.classShortName = classShortName;
                            container.classServicePath = path.value();
                            Description classD = clazz.getDeclaredAnnotation(Description.class);
                            if (classD != null)
                                container.description = classD.value();
                            Method[] methods = clazz.getDeclaredMethods();
                            if (Arrays.isNotNullOrEmpty(methods)) {
                                for (Method method : methods) {
                                    Path methodPath = method.getDeclaredAnnotation(Path.class);
                                    if (methodPath != null) {
                                        ServicePathContainer.ServiceDescription methodDescription = new ServicePathContainer.ServiceDescription();
                                        methodDescription.serviceName = method.getName();
                                        methodDescription.path = methodPath.value();
                                        methodDescription.method = method.getDeclaredAnnotation(POST.class) != null ? METHOD.POST : METHOD.GET;
                                        Description methodD = method.getDeclaredAnnotation(Description.class);
                                        if (methodD != null)
                                            methodDescription.description = methodD.value();
                                        Example methodE = method.getDeclaredAnnotation(Example.class);
                                        if (methodE != null)
                                            methodDescription.example = methodE.value();
                                        ArrayList<ServicePathContainer.ServiceParam> paramList = new ArrayList<ServicePathContainer.ServiceParam>();
                                        Parameter[] params = method.getParameters();
                                        if (Arrays.isNotNullOrEmpty(params)) {
                                            for (Parameter parameter : params) {
                                                QueryParam queryParam = parameter.getDeclaredAnnotation(QueryParam.class);
                                                PathParam pathParam = parameter.getDeclaredAnnotation(PathParam.class);
                                                FormParam formParam = parameter.getDeclaredAnnotation(FormParam.class);
                                                ServicePathContainer.ServiceParam serviceParam = new ServicePathContainer.ServiceParam();
                                                serviceParam.name = parameter.getName();
                                                Description paramD = parameter.getDeclaredAnnotation(Description.class);
                                                if (paramD != null)
                                                    serviceParam.description = paramD.value();
                                                if (queryParam != null) {
                                                    serviceParam.type = PARAM_TYPE.Query;
                                                    serviceParam.paramName = queryParam.value();
                                                    paramList.add(serviceParam);
                                                }
                                                else if (pathParam != null) {
                                                    serviceParam.type = PARAM_TYPE.Path;
                                                    serviceParam.paramName = pathParam.value();
                                                    paramList.add(serviceParam);
                                                }
                                                else if (formParam != null) {
                                                    serviceParam.type = PARAM_TYPE.Form;
                                                    serviceParam.paramName = formParam.value();
                                                    paramList.add(serviceParam);
                                                }
                                            }
                                            methodDescription.params = paramList.toArray(new ServicePathContainer.ServiceParam[0]);
                                        }
                                        container.methods.put(methodDescription.serviceName, methodDescription);
                                    }
                                }
                            }
                            result.put(container.classShortName, container);
                        }
                    }
                }
            }
            
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return result;
    }
    
    /**
     * 服务类VO
     * 
     * @author wenlai
     */
    public static class ServicePathContainer {
        
        public String className;
        
        public String classShortName;
        
        public String description;
        
        public String classServicePath;
        
        public Map<String, ServiceDescription> methods;
        
        /**
         * 服务描述VO
         * 
         * @author wenlai
         */
        public static class ServiceDescription {
            
            public String serviceName;
            
            public String description;
            
            public String example;
            
            public String path;
            
            /**
             * get<br>
             * post
             */
            public METHOD method;
            
            /**
             * 参数列表
             */
            public ServiceParam[] params;
        }
        
        public static class ServiceParam {
            
            public String name;
            
            public String paramName;
            
            public String description;
            
            /**
             * query<br>
             * path
             */
            public PARAM_TYPE type;
        }
        
        public static enum PARAM_TYPE {
            Query, Path, Form
        }
        
        public static enum METHOD {
            POST, GET
        }
        
    }
}
