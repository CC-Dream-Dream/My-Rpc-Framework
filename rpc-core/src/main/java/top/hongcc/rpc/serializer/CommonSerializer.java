package top.hongcc.rpc.serializer;

import javax.sql.rowset.serial.SerialException;

/**
 * description: 通用的序列化反序列化接口
 * author: hcc
 */
public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer PROTOBUF_SERIALIZER = 2;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    byte[] serialize(Object obj) throws SerialException;

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }

}
