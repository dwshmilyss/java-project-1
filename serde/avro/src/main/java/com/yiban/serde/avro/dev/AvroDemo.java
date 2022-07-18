package com.yiban.serde.avro.dev;


import com.yiban.serde.avro.entity.User;
import com.yiban.serde.avro.entity.UserActionLog;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * demo
 *
 * @auther WEI.DUAN
 * @date 2018/8/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class AvroDemo<T> {
    public static void main(String[] args) {
//        try {
////            serializeAvroToFile("d:/users.avro");
//            deserializeAvroFromFile("d:/users.avro");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        try {
//            byte[] res = serializeAvroToByteArray(createObj());
//            deserialzeAvroFromByteArray(res);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
//            testGenericSer();
            testGenericDeSer();
//            testSpecificSer();
//            testSpecificDeSer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 提前生成类的avro序列化
     * 579ms
     * @throws IOException
     */
    private static void testSpecificSer() throws IOException {
        org.apache.commons.lang3.time.StopWatch stopWatch = org.apache.commons.lang3.time.StopWatch.createStarted();//commons-lang3
        SpecificDatumWriter specificDatumWriter = new SpecificDatumWriter<UserActionLog>();
        DataFileWriter fileWriter = new DataFileWriter(specificDatumWriter);
        fileWriter.create(UserActionLog.getClassSchema(), new File("/Users/edz/Desktop/UserActionLog_Specific.avro"));
        for (int i = 0; i < 1000000; i++) {
            UserActionLog userActionLog = new UserActionLog();
            userActionLog.setUserName("001");
            userActionLog.setActionType("aaa");
            userActionLog.setIpAddress("abcd");
            userActionLog.setGender(15);
            userActionLog.setProvience("henan");
            fileWriter.append(userActionLog);
        }
        fileWriter.flush();
        fileWriter.close();
        stopWatch.stop();
        System.out.println("stopWatch.getTime() = " + stopWatch.getTime());
    }

    /**
     * 提前生成类的avro反序列化
     * 673ms
     * @throws IOException
     */
    private static void testSpecificDeSer() throws IOException {
        org.apache.commons.lang3.time.StopWatch stopWatch = org.apache.commons.lang3.time.StopWatch.createStarted();//commons-lang3
        File file = new File("/Users/edz/Desktop/UserActionLog_Specific.avro");
        DatumReader<UserActionLog> datumReader = new SpecificDatumReader<>();
        DataFileReader<UserActionLog> dataFileReader = new DataFileReader(file, datumReader);
        int cn = 0;
        UserActionLog readUserActionLog = null;
        while (dataFileReader.hasNext()) {
            readUserActionLog = dataFileReader.next();
            cn += 1;
            if (cn < 10) {
                System.out.println("aaa -> " + readUserActionLog.toString());
            }
        }
        dataFileReader.close();
        stopWatch.stop();
        System.out.println("cn = " + cn);
        System.out.println("stopWatch cost time = " + stopWatch.getTime());
    }

    /**
     * 不用提前生成类的avro序列化
     * 626ms
     * @throws IOException
     */
    private static void testGenericSer() throws IOException {
        org.apache.commons.lang3.time.StopWatch stopWatch = org.apache.commons.lang3.time.StopWatch.createStarted();//commons-lang3
        InputStream avscInputStream = AvroDemo.class.getClassLoader().getResourceAsStream("UserActionLog.avsc");
        Schema schema = new Schema.Parser().parse(avscInputStream);
        GenericDatumWriter genericDatumWriter = new GenericDatumWriter(schema);
        DataFileWriter fileWriter = new DataFileWriter(genericDatumWriter);
        fileWriter.create(schema, new File("/Users/edz/Desktop/UserActionLog.avro"));
//        StopWatch stopWatch = new StopWatch();//commons-lang
//        stopWatch.start();

//        StopWatch stopWatch = new StopWatch(UUID.randomUUID().toString());//spring-core
//        stopWatch.start();
        for (int i = 0; i < 1000000; i++) {
            GenericRecord userActionLog = new GenericData.Record(schema);
            userActionLog.put("userName", "001");
            userActionLog.put("actionType", "aaa");
            userActionLog.put("ipAddress", "abcd");
            userActionLog.put("gender", 15);
            userActionLog.put("provience", "henan");
            fileWriter.append(userActionLog);
        }
        fileWriter.close();
        stopWatch.stop();
//        System.out.println("stopWatch.prettyPrint() = " + stopWatch.prettyPrint());
        System.out.println("stopWatch.getTime() = " + stopWatch.getTime());
    }

    /**
     * 不用提前生成类的avro反序列化
     * 492ms 比提前生成类的还要快一点 费解？
     * @throws IOException
     */
    private static void testGenericDeSer() throws IOException {
        org.apache.commons.lang3.time.StopWatch stopWatch = org.apache.commons.lang3.time.StopWatch.createStarted();//commons-lang3
        InputStream avscInputStream = AvroDemo.class.getClassLoader().getResourceAsStream("UserActionLog.avsc");
        Schema schema = new Schema.Parser().parse(avscInputStream);
        GenericRecord userActionLog = null;
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader(new File("/Users/edz/Desktop/UserActionLog.avro"), datumReader);
        int cn = 0;
        while (dataFileReader.hasNext()) {
            userActionLog = dataFileReader.next();
            cn += 1;
            if (cn < 10) {
                System.out.println("aaa -> " + userActionLog.toString());
            }
        }
        dataFileReader.close();
        stopWatch.stop();
        System.out.println("cn = " + cn);
        System.out.println("stopWatch cost time = " + stopWatch.getTime());
    }

    /**
     * 三种方法可以创建avro生成的类的对象
     */
    public static List<User> createObj() {
        List<User> userList = new ArrayList<User>();
        //1.使用构造函数
        User user1 = new User("user1", 10, "red");

        //2.使用setter方法
        User user2 = new User();
        user2.setName("user2");
        user2.setFavoriteNumber(11);
        user2.setFavoriteColor("white");

        //3.使用avro自带的build方法
        User user3 = User.newBuilder()
                .setName("user3")
                .setFavoriteNumber(12)
                .setFavoriteColor("black")
                .build();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        return userList;
    }

    /**
     * 把User对象序列化到文件中
     */
    public static void serializeAvroToFile(String fileName) throws IOException {
        DatumWriter<User> userDatumWriter = new SpecificDatumWriter<>(User.class);
        DataFileWriter<User> dataFileWriter = new DataFileWriter<>(userDatumWriter);
        List<User> userList = createObj();
        dataFileWriter.create(userList.get(0).getSchema(), new File(fileName));
        for (User user : userList) {
            dataFileWriter.append(user);
        }
        dataFileWriter.close();
    }


    /**
     * 从文件中反序列化对象
     *
     * @param fileName
     * @throws IOException
     */
    public static void deserializeAvroFromFile(String fileName) throws IOException {
        File file = new File(fileName);
        DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
        DataFileReader<User> dataFileReader = new DataFileReader<User>(file, userDatumReader);
        User user = null;
        System.out.println("----------------deserializeAvroFromFile-------------------");
        while (dataFileReader.hasNext()) {
            user = dataFileReader.next(user);
            System.out.println(user);
        }
    }

    /**
     * 序列化对象成byte 数组
     *
     * @param userList
     * @return
     * @throws IOException
     */
    public static byte[] serializeAvroToByteArray(List<User> userList) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DatumWriter<User> userDatumWriter = new SpecificDatumWriter<>(User.class);
        DataFileWriter<User> dataFileWriter = new DataFileWriter<>(userDatumWriter);
        dataFileWriter.create(userList.get(0).getSchema(), baos);
        for (User user : userList) {
            dataFileWriter.append(user);
        }
        dataFileWriter.close();
        return baos.toByteArray();
    }

    /**
     * 从byte数组中反序列化成对象
     *
     * @param usersByteArray
     * @throws IOException
     */
    public static void deserialzeAvroFromByteArray(byte[] usersByteArray) throws IOException {
        SeekableByteArrayInput sbai = new SeekableByteArrayInput(usersByteArray);
        DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
        DataFileReader<User> dataFileReader = new DataFileReader<User>(sbai, userDatumReader);
        System.out.println("----------------deserialzeAvroFromByteArray-------------------");
        User readUser = null;
        while (dataFileReader.hasNext()) {
            readUser = dataFileReader.next(readUser);
            System.out.println(readUser);
        }
    }
}
