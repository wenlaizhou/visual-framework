/**
 * @项目名称: core
 * @文件名称: Number.java
 * @Date: 2016年2月19日
 * @author: wenlai
 * @type: Number
 */
package cn.framework.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author wenlai
 *
 */
public class Number {
    
    /**
     * 保留特定小数位
     * 
     * @param number
     * @param digit 保留小数位数
     * @return
     */
    public static long roundUp(long number, int digit) {
        return new BigDecimal(number).setScale(digit, RoundingMode.HALF_UP).longValue();
    }
    
    /**
     * 保留特定小数位
     * 
     * @param number
     * @param digit 保留小数位数
     * @return
     */
    public static double roundUp(double number, int digit) {
        return new BigDecimal(number).setScale(digit, RoundingMode.HALF_UP).doubleValue();
    }
    
}
