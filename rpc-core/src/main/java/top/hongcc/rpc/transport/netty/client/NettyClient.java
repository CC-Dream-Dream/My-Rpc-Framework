package top.hongcc.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.enumeration.RpcError;
import top.hongcc.rpc.RpcClient;
import top.hongcc.rpc.entity.RpcRequest;
import top.hongcc.rpc.entity.RpcResponse;
import top.hongcc.rpc.exception.RpcException;
import top.hongcc.rpc.loadBalancer.LoadBalancer;
import top.hongcc.rpc.registry.ServiceRegistry;
import top.hongcc.rpc.serializer.CommonSerializer;
import top.hongcc.rpc.registry.NacosServiceRegistry;
import top.hongcc.rpc.util.RpcMessageChecker;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * NIO方式消费侧客户端类
 * author hcc
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final ServiceRegistry serviceRegistry;
    private static final Bootstrap bootstrap;

    private CommonSerializer serializer;

    /**
     * 配置好了 Netty 客户端，等待发送数据时启动
     */
    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this.serviceRegistry = new NacosServiceRegistry(loadBalancer);
    }


    /**
     * channel 将 RpcRequest 对象写出，并且等待服务端返回的结果。
     * 注意这里的发送是非阻塞的，所以发送后会立刻返回，而无法得到结果。这里通过 AttributeKey 的方式阻塞获得返回结果：
     * @param rpcRequest
     * @return
     */
    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try {
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
            System.out.println("处理请求的ip和port: " + inetSocketAddress.getAddress().getHostName() + inetSocketAddress.getPort());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if(channel.isActive()) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        logger.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
                channel.closeFuture().sync();

                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                logger.info("请求和响应id:{}, {}", rpcRequest.getRequestId(), rpcResponse.getRequestId());
                RpcMessageChecker.check(rpcRequest, rpcResponse); // 校验请求和响应是否正确
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生: ", e);
        }
        return null;
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

}