package top.hongcc.rpc.serializer;

import javax.sql.rowset.serial.SerialException;

/**
 * description: 通用的序列化反序列化接口
 * author: hcc
 */
public interface CommonSerializer {

    byte[] serialize(Object obj) throws SerialException;

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }

}
