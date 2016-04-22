package cn.framework.cache.session;

import cn.framework.core.container.FrameworkContainer;
import cn.framework.core.utils.Springs;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * project visual-framework
 * package cn.framework.cache.session
 * create at 16/4/22 下午2:09
 *
 * @author wenlai
 */
@Service(FrameworkSessionContainer.BEAN_NAME)
public class FrameworkSessionContainer {

    public static final String BEAN_NAME = "frameworkSessionContainer";
    /**
     * cache container
     */
    private Cache<String, FrameworkSession> cache = CacheBuilder.newBuilder().expireAfterAccess(3600, TimeUnit.SECONDS).build();

    /**
     * @return
     */
    public static final FrameworkContainer scope() {
        return Springs.get(FrameworkSessionContainer.BEAN_NAME);
    }

    public void add(FrameworkSession session) {
        cache.put("", session);
    }

    public FrameworkSession get(String sessionId) {
        return cache.getIfPresent(sessionId);
    }
}
