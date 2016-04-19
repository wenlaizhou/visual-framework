package cn.framework.db.view;

import cn.framework.core.container.FrameworkContainer;
import cn.framework.core.utils.Strings;
import com.alibaba.fastjson.JSON;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static cn.framework.core.utils.Pair.newPair;

/**
 * project code
 * package cn.framework.db.view
 * create at 16/3/19 下午3:50
 *
 * @author wenlai
 */
public class DbViewUI extends HttpServlet {

    /**
     * ui列表模板
     */
    public final String TEMPLATE_HTML = FrameworkContainer.buildUI("cn/framework/db/view/DbViewUI.html", DbViewUI.class.getClassLoader());

    /**
     * 接口详情模板
     */
    public final String TEMPLAETE_DETAIL = FrameworkContainer.buildUI("cn/framework/db/view/DbViewDetail.html", DbViewUI.class.getClassLoader());

    /**
     * 详情tr模板
     */
    public final String PARAM_TEMPLATE = "<tr>" +
            "<td>${no}</td>" +
            "<td>${param}</td>" +
            "<td>${type}</td>" +
            "<td>${desctiption}</td>" +
            "</tr>";

    /**
     * 配置信息
     */
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestUrl = req.getRequestURI();
        resp.addHeader("Content-Type", "text/html;charset=utf-8");
        if (requestUrl.contains("action")) { //jump to detail
            String actionId = req.getParameter("id");
            DbViewer viewer = DbViewInitProvider.getDbViewer(actionId);
            if (viewer != null) {
                String tbody = Strings.EMPTY;
                if (viewer.params != null && viewer.params.size() > 0) {
                    StringBuilder tbodyBuilder = new StringBuilder();
                    int no = 0;
                    for (DbViewer.Param param : viewer.params) {
                        no++;
                        tbodyBuilder.append(Strings.format(PARAM_TEMPLATE, newPair("no", no), newPair("param", param.name), newPair("type", param.type), newPair("desctiption", param.desctiption)));
                    }
                    tbody = tbodyBuilder.toString();
                }
                resp.getWriter().append(Strings.format(TEMPLAETE_DETAIL, newPair("method", viewer.method), newPair("actionId", viewer.actionId), newPair("name", viewer.name), newPair("description", viewer.description), newPair("body", tbody)));
            }
            else {
                if (!resp.isCommitted()) {
                    resp.setStatus(450);
                    req.getRequestDispatcher("/Error.html").forward(req, resp);
                }
            }
        }
        else { //list
            resp.getWriter().append(TEMPLATE_HTML.replace("${data}", JSON.toJSONString(DbViewInitProvider.getDbViewers())));
        }

    }

    @Override
    public ServletConfig getServletConfig() {
        return this.config;
    }
}
