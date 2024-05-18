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
import top.hongcc.rpc.codec.CommonDecoder;
import top.hongcc.rpc.codec.CommonEncoder;
import top.hongcc.rpc.loadBalancer.LoadBalancer;
import top.hongcc.rpc.provider.ServiceProvider;
import top.hongcc.rpc.registry.ServiceRegistry;
import top.hongcc.rpc.serializer.CommonSerializer;
import top.hongcc.rpc.serializer.KryoSerializer;
import top.hongcc.rpc.registry.NacosServiceRegistry;
import top.hongcc.rpc.provider.ServiceProviderImpl;
import top.hongcc.rpc.transport.AbstractRpcServer;

/**
 * description: NIO方式服务提供侧
 * author: hcc
 */
public class NettyServer extends AbstractRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private CommonSerializer serializer;

    public NettyServer(String host, int port, LoadBalancer loadBalancer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry(loadBalancer);
        serviceProvider = new ServiceProviderImpl();
        scanServices(); // 自动注册服务
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