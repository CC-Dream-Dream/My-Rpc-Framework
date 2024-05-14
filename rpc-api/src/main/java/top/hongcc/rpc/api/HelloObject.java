package top.hongcc.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * description: HelloObject 测试用的api的实体类
 * date: 2024/5/13 18:53
 * author: hcc
 * version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject implements Serializable {

    private Integer id;

    private String message;
}
