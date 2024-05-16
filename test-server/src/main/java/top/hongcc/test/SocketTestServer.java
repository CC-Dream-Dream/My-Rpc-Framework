package top.hongcc.test;

import top.hongcc.rpc.api.HelloService;
import top.hongcc.rpc.serializer.ProtobufSerializer;
import top.hongcc.rpc.transport.socket.server.SocketServer;

/**
 * description: TestServer 测试服务提供方
 * author: hcc
 * version: 1.0
 */
public class SocketTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        SocketServer socketServer = new SocketServer("127.0.0.1", 9998);
        socketServer.setSerializer(new ProtobufSerializer());
        socketServer.publishService(helloService, HelloService.class);
    }
}
