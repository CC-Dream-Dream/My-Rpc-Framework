package top.hongcc.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.rpc.RpcClient;
import top.hongcc.rpc.entity.RpcRequest;
import top.hongcc.rpc.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * description: RpcClientProxy 客户端动态代理
 * RPC的本地存根就是通过动态代理实现的。 本地存根最重要的作用就是让远程调用像本地调用一样直接进行函数调用，无须关心地址空间隔离、
 * 函数不匹配等问题。本地存根让调用者不需要感知是如何发生RPC调用的，它屏蔽了下游的编解码、序列化、网络通道等一切细节，
 * 让调用者认为只是发起了一次本地函数调用。
 * author: hcc
 * version: 1.0
 */
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);
    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    /**
     * getProxy()方法来生成代理对象
     * 读取需要代理的服务接口的元数据信息，根据获取的类信息生成对应的代理类字节码和实例化代理类，以及创建类对象的目的
     * @param clazz
     * @return
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * invoke()方法，来指明 代理对象的方法 被调用时的动作
     * 在这里，我们需要生成一个RpcRequest对象，发送出去，然后返回从服务端接收到的结果即可：
     * @param proxy the proxy instance that the method was invoked on
     *
     * @param method the {@code Method} instance corresponding to
     * the interface method invoked on the proxy instance.  The declaring
     * class of the {@code Method} object will be the interface that
     * the method was declared in, which may be a superinterface of the
     * proxy interface that the proxy class inherits the method through.
     *
     * @param args an array of objects containing the values of the
     * arguments passed in the method invocation on the proxy instance,
     * or {@code null} if interface method takes no arguments.
     * Arguments of primitive types are wrapped in instances of the
     * appropriate primitive wrapper class, such as
     * {@code java.lang.Integer} or {@code java.lang.Boolean}.
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes());
        return client.sendRequest(rpcRequest);
    }
}
