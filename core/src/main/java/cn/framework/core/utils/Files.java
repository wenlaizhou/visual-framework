package cn.framework.core.utils;

import cn.framework.core.log.LogProvider;
import com.google.common.base.Charsets;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Scanner;

/**
 * nio文件帮助类
 *
 * @author wenlai
 */
public final class Files {

    /**
     * 读取文件或资源文本
     *
     * @param path
     * @param charset
     * @param classLoader
     *
     * @return
     */
    public static String readFileOrResourceText(String path, Charset charset, ClassLoader classLoader) throws Exception {
        return new String(readFileOrResource(path, classLoader), charset);
    }

    /**
     * 读取文件或资源文本
     *
     * @param path
     *
     * @return
     *
     * @throws Exception
     */
    public static String readFileOrResourceText(String path) throws Exception {
        return readFileOrResourceText(path, Charsets.UTF_8, Projects.MAIN_CLASS_LOADER);
    }

    /**
     * 读取文件或资源
     *
     * @param path        路径
     * @param classLoader classLoader
     *
     * @return
     *
     * @throws Exception
     */
    public static byte[] readFileOrResource(String path, ClassLoader classLoader) throws Exception {
        if (exist(path)) {
            return java.nio.file.Files.readAllBytes(Paths.get(path));
        }
        if (existResource(path, classLoader == null ? Projects.MAIN_CLASS_LOADER : classLoader)) {
            return Files.readResource(path, classLoader);
        }
        return null;
    }

    /**
     * 读取文件或资源
     *
     * @param path path
     *
     * @return
     */
    public static byte[] readFileOrResource(String path) throws Exception {
        return readFileOrResource(path, Projects.MAIN_CLASS_LOADER);
    }

    /**
     * 获取资源的inputStream
     *
     * @param path        路径
     * @param classLoader classloader
     *
     * @return
     */
    public static InputStream getResourceInputStream(String path, ClassLoader classLoader) {
        return classLoader.getResourceAsStream(path);
    }

    /**
     * @param path
     *
     * @return
     */
    public static InputStream getResourceInputStream(String path) {
        return getResourceInputStream(path, Projects.MAIN_CLASS_LOADER);
    }

    /**
     * 创建目录<br>
     * 如果存在目录则不创建
     *
     * @param path
     */
    public static void createDirectory(String path) {
        if (!Files.exist(path)) {
            try {
                java.nio.file.Files.createDirectories(Paths.get(path));
            }
            catch (Exception x) {
                LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
                return;
            }
        }
    }

    /**
     * 获取classpath资源
     *
     * @param path
     *
     * @return
     */
    public static byte[] readResource(String path) {
        return readResource(path, Projects.MAIN_CLASS_LOADER);
    }

