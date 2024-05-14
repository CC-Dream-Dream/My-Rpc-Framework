package top.hongcc.test;


import top.hongcc.rpc.RpcClient;
import top.hongcc.rpc.RpcClientProxy;
import top.hongcc.rpc.api.HelloObject;
import top.hongcc.rpc.api.HelloService;
import top.hongcc.rpc.netty.client.NettyClient;

/**
 * description: 测试用Netty消费者
 * author: hcc
 */
public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient("127.0.0.1", 9999);
        // 通过传入不同的 Client（SocketClient、NettyClient）来切换客户端不同的发送方式
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);

    }

}
