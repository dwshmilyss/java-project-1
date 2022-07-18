package com.yiban.kafka.kafka_0_8.api;

/**
 * 常量类
 *
 * @auther WEI.DUAN
 * @date 2017/5/17
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Constant {

    public static final int TIMEOUT = 0;
    public static final int BUFFERSIZE = 0;

    private String topicName;
    private String partitionId;
    public String CLIENTID = "Client_";

    public Constant(String topicName, String partitionId) {
        this.topicName = topicName;
        this.partitionId = partitionId;
        CLIENTID = CLIENTID.concat(topicName).concat("_").concat(partitionId);
    }

    public static void main(String[] args){
        Constant constant = new Constant("1","2");
        System.out.println(constant.CLIENTID);
    }
}
