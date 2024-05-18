package top.hongcc.test;


import top.hongcc.rpc.api.ByeByeService;
import top.hongcc.rpc.transport.RpcClient;
import top.hongcc.rpc.transport.RpcClientProxy;
import top.hongcc.rpc.api.HelloObject;
import top.hongcc.rpc.api.HelloService;
import top.hongcc.rpc.transport.netty.client.NettyClient;

/**
 * description: 测试用Netty消费者
 * author: hcc
 */
public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        // 通过传入不同的 Client（SocketClient、NettyClient）来切换客户端不同的发送方式
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        String res2 = helloService.helloTwice(object);
        System.out.println(res);
        System.out.println(res2);
        // 测试负载均衡策略
        for(int i = 0; i < 10; i++){
            System.out.println(helloService.hello(object));
        }
        // bye接口的方法调用
        ByeByeService byeByeService = rpcClientProxy.getProxy(ByeByeService.class);
        System.out.println(byeByeService.bye("Netty Netty"));
    }

}
