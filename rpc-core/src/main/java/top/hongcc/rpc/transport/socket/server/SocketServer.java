package top.hongcc.rpc.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.rpc.handler.RequestHandler;
import top.hongcc.rpc.hook.ShutdownHook;
import top.hongcc.rpc.loadBalancer.LoadBalancer;
import top.hongcc.rpc.provider.ServiceProviderImpl;
import top.hongcc.rpc.registry.NacosServiceRegistry;
import top.hongcc.rpc.serializer.CommonSerializer;
import top.hongcc.rpc.transport.AbstractRpcServer;
import top.hongcc.rpc.util.ThreadPoolFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * description: Socket方式远程方法调用的提供者（服务端）
 * author: hcc
 * version: 1.0
 */
public class SocketServer extends AbstractRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private final ExecutorService threadPool;;
    private CommonSerializer serializer;
    private RequestHandler requestHandler = new RequestHandler();

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices(); // 自动注册服务
    }

    @Override
    public void start(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("服务器启动......");
            // 服务器关闭时自动注销服务
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while((socket = serverSocket.accept()) != null){
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, serviceRegistry, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e){
            logger.error("服务器启动时发生错误: ", e);
        }
    }

}
