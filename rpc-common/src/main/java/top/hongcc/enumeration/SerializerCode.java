package top.hongcc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description: 字节流中标识序列化和反序列化器
 * author: hcc
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {

    JSON(1);

    private final int code;

}