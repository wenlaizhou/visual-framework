package cn.framework.db.view;

import cn.framework.core.utils.KVMap;

import javax.servlet.http.HttpServletRequest;

/**
 * project code
 * package cn.framework.db.process
 * create at 16/3/17 下午8:25
 *
 * @author wenlai
 */
public interface BeforeProcessor {

    /**
     * 将request转换成sql参数
     *
     * @param request request
     *
     * @return
     */
    KVMap pre(DbRequest request);

}
