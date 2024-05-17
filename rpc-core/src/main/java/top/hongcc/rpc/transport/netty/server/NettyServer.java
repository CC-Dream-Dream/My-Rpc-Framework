package top.hongcc.rpc.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.enumeration.RpcError;
import top.hongcc.rpc.RpcServer;
import top.hongcc.rpc.codec.CommonDecoder;
import top.hongcc.rpc.codec.CommonEncoder;
import top.hongcc.rpc.exception.RpcException;
import top.hongcc.rpc.loadBalancer.LoadBalancer;
import top.hongcc.rpc.provider.ServiceProvider;
import top.hongcc.rpc.registry.ServiceRegistry;
import top.hongcc.rpc.serializer.CommonSerializer;
import top.hongcc.rpc.serializer.KryoSerializer;
import top.hongcc.rpc.registry.NacosServiceRegistry;
import top.hongcc.rpc.provider.ServiceProviderImpl;

import java.net.InetSocketAddress;

/**
 * description: NIO方式服务提供侧
 * author: hcc
 */
public class NettyServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final String host;
    private final int port;

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    private CommonSerializer serializer;

    public NettyServer(String host, int port, LoadBalancer loadBalancer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry(loadBalancer);
        serviceProvider = new ServiceProviderImpl();
    }

    /**
     * 将服务保存在本地的注册表，同时注册到 Nacos 上
     * todo: 这里的实现是注册完一个服务后直接调用 start() 方法，这是个不太好的实现……导致一个服务端只能注册一个服务，之后可以多注册几个然后再手动调用 start() 方法。
     * @param service
     * @param serviceClass
     * @param <T>
     */
    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service); // 注册到本地
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port)); // 注册到注册中心Nacos
        start();
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 编码器，解码器和数据处理器
//                            pipeline.addLast(new CommonEncoder(new JsonSerializer()));
                            pipeline.addLast(new CommonEncoder(new KryoSerializer()));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

}