package top.hongcc.test;

import top.hongcc.rpc.api.HelloObject;
import top.hongcc.rpc.api.HelloService;
import top.hongcc.rpc.client.RpcClientProxy;

/**
 * description: TestClient 测试用消费者
 * author: hcc
 * version: 1.0
 */
public class TestClient {

    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(123, "This is a man");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
