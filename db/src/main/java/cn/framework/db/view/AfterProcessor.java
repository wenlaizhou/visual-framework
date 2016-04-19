package cn.framework.db.view;

import cn.framework.db.sql.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * project code
 * package cn.framework.db.process
 * create at 16/3/17 下午8:25
 *
 * @author wenlai
 */
public interface AfterProcessor {

    /**
     * 数据库原始参数转换成接口返回值
     *
     * @param result ActionResult
     *
     * @return
     */
    void post(Result result, HttpServletRequest request, HttpServletResponse response);

}
