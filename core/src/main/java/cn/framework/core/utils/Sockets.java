/**
 * @项目名称: framework
 * @文件名称: Sockets.java
 * @Date: 2015年7月21日
 * @Copyright: 2015 悦畅科技有限公司. All rights reserved.
 *             注意：本内容仅限于悦畅科技有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package cn.framework.core.utils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import cn.framework.core.log.LogProvider;

/**
 * @author wenlai
 *
 */
public final class Sockets {
    
    /**
     * 判断端口是否开启
     * 
     * @param host
     * @param port
     * @return
     */
    public static boolean isOpen(String host, int port) {
        try (Socket socket = new Socket();) {
            socket.connect(new InetSocketAddress(host, port));
            return true;
        }
        catch (Throwable e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 发送简单消息
     * 
     * @param message
     * @param host
     * @param port
     * @return
     */
    public static boolean send(String message, String host, int port) {
        try (Socket socket = new Socket();) {
            socket.connect(new InetSocketAddress(host, port));
            socket.getOutputStream().write(message.getBytes());
            return true;
        }
        catch (Throwable e) {
            LogProvider.getFrameworkErrorLogger().error(e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 打开网络服务
     * 
     * @param port
     */
    public static void startServer(int port) {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open();) { // 获得一个ServerSocket通道
            serverChannel.configureBlocking(false);// 设置通道为非阻塞
            serverChannel.socket().bind(new InetSocketAddress(port)); // 将该通道对应的ServerSocket绑定到port端口
            Selector selector = Selector.open();// 获得一个通道管理器
            // 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
            // 当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {// 轮询访问selector
                          // 当注册的事件到达时，方法返回；否则,该方法会一直阻塞
                selector.select();
                // 获得selector中选中的项的迭代器，选中的项为注册的事件
                Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
                while (ite.hasNext()) {
                    SelectionKey key = (SelectionKey) ite.next();
                    // 删除已选的key,以防重复处理
                    ite.remove();
                    // 客户端请求连接事件
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        // 获得和客户端连接的通道
                        SocketChannel channel = server.accept();
                        // 设置成非阻塞
                        channel.configureBlocking(false);
                        // 在这里可以给客户端发送信息哦
                        channel.write(ByteBuffer.wrap(new String("向客户端发送了一条信息").getBytes()));
                        // 在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
                        channel.register(selector, SelectionKey.OP_READ);
                    }
                    else if (key.isReadable()) { // 获得了可读的事件
                        SocketChannel channel = (SocketChannel) key.channel();
                        // 创建读取的缓冲区
                        ByteBuffer buffer = ByteBuffer.allocate(80);
                        buffer.clear();
                        channel.read(buffer);
                        byte[] data = buffer.array();
                        // buffer.position(0);
                        String msg = new String(data).trim();
                        System.out.println("服务端收到信息：" + msg);
                        channel.write(buffer);// 将消息回送给客户端
                    }
                }
            }
        }
        catch (Throwable x) {
            LogProvider.getFrameworkErrorLogger().error(x.getMessage(), x);
        }
    }
    
}
