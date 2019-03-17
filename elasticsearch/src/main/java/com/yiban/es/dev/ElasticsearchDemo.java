package com.yiban.es.dev;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @auther WEI.DUAN
 * @date 2019/3/17
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ElasticsearchDemo {
    /**
     * 建立客户端连接
     * @return
     */
    public static RestHighLevelClient getClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.200.128", 9200, "http")));
        //这里可以有多个客户端地址（集群嘛）
//                        new HttpHost("192.168.200.128", 9200, "http")));
//                        new HttpHost("192.168.200.128", 9200, "http")));
        return client;
    }


    /**
     * 创建索引
     */
    public static void createIndex(RestHighLevelClient client){
         //1. 创建索引名（相当于mysql中的表）
        CreateIndexRequest request = new CreateIndexRequest("student");
    }
}