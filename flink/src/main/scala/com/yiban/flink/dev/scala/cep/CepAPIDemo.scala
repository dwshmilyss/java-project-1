package com.yiban.flink.dev.scala.cep

import org.apache.flink.api.scala._
import org.apache.flink.cep.scala.CEP
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object CepAPIDemo {
  def main(args: Array[String]): Unit = {
//    testNoNext()
    testGreedyAndOptional()
  }

  /**
   * NoNext
   */
  def testNoNext(): Unit = {
    // 创建 Flink 流处理环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 模拟输入数据
    val input = env.fromElements(("a", 1L), ("b", 2L), ("c", 3L), ("d", 4L), ("a", 5L), ("e", 6L), ("f", 7L)).assignAscendingTimestamps(_._2 * 1000)

    // 定义 CEP 模式
    val pattern: Pattern[(String, Long), (String, Long)] = Pattern
      .begin[(String, Long)]("start")
      .where(_._1 == "a") // 第一个事件是 "a"
      // 紧接的事件不能是 "b" 这里依然能输出 [start=(a,5),end=(e,6)]
      .notNext("middle").where(_._1 == "b")
      // 但如果是 next.where(_1 != "b") 则整个模式匹配终止，不会输出[start=(a,5),end=(e,6)],但是这里输出了[start=(a,5),end=(f,7)] 疑惑...
      // 所以如果要匹配 去除a后面是b的模式，就用notNext
      //      .next("middle").where(_._1 != "b")
      .followedBy("end") // 后面可以是任意事件
    //      .followedByAny("end") // 后面可以是任意事件
    //notNext+oneOrMore输出[start=(a,5),end=(e,6)],[start=(a,5),end=(e,6)(f,7)]
    //
    //          .oneOrMore
    //      .consecutive()

    // 将模式应用到数据流
    val patternStream = CEP.pattern(input, pattern)

    // 处理匹配结果
    val result = patternStream.select((pattern: collection.Map[String, Iterable[(String, Long)]]) => {
      val start = pattern.getOrElse("start", null).iterator.next()
      var end: StringBuilder = new StringBuilder()
      var endStr = ""
      pattern.get("end") match {
        case Some(value) => {
          //          end = value.toList.mkString
          val iterator = value.iterator
          while (iterator.hasNext) {
            end = end.append(iterator.next()).append(",")
          }
          endStr = end.toString()
          if (endStr.endsWith(",")) {
            endStr = endStr.substring(0, endStr.length - 1)
          }
        }
        case None => None
      }
      s"Matched: start=$start,end=$endStr"
    })

    // 打印结果
    result.print()
    // 执行任务
    env.execute("CEP NotNext Example")
  }

  def testGreedyAndOptional(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

//    val input = env.fromElements("a", "a", "b" , "c")
    val input = env.fromElements(("a", 1L), ("a", 2L), ("c", 3L), ("d", 4L)).assignAscendingTimestamps(_._2 * 1000)

    /**
     * Matched: (a,1)
     * Matched: (a,1),(a,2)
     * Matched: (a,2)
     */
    val pattern = Pattern.begin[(String,Long)]("start")
      .where(_._1 == "a")
      .oneOrMore
      .greedy

    val patternStream = CEP.pattern(input, pattern)

    val result = patternStream.select((pattern: collection.Map[String, Iterable[(String,Long)]]) => {
      val events = pattern("start").toList
      s"Matched: ${events.mkString(",")}"
    })

    result.print()

    env.execute("test greedy or optional")
  }
}
