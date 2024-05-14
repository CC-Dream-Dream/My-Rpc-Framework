package top.hongcc.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.rpc.entity.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * description: 客户端类通用接口
 * author: hcc
 * version: 1.0
 */
public interface RpcClient {

    Object sendRequest(RpcRequest rpcRequest);

}
