#
https://docs.spring.io/spring-kafka/reference/html/_reference.html

测试swegger1
1. 启动KafkaBatchConsumerApplication
2. http://127.0.0.1:5558/swagger-ui.html
3. ResponseBody是map类型的时候，传参格式：{"aa":"bb"}

#### 关于KafkaOffsetMonitor监控spring-kafka
```$xslt
测试了三个kafka版本 都是可以监控到的
1、kafka如果是0.10.x.x的版本 那么spring-kafka无需指定版本号。默认是1.1.8
2、kafka如果是0.11.x.x的版本，那么spring-kafka可以指定1.3.x的版本（测试用的1.3.6）
3、kafka如果是1.1.x的版本，同kafka 0.11.x.x
注意 以上三个版本都只能用subscirbe的方式消费，不能用assign的方式，否则无法监控到active consumer。 
```