package top.hongcc.rpc.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * description: 随机
 * author: hcc
 */
public class RandomLoadBalancer implements LoadBalancer{

    @Override
    public Instance select(List<Instance> instanceList) {
        return instanceList.get(new Random().nextInt(instanceList.size()));
    }

}
