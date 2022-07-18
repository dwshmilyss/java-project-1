package com.yiban.serde.protobuf.dev;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.yiban.serde.protobuf.model.UserActionLog;
import com.yiban.serde.protobuf.model.UserActionLogModel;
import com.yiban.serde.protobuf.pojo.MyUserActionLog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ProtoBufDemo {
    public static void main(String[] args) throws IOException {
        test();
//        protoObjectToJson();
//        jsonToProtoObject();
    }

    private static void test() throws IOException {
        UserActionLog.Builder builder = UserActionLog.newBuilder();
        builder.setUserName("001");
        builder.setActionType("aaa");
        builder.setIpAddress("abcd");
        builder.setGender(14);
        builder.setProvience("henan");

        UserActionLog userActionLog = builder.build();
        System.out.println("userActionLog.getUserName() = " + userActionLog.getUserName());
        System.out.println("userActionLog.getIpAddress() = " + userActionLog.getIpAddress());
        System.out.println("userActionLog.getProvience() = " + userActionLog.getProvience());
        System.out.println("------------");
        //模拟序列化
        byte[] bytes = userActionLog.toByteArray();
        System.out.println("bytes.length = " + bytes.length);//打印proto序列化后的字节大小
        //对象数据和上面一样，普通的java bean序列化后的字节要比proto大很多
        MyUserActionLog myUserActionLog = new MyUserActionLog("001", "aaa", "abcd", 14, "henan");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(myUserActionLog);
        System.out.println("byteArrayOutputStream.toByteArray().length = " + byteArrayOutputStream.toByteArray().length);
        //模拟反序列化
        try {
            UserActionLog userActionLog1 = UserActionLog.parseFrom(bytes);
            System.out.println("userActionLog1.getUserName() = " + userActionLog1.getUserName());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * proto对象转json字符串
     */
    private static void protoObjectToJson() {
        UserActionLog.Builder builder = UserActionLog.newBuilder();
        builder.setUserName("001");
        builder.setActionType("aaa");
        builder.setIpAddress("abcd");
        builder.setGender(14);
        builder.setProvience("henan");

        UserActionLog userActionLog = builder.build();
        JsonFormat.Printer printer = JsonFormat.printer();
        String print = "";
        try {
            print = printer.print(userActionLog);
            System.out.println(print);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * json字符串转proto对象
     */
    private static void jsonToProtoObject() {
        String jsonStr = "{\n" +
                "  \"userName\": \"001\",\n" +
                "  \"actionType\": \"aaa\",\n" +
                "  \"ipAddress\": \"abcd\",\n" +
                "  \"gender\": 14,\n" +
                "  \"provience\": \"henan\"\n" +
                "}";
        //to Object
        JsonFormat.Parser parser = JsonFormat.parser();
        try {
            UserActionLog.Builder builder = UserActionLog.newBuilder();
            parser.merge(jsonStr, builder);
            System.out.println(builder.build().toString());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

}
