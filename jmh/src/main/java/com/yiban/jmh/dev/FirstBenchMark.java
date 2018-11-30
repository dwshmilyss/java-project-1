package com.yiban.jmh.dev;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @auther WEI.DUAN
 * @date 2018/11/30
 * @website http://blog.csdn.net/dwshmilyss
 */
@BenchmarkMode({Mode.All}) // 测试方法平均执行时间
@OutputTimeUnit(TimeUnit.MICROSECONDS) // 输出结果的时间粒度为微秒
@State(Scope.Thread) // 每个测试线程一个实例
public class FirstBenchMark {
    /** Logger **/
    private static final Logger LOGGER = LoggerFactory.getLogger(FirstBenchMark.class);

    @Benchmark
    public String stringConcat() {
        String a = "a";
        String b = "b";
        String c = "c";
        String s = a + b + c;
        LOGGER.debug(s);
        return s;
    }

    public static void main(String[] args) throws RunnerException {
        // 使用一个单独进程执行测试，执行5遍warmup，然后执行5遍测试
        Options opt = new OptionsBuilder().include(FirstBenchMark.class.getSimpleName()).forks(1).warmupIterations(5)
                .measurementIterations(5).build();
        new Runner(opt).run();
    }

}