package cn.framework.db.view;

import cn.framework.core.utils.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * project code
 * package cn.framework.db.model
 * create at 16/3/17 下午8:23
 *
 * @author wenlai
 */
public class DbViewer {

    /**
     * view-actionId
     */
    public String actionId;

    /**
     * view-name
     */
    public String name;

    /**
     * 过程id
     */
    public String procedureId;

    /**
     * method
     */
    public String method;

    /**
     * 执行之前的id
     */
    public String beforeId = Strings.EMPTY;

    /**
     * 执行之后的id
     */
    public String afterId = Strings.EMPTY;

    /**
     * procedure 描述
     */
    public String description;

    /**
     * 参数列表
     */
    public List<Param> params = new ArrayList<>();

    /**
     * 参数
     */
    public static class Param {

        /**
         * 参数名
         */
        public String name;

        /**
         * 参数类型
         */
        public String type;

        /**
         * 参数描述
         */
        public String desctiption;
    }

}
