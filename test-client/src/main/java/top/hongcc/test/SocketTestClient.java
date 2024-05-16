package top.hongcc.test;

import top.hongcc.rpc.api.HelloObject;
import top.hongcc.rpc.api.HelloService;
import top.hongcc.rpc.RpcClientProxy;
import top.hongcc.rpc.serializer.ProtobufSerializer;
import top.hongcc.rpc.transport.socket.client.SocketClient;

/**
 * description: TestClient 测试用消费者
 * author: hcc
 * version: 1.0
 */
public class SocketTestClient {

    public static void main(String[] args) {
        SocketClient client = new SocketClient();
        client.setSerializer(new ProtobufSerializer());
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(123, "This is a man");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
