package top.hongcc.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.hongcc.enumeration.ResponseCode;
import top.hongcc.enumeration.SerializerCode;
import top.hongcc.rpc.entity.RpcRequest;
import top.hongcc.rpc.entity.RpcResponse;

import javax.sql.rowset.serial.SerialException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * description:
 * Kryo 是一个快速序列化/反序列化工具，其使用了字节码生成机制（底层依赖了 ASM 库），因此具有比较好的运行速度。
 * Kryo 序列化出来的结果，是其自定义的、独有的一种格式，不再是 JSON 或者其他现有的通用格式；
 * 而且，其序列化出来的结果是二进制的（即 byte[]；而 JSON 本质上是字符串 String）；二进制数据显然体积更小，序列化、反序列化时的速度也更快。
 * Kryo 一般只用来进行序列化（然后作为缓存，或者落地到存储设备之中）、反序列化，而不用于在多个系统、甚至多种语言间进行数据交换 —— 目前 kryo 也只有 java 实现。
 * author: hcc
 */
public class KryoSerializer implements CommonSerializer{

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    // Kryo 可能存在线程安全问题，推荐放在 ThreadLocal 里，一个线程一个 Kryo
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) throws SerializationException {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)){
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e){
            logger.error("序列化时发生错误: ", e);
            throw new SerializationException("序列化时发生错误");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) throws SerializationException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return o;
        } catch (Exception e) {
            logger.error("反序列化时发生错误:", e);
            throw new SerializationException("反序列化时有错误发生");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
