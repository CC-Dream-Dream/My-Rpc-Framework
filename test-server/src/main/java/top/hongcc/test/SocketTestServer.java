package top.hongcc.test;

import top.hongcc.rpc.api.HelloService;
import top.hongcc.rpc.registry.DefaultServiceRegistry;
import top.hongcc.rpc.registry.ServiceRegistry;
import top.hongcc.rpc.RpcServer;
import top.hongcc.rpc.socket.server.SocketServer;

/**
 * description: TestServer 测试服务提供方
 * author: hcc
 * version: 1.0
 */
public class SocketTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);

        SocketServer socketServer = new SocketServer(serviceRegistry);
        socketServer.start(9000);
    }
}
