package cn.framework.core.utils;

import com.google.common.base.Charsets;
import com.google.common.reflect.ClassPath;
import org.apache.jasper.JspC;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * project code
 * package cn.framework.core.utils
 * create at 16/4/5 下午2:40
 *
 * @author wenlai
 */
public class Compiler {
    private Compiler() {

    }


    /**
     * 动态加载类
     *
     * @param classRootDir class文件根目录
     *
     * @return
     */
    public static ClassLoader load(String classRootDir) {
        return load(classRootDir, null);
    }

    /**
     * 动态加载类
     *
     * @param classRootDir class文件根目录
     * @param parent       父classloader
     *
     * @return
     */
    public static ClassLoader load(String classRootDir, ClassLoader parent) {

        if (Files.exist(classRootDir) && java.nio.file.Files.isDirectory(Paths.get(classRootDir))) {
            try {
                if (parent != null) {
                    return URLClassLoader.newInstance(new URL[]{new File(classRootDir).toURI().toURL()}, parent);
                }
                else {
                    return URLClassLoader.newInstance(new URL[]{new File(classRootDir).toURI().toURL()});
                }
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }
        }
        return null;

    }

    /**
     * 初始化Classloader中的全部jsp文件 <br>
     * 目录为resource.dir
     *
     * @return
     *
     * @throws Exception
     */
    public static ArrayList<Class<?>> initViews(ClassLoader loader) throws Exception {

        loader = loader != null ? loader : Thread.currentThread().getContextClassLoader();

        ClassPath cp = ClassPath.from(loader);

        HashMap<String, String> pkgs = new HashMap<>();

        Stream<ClassPath.ResourceInfo> jsps = cp.getResources().parallelStream().filter((res) -> res.getResourceName().contains(".jsp"));

        for (ClassPath.ResourceInfo jsp : jsps.toArray(ClassPath.ResourceInfo[]::new)) {
            int jspFilenamePos = jsp.getResourceName().lastIndexOf("/");
            String pkgPath = jsp.getResourceName().substring(0, jspFilenamePos);
            if (!Files.exist(Projects.RESOURCE_DIR + "/" + pkgPath)) {
                Files.createDirectory(Projects.RESOURCE_DIR + "/" + pkgPath);
            }
            Files.write(Projects.RESOURCE_DIR + "/" + jsp.getResourceName(), Files.readResource(jsp.getResourceName(), loader));
            pkgs.put(pkgPath, Strings.EMPTY);
        }

        for (String pkg : pkgs.keySet()) {
            Compiler.compileJsp(Projects.RESOURCE_DIR, pkg.replace("/", "."));
        }

        ClassLoader newLoader = load(Projects.RESOURCE_DIR);

        ArrayList<Class<?>> result = new ArrayList<>();

        for (String pkg : pkgs.keySet()) {
            ClassPath.from(newLoader).getTopLevelClasses(pkg.replace("/", ".")).parallelStream().forEach((element) -> {
                result.add(element.load());
            });
        }

        return result;
    }

    /**
     * 初始化Classloader中的全部jsp文件 <br>
     * 目录为resource.dir
     *
     * @param className 根据类名获取 classloader
     *
     * @return
     *
     * @throws Exception
     */
    public static ArrayList<Class<?>> initViews(String className) throws Exception {
        return initViews(Class.forName(className).getClassLoader());
    }

    /**
     * 编译jsp文件根目录
     *
     * @param dir dir
     * @param pkg pakage xxx.xxxx.xx
     */
    public static void compileJsp(String dir, String pkg) {

        JspC compiler = new JspC();

        compiler.setPackage(pkg);
        compiler.setOutputDir(dir);
        compiler.setUriroot(dir + "/" + pkg.replace(".", "/"));
        compiler.setCompile(true);
        compiler.setJavaEncoding(Charsets.UTF_8.name());

        compiler.execute();
    }
}
