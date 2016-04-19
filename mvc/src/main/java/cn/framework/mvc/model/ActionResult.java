/**
 * @项目名称: framework
 * @文件名称: ViewResult.java
 * @Date: 2015年10月26日
 * @author: wenlai
 * @type: ViewResult
 */
package cn.framework.mvc.model;

import cn.framework.core.utils.KVMap;

/**
 * action返回结果<br>
 * mvc框架中的action<strong><b>必须</b></strong>返回此结果<br>
 * 默认为ACTION.VIEW
 *
 * @author wenlai
 */
public class ActionResult {


    /**
     * @param model
     *
     * @return
     */
    public static ActionResult json(Object model) {
        return new ActionResult(ACTION.JSON, model);
    }

    /**
     * @param message
     *
     * @return
     */
    public static ActionResult text(String message) {
        return new ActionResult(ACTION.PLAIN, message);
    }

    /**
     * @param forward
     * @param model
     *
     * @return
     */
    public static ActionResult dispatch(String forward, Object model) {
        return new ActionResult(ACTION.DISPATCHER, forward);
    }

    /**
     * @param model
     *
     * @return
     */
    public static ActionResult view(Object model) {
        return new ActionResult(ACTION.VIEW, model);
    }

    /**
     * 跳转
     *
     * @param url
     *
     * @return
     */
    public static ActionResult redirect(String url) {
        return new ActionResult(ACTION.REDIRECT, url);
    }

    /**
     * 返回http码
     *
     * @param code
     * @param message
     *
     * @return
     */
    public static ActionResult sendCode(int code, String message) {
        return new ActionResult(ACTION.ERROR, KVMap.newKvMap("code", code).addKV("message", message));
    }

    /**
     * 创建结果
     */
    public ActionResult() {

    }

    /**
     * @param model model
     *
     * @see ActionResult#ActionResult()
     */
    public ActionResult(Object model) {
        this.model = model;
    }

    /**
     * 创建action结果并将数据赋值给model<br>
     * 当action为redirect时则model即为重定向url地址
     *
     * @param act
     * @param data
     */
    public ActionResult(ACTION act, Object model) {
        this.action = act;
        this.model = model;
    }

    /**
     * mvc -> model
     */
    public Object model;

    /**
     * 动作
     */
    public ACTION action = ACTION.VIEW;

    /**
     * 动作列表
     *
     * @author wenlai
     */
    public enum ACTION {
        /**
         * 直接返回文本
         */
        PLAIN,

        /**
         * 302重定向<br>
         * 会直接跳转到{@link ActionResult#model}指示的toString()地址
         */
        REDIRECT,

        /**
         * 返回json数据
         */
        JSON,

        /**
         * 返回视图
         */
        VIEW,

        /**
         * 异步执行
         */
        ASYNC,

        /**
         * 跳转链<br>
         * 会直接跳转到{@link ActionResult#model}指示的地址
         */
        DISPATCHER,

        /**
         * 返回错误码<br>
         * 对应要将model设置成KVMap类型，路由会取model中的code和message
         */
        ERROR
    }

    /**
     * 向客户端发送错误码时code使用的key
     */
    public final static String CODE_KEY = "code";

    /**
     * 向客户端发送错误码时message使用的key
     */
    public final static String MESSAGE_KEY = "message";

    /**
     * 设置model值，并返回this
     *
     * @param model
     *
     * @return this
     */
    public ActionResult setModel(Object model) {
        this.model = model;
        return this;
    }
}
