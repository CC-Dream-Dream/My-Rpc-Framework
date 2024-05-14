package top.hongcc.test;

import top.hongcc.rpc.api.HelloService;
import top.hongcc.rpc.netty.server.NettyServer;
import top.hongcc.rpc.registry.DefaultServiceRegistry;
import top.hongcc.rpc.registry.ServiceRegistry;

/**
 * description: 测试用Netty服务提供者（服务端）
 * author: hcc
 */
public class NettyTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9999);
    }

}
