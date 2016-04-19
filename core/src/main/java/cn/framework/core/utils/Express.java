/**
 * @项目名称: framework
 * @文件名称: Express.java
 * @Date: 2015年11月12日
 * @author: wenlai
 * @type: Express
 */
package cn.framework.core.utils;

/**
 * @author wenlai
 *
 */
public class Express {
    
    /**
     * 获取自定义函数包裹内容
     * 
     * @param functionName
     * @param expression
     * @return
     */
    public static String funcFilter(String functionName, String expression) {
        String[] res = Regexs.match(functionName + "\\s*\\((.*)\\)", expression);
        if (Arrays.isNotNullOrEmpty(res))
            return res[0];
        return Strings.EMPTY;
    }
}
