package com.github.liuyueyi.quick.algorithm.math;

import java.util.List;

/**
 * @author yihui
 * @date 2021/12/6
 */
public class GaussianEliminationTest {
    public static void main(String[] args) {
        double[][] matrix = {
                // 行
                {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15},
                {0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 20},
                {0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 10},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 30},
                // 列
                {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 20},
                {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 20},
                {0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 35},
                // 0值
                {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        };
        List<Double> result = GaussianElimination.calculate((matrix));
        System.out.println("---------\n" + result);
        System.out.println("------------");

        double[][] out = {
                {1, 1, 1, 10},
                {2, 2, 2, 20},
                {1, 1, 0, 5},
        };
        System.out.println(GaussianElimination.calculate(out));
    }
}
