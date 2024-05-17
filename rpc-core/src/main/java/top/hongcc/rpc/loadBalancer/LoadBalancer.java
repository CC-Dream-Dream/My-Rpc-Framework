package top.hongcc.rpc.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * description: 负载均衡接口
 * author: hcc
 */
public interface LoadBalancer {

    Instance select(List<Instance> instanceList);

}
