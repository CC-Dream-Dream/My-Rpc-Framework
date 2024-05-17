package top.hongcc.rpc.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * description: 轮转
 * author: hcc
 */
public class RoundRibbonLoadBalancer implements LoadBalancer{

    private int index = 0;

    @Override
    public Instance select(List<Instance> instanceList) {
        if(index >= instanceList.size()){
            index = index % instanceList.size();
        }
        return instanceList.get(index++);
    }

}
