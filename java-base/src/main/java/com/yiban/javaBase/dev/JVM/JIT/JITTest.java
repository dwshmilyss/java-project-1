package com.yiban.javaBase.dev.JVM.JIT;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Callable;

/**
 * 测试JIT编译器的优化原理
 * 将直接操作字段和通过getter/setter方法做了对比。如果简单的getters和setters方法没有使用内联的话，那调用它们的代价是相当大的，因为方法调用比直接操作字段代价更高。
 * 1、测试的时候可以通过 添加/去除“-Djava.compiler=none”参数来选择是否使用JIT优化
 * 2、在输出的时候可以加上参数 “-XX:+PrintCompilation” 来查看JIT优化的详细信息
 *
 * @auther WEI.DUAN
 * @date 2017/4/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class JITTest {
    private static double timeTestRun(String desc, int runs,
                                      Callable<Double> callable) throws Exception {
        long start = System.nanoTime();
        callable.call();
        long time = System.nanoTime() - start;
        return (double) time / runs;
    }

    // Housekeeping method to provide nice uptime values for us
    private static long uptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime() + 15;
        // fudge factor
    }

    public static void main(String... args) throws Exception {
        int iterations = 0;

        for (int i : new int[]
                {100, 1000, 5000, 9000, 10000, 11000, 13000, 20000,30000,40000, 50000, 100000}) {
            final int runs = i;
//            final int runs = i - iterations;
//            iterations += runs;

            // NOTE: We return double (sum of values) from our test cases to
            // prevent aggressive JIT compilation from eliminating the loop in
            // unrealistic ways
            Callable<Double> directCall = new JITTest().new DFACaller(runs);
            Callable<Double> viaGetSet = new JITTest().new GetSetCaller(runs);

            double time1 = timeTestRun("public fields", runs, directCall);
            double time2 = timeTestRun("getter/setter fields", runs, viaGetSet);

            System.out.printf("%7d %,7d\t\tfield access=%.1f ns, getter/setter=%.1f ns%n",
                    uptime(), runs, time1, time2);
            // added to improve readability of the output
            Thread.sleep(100);
        }
    }

    private class DFACaller implements Callable<Double> {
        private final int runs;

        public DFACaller(int runs) {
            this.runs = runs;
        }

        @Override
        public Double call() throws Exception {
        DirectFieldAccess direct = new DirectFieldAccess();
        double sum = 0;
        for (int i = 0; i < runs; i++) {
            direct.one++;
            sum += direct.one;
        }
        return sum;

    }
}

    private class DirectFieldAccess {
        int one;
    }

    public class GetSetCaller implements Callable<Double> {
        private final int runs;

        public GetSetCaller(int runs_) {
            runs = runs_;
        }

        @Override
        public Double call() {
            ViaGetSet getSet = new ViaGetSet();
            double sum = 0;
            for (int i = 0; i < runs; i++) {
                getSet.setOne(getSet.getOne() + 1);
                sum += getSet.getOne();
            }
            return sum;
        }
    }

    public class ViaGetSet {
        private int one;

        public int getOne() {
            return one;
        }

        public void setOne(int one) {
            this.one = one;
        }
    }
}
