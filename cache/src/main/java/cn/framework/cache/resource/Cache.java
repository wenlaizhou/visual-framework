package cn.framework.cache.resource;

import cn.framework.core.utils.Springs;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * project code
 * package cn.framework.cache.resource
 * create at 16/3/18 下午11:56
 *
 * @author wenlai
 */
@Service("cache")
@Scope(Springs.SCOPE_SINGLETON)
public class Cache {

    @PostConstruct
    public void init() {
    }


    @PreDestroy
    public void destroy() {

    }

}
