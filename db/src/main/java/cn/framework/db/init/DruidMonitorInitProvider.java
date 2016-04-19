package cn.framework.db.init;

import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import cn.framework.core.utils.KVMap;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.stereotype.Service;

/**
 * project code
 * package cn.framework.db.init
 * create at 16-3-8 下午6:05
 * <p>
 * Druid监控初始化器
 *
 * @author wenlai
 */
@Service("druidMonitorInit")
public class DruidMonitorInitProvider implements InitProvider {

    /**
     * @param context 配置上下文
     *
     * @throws Exception
     */
    @Override
    public void init(Context context) throws Exception {
        KVMap druidFilterParams = KVMap.newKvMap("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        druidFilterParams.addKV("sessionStatEnable", "true");
        druidFilterParams.addKV("profileEnable", "true");
        context.addFilter("DruidWebStatFilter", WebStatFilter.class.getName(), "/*", druidFilterParams);
        context.addServlet(null, "DruidStatView", StatViewServlet.class.getName(), "/druid/*", null, -1, true);
        context.addServlet(null, "druid-ui", DruidUI.class.getName(), "/druid-ui", null, -1, true);
    }
}