    /**
     * 获取classpath资源
     *
     * @param path
     * @param classLoader
     *
     * @return
     */
    public static byte[] readResource(String path, ClassLoader classLoader) {
        try {
            classLoader = classLoader != null ? classLoader : Projects.MAIN_CLASS_LOADER;
            InputStream reader = classLoader.getResourceAsStream(path);
            if (reader != null) {
                byte[] result = new byte[reader.available()];
                reader.read(result);
                return result;
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return null;
    }

    /**
     * 以utf-8格式读取资源
     *
     * @param path 文件路径
     *
     * @return
     */
    public static String readResourceText(String path) {
        try {
            byte[] result = readResource(path);
            if (result != null) {
                return new String(result, Charsets.UTF_8);
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return Strings.EMPTY;
    }

    /**
     * 获取classpath资源
     *
     * @param path
     * @param classLoader
     *
     * @return 以文本形式读取的字符串
     */
    public static String readResourceText(String path, ClassLoader classLoader) {
        try {
            byte[] result = readResource(path, classLoader);
            if (result != null) {
                return new String(result, Charsets.UTF_8);
            }
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return Strings.EMPTY;
    }


    /**
     * 写文件
     *
     * @param path
     * @param data
     */
    public static void write(String path, byte[] data) {
        try {
            java.nio.file.Files.write(Paths.get(path), data, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
    }

    /**
     * @param path
     *
     * @return
     */
    public static String getDirectory(String path) {
        Path parent = Paths.get(path).getParent();
        return parent != null ? parent.toString() : Strings.EMPTY;
    }

    /**
     * 获取文件扩展名
     *
     * @param path
     *
     * @return
     */
    public static String getExtension(String path) {
        File f = new File(path);
        String fileName = f.getName();
        int indexOfDot = fileName.lastIndexOf(".");
        return indexOfDot > -1 ? fileName.substring(indexOfDot + 1).toLowerCase() : Strings.EMPTY;
    }

    /**
     * 获取去除路径后的文件名<br>
     * cn/framework/core/hello.class -> hello.class
     *
     * @return
     */
    public static String getName(String path) {
        int index = path.lastIndexOf('/');
        index = index > -1 ? index : path.lastIndexOf('\\');
        return index > -1 ? path.substring(index + 1) : path;
    }

    /**
     * 去除路径和后缀名的文件名
     *
     * @param path
     *
     * @return
     */
    public static String getFileNameWithoutExtension(String path) {
        String fileName = getName(path);
        if (!fileName.contains(".")) {
            return fileName;
        }
        int index = fileName.lastIndexOf('.');
        return fileName.substring(0, index);
    }

    /**
     * 文件路径，返回Reader
     *
     * @param path 文件路径
     * @param type 可为空，指定Reader的类型
     */
    public static Reader newReader(String path, ReaderType... type) {
        try {
            if (null == type) {
                return java.nio.file.Files.newBufferedReader(Paths.get(path));
            }
            switch (type[0]) {
                case BUFFER:
                    return java.nio.file.Files.newBufferedReader(Paths.get(path));
                default:
                    return java.nio.file.Files.newBufferedReader(Paths.get(path));
            }
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x);
        }
        return null;
    }

    /**
     * 文件路径，返回InputStream
     *
     * @param path 文件路径
     *
     * @return
     */
    public static InputStream newInputStream(String path, OpenOption... option) {
        try {
            if (exist(path)) {
                return java.nio.file.Files.newInputStream(Paths.get(path), option != null && option.length > 0 ? option[0] : StandardOpenOption.READ);
            }
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x);
        }
        return null;
    }

    /**
     * 文件是否存在
     *
     * @param filePath 文件路径
     *
     * @return
     */
    public static boolean exist(String filePath) {
        try {
            File file = new File(filePath);
            return file.exists();
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x);
            return false;
        }
    }

    /**
     * 判断文件是否存在,以下情况都会返回true<br/>
     * 1 文件存在于目录系统中<br/>
     * 2 文件存在于classpath路径下
     *
     * @param path 文件资源路径
     *
     * @return true if file exists
     */
    public static boolean existFilesOrResource(String path) {
        return existFilesOrResource(path, Projects.MAIN_CLASS_LOADER);
    }

    /**
     * 判断文件是否存在,以下情况都会返回true<br/>
     * 1 文件存在于目录系统中<br/>
     * 2 文件存在于classpath路径下
     *
     * @param path 文件资源路径
     * @param cl   classloader
     *
     * @return true if file exists
     */
    public static boolean existFilesOrResource(String path, ClassLoader cl) {
        try {
            if (!Strings.isNotNullOrEmpty(path)) {
                return false;
            }
            if (exist(path)) {
                return true;
            }
            try (InputStream stream = cl.getResourceAsStream(path);) {
                if (stream != null && stream.available() > 0) {
                    return true;
                }
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return false;
    }

    /**
     * 将文件当做文本文档读取，并返回所有字符
     *
     * @param contentOrPath 文件路径
     *
     * @return
     */
    public static String read(String contentOrPath, String... charset) {
        try {
            if (exist(contentOrPath)) {
                return new String(java.nio.file.Files.readAllBytes(Paths.get(contentOrPath)), charset != null && charset.length > 0 ? Charset.forName(charset[0]) : Charset.forName("utf-8"));
            }
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x);
        }
        return contentOrPath;
    }

    /**
     * 读取文件，附带编码读取
     *
     * @param contentOrPath
     * @param charset
     *
     * @return
     */
    public static String read(String contentOrPath, String charset) {
        try {
            if (exist(contentOrPath)) {
                List<String> content = java.nio.file.Files.readAllLines(Paths.get(contentOrPath), Charset.forName(charset));
                if (content != null && content.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (String str : content)
                        builder.append(str).append("\n");
                    return builder.toString();
                }
            }
            else {
                InputStream stream = ClassLoader.getSystemResourceAsStream(contentOrPath);
                if (stream != null) {
                    byte[] data = new byte[stream.available()];
                    stream.read(data);
                    return new String(data, charset);
                }
            }
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x);
        }
        return contentOrPath;
    }

    /**
     * 二进制读取
     *
     * @param filePath
     *
     * @return
     */
    public static byte[] readBytes(String filePath) {
        try {
            if (exist(filePath)) {
                return java.nio.file.Files.readAllBytes(Paths.get(filePath));
            }
            else {
                InputStream stream = ClassLoader.getSystemResourceAsStream(filePath);
                if (stream != null) {
                    byte[] data = new byte[stream.available()];
                    stream.read(data);
                    return data;
                }
            }
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x);
        }
        return null;
    }

    /**
     * 将文件当成文本文档读取，并返回所有行数组
     *
     * @param filePath 文件路径
     *
     * @return
     */
    public static String[] readAllLines(String filePath) {
        try {
            List<String> lines = java.nio.file.Files.readAllLines(Paths.get(filePath));
            return lines.toArray(new String[0]);
        }
        catch (Throwable x) {
            return null;
        }
    }

    /**
     * 输出文件每一行数据
     *
     * @param filePath
     */
    public static void readLine(String filePath) {
        try (Scanner scanner = new Scanner(Paths.get(filePath), "utf-8");) {
            scanner.useDelimiter("\n");
            while (scanner.hasNext()) {
                String next = scanner.next();
                System.out.println(next);
            }
        }
        catch (Exception e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
        }
        return;
    }

    /**
     * 添加内容到指定文件
     *
     * @param content
     * @param filePath
     */
    public static boolean append(String content, String filePath) {
        return append(content.getBytes(Charset.forName("UTF-8")), filePath);
    }

    /**
     * 将内容添加到指定文件中
     *
     * @param buffer
     * @param filePath
     *
     * @return
     */
    public static boolean append(byte[] buffer, String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!exist(filePath)) {
                java.nio.file.Files.createFile(path);
            }
            java.nio.file.Files.write(path, buffer, StandardOpenOption.APPEND);
            return true;
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
            return false;
        }
    }

    /**
     * 指定buffer长度添加到文件
     *
     * @param buffer
     * @param length
     * @param filePath
     *
     * @return
     */
    public static boolean append(byte[] buffer, int length, String filePath) {
        byte[] newBuffer = new byte[length];
        System.arraycopy(buffer, 0, newBuffer, 0, length);
        return append(newBuffer, filePath);
    }

    /**
     * 将内容添加到文件末尾，并加上换行符
     *
     * @param content
     * @param filePath
     *
     * @return
     */
    public static boolean appendLine(String content, String filePath) {
        return append(content + "\n", filePath);
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static boolean delete(String filePath) {
        try {
            if (exist(filePath)) {
                java.nio.file.Files.delete(Paths.get(filePath));
            }
            return true;
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return false;
    }

    /**
     * 写文件
     *
     * @param stream
     * @param path
     *
     * @return
     */
    public static boolean write(InputStream stream, String path) {
        try {
            byte[] buffer = new byte[1024 * 1024];
            int readed = 0;
            while ((readed = stream.read(buffer)) > 0) {
                append(buffer, readed, path);
            }
            return true;
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return false;
    }

    /**
     * 覆盖文件
     *
     * @param content
     * @param filePath
     *
     * @return
     */
    public static boolean writeOverride(String content, String filePath) {
        try {
            java.nio.file.Files.write(Paths.get(filePath), content.getBytes(Charset.forName("UTF-8")), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            return true;
        }
        catch (Exception x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
        return false;
    }

    /**
     * 判断classloader中是否具有resource
     *
     * @param resourcePath 资源路径 xxx/xxx/xxx/xxx
     * @param classLoader  classloader
     *
     * @return
     */
    public static boolean existResource(String resourcePath, ClassLoader classLoader) {
        try {
            return classLoader.getResource(resourcePath) != null;
        }
        catch (Exception x) {
            Exceptions.processException(x);
            return false;
        }
    }

    /**
     * 判断中是否具有resource
     *
     * @param resourcePath 资源路径 xxx/xxx/xxx/xxx
     *
     * @return
     */
    public static boolean existResource(String resourcePath) {
        return existResource(resourcePath, Projects.MAIN_CLASS_LOADER);
    }

    /**
     * 删除目录下所有文件
     *
     * @param path
     */
    public static void deleteFilesInPath(String path) {
        try {
            Path p = Paths.get(path);
            if (java.nio.file.Files.exists(p) && java.nio.file.Files.isDirectory(p)) {
                java.nio.file.Files.walk(p).forEach(filePath -> {
                    try {
                        if (java.nio.file.Files.exists(filePath) && !java.nio.file.Files.isDirectory(filePath)) {
                            java.nio.file.Files.delete(filePath);
                        }
                    }
                    catch (Exception x) {
                        Exceptions.processException(x);
                    }
                });
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
    }

    /**
     * newReader使用，标识newReader返回的reader类型
     *
     * @author wenlai
     */
    public enum ReaderType {
        /**
         * BufferedReader
         */
        BUFFER
    }
}
