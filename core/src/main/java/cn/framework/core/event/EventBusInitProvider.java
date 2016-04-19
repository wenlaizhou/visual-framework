package cn.framework.core.event;

import cn.framework.core.container.Context;
import cn.framework.core.container.InitProvider;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * project code
 * package cn.framework.core.event
 * create at 16/3/25 下午7:43
 *
 * @author wenlai
 */
@Service("eventBusInit")
public class EventBusInitProvider implements InitProvider {

    @Override
    public void init(Context context) throws Exception {
        EventBus eve = new EventBus("");
        AsyncEventBus bus = new AsyncEventBus("", Executors.newSingleThreadExecutor());
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }
}
