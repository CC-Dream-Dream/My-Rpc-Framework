package top.hongcc.rpc.transport.socket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.enumeration.ResponseCode;
import top.hongcc.enumeration.RpcError;
import top.hongcc.rpc.transport.RpcClient;
import top.hongcc.rpc.entity.RpcRequest;
import top.hongcc.rpc.entity.RpcResponse;
import top.hongcc.rpc.exception.RpcException;
import top.hongcc.rpc.loadBalancer.LoadBalancer;
import top.hongcc.rpc.registry.NacosServiceRegistry;
import top.hongcc.rpc.registry.ServiceRegistry;
import top.hongcc.rpc.serializer.CommonSerializer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * description: Socket方式远程方法调用的消费者（客户端）
 * author: hcc
 */
public class SocketClient implements RpcClient{

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final ServiceRegistry serviceRegistry;

    private CommonSerializer serializer;

    public SocketClient(LoadBalancer loadBalancer) {
        this.serviceRegistry = new NacosServiceRegistry(loadBalancer);
    }

    /**
     * 直接使用Java的序列化方式，通过Socket传输
     * @param rpcRequest
     * @return
     */
    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
            if(rpcResponse == null) {
                logger.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if(rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                logger.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            return rpcResponse.getData();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生：", e);
            throw new RpcException("服务调用失败: ", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
