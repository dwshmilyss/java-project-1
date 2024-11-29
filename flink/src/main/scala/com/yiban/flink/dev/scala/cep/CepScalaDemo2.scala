package com.yiban.flink.dev.scala.cep

import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.cep.scala.CEP
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.api.scala._

object CepScalaDemo2 {

  case class Message(userId: String, ip: String, msg: String, ts:Long)

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // 模拟数据源
    val loginEventStream: DataStream[Message] = env.fromCollection[Message](
      List(
        Message("1", "192.168.0.1", "beijing",1),
        Message("1", "192.168.0.2", "beijing",2),
        Message("1", "192.168.0.3", "beijing",3),
        Message("1", "192.168.0.4", "beijing",4),
        Message("2", "192.168.10.10", "shanghai",5),
        Message("3", "192.168.10.10", "beijing",6),
        Message("3", "192.168.10.11", "beijing",7),
        Message("4", "192.168.10.10", "beijing",8),
        Message("5", "192.168.10.11", "shanghai",9),
        Message("4", "192.168.10.12", "beijing",10),
        Message("5", "192.168.10.13", "shanghai",11),
        Message("5", "192.168.10.14", "shanghai",12),
        Message("5", "192.168.10.15", "beijing",13),
        Message("6", "192.168.10.16", "beijing",14),
        Message("6", "192.168.10.17", "beijing",15),
        Message("6", "192.168.10.18", "beijing",16),
        Message("5", "192.168.10.18", "shanghai",17),
        Message("6", "192.168.10.19", "beijing",18),
        Message("6", "192.168.10.19", "beijing",19),
        Message("5", "192.168.10.18", "shanghai",20)
      )
    ).assignAscendingTimestamps(_.ts * 1000)

    //定义模式
    val loginbeijingPattern = Pattern.begin[Message]("start")
      .where(_.msg != null) //一条登录失败
      .times(5).optional  //将满足五次的数据配对打印
      .within(Time.minutes(10))

    //进行分组匹配
    val loginbeijingDataPattern = CEP.pattern(loginEventStream.keyBy(_.userId), loginbeijingPattern)

    //查找符合规则的数据
    val loginbeijingResult: DataStream[Option[Iterable[Message]]] = loginbeijingDataPattern.select(patternSelectFun = (pattern: collection.Map[String, Iterable[Message]]) => {
      var loginEventList: Option[Iterable[Message]] = null
      loginEventList = pattern.get("start") match {
        case Some(value) => {
          if (value.toList.map(x => (x.userId, x.msg)).distinct.size == 1) {
            Some(value)
          } else {
            None
          }
        }
      }
      loginEventList
    })

    //打印测试
    loginbeijingResult.filter(x=>x!=None).map(x=>{
      x match {
        case Some(value)=> value
      }
    }).print()

    env.execute("BarrageBehavior02")
  }
}
