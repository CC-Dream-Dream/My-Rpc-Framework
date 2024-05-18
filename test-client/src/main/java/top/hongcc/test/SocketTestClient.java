package top.hongcc.test;

import top.hongcc.rpc.api.ByeByeService;
import top.hongcc.rpc.api.HelloObject;
import top.hongcc.rpc.api.HelloService;
import top.hongcc.rpc.transport.RpcClientProxy;
import top.hongcc.rpc.loadBalancer.RandomLoadBalancer;
import top.hongcc.rpc.serializer.ProtobufSerializer;
import top.hongcc.rpc.transport.socket.client.SocketClient;

/**
 * description: TestClient 测试用消费者
 * author: hcc
 * version: 1.0
 */
public class SocketTestClient {

    public static void main(String[] args) {
        SocketClient client = new SocketClient(new RandomLoadBalancer());
        client.setSerializer(new ProtobufSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(123, "This is a man");
        String res = helloService.hello(object);
        System.out.println(res);
        // bye接口的方法调用
        ByeByeService byeByeService = rpcClientProxy.getProxy(ByeByeService.class);
        System.out.println(byeByeService.bye("Socket Socket"));
    }
}
