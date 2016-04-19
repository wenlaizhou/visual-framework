package cn.framework.db.view;

import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.KVMap;
import cn.framework.core.utils.KVPair;
import cn.framework.core.utils.Strings;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * project code
 * package cn.framework.db.view
 * create at 16/3/28 下午8:24
 *
 * @author wenlai
 */
public class DbRequest {

    private final HttpServletRequest request;

    private final KVMap dbParams = new KVMap();

    public DbRequest(HttpServletRequest request) {
        this.request = request;
        try {
            Enumeration<String> paramNames = request.getParameterNames();
            if (paramNames != null) {
                while (paramNames.hasMoreElements()) {
                    String name = paramNames.nextElement();
                    if (Strings.isNotNullOrEmpty(name)) {
                        String value = request.getParameter(name);
                        if (Strings.isNotNullOrEmpty(value)) {
                            this.dbParams.addKV(name, value);
                        }
                    }
                }
            }
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
    }

    public static DbRequest wrapper(HttpServletRequest request) {
        return new DbRequest(request);
    }

    public KVMap dbParam() {
        return this.dbParams;
    }

    public KVMap dbParam(KVPair... pairs) {
        if (pairs != null && pairs.length > 0) {
            for (KVPair pair : pairs) {
                this.dbParams.put(pair.key().toString(), pair.value());
            }
        }
        return this.dbParams;
    }

    public HttpServletRequest request() {
        return this.request;
    }

}
