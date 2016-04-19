package cn.framework.cache.session;

import cn.framework.cache.resource.CacheWrapper;
import cn.framework.core.utils.Strings;
import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardSession;

import java.util.HashSet;
import java.util.Set;

/**
 * project code
 * package cn.framework.cache.session
 * create at 16/3/30 下午2:27<br/>
 * session implement by wenlai
 *
 * @author wenlai
 */
public class FrameworkSession extends StandardSession implements AutoCloseable {

    /**
     * cache wrapper
     */
    private CacheWrapper handler;

    /**
     * keys
     */
    private Set<String> keys = new HashSet<>();

    /**
     * constructor
     *
     * @param manager
     * @param id
     */
    public FrameworkSession(Manager manager, String id) {
        super(manager);
        if (!Strings.isNotNullOrEmpty(id)) {
            id = manager.getSessionIdGenerator().generateSessionId();
        }
        super.setId(id);
        super.setValid(true);
        this.handler = CacheWrapper.wrap(id);
    }

    /**
     * set attr
     *
     * @param name
     * @param value
     */
    @Override
    public void setAttribute(String name, Object value) {
        this.keys.add(name);
        this.handler.add(name, value);
    }

    /**
     * keys
     *
     * @return
     */
    @Override
    protected String[] keys() {
        return this.keys.toArray(new String[0]);
    }

    /**
     * get attr
     *
     * @param name
     *
     * @return
     */
    @Override
    public Object getAttribute(String name) {
        return this.handler.select(name);
    }

    /**
     * remove attr
     *
     * @param name
     */
    @Override
    public void removeAttribute(String name) {
        this.keys.remove(name);
        this.handler.delete(name);
    }

    /**
     * remove all
     */
    public void removeSelf() {
        this.handler.removeAll();
    }

    @Override
    public String getId() {
        return this.id;
    }

    /**
     * close
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        this.removeSelf();
    }
}
