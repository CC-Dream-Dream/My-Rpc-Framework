package top.hongcc.rpc.transport;

import top.hongcc.rpc.serializer.CommonSerializer;

/**
 * description: 服务器类通用接口
 * author: hcc
 * version: 1.0
 */
public interface RpcServer {

    int DEFAULT_SERIALIZER = CommonSerializer.DEFAULT_SERIALIZER;

    void start();

    /**
     * 用于向 Nacos 注册服务
     * @param service
     * @param serviceName
     * @param <T>
     */
    <T> void publishService(Object service, String serviceName);

}