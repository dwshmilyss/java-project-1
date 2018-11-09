package com.yiban.javaBase.dev.JIT;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 2018/10/25 0025.
 */
public class Demo1 {

    private static double timeTestRun(String desc, int runs,Callable<Double> callable) throws Exception {
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
                { 100, 1000, 5000, 9000, 10000, 11000, 13000, 20000, 100000} ) {
            final int runs = i - iterations;
            iterations += runs;

            // NOTE: We return double (sum of values) from our test cases to
            // prevent aggressive JIT compilation from eliminating the loop in
            // unrealistic ways
            Callable<Double> directCall = new DFACaller(runs);
            Callable<Double> viaGetSet = new GetSetCaller(runs);
            double time1 = timeTestRun("public fields", runs, directCall);
            double time2 = timeTestRun("getter/setter fields", runs, viaGetSet);

            System.out.printf("%7d %,7d\t\tfield access=%.1f ns, getter/setter=%.1f ns%n",
                    uptime(), iterations, time1, time2);

            // added to improve readability of the output
            Thread.sleep(100);
        }
    }

}

class DFACaller implements Callable<Double> {
    private final int runs;

    public DFACaller(int runs_) {
        runs = runs_;
    }

    @Override
    public Double call() {
        DirectFieldAccess direct = new DirectFieldAccess();
        double sum = 0;
        for (int i = 0; i < runs; i++) {
            direct.one++;
            sum += direct.one;
        }
        return sum;
    }
}

class DirectFieldAccess {
    int one;
}


class GetSetCaller implements Callable<Double> {
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

class ViaGetSet {
    private int one;

    public int getOne() {
        return one;
    }

    public void setOne(int one) {
        this.one = one;
    }
}
