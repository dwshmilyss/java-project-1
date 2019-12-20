##### Redis是如何实现点赞、取消点赞的
[URL](https://mp.weixin.qq.com/s/zUg2ooAcHnToqC6wdw7Sww)
```aidl
 本文基于 SpringCloud, 用户发起点赞、取消点赞后先存入 Redis 中，再每隔两小时从 Redis 读取点赞数据写入数据库中做持久化存储。
```
