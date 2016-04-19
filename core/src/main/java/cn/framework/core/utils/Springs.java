package cn.framework.core.utils;

/**
 * project code
 * package cn.framework.core.utils
 * create at 16-3-8 下午1:42
 *
 * @author wenlai
 */

import cn.framework.core.container.FrameworkSpringRegister;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * bean工具类<br>
 * 统一设置成WebApplicationContext
 */
public final class Springs {

    /**
     * scope - singleton
     */
    public final static String SCOPE_SINGLETON = "singleton";

    /**
     * scope - prototype
     */
    public final static String SCOPE_PROTOTYPE = "prototype";


    /**
     * 获取bean实例
     *
     * @param beanId 名称
     *
     * @return 获取bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String beanId) {
        try {
            if (Strings.isNotNullOrEmpty(beanId)) {
                Object bean = FrameworkSpringRegister.context.getBean(beanId);
                if (bean != null) {
                    return (T) bean;
                }
                else {
                    Exceptions.logProcessor().logger().info("bean {} is not found", beanId);
                    return null;
                }
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return null;
    }


    /**
     * 获取bean实例
     *
     * @param beanId     名称
     * @param returnType 返回类型
     * @param <T>        类型
     *
     * @return
     */
    public static <T> T get(String beanId, Class<T> returnType) {
        try {
            T bean = FrameworkSpringRegister.context.getBean(beanId, returnType);
            if (bean != null) {
                return bean;
            }
            else {
                Exceptions.logProcessor().logger().info("bean {} is not found", beanId);
                return null;
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
        return null;
    }

    /**
     * 获取Spring上下文
     *
     * @return
     */
    public synchronized static final ApplicationContext getContext() {
        return FrameworkSpringRegister.context;
    }

    /**
     * 增加context
     *
     * @param context 上下文实体
     */
    public static synchronized void addContext(ConfigurableApplicationContext context) {
        context.setParent(FrameworkSpringRegister.context);
        FrameworkSpringRegister.context = context;
    }

    /**
     * 获取annotation注解配置bean name
     *
     * @param clazz class
     *
     * @return
     */
    public static String getBeanName(Class clazz) {
        if (clazz.getDeclaredAnnotation(Component.class) != null) {
            return ((Component) clazz.getDeclaredAnnotation(Component.class)).value();
        }
        else if (clazz.getDeclaredAnnotation(Service.class) != null) {
            return ((Service) clazz.getDeclaredAnnotation(Service.class)).value();
        }
        else if (clazz.getDeclaredAnnotation(Repository.class) != null) {
            return ((Repository) clazz.getDeclaredAnnotation(Repository.class)).value();
        }
        else if (clazz.getDeclaredAnnotation(Controller.class) != null) {
            return ((Controller) clazz.getDeclaredAnnotation(Controller.class)).value();
        }
        return Strings.EMPTY;
    }

    /**
     * 获取上下文实体
     */
    public static class ContextRegister implements ServletContextListener {

        @Override
        public synchronized void contextInitialized(ServletContextEvent servletContextEvent) {
            try {
                Object context = servletContextEvent.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
                if (context != null && context instanceof ApplicationContext) {
                    FrameworkSpringRegister.context.setParent((ApplicationContext) context);
                }
            }
            catch (Exception x) {
                Exceptions.processException(x);
            }
        }

        @Override
        public void contextDestroyed(ServletContextEvent servletContextEvent) {

        }
    }
}
