package com.yiban.flink.dev.scala.cep

import org.apache.flink.api.scala._
import org.apache.flink.cep.PatternSelectFunction
import org.apache.flink.cep.scala.{CEP, PatternStream}
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * 用户如果在 10s 内，同时输入 TMD 超过 5 次，就认为用户为恶意攻击，识别出该用户。
 */
object CepScalaDemo1 {
  case class  LoginEvent(userId:String, message:String, timestamp:Long){
    override def toString: String = userId
  }

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    //flink从1.12开始默认就是TimeCharacteristic.EventTime，这里只是为了让逻辑更清晰
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    // 模拟数据源
    val loginEventStream: DataStream[LoginEvent] = env.fromCollection(
      List(
        LoginEvent("1", "TMD", 1618498576),
        LoginEvent("1", "TMD", 1618498577),
        LoginEvent("1", "TMD", 1618498579),
        LoginEvent("1", "TMD", 1618498580),
        LoginEvent("1", "TMD", 1618498581),
        LoginEvent("1", "TMD", 1618498582),
        LoginEvent("2", "TMD", 1618498583),
        LoginEvent("2", "TMD", 1618498584),
        LoginEvent("2", "TMD", 1618498585),
        LoginEvent("2", "TMD", 1618498586),
        LoginEvent("2", "TMD", 1618498587),
        LoginEvent("1", "aaa", 1618498588),
        LoginEvent("1", "TMD", 1618498585)
      )
    ).assignAscendingTimestamps(_.timestamp * 1000)

// 效果同下面加上 consecutive 一样
//    val loginEventPattern : Pattern[LoginEvent,LoginEvent] = Pattern.begin[LoginEvent]("start")
//      .where(_.message == "TMD")
//      .next("second")
//      .where(_.message == "TMD")
//      .next("third")
//      .where(_.message == "TMD")
//      .next("four")
//      .where(_.message == "TMD")
//      .next("five")
//      .where(_.message == "TMD")
//      .within(Time.seconds(10))

    //定义模式
    val loginEventPattern : Pattern[LoginEvent,LoginEvent] = Pattern.begin[LoginEvent]("start")
      .where(_.message == "TMD")
      .times(5)
      .consecutive() //强制连续
      .within(Time.seconds(10))

    //匹配模式
    val patternStream: PatternStream[LoginEvent] = CEP.pattern(loginEventStream.keyBy(_.userId),loginEventPattern)

    import scala.collection.Map
    val result = patternStream.select((pattern:Map[String,Iterable[LoginEvent]]) => {
      val first = pattern.getOrElse("start",null).iterator.next()
      (first.userId,first.timestamp)
    })

    //恶意用户，实际处理可将按用户进行禁言等处理，为简化此处仅打印出该用户
    result.print("恶意用户>>>")
    env.execute("BarrageBehavior01")
  }
}
