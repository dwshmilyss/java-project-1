package com.yiban.avro.dev;

import com.yiban.avro.entity.User;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
//        String path = AvroDemo.class.getClassLoader().getResource(".").getPath();
//        System.out.println("path = " + path);

//        try {
////            serializeAvroToFile("d:/users.avro");
//            deserializeAvroFromFile("d:/users.avro");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        try {
            byte[] res = serializeAvroToByteArray(createObj());
            deserialzeAvroFromByteArray(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * //三种方法可以创建avro生成的类的对象
     */
    public static List<User> createObj(){
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
        for (User user: userList) {
            dataFileWriter.append(user);
        }
        dataFileWriter.close();
    }


    /**
     * 从文件中反序列化对象
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
     * @param userList
     * @return
     * @throws IOException
     */
    public static byte[] serializeAvroToByteArray(List<User> userList) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DatumWriter<User> userDatumWriter = new SpecificDatumWriter<>(User.class);
        DataFileWriter<User> dataFileWriter = new DataFileWriter<>(userDatumWriter);
        dataFileWriter.create(userList.get(0).getSchema(),baos);
        for (User user: userList) {
            dataFileWriter.append(user);
        }
        dataFileWriter.close();
        return baos.toByteArray();
    }

    /**
     * 从byte数组中反序列化成对象
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
