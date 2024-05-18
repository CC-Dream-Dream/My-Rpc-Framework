package top.hongcc.test;

import top.hongcc.rpc.annotation.ServiceScan;
import top.hongcc.rpc.api.HelloService;
import top.hongcc.rpc.loadBalancer.RandomLoadBalancer;
import top.hongcc.rpc.serializer.ProtobufSerializer;
import top.hongcc.rpc.transport.socket.server.SocketServer;

/**
 * description: TestServer 测试服务提供方
 * author: hcc
 * version: 1.0
 */
@ServiceScan
public class SocketTestServer {

    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer("127.0.0.1", 9998, new RandomLoadBalancer());
        socketServer.setSerializer(new ProtobufSerializer());
        socketServer.start();
    }

}
