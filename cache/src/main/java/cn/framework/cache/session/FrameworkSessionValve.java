package cn.framework.cache.session;

import cn.framework.core.utils.Exceptions;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * project code
 * package cn.framework.cache.session
 * create at 16/3/30 上午10:29
 *
 * @author wenlai
 */
public class FrameworkSessionValve extends ValveBase {

    /**
     * manager
     */
    private FrameworkSessionManager manager;

    /**
     * @param request
     * @param response
     *
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        try {
            getNext().invoke(request, response);
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
    }

    /**
     * 设置session manager
     * @param manager
     */
    public void setSessionManager(FrameworkSessionManager manager) {
        this.manager = manager;
    }
}
