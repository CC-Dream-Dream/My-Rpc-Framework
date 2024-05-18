package top.hongcc.rpc.registry;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.protostuff.Rpc;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.enumeration.RpcError;
import top.hongcc.rpc.exception.RpcException;
import top.hongcc.rpc.loadBalancer.LoadBalancer;
import top.hongcc.rpc.loadBalancer.RandomLoadBalancer;

/**
 * description: NocasRegistry Nacos服务注册中心
 * author: hcc
 */
public class NacosServiceRegistry implements ServiceRegistry{


    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;
    private  final LoadBalancer loadBalancer;

    public NacosServiceRegistry(LoadBalancer loadBalancer) {
        if(Objects.isNull(loadBalancer)){
            this.loadBalancer = new RandomLoadBalancer();
        }else {
            this.loadBalancer = loadBalancer;
        }
    }

    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
            logger.info("连接到Nacos");
        } catch (NacosException e){
            logger.error("连接到Nacos时发生错误: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            logger.info("hostName: " + inetSocketAddress.getAddress().getHostName());
            namingService.registerInstance(serviceName, inetSocketAddress.getAddress().getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e){
            logger.error("注册服务时发生错误: ", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            logger.info("instances数量: " + instances.size());
            if(instances.size() == 0) {
                logger.error("找不到对应的服务: " + serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            // 负载均衡
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时发生错误: ", e);
            throw new RpcException(RpcError.FAILED_TO_GET_SERVICE);
        }
    }
}
