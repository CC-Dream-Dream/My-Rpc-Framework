package top.hongcc.rpc.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.enumeration.RpcError;
import top.hongcc.rpc.exception.RpcException;
import top.hongcc.rpc.handler.RequestHandler;
import top.hongcc.rpc.RpcServer;
import top.hongcc.rpc.loadBalancer.LoadBalancer;
import top.hongcc.rpc.provider.ServiceProvider;
import top.hongcc.rpc.provider.ServiceProviderImpl;
import top.hongcc.rpc.registry.NacosServiceRegistry;
import top.hongcc.rpc.registry.ServiceRegistry;
import top.hongcc.rpc.serializer.CommonSerializer;
import top.hongcc.rpc.util.ThreadPoolFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * description: Socket方式远程方法调用的提供者（服务端）
 * author: hcc
 * version: 1.0
 */
public class SocketServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private final ExecutorService threadPool;;
    private final String host;
    private final int port;
    private CommonSerializer serializer;
    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    public SocketServer(String host, int port, LoadBalancer loadBalancer) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry(loadBalancer);
        this.serviceProvider = new ServiceProviderImpl();
    }

    @Override
    public void start(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("服务器启动......");
            Socket socket;
            while((socket = serverSocket.accept()) != null){
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e){
            logger.error("服务器启动时发生错误: ", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

}
