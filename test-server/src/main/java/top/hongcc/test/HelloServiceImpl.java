package top.hongcc.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.rpc.api.HelloObject;
import top.hongcc.rpc.api.HelloService;

/**
 * description: HelloServiceImpl
 * date: 2024/5/13 18:58
 * author: hcc
 * version: 1.0
 */
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到: {}", object.getMessage());
        return "这是调用的返回值，id = " + object.getId() + ", hello";
    }

    @Override
    public String helloTwice(HelloObject object) {
        logger.info("接收到: {}", object.getMessage());
        return "这是调用的返回值，id = " + object.getId() + ", hello hello";
    }
}
