package com.yiban.kafka.kafka_0_8.api;

import java.io.Serializable;
import java.util.HashMap;

/**
 * kafka记录类
 *
 * @auther WEI.DUAN
 * @date 2017/5/17
 * @website http://blog.csdn.net/dwshmilyss
 */
public class KafkaTopicOffset implements Serializable {
    private static final long serialVersionUID = 113144571025441911L;
    private String topicName;
    private HashMap<Integer,Long> offsetList;
    private HashMap<Integer,String> leaderList;

    public KafkaTopicOffset(String topicName){
        this.topicName = topicName;
        this.offsetList = new HashMap<Integer,Long>();
        this.leaderList = new HashMap<Integer, String>();
    }

    public String getTopicName() {
        return topicName;
    }

    public HashMap<Integer, Long> getOffsetList() {
        return offsetList;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setOffsetList(HashMap<Integer, Long> offsetList) {
        this.offsetList = offsetList;
    }

    public HashMap<Integer, String> getLeaderList() {
        return leaderList;
    }

    public void setLeaderList(HashMap<Integer, String> leaderList) {
        this.leaderList = leaderList;
    }

    public String toString(){
        return "topic:"+topicName+",offsetList:"+this.offsetList+",leaderList:"+this.leaderList;
    }
}
