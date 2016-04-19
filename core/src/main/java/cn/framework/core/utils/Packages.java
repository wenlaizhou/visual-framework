/**
 * @项目名称: core
 * @文件名称: Packages.java
 * @Date: 2015年12月7日
 * @author: wenlai
 * @type: Packages
 */
package cn.framework.core.utils;

import cn.framework.core.log.LogProvider;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author wenlai
 */
public class Packages {

    /**
     * @param pkg
     *
     * @return classname array
     */
    public static String[] scanClasses(String pkg) {
        ArrayList<String> result = new ArrayList<>();
        String path = pkg.replace(".", "/");
        String path2 = pkg.replace(".", "\\");
        String[] absPaths = scan(pkg, ".class");
        if (absPaths != null && absPaths.length > 0) {
            for (String absPath : absPaths) {
                if (absPath.contains(path)) {
                    String classPath = absPath.substring(absPath.indexOf(path));
                    String className = classPath.replace("/", ".").substring(0, classPath.indexOf(".class"));
                    result.add(className);
                }
                else if (absPath.contains(path2)) {
                    String classPath = absPath.substring(absPath.indexOf(path2));
                    String className = classPath.replace("\\", ".").substring(0, classPath.indexOf(".class"));
                    result.add(className);
                }
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * @param pkg
     *
     * @return
     */
    public static String[] scan(String pkg) {
        return scan(pkg, Strings.EMPTY);
    }

    /**
     * @param pkg
     * @param suffix
     *
     * @return
     */
    public static String[] scan(String pkg, String suffix) {
        ArrayList<String> result = new ArrayList<String>();
        String path = pkg.replace(".", "/");
        boolean hasSuffix = Strings.isNotNullOrEmpty(suffix);
        if (Projects.UNPACK_ENABLED) {
            File file = new File(Projects.UNPACK_DIR + "/" + path);
            if (file.exists()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File subFile : files) {
                        if (hasSuffix) {
                            String absolutePath = subFile.getAbsolutePath();
                            if (hasSuffix) {
                                if (absolutePath.endsWith(suffix)) {
                                    result.add(absolutePath);
                                }
                            }
                            else {
                                result.add(absolutePath);
                            }
                        }
                    }
                }
                return result.toArray(new String[0]);
            }
        }
        else {
            try (JarFile mainJar = new JarFile(Projects.MAIN_JAR_PATH);) {
                Enumeration<JarEntry> enters = mainJar.entries();
                while (enters.hasMoreElements()) {
                    String name = enters.nextElement().getName();
                    if (name.startsWith(path)) {
                        if (hasSuffix) {
                            if (name.endsWith(suffix)) {
                                result.add(name);
                            }
                        }
                        else {
                            result.add(name);
                        }
                    }
                }
                return result.toArray(new String[0]);
            }
            catch (Exception e) {
                LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 加载jar、class或目录
     *
     * @param libPaths 待加载库列表，可以是文件列表也可以是目录列表
     *
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
                        if (Arrays.isNotNullOrEmpty(fileList)) {
                            for (File c : fileList)
                                if (c.getName().endsWith(".class") || c.getName().endsWith(".jar")) {
                                    urlList.add(c.toURI().toURL());
                                }
                        }
                    }
                    else if (file.isFile()) {
                        if (file.getName().endsWith(".class") || file.getName().endsWith(".jar")) {
                            urlList.add(file.toURI().toURL());
                        }
                    }
                }
            }
            if (Arrays.isNotNullOrEmpty(urlList)) {
                URLClassLoader loader = new URLClassLoader(urlList.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
                Thread.currentThread().setContextClassLoader(loader);
            }
        }
    }

