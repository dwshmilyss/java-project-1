package test.java.com.yiban.test;

import com.yiban.serde.avro.dev.AvroDemo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class TestUnit {
    @Test
    public void testResourcesPath() {
//        String path = AvroDemo.class.getClassLoader().getResource("UserActionLog.avsc").getPath();
        String path = AvroDemo.class.getResource("/UserActionLog.avsc").getPath();
        System.out.println("path = " + path);
    }

    @Test
    public void testResourcesStream() throws IOException {
        //用getClassLoader就用相对路径
//        InputStream inputStream = AvroDemo.class.getClassLoader().getResourceAsStream("UserActionLog.avsc");
        //不用getClassLoader就用绝对路径(两种方式都可以)
        InputStream inputStream = AvroDemo.class.getResourceAsStream("/UserActionLog.avsc");
        System.out.println("path = " + inputStream.read());
    }
}
