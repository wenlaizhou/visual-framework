/**
 * @项目名称: framework
 * @文件名称: StopWatch.java
 * @Date: 2015年11月12日
 * @author: wenlai
 * @type: StopWatch
 */
package cn.framework.core.utils;

import java.util.Date;

/**
 * 计时器
 * 
 * @author wenlai
 */
public class StopWatch {
    
    /**
     * 返回计时器
     * 
     * @return
     */
    public static StopWatch newWatch() {
        return new StopWatch();
    }
    
    /**
     * 打点返回和初始时间比较的miliseconds
     * 
     * @return
     */
    public long checkByOriginal() {
        this.lastTime = null;
        this.lastTime = new Date();
        return this.lastTime.getTime() - this.youth.getTime();
    }
    
    /**
     * 打点返回距离上次打点的miliseconds
     * 
     * @return
     */
    public long checkByLastTime() {
        Date last = (Date) lastTime.clone();
        this.lastTime = new Date();
        return this.lastTime.getTime() - last.getTime();
    }
    
    private StopWatch() {
        this.youth = new Date();
        this.lastTime = new Date();
    }
    
    private Date youth;
    
    private Date lastTime = null;
}
