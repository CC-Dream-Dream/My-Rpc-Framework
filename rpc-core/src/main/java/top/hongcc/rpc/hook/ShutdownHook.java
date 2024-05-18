package top.hongcc.rpc.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.rpc.util.ThreadPoolFactory;
import top.hongcc.rpc.util.NacosUtil;

/**
 * description: 添加钩子函数，关闭服务端时自动注销服务和关闭线程池
 * author: hcc
 */
public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("关闭服务端后将自动注销所有服务");
        // 添加钩子函数
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry(); // 自动注销服务
            ThreadPoolFactory.shutDownAll(); // 关闭线程池
        }));
    }

}
