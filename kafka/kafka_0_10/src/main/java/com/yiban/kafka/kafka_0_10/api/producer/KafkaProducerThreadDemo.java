package com.yiban.kafka.kafka_0_10.api.producer;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.message.Message;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import scala.util.parsing.json.JSONFormat;
import scala.util.parsing.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * kafka producer
 *
 * @auther WEI.DUAN
 * @create 2017/5/14
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class KafkaProducerThreadDemo {
    private static final Logger logger = Logger.getLogger(KafkaProducerThreadDemo.class);

    private static final String BOOTSTRAP = "localhost:9092";
    private static final String TOPICNAME = "amazon_reviews_us_Books_v1_00";
    //多个副本的确认方式
    // 0 不确认，即发送给broker就不管了，也不知道是否成功
    // 1 只有leader确认，不管其他slave
    // 2 leader和所有的slave都确认才算成功，这种方式性能最差，但是数据可靠性最高
    private static final int ACKS = 1;
    private static final int RETRIES = 0;
    private static final int BATCHSIZE = 0;

    private static final int NUMPARTITIONS = 1;

    public static void main(String[] args) throws JsonProcessingException {
        // 修改kafka日志输出级别(只针对当前的console)
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("kafka").setLevel(Level.WARN);
        String filePath = KafkaProducerThreadDemo.class.getClassLoader().getResource("amazon_reviews_us_Books_v1_00.tsv").getPath();
        List<AmazonReviewsUSBooks> amazonReviewsUSBooksList = readCSV(filePath);
        System.out.println("amazonReviewsUSBooksList.size() = " + amazonReviewsUSBooksList.size());
        KafkaProcuderThread kafkaProcuderThread = new KafkaProcuderThread(BOOTSTRAP, ACKS, RETRIES, TOPICNAME);
        kafkaProcuderThread.amazonReviewsUSBooksList = amazonReviewsUSBooksList;
        kafkaProcuderThread.pushMsg();
    }

    static class KafkaProcuderThread {

        private KafkaProducer kafkaProducer;
        private String topicName;
        private List<AmazonReviewsUSBooks> amazonReviewsUSBooksList;

        public KafkaProcuderThread(String bootstrap, int acks, int retries, String topicName) {
            Properties props = new Properties();
            props.put("bootstrap.servers", bootstrap);
            props.put("acks", String.valueOf(acks));
            props.put("retries", String.valueOf(retries));
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            kafkaProducer = new KafkaProducer(props);
            this.topicName = topicName;
        }

        private void pushMsg() {
            try {
                int i = 0;
                for (AmazonReviewsUSBooks amazonReviewsUSBooks  : amazonReviewsUSBooksList) {
                    String jsonString = JSON.toJSONString(amazonReviewsUSBooks);
                    ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, String.valueOf(i), jsonString);
                    kafkaProducer.send(record, new Callback() {
                        //注册回调方法
                        public void onCompletion(RecordMetadata metadata, Exception e) {
                            if (e != null)
                                e.printStackTrace();
//                            System.out.printf("Send record partition:%d, offset:%d, keysize:%d, valuesize:%d %n",
//                                    metadata.partition(), metadata.offset(), metadata.serializedKeySize(),
//                                    metadata.serializedValueSize());
                            System.out.println(Thread.currentThread().getName() + " -- message send to partition " + metadata.partition() + ", offset: " + metadata.offset());
                        }
                    });
                    i++;
                    if (i == 100) {
                        Thread.sleep(100);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> transTSVToJSON(List<AmazonReviewsUSBooks> dataList) throws JsonProcessingException {
        List<String> jsonList = new java.util.ArrayList<String>(10000);
        int cnt = dataList.size();
        for (int i = 1; i < cnt; i++) {//从1开始是为了去掉header
            AmazonReviewsUSBooks amazonReviewsUSBooks = dataList.get(i);
//            String jsonString = JSON.toJSONString(amazonReviewsUSBooks);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(amazonReviewsUSBooks);
//            System.out.println("jsonString = " + jsonString);
            jsonList.add(jsonString);
        }
        return jsonList;
    }

    public static List<AmazonReviewsUSBooks> readCSV(String filePath) {
        List<AmazonReviewsUSBooks> dataList;
        try {
            Path path = Paths.get(filePath);
            dataList = Files.lines(path)
                    .map(line -> line.split("\t"))
                    .map(data -> new AmazonReviewsUSBooks(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9], data[10], data[11], data[12], data[13], data[14]))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            dataList = null;
        }
        return dataList;
    }



    static class AmazonReviewsUSBooks {
        String marketplace;
        String customerId;
        String reviewId;
        String productId;
        String productParent;
        String productTitle;
        String productCategory;
        String starRating;
        String helpfulVotes;
        String totalVotes;
        String vine;
        String verifiedPurchase;
        String reviewHeadline;
        String reviewBody;
        String reviewDate;

        public AmazonReviewsUSBooks(String marketplace, String customerId, String reviewId, String productId, String productParent, String productTitle, String productCategory, String starRating, String helpfulVotes, String totalVotes, String vine, String verifiedPurchase, String reviewHeadline, String reviewBody, String reviewDate) {
            this.marketplace = marketplace;
            this.customerId = customerId;
            this.reviewId = reviewId;
            this.productId = productId;
            this.productParent = productParent;
            this.productTitle = productTitle;
            this.productCategory = productCategory;
            this.starRating = starRating;
            this.helpfulVotes = helpfulVotes;
            this.totalVotes = totalVotes;
            this.vine = vine;
            this.verifiedPurchase = verifiedPurchase;
            this.reviewHeadline = reviewHeadline;
            this.reviewBody = reviewBody;
            this.reviewDate = reviewDate;
        }


        @Override
        public String toString() {
            return "AmazonReviewsUSBooks{" +
                    "marketplace='" + marketplace + '\'' +
                    ", customerId='" + customerId + '\'' +
                    ", reviewId='" + reviewId + '\'' +
                    ", productId='" + productId + '\'' +
                    ", productParent='" + productParent + '\'' +
                    ", productTitle='" + productTitle + '\'' +
                    ", productCategory='" + productCategory + '\'' +
                    ", starRating='" + starRating + '\'' +
                    ", helpfulVotes='" + helpfulVotes + '\'' +
                    ", totalVotes='" + totalVotes + '\'' +
                    ", vine='" + vine + '\'' +
                    ", verifiedPurchase='" + verifiedPurchase + '\'' +
                    ", reviewHeadline='" + reviewHeadline + '\'' +
                    ", reviewBody='" + reviewBody + '\'' +
                    ", reviewDate='" + reviewDate + '\'' +
                    '}';
        }

        public String getMarketplace() {
            return marketplace;
        }

        public void setMarketplace(String marketplace) {
            this.marketplace = marketplace;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getReviewId() {
            return reviewId;
        }

        public void setReviewId(String reviewId) {
            this.reviewId = reviewId;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductParent() {
            return productParent;
        }

        public void setProductParent(String productParent) {
            this.productParent = productParent;
        }

        public String getProductTitle() {
            return productTitle;
        }

        public void setProductTitle(String productTitle) {
            this.productTitle = productTitle;
        }

        public String getProductCategory() {
            return productCategory;
        }

        public void setProductCategory(String productCategory) {
            this.productCategory = productCategory;
        }

        public String getStarRating() {
            return starRating;
        }

        public void setStarRating(String starRating) {
            this.starRating = starRating;
        }

        public String getHelpfulVotes() {
            return helpfulVotes;
        }

        public void setHelpfulVotes(String helpfulVotes) {
            this.helpfulVotes = helpfulVotes;
        }

        public String getTotalVotes() {
            return totalVotes;
        }

        public void setTotalVotes(String totalVotes) {
            this.totalVotes = totalVotes;
        }

        public String getVine() {
            return vine;
        }

        public void setVine(String vine) {
            this.vine = vine;
        }

        public String getVerifiedPurchase() {
            return verifiedPurchase;
        }

        public void setVerifiedPurchase(String verifiedPurchase) {
            this.verifiedPurchase = verifiedPurchase;
        }

        public String getReviewHeadline() {
            return reviewHeadline;
        }

        public void setReviewHeadline(String reviewHeadline) {
            this.reviewHeadline = reviewHeadline;
        }

        public String getReviewBody() {
            return reviewBody;
        }

        public void setReviewBody(String reviewBody) {
            this.reviewBody = reviewBody;
        }

        public String getReviewDate() {
            return reviewDate;
        }

        public void setReviewDate(String reviewDate) {
            this.reviewDate = reviewDate;
        }
    }
}
