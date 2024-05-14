package top.hongcc.rpc.exception;

import top.hongcc.enumeration.RpcError;

/**
 * description: RpcException Rpc调用异常
 * author: hcc
 * version: 1.0
 */
public class RpcException extends RuntimeException{

    public RpcException(RpcError error) {
        super(error.getMessage());
    }

    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

}
