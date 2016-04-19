package cn.framework.core.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式类
 * 
 * @author wenlai
 */
public final class Regexs {
    
    /**
     * 返回是否匹配
     * 
     * @param regex 正则表达式
     * @param content 要检测的内容
     */
    public static boolean test(String regex, String content) {
        // return Pattern.matches(regex, content);
        return Pattern.compile(regex).matcher(content).find();
    }
    
    /**
     * 返回正则表达式匹配结果
     * 
     * @param regex 正则表达式
     * @param content 要匹配的内容
     */
    public static String[] match(String regex, String content) {
        Matcher matcher = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(content);
        ArrayList<String> set = new ArrayList<String>();
        while (matcher.find())
            if (matcher.groupCount() >= 1)
                set.add(matcher.group(1));
        return set.toArray(new String[0]);
    }
    
    /**
     * 返回正则表达式匹配结果
     * 
     * @param regex 正则表达式
     * @param filePath 文件路径
     */
    public static Matcher matchFile(String regex, String filePath) {
        return Pattern.compile(regex).matcher(Files.read(filePath));
    }
}
