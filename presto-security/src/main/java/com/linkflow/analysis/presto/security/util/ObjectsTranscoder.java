package com.linkflow.analysis.presto.security.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @auther WEI.DUAN
 * @date 2017/5/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ObjectsTranscoder<M extends Serializable> extends SerializeTranscoder {
    private static final Logger logger = LoggerFactory.getLogger(ObjectsTranscoder.class);

    public static ObjectsTranscoder getObjectsTranscoder() {
        return new ObjectsTranscoder();
    }

    @SuppressWarnings("unchecked")
    @Override
    public byte[] serialize(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] result = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            M m = (M) value;
            os.writeObject(m);
            os.close();
            bos.close();
            result = bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Non-serializable object", e);
        } finally {
            close(os);
            close(bos);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public M deserialize(byte[] in) {
        M result = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if (in != null) {
                bis = new ByteArrayInputStream(in);
                is = new ObjectInputStream(bis);
                result = (M) is.readObject();
                is.close();
                bis.close();
            }
        } catch (IOException e) {
            logger.error(String.format("Caught IOException decoding %d bytes of data",
                    in == null ? 0 : in.length) + e);
        } catch (ClassNotFoundException e) {
            logger.error(String.format("Caught CNFE decoding %d bytes of data",
                    in == null ? 0 : in.length) + e);
        } finally {
            close(is);
            close(bis);
        }
        return result;
    }
}