    /**
     * 加载jar、class或目录
     *
     * @param libPath 待加载库，可以是文件列表也可以是目录
     *
     * @throws Exception
     */
    public static void load(String libPath) throws Exception {
        if (Strings.isNotNullOrEmpty(libPath)) {
            ArrayList<URL> urlList = new ArrayList<>();
            File file = new File(libPath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] fileList = file.listFiles();
                    if (Arrays.isNotNullOrEmpty(fileList)) {
                        for (File c : fileList)
                            if (c.getName().endsWith(".class") || c.getName().endsWith(".jar")) {
                                //ClassLoader.getSystemResource(c.getName());
                                urlList.add(c.toURI().toURL());
                            }
                    }
                    // urlList.add(c.toURI().toURL());
                }
                else if (file.isFile()) {
                    if (file.getName().endsWith(".class") || file.getName().endsWith(".jar")) {
                        //                        ClassLoader.getSystemResource(file.getName());
                        urlList.add(file.toURI().toURL());
                    }
                }
                // urlList.add(file.toURI().toURL());
            }
            if (Arrays.isNotNullOrEmpty(urlList)) {
                URLClassLoader loader = new URLClassLoader(urlList.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
                Thread.currentThread().setContextClassLoader(loader);
            }
        }
    }

    public static URLClassLoader loadClass(String classPath) throws Exception {
        File clazzPath = new File(classPath);
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        boolean accessible = method.isAccessible();
        try {
            if (accessible == false) {
                method.setAccessible(true);
            }
            // 设置类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

            // 将当前类路径加入到类加载器中
            method.invoke(classLoader, clazzPath.toURI().toURL());
            return classLoader;
        }
        finally {
            method.setAccessible(accessible);
        }
    }

    public static void main(String[] args) throws Exception {
        String packageName = "com.wang.vo.request.hotel";
        // List<String> classNames = getClassName(packageName);
        List<String> classNames = getClassName(packageName, false);
        if (classNames != null) {
            for (String className : classNames) {
                System.out.println(className);
            }
        }
    }

    /**
     * 获取某包下（包括该包的所有子包）所有类
     *
     * @param packageName 包名
     *
     * @return 类的完整名称
     */
    public static List<String> getClassName(String packageName) {
        return getClassName(packageName, true);
    }

    /**
     * 获取某包下所有类
     *
     * @param packageName  包名
     * @param childPackage 是否遍历子包
     *
     * @return 类的完整名称
     */
    public static List<String> getClassName(String packageName, boolean childPackage) {
        List<String> fileNames = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();
            if (type.equals("file")) {
                fileNames = getClassNameByFile(url.getPath(), null, childPackage);
            }
            else if (type.equals("jar")) {
                fileNames = getClassNameByJar(url.getPath(), childPackage);
            }
        }
        else {
            fileNames = getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage);
        }
        return fileNames;
    }

    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath     文件路径
     * @param className    类名集合
     * @param childPackage 是否遍历子包
     *
     * @return 类的完整名称
     */
    private static List<String> getClassNameByFile(String filePath, List<String> className, boolean childPackage) {
        List<String> myClassName = new ArrayList<String>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), myClassName, childPackage));
                }
            }
            else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("\\", ".");
                    myClassName.add(childFilePath);
                }
            }
        }

        return myClassName;
    }

    /**
     * 从jar获取某包下所有类
     *
     * @param jarPath      jar文件路径
     * @param childPackage 是否遍历子包
     *
     * @return 类的完整名称
     */
    private static List<String> getClassNameByJar(String jarPath, boolean childPackage) {
        List<String> myClassName = new ArrayList<String>();
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        String packagePath = jarInfo[1].substring(1);
        try (JarFile jarFile = new JarFile(jarFilePath);) {
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    if (childPackage) {
                        if (entryName.startsWith(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                            myClassName.add(entryName);
                        }
                    }
                    else {
                        int index = entryName.lastIndexOf("/");
                        String myPackagePath;
                        if (index != -1) {
                            myPackagePath = entryName.substring(0, index);
                        }
                        else {
                            myPackagePath = entryName;
                        }
                        if (myPackagePath.equals(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                            myClassName.add(entryName);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return myClassName;
    }

    /**
     * 从所有jar中搜索该包，并获取该包下所有类
     *
     * @param urls         URL集合
     * @param packagePath  包路径
     * @param childPackage 是否遍历子包
     *
     * @return 类的完整名称
     */
    private static List<String> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage) {
        List<String> myClassName = new ArrayList<String>();
        if (urls != null) {
            for (int i = 0; i < urls.length; i++) {
                URL url = urls[i];
                String urlPath = url.getPath();
                // 不必搜索classes文件夹
                if (urlPath.endsWith("classes/")) {
                    continue;
                }
                String jarPath = urlPath + "!/" + packagePath;
                myClassName.addAll(getClassNameByJar(jarPath, childPackage));
            }
        }
        return myClassName;
    }
}
