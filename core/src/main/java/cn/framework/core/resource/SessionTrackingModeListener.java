package cn.framework.core.resource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.SessionTrackingMode;
import java.util.EnumSet;

/**
 * project code
 * package cn.framework.core.resource
 * create at 16/3/19 上午10:21
 *
 * @author wenlai
 */
public class SessionTrackingModeListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        EnumSet<SessionTrackingMode> modes = EnumSet.of(SessionTrackingMode.SSL);
        context.setSessionTrackingModes(modes);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
