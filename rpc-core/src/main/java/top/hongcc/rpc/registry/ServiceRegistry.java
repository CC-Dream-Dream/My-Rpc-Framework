package top.hongcc.rpc.registry;

import java.net.InetSocketAddress;

/**
 * description: ServiceRegistry 服务注册中心通用接口
 * author: hcc
 */
public interface ServiceRegistry {

    /**
     * 将一个服务注册进注册表
     * @param serviceName 服务名称
     * @param inetSocketAddress 提供服务的地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);


}
