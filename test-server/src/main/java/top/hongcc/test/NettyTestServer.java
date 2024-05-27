package top.hongcc.test;

import top.hongcc.rpc.annotation.ServiceScan;
import top.hongcc.rpc.transport.netty.server.NettyServer;

/**
 * description: 测试用Netty服务提供者（服务端）
 * author: hcc
 */
@ServiceScan
public class NettyTestServer {

    public static void main(String[] args) {

        NettyServer server = new NettyServer("127.0.0.1", 9980);
        server.start();

    }

}
