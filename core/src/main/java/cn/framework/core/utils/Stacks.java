package cn.framework.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * 栈帮助类
 * 
 * @author wenlai
 */
public final class Stacks {
    
    /**
     * 返回线程dump信息
     */
    public static String stackTrace() {
        StringBuilder result = new StringBuilder();
        Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
        for (Thread thread : traces.keySet())
            result.append(Arrays.print(traces.get(thread))).append("\n");
        return result.toString();
    }
    
    /**
     * 返回异常中的堆栈信息
     * 
     * @param e
     * @return
     */
    public static String stackTrace(Exception e) {
        Writer w = new StringWriter();
        PrintWriter pw = new PrintWriter(w);
        e.printStackTrace(pw);
        return w.toString();
    }
}
