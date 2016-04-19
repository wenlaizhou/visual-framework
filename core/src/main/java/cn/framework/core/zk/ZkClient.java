package cn.framework.core.zk;

import cn.framework.core.pool.Task;
import cn.framework.core.pool.ThreadPool;
import cn.framework.core.utils.Exceptions;
import cn.framework.core.utils.Springs;
import com.google.common.base.Charsets;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Service;

/**
 * project code
 * package cn.framework.core.zk
 * create at 16/4/1 下午7:13
 *
 * @author wenlai
 */
@Service("zkClient")
public class ZkClient {

    public ZooKeeper client;

    private int flag = 0;

    public void init() {

        try {
            this.client = new ZooKeeper("127.0.0.1:1234", 3000, (WatchedEvent event) -> {
                System.out.println(event.getPath());
                System.out.println(event.getState());
            });
            ThreadPool.addScheduledTaskAndWaitForDone(Task.wrap("zk create", () -> {
                try {
                    ZooKeeper client = Springs.get("zkClient", ZkClient.class).client;
                    String res = client.create("/wenlai" + flag++, "hello-zook".getBytes(Charsets.UTF_8), null, CreateMode.PERSISTENT);
                    System.out.println(res);
                }
                catch (Exception x) {
                    Exceptions.processException(x);
                }
            }), 10);
            ThreadPool.addScheduledTaskAndWaitForDone(Task.wrap("zk get", () -> {
                try {
                    byte[] data = client.getData("/wenlai" + flag--, false, null);
                    System.out.println(new String(data));
                }
                catch (Exception x) {
                    Exceptions.processException(x);
                }
            }), 10);
        }
        catch (Exception x) {
            Exceptions.processException(x);
        }
    }
}
