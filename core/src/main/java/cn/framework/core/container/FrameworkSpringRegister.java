package cn.framework.core.container;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * project code
 * package cn.framework.core.container
 * create at 16/3/17 下午3:05
 *
 * @author wenlai
 */
@Configuration
@ComponentScan("cn.framework")
@Service("framework")
@Scope("singleton")
public class FrameworkSpringRegister {

    /**
     * Spring注册类
     */
    public static ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(FrameworkSpringRegister.class);

    @PostConstruct
    public void init() {

    }

    @PreDestroy
    public void destroy() {

    }

}