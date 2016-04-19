/**
 * @项目名称: core
 * @文件名称: Callback.java
 * @Date: 2016年1月11日
 * @author: wenlai
 * @type: Callback
 */
package cn.framework.core.async;

import cn.framework.core.utils.KVMap;

/**
 * 异步执行
 * @author wenlai
 */
public interface AsyncCallback {
    
    /**
     * 初始化
     * @param initParam 参数
     */
    void init(KVMap initParam);
    
    /**
     * 执行完毕回调
     * @param <T> 回调类型
     * @param context 上下文
     * @throws Exception
     */
    <T> void done(Context<T> context) throws Exception;
    
    /**
     * 回调上下文
     * @author wenlai
     *
     * @param <T>
     */
    public static class Context<T> {
        
        /**
         * 创建上下文
         * @param success
         * @param totalSeconds
         * @param result
         */
        private Context(boolean success, int totalSeconds, T result, Object attach) {
            this.success = success;
            this.totalMilliSeconds = totalSeconds;
            this.result = result;
            this.attach = attach;
        }
        
        /**
         * 获取附带数据
         * @return
         */
        public Object getAttach() {
            return this.attach;
        }
        
        /**
         * 获取回调数据
         * @return
         */
        public T getResult() {
            return this.result;
        }
        
        /**
         * 获取回调持续时间
         * @return
         */
        public int getExpireMilliSeconds() {
            return this.totalMilliSeconds;
        }
        
        /**
         * 是否成功执行
         * @return
         */
        public boolean isSuccess() {
            return this.success;
        }
        
        /**
         * 创建回调上下文
         * @param success
         * @param totalMilliSeconds
         * @param result 附带数据
         * @return
         */
        public static <T> Context<T> build(boolean success, int totalMilliSeconds, T result, Object attach) {
            return new Context<T>(success, totalMilliSeconds, result, attach);
        }
        
        /**
         * 是否执行成功
         */
        private boolean success;
        
        /**
         * 总体运行时长
         */
        private int totalMilliSeconds;
        
        /**
         * 回调数据
         */
        private T result;
        
        /**
         * 附带数据
         */
        private Object attach;
        
    }
}
