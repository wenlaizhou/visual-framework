package cn.framework.core.utils;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import cn.framework.core.log.LogProvider;

/**
 * 反射帮助类
 * 
 * @author wenlai
 */
public final class Reflects {
    
    public static void scan() {
        
    }
    
    /**
     * 设置字段值
     * 
     * @param className 类名称
     * @param fieldName 字段名称
     * @param value 设置的字段值
     * @param instance 要设置字段的类对象
     */
    public static boolean setField(String className, String fieldName, Object value, Object instance) {
        try {
            Field field = Class.forName(className).getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
            return true;
        }
        catch (Throwable x) {
            Exceptions.processException(x);
        }
        return false;
    }
    
    /**
     * 调用静态方法
     * 
     * @param className
     * @param methodName
     * @param params
     * @return
     */
    public static Object invokeStatic(String className, String methodName, Object... params) {
        try {
            Method method = Class.forName(className).getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(null, params);
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return null;
    }
    
    /**
     * 调用方法
     * 
     * @param className
     * @param methodName
     * @param obj
     * @param params
     * @return
     */
    public static Object invoke(String className, String methodName, Class<?>[] parameterTypes, Object obj, Object... params) {
        try {
            Method method = Class.forName(className).getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, params);
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return null;
    }
    
    /**
     * 调用方法
     * 
     * @param className
     * @param methodName
     * @param obj
     * @return
     */
    public static Object invoke(String className, String methodName, Object obj) {
        try {
            Method method = Class.forName(className).getMethod(methodName);
            method.setAccessible(true);
            return method.invoke(obj);
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T createInstance(String className) {
        try {
            return (T) Class.forName(className).getConstructor().newInstance();
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return null;
    }
    
    /** (1) 文件过滤器，过滤掉不需要的文件 **/
    private static FileFilter fileFilter = new FileFilter() {
        
        @Override
        public boolean accept(File pathname) {
            return isClass(pathname.getName()) || isDirectory(pathname) || isJarFile(pathname.getName());
        }
    };
    
    private static void ckeckNullPackageName(String packageName) {
        if (packageName == null || packageName.trim().length() == 0)
            throw new NullPointerException("packageName can't be null");
    }
    
    private static String getWellFormedPackageName(String packageName) {
        return packageName.lastIndexOf(".") != packageName.length() - 1 ? packageName + "." : packageName;
    }
    
    public static List<Class<?>> scanPackage(String packageName, ClassFilter classFilter) {
        // 检测packageName 是否为空，如果为空就抛出NullPointException
        ckeckNullPackageName(packageName);
        // 实例化一个篮子 P: 放置class
        final List<Class<?>> classes = new ArrayList<Class<?>>();
        // 遍历在classpath 下面的jar包，class文件夹(现在没有包括 java jre)
        // System.getProperty("java.home"); 不包括 jre
        for (String classPath : System.getProperty("java.class.path").split(System.getProperty("path.separator"))) {
            // 填充 classes
            fillClasses(new File(classPath), getWellFormedPackageName(packageName), classFilter, classes);
        }
        return classes;
    }
    
    private static void fillClasses(File file, String packageName, ClassFilter classFilter, List<Class<?>> classes) {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles(fileFilter)) {
                fillClasses(subFile, packageName, classFilter, classes);
            }
        }
        else if (file.getName().endsWith(".class")) {
            final String filePathWithDot = file.getAbsolutePath().replace(File.separator, ".");
            int subIndex = -1;
            if ((subIndex = filePathWithDot.indexOf(packageName)) != -1) {
                final String className = filePathWithDot.substring(subIndex).replace(".class", Strings.EMPTY);
                fillClass(className, packageName, classes, classFilter);
            }
        }
        else if (file.getName().endsWith(".jar")) {
            try {
                for (ZipEntry entry : Collections.list(new ZipFile(file).entries())) {
                    if (isClass(entry.getName())) {
                        final String className = entry.getName().replace("/", ".").replace(".class", Strings.EMPTY);
                        fillClass(className, packageName, classes, classFilter);
                    }
                }
            }
            catch (Exception ex) {
            }
        }
    }
    
    private static void fillClass(String className, String packageName, List<Class<?>> classes, ClassFilter classFilter) {
        if (checkClassName(className, packageName)) {
            try {
                final Class<?> clazz = Class.forName(className, Boolean.FALSE, Reflects.class.getClassLoader());
                if (checkClassFilter(classFilter, clazz)) {
                    classes.add(clazz);
                }
            }
            catch (ClassNotFoundException ex) {
            }
        }
    }
    
    private static boolean checkClassName(String className, String packageName) {
        return className.indexOf(packageName) == 0;
    }
    
    private static boolean checkClassFilter(ClassFilter classFilter, Class<?> clazz) {
        return classFilter == null || classFilter.accept(clazz);
    }
    
    private static boolean isClass(String fileName) {
        return fileName.endsWith(".class");
    }
    
    private static boolean isDirectory(File file) {
        return file.isDirectory();
    }
    
    private static boolean isJarFile(String fileName) {
        return fileName.endsWith(".jar");
    }
    
    public interface ClassFilter {
        
        boolean accept(Class<?> clazz);
    }
    
}
