package top.hongcc.test;

import top.hongcc.rpc.api.HelloService;
import top.hongcc.rpc.transport.netty.server.NettyServer;
import top.hongcc.rpc.serializer.KryoSerializer;

/**
 * description: 测试用Netty服务提供者（服务端）
 * author: hcc
 */
public class NettyTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.setSerializer(new KryoSerializer());
        server.publishService(helloService, HelloService.class);
    }

}
