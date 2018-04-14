package com.yiban.javaBase.dev.ml.HMM;

import static com.yiban.javaBase.dev.ml.HMM.WeatherExample.Activity.*;
import static com.yiban.javaBase.dev.ml.HMM.WeatherExample.Weather.Rainy;
import static com.yiban.javaBase.dev.ml.HMM.WeatherExample.Weather.Sunny;

/**
 * Created by Administrator on 2018/2/4 0004.
 */
public class WeatherExample {
    static int[] states = new int[]{Rainy.ordinal(), Sunny.ordinal()};
    static int[] observations = new int[]{walk.ordinal(), shop.ordinal(), clean.ordinal()};
    static double[] start_probability = new double[]{0.6, 0.4};
    static double[][] transititon_probability = new double[][]{
            {0.7, 0.3},
            {0.4, 0.6},
    };
    static double[][] emission_probability = new double[][]{
            {0.1, 0.4, 0.5},
            {0.6, 0.3, 0.1},
    };

    public static void main(String[] args) {
        int[] result = Viterbi.compute(observations, states, start_probability, transititon_probability, emission_probability);
        for (int r : result) {
            System.out.print(Weather.values()[r] + " ");
        }
        System.out.println();
    }
    enum Weather {
        Rainy,
        Sunny,
    }

    enum Activity {
        walk,
        shop,
        clean,
    }
}
