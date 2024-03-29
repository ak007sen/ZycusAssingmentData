
package com.akash.diskmap.utils;

import com.caucho.hessian.io.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;

public class Hessian2ObjectConverter implements ObjectConverter {
    private Hessian2Output os;
    private SerializerFactory factory;

    public Hessian2ObjectConverter() {
        this.os = new Hessian2Output(null);
        this.factory = new SerializerFactory();
        factory.addFactory(new AbstractSerializerFactory(){
            @Override
            public Serializer getSerializer(Class cl) throws HessianProtocolException {
                if(cl.isAssignableFrom(BigDecimal.class)){
                    return StringValueSerializer.SER;
                }
                return null;
            }

            @Override
            public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
                if(cl.isAssignableFrom(BigDecimal.class)){
                    return new StringValueDeserializer(BigDecimal.class);
                }
                return null;
            }
        });
        this.os.setSerializerFactory(factory);


    }

    @Override
    public byte[] serialize(Serializable object) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        synchronized (os) {
            os.init(buffer);
            os.writeObject(object);
            os.close();
        }
        return buffer.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] buffer) {
        T v;
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
            Hessian2Input is = new Hessian2Input(stream);
            is.setSerializerFactory(factory);
            v = (T) is.readObject();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return v;
    }
}
