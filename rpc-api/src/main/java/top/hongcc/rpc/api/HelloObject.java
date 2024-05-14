package top.hongcc.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * description: HelloObject
 * date: 2024/5/13 18:53
 * author: hcc
 * version: 1.0
 */
@Data
@AllArgsConstructor
public class HelloObject implements Serializable {

    private Integer id;

    private String message;
}
