package top.hongcc.rpc.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.enumeration.RpcError;
import top.hongcc.rpc.exception.RpcException;

import javax.imageio.spi.RegisterableService;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 默认的服务注册表
 * author: hcc
 * version: 1.0
 */
public class DefaultServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);

    //  static ，这样就能保证全局唯一的注册信息，并且在创建 RpcServer 时也就不需要传入
    // 服务名(接口名）与提供服务的对象（接口实现类名）的对应关系保存
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    // 保存当前有哪些对象已经被注册
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();


    @Override
    public <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if(registeredService.contains(serviceName)){
            return;
        }
        registeredService.add(serviceName);

        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for(Class<?> i : interfaces){
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("向接口: {} 注册服务： {}", interfaces, serviceName);
    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
