package top.hongcc.rpc.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.rpc.handler.RequestHandler;
import top.hongcc.rpc.entity.RpcRequest;
import top.hongcc.rpc.entity.RpcResponse;
import top.hongcc.rpc.registry.ServiceRegistry;
import top.hongcc.rpc.serializer.CommonSerializer;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * description: Socket服务端处理RpcRequest的工作线程
 * author: hcc
 */
public class SocketRequestHandlerThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(SocketRequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public SocketRequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
        this.serializer = serializer;
    }

    /**
     * todo: 根据参数serailizer，使用不同的序列化和反序列化方法
     */
    @Override
    public void run() {
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream((socket.getInputStream()))){
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object result = requestHandler.handle(rpcRequest);
            objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e){
            logger.error("调用或发送时有错误发生：", e);
        }

    }
}
