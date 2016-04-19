package cn.framework.db.view;

import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Springs;
import cn.framework.core.utils.Strings;
import cn.framework.db.sql.Procedure;
import cn.framework.db.sql.Result;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * DbViewer路由器
 * project code
 * package cn.framework.db.process
 * create at 16/3/17 下午8:15
 *
 * @author wenlai
 */
public class DbActionProcessor implements Servlet {

    /**
     * 默认的处理器
     */
    public static final String DEFAULT_BEFORE = "defaultBeforeDbViewer";
    /**
     * 默认的处理器
     */
    public static final String DEFAULT_AFTER = "defaultAfterDbViewer";

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET,POST");
        response.addHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        DbViewer db = getDbViewer(request, response);
        if (db != null) {
            process(request, response, db);
            return;
        }
        response.setStatus(450);
        if (!response.isCommitted()) {
            request.getRequestDispatcher("/Error.html").forward(request, response);
        }
        return;
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }

    /**
     * 执行sql过程
     *
     * @param req  request
     * @param resp response
     * @param db   dbViewer
     *
     * @throws IOException
     */
    private void process(HttpServletRequest req, HttpServletResponse resp, DbViewer db) throws IOException {
        try {
            DbRequest request = DbRequest.wrapper(req);
            BeforeProcessor before = Springs.get(db.beforeId);
            if (before != null) {
                before.pre(request);
            }
            try (Result dbResult = Procedure.getProcedure(db.procedureId).process(request.dbParam());) {
                AfterProcessor after = Springs.get(db.afterId);
                if (after != null) {
                    after.post(dbResult, req, resp);
                }
                else {
                    resp.getWriter().append(dbResult.toString());
                }
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
    }

    /**
     * 初始化参数
     *
     * @param config config
     *
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    /**
     * 从request中获取DbViewer
     *
     * @param request request
     *
     * @return
     */
    private DbViewer getDbViewer(HttpServletRequest request, HttpServletResponse response) {
        try {
            String action = request.getParameter("request_action");
            if (Strings.isNotNullOrEmpty(action)) {
                DbViewer result = DbViewInitProvider.getDbViewer(action);
                if (request.getMethod().toUpperCase().equals(result.method)) {
                    return result;
                }
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return null;
    }
}
