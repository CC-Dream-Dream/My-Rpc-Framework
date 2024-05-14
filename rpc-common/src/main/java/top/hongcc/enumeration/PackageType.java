package top.hongcc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description: PackageType 发送包的类型：请求 or 响应
 * author: hcc
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;

}
