package cn.framework.core.cluster;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * project code
 * package cn.framework.core.cluster
 * create at 16/3/25 下午7:51
 *
 * @author wenlai
 */
@Service("clusterHandler")
public class ClusterHandler extends IoHandlerAdapter {

    private HashMap<Long, IoSession> sessions = new HashMap<>();


    /**
     * 发送
     *
     * @param id
     * @param message
     */
    public void send(long id, Object message) {
        sessions.get(id).write(message);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        System.out.println("sessionCreated");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("sessionOpened");
        session.write(session.getId());
        sessions.put(session.getId(), session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        sessions.remove(session.getId());
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        System.out.println("sessionIdle");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

        System.out.println("exceptionCaught");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        System.out.println(message);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        session.write(message);
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {

        System.out.println("input closed!");
    }
}
